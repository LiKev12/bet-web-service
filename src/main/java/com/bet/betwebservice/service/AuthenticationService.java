package com.bet.betwebservice.service;

import com.bet.betwebservice.authentication.JwtTokenService;
import com.bet.betwebservice.common.*;
import com.bet.betwebservice.config.ApplicationConfiguration;
import com.bet.betwebservice.dao.ForgotPasswordCodeRepository;
import com.bet.betwebservice.dao.UserRepository;
import com.bet.betwebservice.entity.ForgotPasswordCodeEntity;
import com.bet.betwebservice.entity.UserEntity;
import com.bet.betwebservice.model.AuthenticationModel;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;

@Service
@Transactional
public class AuthenticationService {

    private UserRepository userRepository;
    private ForgotPasswordCodeRepository forgotPasswordCodeRepository;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private JwtTokenService jwtTokenService;
    private JwtDecoder jwtDecoder;
    private JavaMailSender javaMailSender;
    private ApplicationConfiguration applicationConfiguration;
    private Utilities utilities;

    public AuthenticationService(
        UserRepository userRepository,
        ForgotPasswordCodeRepository forgotPasswordCodeRepository,
        PasswordEncoder passwordEncoder,
        AuthenticationManager authenticationManager,
        JwtTokenService jwtTokenService,
        JwtDecoder jwtDecoder,
        JavaMailSender javaMailSender,
        ApplicationConfiguration applicationConfiguration,
        Utilities utilities
    ) {
        this.userRepository = userRepository;
        this.forgotPasswordCodeRepository = forgotPasswordCodeRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenService = jwtTokenService;
        this.jwtDecoder = jwtDecoder;
        this.javaMailSender = javaMailSender;
        this.applicationConfiguration = applicationConfiguration;
        this.utilities = utilities;
    }

