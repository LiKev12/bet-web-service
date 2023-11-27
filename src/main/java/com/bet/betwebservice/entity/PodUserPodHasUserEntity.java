package com.bet.betwebservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name="pod_user_pod_has_user")
@Data
public class PodUserPodHasUserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private UUID id;

    @Column(name="timestamp_unix")
    private int timestampUnix;

    @Column(name="id__pod")
    private UUID idPod;

    @Column(name="id__user")
    private UUID idUser;

    @Column(name="is_member")
    private boolean isMember;

    @Column(name="is_moderator")
    private boolean isModerator;
}
