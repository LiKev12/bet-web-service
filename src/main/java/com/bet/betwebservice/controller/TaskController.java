package com.bet.betwebservice.controller;

import com.bet.betwebservice.model.TaskModel;
import com.bet.betwebservice.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("http://localhost:3000")
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("")
    public List<TaskModel> getTasksPersonal(
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
        return this.taskService.getTasksPersonal(
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
