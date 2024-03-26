package com.bet.betwebservice.controller;

import com.bet.betwebservice.model.*;
import com.bet.betwebservice.service.AppService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*")
@RestController
@RequestMapping("api/app")
public class AppController {
    private AppService appService;
    private JwtDecoder jwtDecoder;


    @Autowired
    public AppController(AppService appService, JwtDecoder jwtDecoder) {
        this.appService = appService;
        this.jwtDecoder = jwtDecoder;
    }

    @PostMapping("/GetPodPage")
    public PodPageModel getPodPage(@RequestBody JsonNode rb) throws Exception {
        return this.appService.getPodPage(rb);
    }

    @PostMapping("/GetUserBubblesPodMember")
    public List<UserBubbleModel> getUserBubblesPodMember(@RequestBody JsonNode rb) throws Exception {
        return this.appService.getUserBubblesPodMember(rb);
    }

    @PostMapping("/GetUserBubblesPodModerator")
    public List<UserBubbleModel> getUserBubblesPodModerator(@RequestBody JsonNode rb) throws Exception {
        return this.appService.getUserBubblesPodModerator(rb);
    }

    @PostMapping("/GetUserBubblesInviteJoinPod")
    public List<UserBubbleModel> getUserBubblesInviteJoinPod(@RequestBody JsonNode rb) throws Exception {
        return this.appService.getUserBubblesInviteJoinPod(rb);
    }

    @PostMapping("/GetUserBubblesAddPodModerator")
    public List<UserBubbleModel> getUserBubblesAddPodModerator(@RequestBody JsonNode rb) throws Exception {
        return this.appService.getUserBubblesAddPodModerator(rb);
    }

    @PostMapping("/GetUserBubblesPendingBecomePodModeratorRequest")
    public List<UserBubbleModel> getUserBubblesPendingBecomePodModeratorRequest(@RequestBody JsonNode rb) throws Exception {
        return this.appService.getUserBubblesPendingBecomePodModeratorRequest(rb);
    }

    @PostMapping("/GetPodCardsDiscover")
    public PodCardsPaginatedModel PodCardsDiscover(@RequestBody JsonNode rb) throws Exception {
        return this.appService.getPodCardsDiscover(rb);
    }

    @PostMapping("/GetStampCardsAssociatedWithPod")
    public StampCardsPaginatedModel getStampCardsAssociatedWithPod(@RequestBody JsonNode rb) throws Exception {
        return this.appService.getStampCardsAssociatedWithPod(rb);
    }

    @PostMapping("/GetTasksAssociatedWithPod")
    public List<TaskModel> getTasksAssociatedWithPod(@RequestBody JsonNode rb) throws Exception {
        return this.appService.getTasksAssociatedWithPod(rb);
    }

    @PostMapping("/GetNumberOfPointsInTasksCompletedOverTimeVisualizationAssociatedWithPod")
    public NumberOfPointsInTasksCompletedOverTimeVisualizationModel getNumberOfPointsInTasksCompletedOverTimeVisualizationAssociatedWithPod(@RequestBody JsonNode rb) throws Exception {
        return this.appService.getNumberOfPointsInTasksCompletedOverTimeVisualizationAssociatedWithPod(rb);
    }

    @PostMapping("/CreatePod")
    public PodPageModel createPod(@RequestBody JsonNode rb) throws Exception {
        return this.appService.createPod(rb);
    }

    @PostMapping("/UpdatePod")
    public PodPageModel updatePod(@RequestBody JsonNode rb) throws Exception {
        return this.appService.updatePod(rb);
    }

    @PostMapping("/JoinPod")
    public void joinPod(@RequestBody JsonNode rb) throws Exception {
        this.appService.joinPod(rb);
    }

    @PostMapping("/LeavePod")
    public void leavePod(@RequestBody JsonNode rb) throws Exception {
        this.appService.leavePod(rb);
    }

    @PostMapping("/SendJoinPodInvite")
    public List<UserBubbleModel> sendJoinPodInvite(@RequestBody JsonNode rb) throws Exception {
        return this.appService.sendJoinPodInvite(rb);
    }

