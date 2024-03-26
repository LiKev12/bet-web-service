package com.bet.betwebservice.service;

import com.bet.betwebservice.dao.*;
import com.bet.betwebservice.entity.PodUserPodHasUserEntity;
import com.bet.betwebservice.entity.StampEntity;
import com.bet.betwebservice.entity.StampTaskStampHasTaskEntity;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class AuthorizationService {
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

    public AuthorizationService(
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
            UserUserUser1FollowUser2Repository userUserUser1FollowUser2Repository) {
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

    public void authorizeGetPodPage(
        String idUser, 
        String idPod
    ) throws Exception {
        try {
            if (!this.podRepository.findById(UUID.fromString(idPod)).isPresent()) {
                throw new Exception("UNAUTHORIZED_ACCESS");
            }
            this._privatePodMember(idUser, idPod);
        } catch(Exception e) {
            throw new Exception("UNAUTHORIZED_ACCESS");
        }
    }

    public void authorizeGetUserBubblesPodMember(
        String idUser, 
        String idPod
    ) throws Exception {
        try {
            this._privatePodMember(idUser, idPod);
        } catch(Exception e) {
            throw new Exception("UNAUTHORIZED_ACCESS");
        }
    }

    public void authorizeGetUserBubblesPodModerator(
        String idUser, 
        String idPod
    ) throws Exception {
        try {
            this._privatePodMember(idUser, idPod);
        } catch(Exception e) {
            throw new Exception("UNAUTHORIZED_ACCESS");
        }
    }

    public void authorizeGetUserBubblesInviteJoinPod(
        String idUser, 
        String idPod
    ) throws Exception {
        try {
            this._podMember(idUser, idPod);
        } catch(Exception e) {
            throw new Exception("UNAUTHORIZED_ACCESS");
        }
    }

    public void authorizeGetUserBubblesAddPodModerator(
        String idUser,
        String idPod
    ) throws Exception {
        try {
            this._podModerator(idUser, idPod);
        } catch(Exception e) {
            throw new Exception("UNAUTHORIZED_ACCESS");
        }
    }

    public void authorizeGetUserBubblesPendingBecomePodModeratorRequest(
        String idUser,
        String idPod
    ) throws Exception {
        try {
            this._podModerator(idUser, idPod);
        } catch(Exception e) {
            throw new Exception("UNAUTHORIZED_ACCESS");
        }
    }

    public void authorizeGetPodCardsDiscover(String idUser) throws Exception {
        return;
    }

    public void authorizeGetStampCardsAssociatedWithPod(
        String idUser, 
        String idPod
    ) throws Exception {
        try {
            this._privatePodMember(idUser, idPod);
        } catch(Exception e) {
            throw new Exception("UNAUTHORIZED_ACCESS");
        }
    }

    public void authorizeGetTasksAssociatedWithPod(
        String idUser, 
        String idPod
    ) throws Exception {
        try {
            this._privatePodMember(idUser, idPod);
        } catch(Exception e) {
            throw new Exception("UNAUTHORIZED_ACCESS");
        }
    }

    public void authorizeGetNumberOfPointsInTasksCompletedOverTimeVisualizationAssociatedWithPod(
        String idUser, 
        String idPod
    ) throws Exception {
        try {
            this._privatePodMember(idUser, idPod);
        } catch(Exception e) {
            throw new Exception("UNAUTHORIZED_ACCESS");
        }
    }

    public void authorizeCreatePod(String idUser) throws Exception {
        return;
    }

    public void authorizeUpdatePod(
        String idUser, 
        String idPod
    ) throws Exception {
        try {
            this._podModerator(idUser, idPod);
        } catch(Exception e) {
            throw new Exception("UNAUTHORIZED_ACCESS");
        }
    }

    public void authorizeJoinPod(
        String idUser,
        String idPod
    ) throws Exception {
        try {
            boolean isPodPublic = this.podRepository.findById(UUID.fromString(idPod)).get().isPublic();
            if (!isPodPublic) {
                // only if invited to private pod
                this._isJoinPodInviteSent(idUser, idPod);
            }
        } catch(Exception e) {
            throw new Exception("UNAUTHORIZED_ACCESS");
        }

    }

    public void authorizeLeavePod(
        String idUser, 
        String idPod
    ) throws Exception {
        try {
            this._podMember(idUser, idPod);
        } catch(Exception e) {
            throw new Exception("UNAUTHORIZED_ACCESS");
        }
    }

    public void authorizeSendJoinPodInvite(
        String idUser, 
        String idPod
    ) throws Exception {
        try {
            this._podMember(idUser, idPod);
        } catch(Exception e) {
            throw new Exception("UNAUTHORIZED_ACCESS");
        }
    }

    public void authorizeAcceptJoinPodInvite(
        String idUser, 
        String idPod
    ) throws Exception {
        try {
            this._isJoinPodInviteSent(idUser, idPod);
        } catch(Exception e) {
            throw new Exception("UNAUTHORIZED_ACCESS");
        }
    }

    public void authorizeDeclineJoinPodInvite(
        String idUser, 
        String idPod
    ) throws Exception {
        try {
            this._isJoinPodInviteSent(idUser, idPod);
        } catch(Exception e) {
            throw new Exception("UNAUTHORIZED_ACCESS");
        }
    }

    public void authorizeSendBecomePodModeratorRequest(
        String idUser, 
        String idPod
    ) throws Exception {
        try {
            this._podMember(idUser, idPod);
        } catch(Exception e) {
            throw new Exception("UNAUTHORIZED_ACCESS");
        }
    }

    public void authorizeApproveBecomePodModeratorRequests(
        String idUser, 
        String idPod
    ) throws Exception {
        try {
            this._podModerator(idUser, idPod);
        } catch(Exception e) {
            throw new Exception("UNAUTHORIZED_ACCESS");
        }
    }

    public void authorizeRejectBecomePodModeratorRequests(
        String idUser, 
        String idPod
    ) throws Exception {
        try {
            this._podModerator(idUser, idPod);
        } catch(Exception e) {
            throw new Exception("UNAUTHORIZED_ACCESS");
        }
    }

    public void authorizeAddPodModerators(
        String idUser, 
        String idPod
    ) throws Exception {
        try {
            this._podModerator(idUser, idPod);
        } catch(Exception e) {
            throw new Exception("UNAUTHORIZED_ACCESS");
        }
    }

    public void authorizeGetStampPage(
        String idUser, 
        String idStamp
    ) throws Exception {
        try {
            if (!this.stampRepository.findById(UUID.fromString(idStamp)).isPresent()) {
                throw new Exception("UNAUTHORIZED_ACCESS");
            }
            this._memberOfAllPrivatePodsAssociatedWithStamp(idUser, idStamp);
        } catch(Exception e) {
            throw new Exception("UNAUTHORIZED_ACCESS");
        }

    }

    public void authorizeGetUserBubblesStampCollect(
        String idUser, 
        String idStamp
    ) throws Exception {
        try {
            this._memberOfAllPrivatePodsAssociatedWithStamp(idUser, idStamp);
        } catch(Exception e) {
            throw new Exception("UNAUTHORIZED_ACCESS");
        }
    }

    public void authorizeGetPodCardsAssociatedWithStamp(
        String idUser, 
        String idStamp
    ) throws Exception {
        try {
            this._memberOfAllPrivatePodsAssociatedWithStamp(idUser, idStamp);
        } catch(Exception e) {
            throw new Exception("UNAUTHORIZED_ACCESS");
        }
    }

    public void authorizeGetStampCardsDiscover(String idUser) throws Exception {
        return;
    }

    public void authorizeGetTasksAssociatedWithStamp(
        String idUser, 
        String idStamp
    ) throws Exception {
        try {
            this._memberOfAllPrivatePodsAssociatedWithStamp(idUser, idStamp);
        } catch(Exception e) {
            throw new Exception("UNAUTHORIZED_ACCESS");
        }
    }

    public void authorizeGetNumberOfPointsInTasksCompletedOverTimeVisualizationAssociatedWithStamp(
        String idUser, 
        String idStamp
    ) throws Exception {
        try {
            this._memberOfAllPrivatePodsAssociatedWithStamp(idUser, idStamp);
        } catch(Exception e) {
            throw new Exception("UNAUTHORIZED_ACCESS");
        }
    }

    public void authorizeCreateStamp(String idUser) throws Exception {
        return;
    }

    public void authorizeUpdateStamp(
        String idUser, 
        String idStamp
    ) throws Exception {
        try {
            StampEntity stampEntity = this.stampRepository.findById(UUID.fromString(idStamp)).get();
            if (!stampEntity.getIdUserCreate().toString().equals(idUser)) {
                throw new Exception("UNAUTHORIZED_ACCESS");
            }
        } catch(Exception e) {
            throw new Exception("UNAUTHORIZED_ACCESS");
        }

    }

    public void authorizeCollectStamp(
        String idUser, 
        String idStamp
    ) throws Exception {
        try {
            this._memberOfAllPrivatePodsAssociatedWithStamp(idUser, idStamp);
        } catch(Exception e) {
            throw new Exception("UNAUTHORIZED_ACCESS");
        }
    }

    public void authorizeGetTasksPersonal(String idUser) throws Exception {
        return;
    }

    public void authorizeGetUserBubblesTaskComplete(
        String idUser, 
        String idTask
    ) throws Exception {
        try {
            UUID idPod = this.taskRepository.findById(UUID.fromString(idTask)).get().getIdPod();
            if (idPod != null) {
                this._privatePodMember(idUser, idPod.toString());
            }
        } catch(Exception e) {
            throw new Exception("UNAUTHORIZED_ACCESS");
        }

    }

    public void authorizeGetNumberOfPointsInTasksCompletedOverTimeVisualizationPersonal(String idUser) throws Exception {
        return;
    }

    public void authorizeUpdateTask(
        String idUser, 
        String idTask,
        JsonNode rb
    ) throws Exception {
        try {
            UUID idPod = this.taskRepository.findById(UUID.fromString(idTask)).get().getIdPod();
            if (idPod != null) {
                if (rb.has("name") ||
                    rb.has("description") ||
                    rb.has("imageAsBase64String") ||
                    rb.has("numberOfPoints") ||
                    rb.has("datetimeTarget")
                ) {
                    this._podModerator(idUser, idPod.toString());
                } else if (
                    rb.has("isComplete") ||
                    rb.has("isStar") ||
                    rb.has("isPin") ||
                    rb.has("noteText") ||
                    rb.has("noteImageAsBase64String")
                ) {
                    this._podMember(idUser, idPod.toString());
                }
            } else {
                String idUserCreate = this.taskRepository.findById(UUID.fromString(idTask)).get().getIdUserCreate().toString();
                if (!idUser.equals(idUserCreate)) {
                    throw new Exception("UNAUTHORIZED_ACCESS");
                }
            }
        } catch(Exception e) {
            throw new Exception("UNAUTHORIZED_ACCESS");
        }
    }

    public void authorizeCreateTask(
        String idUser, 
        String idPod
    ) throws Exception {
        try {
            if (idPod != null) {
                this._podModerator(idUser, idPod);
            }
        } catch(Exception e) {
            throw new Exception("UNAUTHORIZED_ACCESS");
        }

    }

    public void authorizeDeleteTask(
        String idUser, 
        String idTask
    ) throws Exception {
        try {
            UUID idPod = this.taskRepository.findById(UUID.fromString(idTask)).get().getIdPod();
            if (idPod != null) {
                this._podModerator(idUser, idPod.toString());
            }
        } catch(Exception e) {
            throw new Exception("UNAUTHORIZED_ACCESS");
        }

    }

    public void authorizeUpdateTaskReaction(
        String idUser, 
        String idTask
    ) throws Exception {
        try {
            UUID idPod = this.taskRepository.findById(UUID.fromString(idTask)).get().getIdPod();
            if (idPod != null) {
                this._privatePodMember(idUser, idPod.toString());
            }
        } catch(Exception e) {
            throw new Exception("UNAUTHORIZED_ACCESS");
        }

    }

    public void authorizeUpdateTaskCommentReaction(
        String idUser, 
        String idTaskComment
    ) throws Exception {
        try {
            UUID idTask = this.taskUserTaskCommentRepository.findById(UUID.fromString(idTaskComment)).get().getIdTask();
            UUID idPod = this.taskRepository.findById(idTask).get().getIdPod();
            if (idPod != null) {
                this._privatePodMember(idUser, idPod.toString());
            }
        } catch(Exception e) {
            throw new Exception("UNAUTHORIZED_ACCESS");
        }

    }

    public void authorizeUpdateTaskCommentReplyReaction(
        String idUser, 
        String idTaskCommentReply
    ) throws Exception {
        try {
            UUID idTaskComment = this.taskUserTaskCommentReplyRepository.findById(UUID.fromString(idTaskCommentReply)).get().getIdTaskComment();
            UUID idTask = this.taskUserTaskCommentRepository.findById(idTaskComment).get().getIdTask();
            UUID idPod = this.taskRepository.findById(idTask).get().getIdPod();
            if (idPod != null) {
                this._privatePodMember(idUser, idPod.toString());
            }
        } catch(Exception e) {
            throw new Exception("UNAUTHORIZED_ACCESS");
        }

    }

    public void authorizeCreateTaskComment(
        String idUser, 
        String idTask
    ) throws Exception {
        try {
            UUID idPod = this.taskRepository.findById(UUID.fromString(idTask)).get().getIdPod();
            if (idPod != null) {
                this._privatePodMember(idUser, idPod.toString());
            }
        } catch(Exception e) {
            throw new Exception("UNAUTHORIZED_ACCESS");
        }

    }

    public void authorizeCreateTaskCommentReply(
        String idUser, 
        String idTaskComment
    ) throws Exception {
        try {
            UUID idTask = this.taskUserTaskCommentRepository.findById(UUID.fromString(idTaskComment)).get().getIdTask();
            UUID idPod = this.taskRepository.findById(idTask).get().getIdPod();
            if (idPod != null) {
                this._privatePodMember(idUser, idPod.toString());
            }
        } catch(Exception e) {
            throw new Exception("UNAUTHORIZED_ACCESS");
        }
    }

    public void authorizeGetTaskReactions(
        String idUser, 
        String idTask
    ) throws Exception {
        try {
            UUID idPod = this.taskRepository.findById(UUID.fromString(idTask)).get().getIdPod();
            if (idPod != null) {
                this._privatePodMember(idUser, idPod.toString());
            }
        } catch(Exception e) {
            throw new Exception("UNAUTHORIZED_ACCESS");
        }
    }

    public void authorizeGetTaskCommentReactions(
        String idUser, 
        String idTaskComment
    ) throws Exception {
        try {
            UUID idTask = this.taskUserTaskCommentRepository.findById(UUID.fromString(idTaskComment)).get().getIdTask();
            UUID idPod = this.taskRepository.findById(idTask).get().getIdPod();
            if (idPod != null) {
                this._privatePodMember(idUser, idPod.toString());
            }
        } catch(Exception e) {
            throw new Exception("UNAUTHORIZED_ACCESS");
        }
    }

    public void authorizeGetTaskCommentReplyReactions(
        String idUser, 
        String idTaskCommentReply
    ) throws Exception {
        try {
            UUID idTaskComment = this.taskUserTaskCommentReplyRepository.findById(UUID.fromString(idTaskCommentReply)).get().getIdTaskComment();
            UUID idTask = this.taskUserTaskCommentRepository.findById(idTaskComment).get().getIdTask();
            UUID idPod = this.taskRepository.findById(idTask).get().getIdPod();
            if (idPod != null) {
                this._privatePodMember(idUser, idPod.toString());
            }
        } catch(Exception e) {
            throw new Exception("UNAUTHORIZED_ACCESS");
        }

    }

    public void authorizeGetTaskReactionsSample(
        String idUser, 
        String idTask
    ) throws Exception {
        try {
            UUID idPod = this.taskRepository.findById(UUID.fromString(idTask)).get().getIdPod();
            if (idPod != null) {
                this._privatePodMember(idUser, idPod.toString());
            }
        } catch(Exception e) {
            throw new Exception("UNAUTHORIZED_ACCESS");
        }
    }

    public void authorizeGetTaskCommentReactionsSample(
        String idUser, 
        String idTaskComment
    ) throws Exception {
        try {
            UUID idTask = this.taskUserTaskCommentRepository.findById(UUID.fromString(idTaskComment)).get().getIdTask();
            UUID idPod = this.taskRepository.findById(idTask).get().getIdPod();
            if (idPod != null) {
                this._privatePodMember(idUser, idPod.toString());
            }
        } catch(Exception e) {
            throw new Exception("UNAUTHORIZED_ACCESS");
        }

    }

    public void authorizeGetTaskCommentReplyReactionsSample(
        String idUser, 
        String idTaskCommentReply
    ) throws Exception {
        try {
            UUID idTaskComment = this.taskUserTaskCommentReplyRepository.findById(UUID.fromString(idTaskCommentReply)).get().getIdTaskComment();
            UUID idTask = this.taskUserTaskCommentRepository.findById(idTaskComment).get().getIdTask();
            UUID idPod = this.taskRepository.findById(idTask).get().getIdPod();
            if (idPod != null) {
                this._privatePodMember(idUser, idPod.toString());
            }
        } catch(Exception e) {
            throw new Exception("UNAUTHORIZED_ACCESS");
        }
    }

    public void authorizeGetTaskComments(
        String idUser, 
        String idTask
    ) throws Exception {
        try {
            UUID idPod = this.taskRepository.findById(UUID.fromString(idTask)).get().getIdPod();
            if (idPod != null) {
                this._privatePodMember(idUser, idPod.toString());
            }
        } catch(Exception e) {
            throw new Exception("UNAUTHORIZED_ACCESS");
        }
    }

    public void authorizeGetTaskCommentReplies(
        String idUser, 
        String idTaskComment
    ) throws Exception {
        try {
            UUID idTask = this.taskUserTaskCommentRepository.findById(UUID.fromString(idTaskComment)).get().getIdTask();
            UUID idPod = this.taskRepository.findById(idTask).get().getIdPod();
            if (idPod != null) {
                this._privatePodMember(idUser, idPod.toString());
            }
        } catch(Exception e) {
            throw new Exception("UNAUTHORIZED_ACCESS");
        }
    }

    public void authorizeGetUserPage(String idUser) throws Exception {
        try {
            if (!this.userRepository.findById(UUID.fromString(idUser)).isPresent()) {
                throw new Exception("UNAUTHORIZED_ACCESS");
            }
        } catch(Exception e) {
            throw new Exception("UNAUTHORIZED_ACCESS");
        }
    }

    public void authorizeGetPersonalPage(String idUser) throws Exception {
        return;
    }

    public void authorizeUpdateUserPage(String idUser) throws Exception {
        return;
    }

    public void authorizeGetUserBubblesFollowing(String idUser) throws Exception {
        return;  
    }

    public void authorizeGetUserBubblesFollower(String idUser) throws Exception {
        return;  
    }

    public void authorizeSendFollowUserRequest(String idUser) throws Exception {
        return;
    }

    public void authorizeUnfollowUser(String idUser, String idUserToBeUnfollowed) throws Exception {
        try {
            boolean isUserFollowingUser2 = this.userUserUser1FollowUser2Repository.findByIdUser1IdUser2(idUser, idUserToBeUnfollowed).size() > 0 &&
                    this.userUserUser1FollowUser2Repository.findByIdUser1IdUser2(idUser, idUserToBeUnfollowed).get(0).isFollowing();
            if (!isUserFollowingUser2) {
                throw new Exception("UNAUTHORIZED_ACCESS");
            }
        } catch(Exception e) {
            throw new Exception("UNAUTHORIZED_ACCESS");
        }
    }

    public void authorizeAcceptFollowUserRequests(
        String idUser, 
        List<String> idUsersWhoSentFollowRequests
    ) throws Exception {
        try {
            for (String idUserWhoSentFollowRequest : idUsersWhoSentFollowRequests) {
                boolean isUserSentFollowRequest = this.userUserUser1FollowUser2Repository.findByIdUser1IdUser2(idUserWhoSentFollowRequest, idUser).size() > 0 && 
                        this.userUserUser1FollowUser2Repository.findByIdUser1IdUser2(idUserWhoSentFollowRequest, idUser).get(0).isRequestSent();
                if (!isUserSentFollowRequest) {
                    throw new Exception("UNAUTHORIZED_ACCESS");
                }
            }
        } catch(Exception e) {
            throw new Exception("UNAUTHORIZED_ACCESS");
        }
    }

    public void authorizeDeclineFollowUserRequests(
        String idUser, 
        List<String> idUsersWhoSentFollowRequests
    ) throws Exception {
        try {
            for (String idUserWhoSentFollowRequest : idUsersWhoSentFollowRequests) {
                boolean isUserSentFollowRequest = this.userUserUser1FollowUser2Repository.findByIdUser1IdUser2(idUserWhoSentFollowRequest, idUser).size() > 0 &&
                        this.userUserUser1FollowUser2Repository.findByIdUser1IdUser2(idUserWhoSentFollowRequest, idUser).get(0).isRequestSent();
                if (!isUserSentFollowRequest) {
                    throw new Exception("UNAUTHORIZED_ACCESS");
                }
            }
        } catch(Exception e) {
            throw new Exception("UNAUTHORIZED_ACCESS");
        }
    }

    public void authorizeGetUserBubblesPendingFollowUserRequest(String idUser) throws Exception {
        return;
    }

    public void authorizeGetPodCardsAssociatedWithUser(String idUser) throws Exception {
        return;
    }

    public void authorizeGetPinnedTasksAssociatedWithUser(String idUser) throws Exception {
        return;
    }

    public void authorizeGetStampCardsAssociatedWithUser(String idUser) throws Exception {
        return;
    }

    public void authorizeGetNotificationsUnseenCount(String idUser) throws Exception {
        return;
    }

    public void authorizeGetNotifications(String idUser) throws Exception {
        return;
    }

    public void authorizeMarkAllNotificationsAsSeen(String idUser) throws Exception {
        return;
    }

    public void authorizeDismissNotification(String idUser) throws Exception {
        return;
    }

    public void authorizeGetAccountSettingsPage(String idUser) throws Exception {
        return;
    }

    public void authorizeUpdateAccountSettingsPage(String idUser) throws Exception {
        return;
    }

    public void authorizeDeleteAccount(String idUser) throws Exception {
        return;
    }

    public void authorizeChangePassword(String idUser) throws Exception {
        return;
    }

    public void _privatePodMember(String idUser, String idPod) throws Exception {
        boolean isPodPublic = this.podRepository.findById(UUID.fromString(idPod)).get().isPublic();
        if (isPodPublic) {
            return;
        } else {
            boolean isUserMemberOfPod = this.podUserPodHasUserRepository.findByIdPodIdUser(idPod, idUser).size() > 0 && 
                    this.podUserPodHasUserRepository.findByIdPodIdUser(idPod, idUser).get(0).isMember();
            if (!isUserMemberOfPod) {
                throw new Exception("UNAUTHORIZED_ACCESS");
            }
        }
    }

    public void _podMember(String idUser, String idPod) throws Exception {
        boolean isUserMemberOfPod = this.podUserPodHasUserRepository.findByIdPodIdUser(idPod, idUser).size() > 0 && 
                this.podUserPodHasUserRepository.findByIdPodIdUser(idPod, idUser).get(0).isMember();
        if (!isUserMemberOfPod) {
            throw new Exception("UNAUTHORIZED_ACCESS");
        }
    }

    public void _podModerator(String idUser, String idPod) throws Exception {
        boolean isUserModeratorOfPod = this.podUserPodHasUserRepository.findByIdPodIdUser(idPod, idUser).size() > 0 && 
                this.podUserPodHasUserRepository.findByIdPodIdUser(idPod, idUser).get(0).isModerator();
        if (!isUserModeratorOfPod) {
            throw new Exception("UNAUTHORIZED_ACCESS");
        }
    }

    public void _isJoinPodInviteSent(String idUser, String idPod) throws Exception {
        List<PodUserPodHasUserEntity> podUserPodHasUserEntityList = this.podUserPodHasUserRepository
            .findByIdPodIdUser(idPod, idUser);
        boolean isJoinPodInviteAlreadySent = podUserPodHasUserEntityList.size() > 0 &&
            podUserPodHasUserEntityList.get(0).isJoinPodInviteSent();
        if (!isJoinPodInviteAlreadySent) {
            throw new Exception("UNAUTHORIZED_ACCESS");
        }
    }

    public void _memberOfAllPrivatePodsAssociatedWithStamp(String idUser, String idStamp) throws Exception {
        List<StampTaskStampHasTaskEntity> stampTaskStampHasTaskEntityList = this.stampTaskStampHasTaskRepository.findByIdStamp(idStamp);
        Set<UUID> idTasks = new HashSet<>();
        for (int i = 0; i < stampTaskStampHasTaskEntityList.size(); i++) {
            idTasks.add(stampTaskStampHasTaskEntityList.get(i).getIdTask());
        }
        Set<String> idPods = new HashSet<>();
        for (UUID idTask : idTasks) {
            UUID idPod = this.taskRepository.findById(idTask).get().getIdPod();
            if (idPod != null) {
                idPods.add(idPod.toString());
            }
        }
        for (String idPod : idPods) {
            this._privatePodMember(idUser, idPod);
        }
    }

}