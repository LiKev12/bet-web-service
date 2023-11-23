package com.bet.betwebservice.controller;

import com.bet.betwebservice.model.TaskModel;
import com.bet.betwebservice.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("http://localhost:3000")
@RestController
@RequestMapping("/api/pods")
public class PodController {
    private TaskService taskService;

    @Autowired
    public PodController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/{idPod}/tasks")
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
}