    @PostMapping("/AcceptJoinPodInvite")
    public void acceptJoinPodInvite(@RequestBody JsonNode rb) throws Exception {
        this.appService.acceptJoinPodInvite(rb);
    }

    @PostMapping("/DeclineJoinPodInvite")
    public void declineJoinPodInvite(@RequestBody JsonNode rb) throws Exception {
        this.appService.declineJoinPodInvite(rb);
    }

    @PostMapping("/SendBecomePodModeratorRequest")
    public void sendBecomePodModeratorRequest(@RequestBody JsonNode rb) throws Exception {
        this.appService.sendBecomePodModeratorRequest(rb);
    }

    @PostMapping("/ApproveBecomePodModeratorRequests")
    public List<UserBubbleModel> approveBecomePodModeratorRequests(@RequestBody JsonNode rb) throws Exception {
        return this.appService.approveBecomePodModeratorRequests(rb);
    }

    @PostMapping("/RejectBecomePodModeratorRequests")
    public List<UserBubbleModel> rejectBecomePodModeratorRequests(@RequestBody JsonNode rb) throws Exception {
        return this.appService.rejectBecomePodModeratorRequests(rb);
    }

    @PostMapping("/AddPodModerators")
    public List<UserBubbleModel> addPodModerators(@RequestBody JsonNode rb) throws Exception {
        return this.appService.addPodModerators(rb);
    }

    @PostMapping("/GetStampPage")
    public StampPageModel getStampPage(@RequestBody JsonNode rb) throws Exception {
        return this.appService.getStampPage(rb);
    }

    @PostMapping("/GetUserBubblesStampCollect")
    public List<UserBubbleModel> getUserBubblesStampCollect(@RequestBody JsonNode rb) throws Exception {
        return this.appService.getUserBubblesStampCollect(rb);
    }

    @PostMapping("/GetPodCardsAssociatedWithStamp")
    public PodCardsPaginatedModel PodCardsAssociatedWithStamp(@RequestBody JsonNode rb) throws Exception {
        return this.appService.getPodCardsAssociatedWithStamp(rb);
    }

    @PostMapping("/GetStampCardsDiscover")
    public StampCardsPaginatedModel getStampCardsDiscover(@RequestBody JsonNode rb) throws Exception {
        return this.appService.getStampCardsDiscover(rb);
    }

    @PostMapping("/GetTasksAssociatedWithStamp")
    public List<TaskModel> getTasksAssociatedWithStamp(@RequestBody JsonNode rb) throws Exception {
        return this.appService.getTasksAssociatedWithStamp(rb);
    }

    @PostMapping("/GetNumberOfPointsInTasksCompletedOverTimeVisualizationAssociatedWithStamp")
    public NumberOfPointsInTasksCompletedOverTimeVisualizationModel getNumberOfPointsInTasksCompletedOverTimeVisualizationAssociatedWithStamp(@RequestBody JsonNode rb) throws Exception {
        return this.appService.getNumberOfPointsInTasksCompletedOverTimeVisualizationAssociatedWithStamp(rb);
    }

    @PostMapping("/CreateStamp")
    public StampPageModel createStamp(@RequestBody JsonNode rb) throws Exception {
        return this.appService.createStamp(rb);
    }

    @PostMapping("/UpdateStamp")
    public StampPageModel updateStamp(@RequestBody JsonNode rb) throws Exception {
        return this.appService.updateStamp(rb);
    }

    @PostMapping("/CollectStamp")
    public void collectStamp(@RequestBody JsonNode rb) throws Exception {
        this.appService.collectStamp(rb);
    }

    @PostMapping("/GetTasksPersonal")
    public List<TaskModel> getTasksPersonal(@RequestBody JsonNode rb) throws Exception {
        return this.appService.getTasksPersonal(rb);
    }

    @PostMapping("/GetUserBubblesTaskComplete")
    public List<UserBubbleModel> getUserBubblesTaskComplete(@RequestBody JsonNode rb) throws Exception {
        return this.appService.getUserBubblesTaskComplete(rb);
    }

