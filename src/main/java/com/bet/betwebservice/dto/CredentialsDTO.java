package com.bet.betwebservice.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.Data;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CredentialsDTO {
    private String login;
    private char[] password;
}