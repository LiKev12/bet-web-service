package com.bet.betwebservice.controller;

import com.bet.betwebservice.model.*;
import com.bet.betwebservice.service.PodService;
import com.bet.betwebservice.service.StampService;
import com.bet.betwebservice.service.TaskService;
import com.bet.betwebservice.service.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("http://localhost:3000")
@RestController
@RequestMapping("api/user")
public class UserController {

    private UserService userService;
    private PodService podService;
    private StampService stampService;
    private TaskService taskService;

    @Autowired
    public UserController(UserService userService, PodService podService, StampService stampService, TaskService taskService) {
        this.userService = userService;
        this.podService = podService;
        this.stampService = stampService;
        this.taskService = taskService;
    }

    @GetMapping("/read/users/{idUser}/userPage")
    public UserPageModel getUserPage(
        @PathVariable("idUser") String idUser,
        @RequestParam(name="idUser") String idViewingUser
    ) throws Exception {
        return this.userService.getUserPage(idUser, idViewingUser);
    }

    @GetMapping("/read/personal/personalPage")
    public PersonalPageModel getPersonalPage(
        // @RequestParam String idUser
    ) throws Exception {
        // return this.userService.getPersonalPage(idUser);
        return this.userService.getPersonalPage();
    }

    @PostMapping("/update/userPage")
    public UserPageModel updateUserPage(
            @RequestParam String idUser,
            @RequestBody JsonNode requestModel
    ) throws Exception {
        return this.userService.updateUserPage(idUser, requestModel);
    }

    @GetMapping("/read/users/{idUser}/userBubblesFollowing")
    public List<UserBubbleModel> getUserBubblesFollowing(
            @PathVariable("idUser") String idUserOfPage,
            @RequestParam(name="idUser") String idViewingUser
    ) {
        return this.userService.getUserBubblesFollowing(
            idUserOfPage, idViewingUser
        );
    }

    @GetMapping("/read/users/{idUser}/userBubblesFollower")
    public List<UserBubbleModel> getUserBubblesFollower(
            @PathVariable("idUser") String idUserOfPage,
            @RequestParam(name="idUser") String idViewingUser
    ) {
        return this.userService.getUserBubblesFollower(
            idUserOfPage, idViewingUser
        );
    }

    @PostMapping("/update/sendFollowUserRequest")
    public void sendFollowUserRequest(
            @RequestParam String idUser,
            @RequestBody JsonNode requestModel
    ) throws Exception {
        this.userService.sendFollowUserRequest(idUser, requestModel);
    }

    @PostMapping("/update/acceptFollowUserRequests")
    public List<UserBubbleModel> acceptFollowUserRequests(
            @RequestParam String idUser,
            @RequestBody JsonNode requestModel
    ) throws Exception {
        return this.userService.acceptFollowUserRequests(idUser, requestModel);
    }

    @PostMapping("/update/declineFollowUserRequests")
    public List<UserBubbleModel> declineFollowUserRequests(
            @RequestParam String idUser,
            @RequestBody JsonNode requestModel
    ) throws Exception {
        return this.userService.declineFollowUserRequests(idUser, requestModel);
    }
    
    @GetMapping("/read/userBubblesPendingFollowUserRequest")
    public List<UserBubbleModel> getUserBubblesPendingFollowUserRequest(
            @RequestParam String idUser
    ) {
        return this.userService.getUserBubblesPendingFollowUserRequest(idUser);
    }