    @PostMapping("/GetNumberOfPointsInTasksCompletedOverTimeVisualizationPersonal")
    public NumberOfPointsInTasksCompletedOverTimeVisualizationModel getNumberOfPointsInTasksCompletedOverTimeVisualizationPersonal() throws Exception {
        return this.appService.getNumberOfPointsInTasksCompletedOverTimeVisualizationPersonal();
    }

    @PostMapping("/UpdateTask")
    public TaskModel updateTask(@RequestBody JsonNode rb) throws Exception {
        return this.appService.updateTask(rb);
    }

    @PostMapping("/CreateTask")
    public void createTask(@RequestBody JsonNode rb) throws Exception {
        this.appService.createTask(rb);
    }

    @PostMapping("/DeleteTask")
    public void deleteTask(@RequestBody JsonNode rb) throws Exception {
        this.appService.deleteTask(rb);
    }

    @PostMapping("/UpdateTaskReaction")
    public void updateTaskReaction(@RequestBody JsonNode rb) throws Exception {
        this.appService.updateTaskReaction(rb);
    }

    @PostMapping("/UpdateTaskCommentReaction")
    public void updateTaskCommentReaction(@RequestBody JsonNode rb) throws Exception {
        this.appService.updateTaskCommentReaction(rb);
    }

    @PostMapping("/UpdateTaskCommentReplyReaction")
    public void updateTaskCommentReplyReaction(@RequestBody JsonNode rb) throws Exception {
        this.appService.updateTaskCommentReplyReaction(rb);
    }

    @PostMapping("/CreateTaskComment")
    public void createTaskComment(@RequestBody JsonNode rb) throws Exception {
        this.appService.createTaskComment(rb);
    }

    @PostMapping("/CreateTaskCommentReply")
    public void createTaskCommentReply(@RequestBody JsonNode rb) throws Exception {
        this.appService.createTaskCommentReply(rb);
    }

    @PostMapping("/GetTaskReactions")
    public ReactionsModel getTaskReactions(@RequestBody JsonNode rb) throws Exception {
        return this.appService.getTaskReactions(rb);
    }

    @PostMapping("/GetTaskCommentReactions")
    public ReactionsModel getTaskCommentReactions(@RequestBody JsonNode rb) throws Exception {
        return this.appService.getTaskCommentReactions(rb);
    }

    @PostMapping("/GetTaskCommentReplyReactions")
    public ReactionsModel getTaskCommentReplyReactions(@RequestBody JsonNode rb) throws Exception {
        return this.appService.getTaskCommentReplyReactions(rb);
    }

    @PostMapping("/GetTaskReactionsSample")
    public ReactionsModel getTaskReactionsSample(@RequestBody JsonNode rb) throws Exception {
        return this.appService.getTaskReactionsSample(rb);
    }

    @PostMapping("/GetTaskCommentReactionsSample")
    public ReactionsModel getTaskCommentReactionsSample(@RequestBody JsonNode rb) throws Exception {
        return this.appService.getTaskCommentReactionsSample(rb);
    }

    @PostMapping("/GetTaskCommentReplyReactionsSample")
    public ReactionsModel getTaskCommentReplyReactionsSample(@RequestBody JsonNode rb) throws Exception {
        return this.appService.getTaskCommentReplyReactionsSample(rb);
    }

    @PostMapping("/GetTaskComments")
    public List<TaskCommentModel> getTaskComments(@RequestBody JsonNode rb) throws Exception {
        return this.appService.getTaskComments(rb);
    }

    @PostMapping("/GetTaskCommentReplies")
    public List<TaskCommentReplyModel> getTaskCommentReplies(@RequestBody JsonNode rb) throws Exception {
        return this.appService.getTaskCommentReplies(rb);
    }

    @PostMapping("/GetUserPage")
    public UserPageModel getUserPage(@RequestBody JsonNode rb) throws Exception {
        return this.appService.getUserPage(rb);
    }
    
    @PostMapping("/GetPersonalPage")
    public PersonalPageModel getPersonalPage() throws Exception {
        return this.appService.getPersonalPage();
    }
    
