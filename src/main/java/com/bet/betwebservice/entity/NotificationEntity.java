package com.bet.betwebservice.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name="notification")
@Data
public class NotificationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private UUID id;

    @Column(name="timestamp_unix")
    private Integer timestampUnix;

    @Column(name="id__user")
    private UUID idUser;

    @Column(name="notification_type")
    private String notificationType;

    @Column(name="notification_message")
    private String notificationMessage;

    @Column(name="link_page_type")
    private String linkPageType;

    @Column(name="id__link_page")
    private UUID idLinkPage;

    @JsonProperty(value="isSeen")
    @Column(name="is_seen")
    private boolean isSeen;

    @JsonProperty(value="isDismissed")
    @Column(name="is_dismissed")
    private boolean isDismissed;
}
