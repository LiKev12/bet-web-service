package com.bet.betwebservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class AccountSettingsPageModel {
    private UUID id;
    private String username;
    private String email;
    private String imageLink;
    private String timeZone;
}
