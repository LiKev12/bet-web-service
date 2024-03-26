package com.bet.betwebservice.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AwsSecret {

    private String springMailUsername;
    private String springMailPassword;
    private String springDatasourceUrl;
    private String springDatasourceUsername;
    private String springDatasourcePassword;
    private String s3BucketName;
}