    public AuthenticationModel register(JsonNode rb) throws Exception {
        RequestBodyValidator.verifyRequiredFieldNamesExist(
                rb,
                Arrays.asList(
                        "username",
                        "name",
                        "email",
                        "timeZone",
                        "password",
                        "passwordConfirmed"));
        String username = RequestBodyValidator.stringRegex(
            RequestBodyValidator.stringRequired(
                RequestBodyFormatter.fString(rb.get("username")), 
                Limits.USER_USERNAME_MIN_LENGTH_CHARACTERS, 
                Limits.USER_USERNAME_MAX_LENGTH_CHARACTERS
            ), 
            Constants.REGEX_USER_USERNAME
        );
        String name = RequestBodyFormatter.fString(rb.get("name"));
        String email = RequestBodyValidator.email(RequestBodyFormatter.fString(rb.get("email")));
        String timeZone = RequestBodyValidator.stringChoice(
                RequestBodyFormatter.fString(rb.get("timeZone")), Constants.TIME_ZONE_CHOICES);
        String password = RequestBodyValidator.stringRequired(
            RequestBodyFormatter.fString(rb.get("password")), 
            Limits.USER_PASSWORD_MIN_LENGTH_CHARACTERS, 
            Limits.USER_PASSWORD_MAX_LENGTH_CHARACTERS
        );
        String passwordConfirmed = RequestBodyFormatter.fString(rb.get("passwordConfirmed"));
        if (!password.equals(passwordConfirmed)) {
            throw new Error("PASSWORDS_DO_NOT_MATCH");
        }
        if (this.userRepository.findByUsername(username).isPresent()) {
            throw new Error("DUPLICATE_ACCOUNT_USERNAME");
        }
        if (this.userRepository.findByEmail(email).isPresent()) {
            throw new Error("DUPLICATE_ACCOUNT_EMAIL");
        }
        String encodedPassword = passwordEncoder.encode(password);
        UserEntity userEntity = new UserEntity();
        userEntity.setTimestampUnix((int) Instant.now().getEpochSecond());
        userEntity.setUsername(username);
        userEntity.setName(name);
        userEntity.setTimeZone(timeZone);
        userEntity.setPassword(encodedPassword);
        userEntity.setEmail(email);
        this.userRepository.save(userEntity);

        Authentication authenticationResult = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                userEntity.getId().toString(),
                password
            )
        );
        String jwtToken = jwtTokenService.generateJwt(authenticationResult);
        return AuthenticationModel.builder()
            .idUser(userEntity.getId())
            .jwtToken(jwtToken)
            .build();
    }

    public AuthenticationModel login(JsonNode rb) throws Exception {
        RequestBodyValidator.verifyRequiredFieldNamesExist(
                rb,
                Arrays.asList(
                        "username",
                        "password"
        ));
        String userUsername = RequestBodyValidator.stringRegex(
            RequestBodyValidator.stringRequired(
                RequestBodyFormatter.fString(rb.get("username")), 
                Limits.USER_USERNAME_MIN_LENGTH_CHARACTERS, 
                Limits.USER_USERNAME_MAX_LENGTH_CHARACTERS
            ), 
            Constants.REGEX_USER_USERNAME
        );
        String userPassword = RequestBodyValidator.stringRequired(
            RequestBodyFormatter.fString(rb.get("password")), 
            Limits.USER_PASSWORD_MIN_LENGTH_CHARACTERS, 
            Limits.USER_PASSWORD_MAX_LENGTH_CHARACTERS
        );
        Optional<UserEntity> userEntityOptional = this.userRepository.findByUsername(userUsername);
        if (!userEntityOptional.isPresent()) {
            throw new Exception("LOGIN_USER_WRONG_USERNAME_PASSWORD");
        }
        UserEntity userEntity = userEntityOptional.get();
        String idUser = userEntity.getId().toString();
        Authentication authenticationResult = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                idUser,
                userPassword
            )
        );
        String jwtToken = jwtTokenService.generateJwt(authenticationResult);
        
        return AuthenticationModel.builder()
            .idUser(userEntity.getId())
            .jwtToken(jwtToken)
            .build();
    }

    /**
     * username
     * email
     */
    public void getForgotPasswordCode(JsonNode rb) throws Exception {
        String username = RequestBodyValidator.stringRegex(
            RequestBodyValidator.stringRequired(
                RequestBodyFormatter.fString(rb.get("username")), 
                Limits.USER_USERNAME_MIN_LENGTH_CHARACTERS, 
                Limits.USER_USERNAME_MAX_LENGTH_CHARACTERS
            ), 
            Constants.REGEX_USER_USERNAME
        );
        String email = RequestBodyValidator.email(RequestBodyFormatter.fString(rb.get("email")));
        boolean isUsernameEmailCombinationExists = 
            this.userRepository.findByUsername(username).isPresent() && 
            this.userRepository.findByUsername(username).get().getEmail().equals(email);
        if (!isUsernameEmailCombinationExists) {
            throw new Exception("INVALID_USERNAME_EMAIL_COMBINATION");
        }
        UserEntity userEntity = this.userRepository.findByUsername(username).get();
        int timeNow = (int) Instant.now().getEpochSecond();
        boolean isRecentlySentForgotPasswordCode = this.forgotPasswordCodeRepository.findByIdUser(userEntity.getId().toString()).size() > 0 && 
            (timeNow - this.forgotPasswordCodeRepository.findByIdUser(userEntity.getId().toString()).get(0).getTimestampUnix()) < Constants.FORGOT_PASSWORD_SECRET_CODE_WAIT_DURATION_SECONDS;
        if (isRecentlySentForgotPasswordCode) {
            throw new Exception("ALREADY_SENT_FORGOT_PASSWORD_CODE");
        }
        String secretCode = utilities.generateForgotPasswordCode();
        ForgotPasswordCodeEntity forgotPasswordCodeEntity = new ForgotPasswordCodeEntity();
        forgotPasswordCodeEntity.setTimestampUnix((int) Instant.now().getEpochSecond());
        forgotPasswordCodeEntity.setIdUser(userEntity.getId());
        forgotPasswordCodeEntity.setSecretCode(secretCode);
        this.forgotPasswordCodeRepository.save(forgotPasswordCodeEntity);
        utilities.sendEmailForgotPasswordCode(
            javaMailSender,
            applicationConfiguration.getAwsSecret().getSpringMailUsername(),
            email,
            secretCode
        );
    }

    /**
     * username
     * secretCode
     * newPassword
     * newPasswordConfirmed
     */
    public void resetPassword(JsonNode rb) throws Exception {
        String secretCode = RequestBodyFormatter.fString(rb.get("secretCode"));
        String username = RequestBodyValidator.stringRegex(
            RequestBodyValidator.stringRequired(
                RequestBodyFormatter.fString(rb.get("username")), 
                Limits.USER_USERNAME_MIN_LENGTH_CHARACTERS, 
                Limits.USER_USERNAME_MAX_LENGTH_CHARACTERS
            ), 
            Constants.REGEX_USER_USERNAME
        );
        UserEntity userEntity = this.userRepository.findByUsername(username).get();
        ForgotPasswordCodeEntity forgotPasswordCodeEntity = this.forgotPasswordCodeRepository.findByIdUser(userEntity.getId().toString()).get(0);
        if (!secretCode.equals(forgotPasswordCodeEntity.getSecretCode())) {
            throw new Exception("INCORRECT_SECRET_CODE");
        }
        String newPassword = RequestBodyValidator.stringRequired(
            RequestBodyFormatter.fString(rb.get("newPassword")), 
            Limits.USER_PASSWORD_MIN_LENGTH_CHARACTERS, 
            Limits.USER_PASSWORD_MAX_LENGTH_CHARACTERS
        );
        String newPasswordConfirmed = RequestBodyFormatter.fString(rb.get("newPasswordConfirmed"));
        if (!newPassword.equals(newPasswordConfirmed)) {
            throw new Error("PASSWORDS_DO_NOT_MATCH");
        }
        String encodedNewPassword = this.passwordEncoder.encode(newPassword);
        userEntity.setPassword(encodedNewPassword);
        this.userRepository.save(userEntity);
    }

    /**
     * email
     */
    public void getForgottenUsername(JsonNode rb) throws Exception {
        String email = RequestBodyValidator.email(RequestBodyFormatter.fString(rb.get("email")));
        boolean isUserWithSpecifiedEmailExists = this.userRepository.findByEmail(email).isPresent();
        if (!isUserWithSpecifiedEmailExists) {
            throw new Exception("NO_USER_WITH_SPECIFIED_EMAIL");
        }
        UserEntity userEntity = this.userRepository.findByEmail(email).get();
        String username = userEntity.getUsername();
        utilities.sendEmailForgottenUsername(
            javaMailSender,
            applicationConfiguration.getAwsSecret().getSpringMailUsername(),
            email,
            username
        );
    }


}

