package com.bet.betwebservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class UserModel {
    private UUID id;
    private Integer timestampUnix;
    private String name;
    private String username;
    private String password;
    private String email;
    private String bio;
    private UUID idImageKey;
    private String timeZone;
    @JsonProperty(value="isPublic")
    private boolean isPublic;
    private String token;
}
