package com.bet.betwebservice.config;

import com.bet.betwebservice.common.AwsSecret;
import com.google.gson.Gson;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

import javax.sql.DataSource;

@Configuration
@Data
public class ApplicationConfiguration {

    @Value("${aws.access.key}")
    private String awsAccessKey;
    @Value("${aws.secret.access.key}")
    private String awsSecretAccessKey;
    @Value("${aws.secretmanagersecretname}")
    private String awsSecretManagerSecretName;
    @Value("${aws.region}")
    private String awsRegion;

    private Gson gson = new Gson();

    @Bean
    public DataSource dataSource() {
        AwsSecret secret = this.getAwsSecret();
        return DataSourceBuilder
                .create()
                .url(secret.getSpringDatasourceUrl())
                .username(secret.getSpringDatasourceUsername())
                .password(secret.getSpringDatasourcePassword())                
                .build();
    }

    public AwsSecret getAwsSecret() {
        Region region = Region.of(awsRegion);

        // Create a Secrets Manager client
        AwsCredentials credentials = AwsBasicCredentials.create(awsAccessKey, awsSecretAccessKey);
        AwsCredentialsProvider provider = StaticCredentialsProvider.create(credentials);
        SecretsManagerClient client = SecretsManagerClient.builder()
                .region(region)
                .credentialsProvider(provider)
                .build();

        GetSecretValueRequest getSecretValueRequest = GetSecretValueRequest.builder()
                .secretId(awsSecretManagerSecretName)
                .build();

        GetSecretValueResponse getSecretValueResponse;

        try {
            getSecretValueResponse = client.getSecretValue(getSecretValueRequest);
        } catch (Exception e) {
            // For a list of exceptions thrown, see
            // https://docs.aws.amazon.com/secretsmanager/latest/apireference/API_GetSecretValue.html
            throw e;
        }

        String secret = getSecretValueResponse.secretString();
        return gson.fromJson(secret, AwsSecret.class);
    }

    public AwsCredentialsProvider getAwsCredentialsProvider() {
        AwsCredentials awsCredentials = AwsBasicCredentials.create(
            this.getAwsAccessKey(),
            this.getAwsSecretAccessKey()
        );
        AwsCredentialsProvider awsCredentialsProvider = StaticCredentialsProvider.create(awsCredentials);
        return awsCredentialsProvider;
    }

    public Region getAwsRegion() {
        return Region.of(this.awsRegion);
    }

}