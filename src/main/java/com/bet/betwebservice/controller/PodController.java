package com.bet.betwebservice.controller;

import com.bet.betwebservice.model.*;
import com.bet.betwebservice.service.PodService;
import com.bet.betwebservice.service.StampService;
import com.bet.betwebservice.service.TaskService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("http://localhost:3000")
@RestController
@RequestMapping("api/pod")
public class PodController {
    private PodService podService;
    private StampService stampService;
    private TaskService taskService;

    @Autowired
    public PodController(PodService podService, StampService stampService, TaskService taskService) {
        this.podService = podService;
        this.stampService = stampService;
        this.taskService = taskService;
    }

    @GetMapping("/read/pods/{idPod}/page")
    public PodPageModel getPodPage(
        @PathVariable("idPod") String idPod,
        @RequestParam String idUser
    ) throws Exception {
        return this.podService.getPodPage(idPod, idUser);
    }

    @GetMapping("/read/pods/{idPod}/userBubblesPodMember")
    public List<UserBubbleModel> getUserBubblesPodMember(
            @PathVariable("idPod") String idPod,
            @RequestParam String idUser
    ) {
        return this.podService.getUserBubblesPodMember(
            idPod,
                idUser
        );
    }

    @GetMapping("/read/pods/{idPod}/userBubblesPodModerator")
    public List<UserBubbleModel> getUserBubblesPodModerator(
            @PathVariable("idPod") String idPod,
            @RequestParam String idUser
    ) {
        return this.podService.getUserBubblesPodModerator(
                idPod,
                idUser
        );
    }

    @GetMapping("/read/pods/{idPod}/userBubblesInviteJoinPod")
    public List<UserBubbleModel> getUserBubblesInviteJoinPod(
            @PathVariable("idPod") String idPod,
            @RequestParam String idUser
    ) {
        return this.podService.getUserBubblesInviteJoinPod(
                idPod,
                idUser
        );
    }

    @GetMapping("/read/pods/{idPod}/userBubblesAddPodModerator")
    public List<UserBubbleModel> getUserBubblesAddPodModerator(
            @PathVariable("idPod") String idPod,
            @RequestParam String idUser
    ) {
        return this.podService.getUserBubblesAddPodModerator(
                idPod,
                idUser
        );
    }


    @GetMapping("/read/pods/{idPod}/userBubblesPendingBecomePodModeratorRequest")
    public List<UserBubbleModel> getUserBubblesPendingBecomePodModeratorRequest(
            @PathVariable("idPod") String idPod,
            @RequestParam String idUser
    ) {
        return this.podService.getUserBubblesPendingBecomePodModeratorRequest(
                idPod,
                idUser
        );
    }