    @GetMapping("/read/users/{idUser}/pods")
    public Page<PodCardModel> getPodCardsAssociatedWithUser(
            @PathVariable("idUser") String idUserProfile,
            @RequestParam String idUser,
            @RequestParam(defaultValue = "") String filterNameOrDescription,
            @RequestParam(name = "filterIsPublic", defaultValue = "true") boolean filterIsPublic,
            @RequestParam(name = "filterIsNotPublic", defaultValue = "true") boolean filterIsNotPublic,
            @RequestParam(name = "filterIsMember", defaultValue = "true") boolean filterIsMemberIndividual,
            @RequestParam(name = "filterIsNotMember", defaultValue = "true") boolean filterIsNotMemberIndividual,
            @RequestParam(name = "filterIsModerator", defaultValue = "true") boolean filterIsModeratorIndividual,
            @RequestParam(name = "filterIsNotModerator", defaultValue = "true") boolean filterIsNotModeratorIndividual,
            Pageable pageable
    ) {
        return this.podService.getPodCardsAssociatedWithUser(
            idUserProfile,
            idUser,
            filterNameOrDescription,
            filterIsPublic,
            filterIsNotPublic,
            filterIsMemberIndividual,
            filterIsNotMemberIndividual,
            filterIsModeratorIndividual,
            filterIsNotModeratorIndividual,
            pageable
        );
    }

    @GetMapping("/read/users/{idUser}/tasksPinned")
    public Page<TaskModel> getPinnedTasksAssociatedWithUser(
            @PathVariable("idUser") String idUserProfile,
            @RequestParam String idUser,
            @RequestParam(defaultValue = "") String filterNameOrDescription,
            @RequestParam(name = "filterIsComplete", defaultValue = "true") boolean filterIsCompleteIndividual,
            @RequestParam(name = "filterIsNotComplete", defaultValue = "true") boolean filterIsNotCompleteIndividual,
            @RequestParam(name = "filterIsStar", defaultValue = "true") boolean filterIsStarIndividual,
            @RequestParam(name = "filterIsNotStar", defaultValue = "true") boolean filterIsNotStarIndividual,
            @RequestParam(name = "filterIsPin", defaultValue = "true") boolean filterIsPinIndividual,
            @RequestParam(name = "filterIsNotPin", defaultValue = "true") boolean filterIsNotPinIndividual,
            Pageable pageable
    ) {
        return this.taskService.getPinnedTasksAssociatedWithUser(
                idUserProfile,
                idUser,
                filterNameOrDescription,
                filterIsCompleteIndividual,
                filterIsNotCompleteIndividual,
                filterIsStarIndividual,
                filterIsNotStarIndividual,
                filterIsPinIndividual,
                filterIsNotPinIndividual,
                pageable
        );
    }

    @GetMapping("/read/users/{idUser}/stamps")
    public Page<StampCardModel> getStampCardsAssociatedWithUser(
            @PathVariable("idUser") String idUserProfile,
            @RequestParam String idUser,
            @RequestParam(defaultValue = "") String filterNameOrDescription,
            @RequestParam(name = "filterIsCollect", defaultValue = "true") boolean filterIsCollectIndividual,
            @RequestParam(name = "filterIsNotCollect", defaultValue = "true") boolean filterIsNotCollectIndividual,
            Pageable pageable
    ) {
        return this.stampService.getStampCardsAssociatedWithUser(
            idUserProfile,
            idUser,
            filterNameOrDescription,
            filterIsCollectIndividual,
            filterIsNotCollectIndividual,
            pageable
        );
    }

    @PostMapping("/read/notificationsCountUnseen")
    public int getNotificationsUnseenCount(
            @RequestParam String idUser
    ) {
        return this.userService.getNotificationsUnseenCount(idUser);
    }

    @PostMapping("/read/notifications")
    public List<NotificationModel> getNotifications(
            @RequestParam String idUser
    ) {
        return this.userService.getNotifications(idUser);
    }

    @PostMapping("/update/markAllNotificationsAsSeen")
    public void markAllNotificationsAsSeen(
            @RequestParam String idUser
    ) {
        this.userService.markAllNotificationsAsSeen(idUser);
    }

    @PostMapping("/update/dismissNotification")
    public void dismissNotification(
            @RequestParam String idUser,
            @RequestBody JsonNode requestModel
    ) throws Exception {
        this.userService.dismissNotification(idUser, requestModel);
    }
}
