package com.bet.betwebservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name="stamp_user_user_collect_stamp")
@Data
public class StampUserUserCollectStampEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private UUID id;

    @Column(name="timestamp_unix")
    private Integer timestampUnix;

    @Column(name="id__stamp")
    private UUID idStamp;

    @Column(name="id__user")
    private UUID idUser;
}
