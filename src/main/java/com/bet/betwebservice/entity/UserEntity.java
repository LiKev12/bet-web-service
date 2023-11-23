package com.bet.betwebservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name="user")
@Data
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private UUID id;

    @Column(name="timestamp_unix")
    private int timestampUnix;

    @Column(name="name")
    private String name;

    @Column(name="username")
    private String username;

    @Column(name="email")
    private String email;

    @Column(name="bio")
    private String bio;

    @Column(name="image")
    private String image;

    @ManyToMany(
            fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE,
                    CascadeType.DETACH,
                    CascadeType.REFRESH
            })
    @JoinTable(
            name="task_user_task_star",
            joinColumns = @JoinColumn(name = "id__user"),
            inverseJoinColumns = @JoinColumn(name = "id__task")
    )
    List<TaskEntity> taskTaskStarList;


}
