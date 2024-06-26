package com.bet.betwebservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class UserModel {
    private UUID id;
    private Integer timestampUnix;
    private String name;
    private String username;
    @JsonIgnore
    private String password;
    private String email;
    private String bio;
    private UUID idImageKey;
    private String timeZone;
    @JsonProperty(value="isPublic")
    private boolean isPublic;
    private String jwtToken;
}