    @PostMapping("/UpdateUserPage")
    public UserPageModel updateUserPage(@RequestBody JsonNode rb) throws Exception {
        return this.appService.updateUserPage(rb);
    }

    @PostMapping("/GetUserBubblesFollowing")
    public List<UserBubbleModel> getUserBubblesFollowing(@RequestBody JsonNode rb) throws Exception {
        return this.appService.getUserBubblesFollowing(rb);
    }

    @PostMapping("/GetUserBubblesFollower")
    public List<UserBubbleModel> getUserBubblesFollower(@RequestBody JsonNode rb) throws Exception {
        return this.appService.getUserBubblesFollower(rb);
    }

    @PostMapping("/SendFollowUserRequest")
    public void sendFollowUserRequest(@RequestBody JsonNode rb) throws Exception {
        this.appService.sendFollowUserRequest(rb);
    }

    @PostMapping("/UnfollowUser")
    public void unfollowUser(@RequestBody JsonNode rb) throws Exception {
        this.appService.unfollowUser(rb);
    }

    @PostMapping("/AcceptFollowUserRequests")
    public List<UserBubbleModel> acceptFollowUserRequests(@RequestBody JsonNode rb) throws Exception {
        return this.appService.acceptFollowUserRequests(rb);
    }

    @PostMapping("/DeclineFollowUserRequests")
    public List<UserBubbleModel> declineFollowUserRequests(@RequestBody JsonNode rb) throws Exception {
        return this.appService.declineFollowUserRequests(rb);
    }

    @PostMapping("/GetUserBubblesPendingFollowUserRequest")
    public List<UserBubbleModel> getUserBubblesPendingFollowUserRequest() throws Exception {
        return this.appService.getUserBubblesPendingFollowUserRequest();
    }

    @PostMapping("/GetPodCardsAssociatedWithUser")
    public PodCardsPaginatedModel PodCardsAssociatedWithUser(@RequestBody JsonNode rb) throws Exception {
        return this.appService.getPodCardsAssociatedWithUser(rb);
    }

    @PostMapping("/GetPinnedTasksAssociatedWithUser")
    public List<TaskModel> getPinnedTasksAssociatedWithUser(@RequestBody JsonNode rb) throws Exception {
        return this.appService.getPinnedTasksAssociatedWithUser(rb);
    }

    @PostMapping("/GetStampCardsAssociatedWithUser")
    public StampCardsPaginatedModel getStampCardsAssociatedWithUser(@RequestBody JsonNode rb) throws Exception {
        return this.appService.getStampCardsAssociatedWithUser(rb);
    }

    @PostMapping("/GetNotificationsUnseenCount")
    public int getNotificationsUnseenCount() throws Exception {
        return this.appService.getNotificationsUnseenCount();
    }

    @PostMapping("/GetNotifications")
    public List<NotificationModel> getNotifications() throws Exception {
        return this.appService.getNotifications();
    }

    @PostMapping("/MarkAllNotificationsAsSeen")
    public void markAllNotificationsAsSeen() throws Exception {
        this.appService.markAllNotificationsAsSeen();
    }

    @PostMapping("/DismissNotification")
    public void dismissNotification(@RequestBody JsonNode rb) throws Exception {
        this.appService.dismissNotification(rb);
    }

    @PostMapping("/GetAccountSettingsPage")
    public AccountSettingsPageModel getAccountSettingsPage() throws Exception {
        return this.appService.getAccountSettingsPage();
    }

    @PostMapping("/UpdateAccountSettingsPage")
    public AccountSettingsPageModel updateAccountSettingsPage(@RequestBody JsonNode rb) throws Exception {
        return this.appService.updateAccountSettingsPage(rb);
    }

    @PostMapping("/DeleteAccount")
    public void deleteAccount(@RequestBody JsonNode rb) throws Exception {
        this.appService.deleteAccount(rb);
    }

    @PostMapping("/ChangePassword")
    public void changePassword(@RequestBody JsonNode rb) throws Exception {
        this.appService.changePassword(rb);
    }
}
