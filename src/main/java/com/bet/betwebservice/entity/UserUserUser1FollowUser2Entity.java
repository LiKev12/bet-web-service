package com.bet.betwebservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name="user_user_user_1_follow_user_2")
@Data
public class UserUserUser1FollowUser2Entity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private UUID id;

    @Column(name="timestamp_unix")
    private Integer timestampUnix;

    @Column(name="id__user_1")
    private UUID idUser1;

    @Column(name="id__user_2")
    private UUID idUser2;

    @Column(name="is_following")
    private boolean isFollowing;

    @Column(name="timestamp_of_following")
    private Integer timestampOfFollowing;

    @Column(name="is_request_sent")
    private boolean isRequestSent;

    @Column(name="timestamp_request_sent")
    private Integer timestampRequestSent;

    @Column(name="is_request_accepted")
    private boolean isRequestAccepted;

    @Column(name="timestamp_request_accepted")
    private Integer timestampRequestAccepted;
}
