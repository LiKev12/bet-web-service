package com.bet.betwebservice.controller;

import com.bet.betwebservice.model.PodCardModel;
import com.bet.betwebservice.model.StampCardModel;
import com.bet.betwebservice.model.TaskModel;
import com.bet.betwebservice.service.PodService;
import com.bet.betwebservice.service.StampService;
import com.bet.betwebservice.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("http://localhost:3000")
@RestController
@RequestMapping("/api/pod")
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

    @GetMapping("/{idPod}/task")
    public List<TaskModel> getTasksInPod(
            @PathVariable("idPod") String idPod,
            @RequestParam String idUser,
            @RequestParam(defaultValue = "") String filterText,
            @RequestParam(defaultValue = "true") boolean filterIsComplete,
            @RequestParam(defaultValue = "true") boolean filterIsNotComplete,
            @RequestParam(defaultValue = "true") boolean filterIsStar,
            @RequestParam(defaultValue = "true") boolean filterIsNotStar,
            @RequestParam(defaultValue = "true") boolean filterIsPin,
            @RequestParam(defaultValue = "true") boolean filterIsNotPin
    ) {
        // TODO: validate request params
        String filterTextTrimmed = filterText.trim();
        return this.taskService.getTasksInPod(
                idPod,
                idUser,
                filterTextTrimmed,
                filterIsComplete,
                filterIsNotComplete,
                filterIsStar,
                filterIsNotStar,
                filterIsPin,
                filterIsNotPin
        );
    }

    @GetMapping("/discover")
    public Page<PodCardModel> getDiscoverPagePodCards(Pageable pageable) {
        return this.podService.getDiscoverPagePodCards(pageable);
    }

    @GetMapping("/{idPod}/stamp")
    public Page<StampCardModel> getStampCardsAssociatedWithPod(
            @PathVariable("idPod") String idPod,
            Pageable pageable
    ) {
        return this.stampService.getStampCardsAssociatedWithPod(idPod, pageable);
    }
}
