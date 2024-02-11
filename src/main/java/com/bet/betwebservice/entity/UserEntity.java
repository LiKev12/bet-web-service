package com.bet.betwebservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name="user")
@Data
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private UUID id;

    @Column(name="timestamp_unix")
    private Integer timestampUnix;

    @Column(name="name")
    private String name;

    @Column(name="username")
    private String username;

    @Column(name="email")
    private String email;

    @Column(name="password")
    private String password;

    @Column(name="bio")
    private String bio;

    @Column(name="id__image_key")
    private UUID idImageKey;

    @Column(name="time_zone")
    private String timeZone;

    @JsonProperty(value="isPublic")
    @Column(name="is_public")
    private boolean isPublic;


}
