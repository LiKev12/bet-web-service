package com.bet.betwebservice.common;

import com.bet.betwebservice.dao.v1.NotificationRepository;
import com.bet.betwebservice.dao.v1.UserRepository;
import com.bet.betwebservice.entity.NotificationEntity;
import com.bet.betwebservice.entity.UserEntity;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

public class Utilities {
    
    public static String getPresignedUrl(String folderName, UUID objectKeyId) {
        if (objectKeyId == null) {
            return null;
        }
        String bucketName = Constants.S3_BUCKET_NAME_ALPHA;
        String keyName = String.format("%s/%s", folderName, objectKeyId.toString());
        AwsCredentials credentials = AwsBasicCredentials.create(Constants.S3_BUCKET_ALPHA_ACCESS_KEY, Constants.S3_BUCKET_ALPHA_SECRET_ACCESS_KEY);
        AwsCredentialsProvider provider = StaticCredentialsProvider.create(credentials);
        Region region = Region.US_WEST_2;
        S3Presigner presigner = S3Presigner.builder().region(region).credentialsProvider(provider).build();
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .build();

        GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(60))
                .getObjectRequest(getObjectRequest)
                .build();

        PresignedGetObjectRequest presignedGetObjectRequest = presigner.presignGetObject(getObjectPresignRequest);
        return presignedGetObjectRequest.url().toString();
    }

    public static void putObjectInS3(String folderName, UUID objectKeyId, RequestBody objectRequestBody) {
        String bucketName = Constants.S3_BUCKET_NAME_ALPHA;
        // https://stackoverflow.com/questions/30939904/uploading-base64-encoded-image-to-amazon-s3-using-java
        // https://stackoverflow.com/questions/23714383/what-are-all-the-possible-values-for-http-content-type-header
        AwsCredentials credentials = AwsBasicCredentials.create(Constants.S3_BUCKET_ALPHA_ACCESS_KEY, Constants.S3_BUCKET_ALPHA_SECRET_ACCESS_KEY);
        AwsCredentialsProvider provider = StaticCredentialsProvider.create(credentials);
        S3Client s3Client = S3Client.builder().region(Region.US_WEST_2).credentialsProvider(provider).build();
        String keyName = String.format("%s/%s", folderName, objectKeyId.toString());
        PutObjectRequest putObjectRequest = PutObjectRequest.builder().bucket(bucketName).key(keyName).build();
        s3Client.putObject(putObjectRequest, objectRequestBody);
    }

    public static String getDatetimeDateOnlyFromTimestamp(ZoneId userTimeZoneZoneId, Integer timestamp) {
        if (timestamp == null || timestamp == 0) {
            return null;
        }
        return Instant.ofEpochSecond(timestamp).atZone(userTimeZoneZoneId).toLocalDate().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
    }

    public static String getDatetimeDateAndTimeFromTimestamp(ZoneId userTimeZoneZoneId, Integer timestamp) {
        if (timestamp == null || timestamp == 0) {
            return null;
        }
        return Instant.ofEpochSecond(timestamp).atZone(userTimeZoneZoneId).format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"));
    }

    public static ZoneId getUserTimeZoneZoneId(String idUser, UserRepository userRepository) {
        Optional<UserEntity> userEntityOptional = userRepository.findById(UUID.fromString(idUser));
        UserEntity userEntity = userEntityOptional.get();
        ZoneId userTimeZoneZoneId = ZoneId.of(userEntity.getTimeZone());
        return userTimeZoneZoneId;
    }

    public static boolean isValidTimestamp(Integer timestamp) {
        if (timestamp == null) {
            return false;
        }
        return timestamp > 0;
    }

    public static void generateNotification(
        NotificationRepository notificationRepository,
        UUID idUser, 
        String notificationType, 
        String notificationMessage, 
        String linkPageType, 
        UUID idLinkPage
    ) {
        NotificationEntity notification = new NotificationEntity();
        notification.setTimestampUnix((int) Instant.now().getEpochSecond());
        notification.setIdUser(idUser);
        notification.setNotificationType(notificationType);
        notification.setNotificationMessage(notificationMessage);
        notification.setLinkPageType(linkPageType);
        notification.setIdLinkPage(idLinkPage);
        notification.setSeen(false);
        notification.setDismissed(false);
        notificationRepository.save(notification);
    }

    public static String generateForgotPasswordCode() {
        String possibleCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Constants.FORGOT_PASSWORD_SECRET_CODE_LENGTH_CHARACTERS; i++) {
            Random random = new Random();
            int idx = random.nextInt(possibleCharacters.length());
            sb.append(possibleCharacters.charAt(idx));
        }
        return sb.toString();
    }

    public static void sendEmailForgotPasswordCode(
        JavaMailSender javaMailSender,
        String emailFromAddress,
        String emailToAddress,
        String forgotPasswordCode
    ) throws Exception {
        // https://www.youtube.com/watch?v=OdQ3GyBsdAA&t=5s&ab_channel=lambdaCode
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

        mimeMessageHelper.setFrom(emailFromAddress);
        mimeMessageHelper.setTo(emailToAddress);
        mimeMessageHelper.setSubject(Constants.EMAIL_SUBJECT_FORGOT_PASSWORD);
        mimeMessageHelper.setText(String.format("Your password recovery code is as follows. Please do not share with anyone: %s", forgotPasswordCode));
        javaMailSender.send(mimeMessage);
    }

    public static void sendEmailForgottenUsername(
        JavaMailSender javaMailSender,
        String emailFromAddress,
        String emailToAddress,
        String forgottenUsername
    ) throws Exception {
        // https://www.youtube.com/watch?v=OdQ3GyBsdAA&t=5s&ab_channel=lambdaCode
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

        mimeMessageHelper.setFrom(emailFromAddress);
        mimeMessageHelper.setTo(emailToAddress);
        mimeMessageHelper.setSubject(Constants.EMAIL_SUBJECT_FORGOT_USERNAME);
        mimeMessageHelper.setText(String.format("Your username associated with this email address is: %s", forgottenUsername));
        javaMailSender.send(mimeMessage);
    }
}
