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
@RequestMapping("api/stamp")
public class StampController {

    private PodService podService;
    private StampService stampService;
    private TaskService taskService;

    @Autowired
    public StampController(PodService podService, StampService stampService, TaskService taskService) {
        this.podService = podService;
        this.stampService = stampService;
        this.taskService = taskService;
    }

    @GetMapping("/read/stamps/{idStamp}/page")
    public StampPageModel getStampPage(
        @PathVariable("idStamp") String idStamp,
        @RequestParam String idUser
    ) throws Exception {
        return this.stampService.getStampPage(idStamp, idUser);
    }

    @GetMapping("/read/stamps/{idStamp}/userBubblesStampCollect")
    public List<UserBubbleModel> getUserBubblesStampCollect(
            @PathVariable("idStamp") String idStamp,
            @RequestParam String idUser
    ) {
        return this.stampService.getUserBubblesStampCollect(
            idStamp,
            idUser
        );
    }

    @GetMapping("/read/stamps/{idStamp}/pods")
    public Page<PodCardModel> getPodCardsAssociatedWithStamp(
            @PathVariable("idStamp") String idStamp,
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
        return this.podService.getPodCardsAssociatedWithStamp(
            idStamp,
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

    @GetMapping(path="/read/discover/stamps")
    public Page<StampCardModel> getStampCardsDiscover(
            @RequestParam String idUser,
            @RequestParam(defaultValue = "") String filterNameOrDescription,
            @RequestParam(name = "filterIsPublic", defaultValue = "true") boolean filterIsPublicShared,
            @RequestParam(name = "filterIsNotPublic", defaultValue = "true") boolean filterIsNotPublicShared,
            @RequestParam(name = "filterIsCollect", defaultValue = "true") boolean filterIsCollectIndividual,
            @RequestParam(name = "filterIsNotCollect", defaultValue = "true") boolean filterIsNotCollectIndividual,
            Pageable pageable
    ) {
        return this.stampService.getStampCardsDiscover(
            idUser,
            filterNameOrDescription,
            filterIsPublicShared,
            filterIsNotPublicShared,
            filterIsCollectIndividual,
            filterIsNotCollectIndividual,
            pageable
        );
    }

    @GetMapping("/read/stamps/{idStamp}/tasks")
    public Page<TaskModel> getTasksAssociatedWithStamp(
            @PathVariable("idStamp") String idStamp,
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
        return this.taskService.getTasksAssociatedWithStamp(
                idStamp,
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

    @GetMapping("/read/stamps/{idStamp}/numberOfPointsInTasksCompletedOverTimeVisualization")
    public NumberOfPointsInTasksCompletedOverTimeVisualizationModel getNumberOfPointsInTasksCompletedOverTimeVisualizationAssociatedWithStamp(
        @PathVariable("idStamp") String idStamp,
        @RequestParam String idUser
    ) {
        return this.taskService.getNumberOfPointsInTasksCompletedOverTimeVisualizationAssociatedWithStamp(idStamp, idUser);
    }

    @PostMapping("/create/stamp")
    public StampPageModel createStamp(
            @RequestParam String idUser,
            @RequestBody JsonNode requestModel
    ) throws Exception {
        return this.stampService.createStamp(idUser, requestModel);
    }

    @PostMapping("/update/stamp")
    public StampPageModel updateStamp(
            @RequestParam String idUser,
            @RequestBody JsonNode requestModel
    ) throws Exception {
        return this.stampService.updateStamp(idUser, requestModel);
    }

    @PostMapping("/update/collectStamp")
    public void collectStamp(
            @RequestParam String idUser,
            @RequestBody JsonNode requestModel
    ) throws Exception {
        this.stampService.collectStamp(idUser, requestModel);
    }
}
