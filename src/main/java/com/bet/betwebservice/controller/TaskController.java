package com.bet.betwebservice.controller;

import com.bet.betwebservice.model.*;
import com.bet.betwebservice.service.TaskService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("http://localhost:3000")
@RestController
@RequestMapping("api/task")
public class TaskController {

    private TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/read/personal/tasks")
    public Page<TaskModel> getTasksPersonal(
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
        return this.taskService.getTasksPersonal(
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

    @GetMapping("/read/tasks/{idTask}/userBubblesTaskComplete")
    public List<UserBubbleModel> getUserBubblesTaskComplete(
            @PathVariable("idTask") String idTask,
            @RequestParam String idUser
    ) {
        return this.taskService.getUserBubblesTaskComplete(
                idTask,
                idUser
        );
    }

    @GetMapping("/read/personal/numberOfPointsInTasksCompletedOverTimeVisualization")
    public NumberOfPointsInTasksCompletedOverTimeVisualizationModel getNumberOfPointsInTasksCompletedOverTimeVisualizationPersonal(
            @RequestParam String idUser
    ) {
        return this.taskService.getNumberOfPointsInTasksCompletedOverTimeVisualizationPersonal(idUser);
    }

    @PostMapping("/update/task")
    public TaskModel updateTask(
            @RequestParam String idUser,
            @RequestBody JsonNode requestModel
    ) throws Exception {
        return this.taskService.updateTask(idUser, requestModel);
    }

    @PostMapping("/create/task")
    public void createTask(
            @RequestParam String idUser,
            @RequestBody JsonNode requestModel
    ) throws Exception {
        this.taskService.createTask(idUser, requestModel);
    }

    @PostMapping("/delete/task")
    public void deleteTask(
            @RequestParam String idUser,
            @RequestBody JsonNode requestModel
    ) throws Exception {
        this.taskService.deleteTask(idUser, requestModel);
    }
    
    @PostMapping("/update/taskReaction")
    public void updateTaskReaction(
            @RequestParam String idUser,
            @RequestBody JsonNode requestModel
    ) throws Exception {
        this.taskService.updateTaskReaction(idUser, requestModel);
    }

    @PostMapping("/update/taskCommentReaction")
    public void updateTaskCommentReaction(
            @RequestParam String idUser,
            @RequestBody JsonNode requestModel
    ) throws Exception {
        this.taskService.updateTaskCommentReaction(idUser, requestModel);
    }

    @PostMapping("/update/taskCommentReplyReaction")
    public void updateTaskCommentReplyReaction(
            @RequestParam String idUser,
            @RequestBody JsonNode requestModel
    ) throws Exception {
        this.taskService.updateTaskCommentReplyReaction(idUser, requestModel);
    }

    @PostMapping("/create/taskComment")
    public void createTaskComment(
            @RequestParam String idUser,
            @RequestBody JsonNode requestModel
    ) throws Exception {
        this.taskService.createTaskComment(idUser, requestModel);
    }

    @PostMapping("/create/taskCommentReply")
    public void createTaskCommentReply(
            @RequestParam String idUser,
            @RequestBody JsonNode requestModel
    ) throws Exception {
        this.taskService.createTaskCommentReply(idUser, requestModel);
    }

    @PostMapping("/read/taskReactions")
    public ReactionsModel getTaskReactions(
            @RequestParam String idUser,
            @RequestBody JsonNode requestModel
    ) throws Exception {
        return this.taskService.getTaskReactions(idUser, requestModel);
    }

    @PostMapping("/read/taskCommentReactions")
    public ReactionsModel getTaskCommentReactions(
            @RequestParam String idUser,
            @RequestBody JsonNode requestModel
    ) throws Exception {
        return this.taskService.getTaskCommentReactions(idUser, requestModel);
    }

    @PostMapping("/read/taskCommentReplyReactions")
    public ReactionsModel getTaskCommentReplyReactions(
            @RequestParam String idUser,
            @RequestBody JsonNode requestModel
    ) throws Exception {
        return this.taskService.getTaskCommentReplyReactions(idUser, requestModel);
    }

    @PostMapping("/read/taskComments")
    public List<TaskCommentModel> getTaskComments(
            @RequestParam String idUser,
            @RequestBody JsonNode requestModel
    ) throws Exception {
        return this.taskService.getTaskComments(idUser, requestModel);
    }

    @PostMapping("/read/taskCommentReplies")
    public List<TaskCommentReplyModel> getTaskCommentReplies(
            @RequestParam String idUser,
            @RequestBody JsonNode requestModel
    ) throws Exception {
        return this.taskService.getTaskCommentReplies(idUser, requestModel);
    }
}
