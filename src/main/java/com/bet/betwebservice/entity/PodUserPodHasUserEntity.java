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
    private Integer timestampUnix;

    @Column(name="id__pod")
    private UUID idPod;

    @Column(name="id__user")
    private UUID idUser;

    @Column(name="is_member")
    private boolean isMember;

    @Column(name="timestamp_become_member")
    private Integer timestampBecomeMember;

    @Column(name="is_moderator")
    private boolean isModerator;

    @Column(name="timestamp_become_moderator")
    private Integer timestampBecomeModerator;

    // join Pod as member - via accepting invite (can also join public Pod directly without invite)
    @Column(name="is_join_pod_invite_sent")
    private boolean isJoinPodInviteSent;

    @Column(name="is_join_pod_invite_accepted")
    private boolean isJoinPodInviteAccepted;

    @Column(name="id__user_join_pod_invite_sender")
    private UUID idUserJoinPodInviteSender;

    @Column(name="timestamp_join_pod_invite_sent")
    private Integer timestampJoinPodInviteSent;

    // become Pod moderator - via approving request (can also be added by moderator)
    @Column(name="is_become_pod_moderator_request_sent")
    private boolean isBecomePodModeratorRequestSent;

    @Column(name="is_become_pod_moderator_request_approved")
    private boolean isBecomePodModeratorRequestApproved;

    @Column(name="id__user_become_pod_moderator_request_approver")
    private UUID idUserBecomePodModeratorRequestApprover;

    @Column(name="timestamp_become_pod_moderator_request_sent")
    private Integer timestampBecomePodModeratorRequestSent;
}
