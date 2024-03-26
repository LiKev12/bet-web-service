package com.bet.betwebservice.service;

import com.bet.betwebservice.dao.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AppServiceRepositoryMiddleLayer {
    private NotificationRepository notificationRepository;
    private PodRepository podRepository;
    private PodUserPodHasUserRepository podUserPodHasUserRepository;
    private StampRepository stampRepository;
    private StampTaskStampHasTaskRepository stampTaskStampHasTaskRepository;
    private StampUserUserCollectStampRepository stampUserUserCollectStampRepository;
    private TaskRepository taskRepository;
    private TaskUserTaskCommentReactionRepository taskUserTaskCommentReactionRepository;
    private TaskUserTaskCommentReplyReactionRepository taskUserTaskCommentReplyReactionRepository;
    private TaskUserTaskCommentReplyRepository taskUserTaskCommentReplyRepository;
    private TaskUserTaskCommentRepository taskUserTaskCommentRepository;
    private TaskUserTaskCompleteRepository taskUserTaskCompleteRepository;
    private TaskUserTaskNoteRepository taskUserTaskNoteRepository;
    private TaskUserTaskPinRepository taskUserTaskPinRepository;
    private TaskUserTaskReactionRepository taskUserTaskReactionRepository;
    private TaskUserTaskStarRepository taskUserTaskStarRepository;
    private UserRepository userRepository;
    private UserUserUser1FollowUser2Repository userUserUser1FollowUser2Repository;

    public AppServiceRepositoryMiddleLayer(
        NotificationRepository notificationRepository,
        PodRepository podRepository,
        PodUserPodHasUserRepository podUserPodHasUserRepository,
        StampRepository stampRepository,
        StampTaskStampHasTaskRepository stampTaskStampHasTaskRepository,
        StampUserUserCollectStampRepository stampUserUserCollectStampRepository,
        TaskRepository taskRepository,
        TaskUserTaskCommentReactionRepository taskUserTaskCommentReactionRepository,
        TaskUserTaskCommentReplyReactionRepository taskUserTaskCommentReplyReactionRepository,
        TaskUserTaskCommentReplyRepository taskUserTaskCommentReplyRepository,
        TaskUserTaskCommentRepository taskUserTaskCommentRepository,
        TaskUserTaskCompleteRepository taskUserTaskCompleteRepository,
        TaskUserTaskNoteRepository taskUserTaskNoteRepository,
        TaskUserTaskPinRepository taskUserTaskPinRepository,
        TaskUserTaskReactionRepository taskUserTaskReactionRepository,
        TaskUserTaskStarRepository taskUserTaskStarRepository,
        UserRepository userRepository,
        UserUserUser1FollowUser2Repository userUserUser1FollowUser2Repository
    ) {
        this.notificationRepository = notificationRepository;
        this.podRepository = podRepository;
        this.podUserPodHasUserRepository = podUserPodHasUserRepository;
        this.stampRepository = stampRepository;
        this.stampTaskStampHasTaskRepository = stampTaskStampHasTaskRepository;
        this.stampUserUserCollectStampRepository = stampUserUserCollectStampRepository;
        this.taskRepository = taskRepository;
        this.taskUserTaskCommentReactionRepository = taskUserTaskCommentReactionRepository;
        this.taskUserTaskCommentReplyReactionRepository = taskUserTaskCommentReplyReactionRepository;
        this.taskUserTaskCommentReplyRepository = taskUserTaskCommentReplyRepository;
        this.taskUserTaskCommentRepository = taskUserTaskCommentRepository;
        this.taskUserTaskCompleteRepository = taskUserTaskCompleteRepository;
        this.taskUserTaskNoteRepository = taskUserTaskNoteRepository;
        this.taskUserTaskPinRepository = taskUserTaskPinRepository;
        this.taskUserTaskReactionRepository = taskUserTaskReactionRepository;
        this.taskUserTaskStarRepository = taskUserTaskStarRepository;
        this.userRepository = userRepository;
        this.userUserUser1FollowUser2Repository = userUserUser1FollowUser2Repository;
    }
    
}