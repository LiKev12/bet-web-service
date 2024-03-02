package com.bet.betwebservice.authentication;

import com.bet.betwebservice.dao.v1.UserRepository;
import com.bet.betwebservice.entity.UserEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class AuthenticationUserDetailsService implements UserDetailsService {

    private UserRepository userRepository;

    public AuthenticationUserDetailsService(
        UserRepository userRepository
    ) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String authenticationUserModelUsername) throws UsernameNotFoundException {
        Optional<UserEntity> userEntityOptional = this.userRepository.findById(UUID.fromString(authenticationUserModelUsername));
        if (!userEntityOptional.isPresent()) {
            throw new UsernameNotFoundException("NO_USER_FOUND");
        }
        UserEntity userEntity = userEntityOptional.get();
        // IMPORTANT: only [username, password] required, AuthenticationUserModel username is the UserModel id (because we want to extract idUser from the JWT token)
        return AuthenticationUserModel.builder()
                .username(authenticationUserModelUsername)
                .password(userEntity.getPassword())
                .build();
    }
}