    @GetMapping("/read/discover/pods")
    public Page<PodCardModel> getPodCardsDiscover(
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
        return this.podService.getPodCardsDiscover(
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

    @GetMapping("/read/pods/{idPod}/stamps")
    public Page<StampCardModel> getStampCardsAssociatedWithPod(
            @PathVariable("idPod") String idPod,
            @RequestParam String idUser,
            @RequestParam(defaultValue = "") String filterNameOrDescription,
            @RequestParam(name = "filterIsCollect", defaultValue = "true") boolean filterIsCollectIndividual,
            @RequestParam(name = "filterIsNotCollect", defaultValue = "true") boolean filterIsNotCollectIndividual,
            Pageable pageable
    ) {
        return this.stampService.getStampCardsAssociatedWithPod(
            idPod, 
            idUser,
            filterNameOrDescription,
            filterIsCollectIndividual,
            filterIsNotCollectIndividual,
            pageable
        );
    }

    @GetMapping("/read/pods/{idPod}/tasks")
    public Page<TaskModel> getTasksAssociatedWithPod(
            @PathVariable("idPod") String idPod,
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
        // TODO: validate request params
        String filterTextTrimmed = filterNameOrDescription.trim();
        return this.taskService.getTasksAssociatedWithPod(
                idPod,
                idUser,
                filterTextTrimmed,
                filterIsCompleteIndividual,
                filterIsNotCompleteIndividual,
                filterIsStarIndividual,
                filterIsNotStarIndividual,
                filterIsPinIndividual,
                filterIsNotPinIndividual,
                pageable
        );
    }

    @GetMapping("/read/pods/{idPod}/numberOfPointsInTasksCompletedOverTimeVisualization")
    public NumberOfPointsInTasksCompletedOverTimeVisualizationModel getNumberOfPointsInTasksCompletedOverTimeVisualizationAssociatedWithPod(
        @PathVariable("idPod") String idPod,
        @RequestParam String idUser
    ) {
        return this.taskService.getNumberOfPointsInTasksCompletedOverTimeVisualizationAssociatedWithPod(idPod, idUser);
    }

    @PostMapping("/create/pod")
    public PodPageModel createPod(
            @RequestParam String idUser,
            @RequestBody JsonNode requestModel
    ) throws Exception {
        return this.podService.createPod(idUser, requestModel);
    }

    @PostMapping("/update/pod")
    public PodPageModel updatePod(
            @RequestParam String idUser,
            @RequestBody JsonNode requestModel
    ) throws Exception {
        return this.podService.updatePod(idUser, requestModel);
    }

    @PostMapping("/update/joinPod")
    public void joinPod(
            @RequestParam String idUser,
            @RequestBody JsonNode requestModel
    ) throws Exception {
        this.podService.joinPod(idUser, requestModel);
    }

    @PostMapping("/update/leavePod")
    public void leavePod(
            @RequestParam String idUser,
            @RequestBody JsonNode requestModel
    ) throws Exception {
        this.podService.leavePod(idUser, requestModel);
    }

    @PostMapping("/update/sendJoinPodInvite")
    public List<UserBubbleModel> sendJoinPodInvite(
            @RequestParam String idUser,
            @RequestBody JsonNode requestModel
    ) throws Exception {
        return this.podService.sendJoinPodInvite(idUser, requestModel);
    }

    @PostMapping("/update/acceptJoinPodInvite")
    public void acceptJoinPodInvite(
            @RequestParam String idUser,
            @RequestBody JsonNode requestModel
    ) throws Exception {
        this.podService.acceptJoinPodInvite(idUser, requestModel);
    }

    @PostMapping("/update/declineJoinPodInvite")
    public void declineJoinPodInvite(
            @RequestParam String idUser,
            @RequestBody JsonNode requestModel
    ) throws Exception {
        this.podService.declineJoinPodInvite(idUser, requestModel);
    }

    @PostMapping("/update/sendBecomePodModeratorRequest")
    public void sendBecomePodModeratorRequest(
            @RequestParam String idUser,
            @RequestBody JsonNode requestModel
    ) throws Exception {
        this.podService.sendBecomePodModeratorRequest(idUser, requestModel);
    }

    @PostMapping("/update/approveBecomePodModeratorRequests")
    public List<UserBubbleModel> approveBecomePodModeratorRequests(
            @RequestParam String idUser,
            @RequestBody JsonNode requestModel
    ) throws Exception {
        return this.podService.approveBecomePodModeratorRequests(idUser, requestModel);
    }

    @PostMapping("/update/rejectBecomePodModeratorRequests")
    public List<UserBubbleModel> rejectBecomePodModeratorRequests(
            @RequestParam String idUser,
            @RequestBody JsonNode requestModel
    ) throws Exception {
        return this.podService.rejectBecomePodModeratorRequests(idUser, requestModel);
    }

    @PostMapping("/update/addPodModerators")
    public List<UserBubbleModel> addPodModerators(
            @RequestParam String idUser,
            @RequestBody JsonNode requestModel
    ) throws Exception {
        return this.podService.addPodModerators(idUser, requestModel);
    }
}
