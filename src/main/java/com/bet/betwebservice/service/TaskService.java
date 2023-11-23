package com.bet.betwebservice.service;

import com.bet.betwebservice.dao.TaskRepository;
import com.bet.betwebservice.model.TaskModel;
import com.bet.betwebservice.utilities.Helpers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TaskService {
    private TaskRepository taskRepository;


    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<TaskModel> getTasksPersonal(
            String idUser,
            String filterText,
            boolean filterIsComplete,
            boolean filterIsNotComplete,
            boolean filterIsStar,
            boolean filterIsNotStar,
            boolean filterIsPin,
            boolean filterIsNotPin
    ) {
        return taskRepository.getTasksPersonal(
                idUser,
                filterText,
                filterIsComplete,
                filterIsNotComplete,
                filterIsStar,
                filterIsNotStar,
                filterIsPin,
                filterIsNotPin
        ).stream().map(
                taskDTO -> TaskModel.builder()
                        .id(taskDTO.getId())
                        .name(taskDTO.getName())
                        .description(taskDTO.getDescription())
                        .image(taskDTO.getImage())
                        .numberOfPoints(taskDTO.getNumberOfPoints())
                        .idPod(taskDTO.getIdPod())
                        .isComplete(taskDTO.isComplete())
                        .isStar(taskDTO.isStar())
                        .isPin(taskDTO.isPin())
                        .datetimeCreate(Helpers.getDatetimeFromTimestamp(taskDTO.getTimestampUnix()))
                        .datetimeUpdate(Helpers.getDatetimeFromTimestamp(taskDTO.getTimestampUpdate()))
                        .datetimeTarget(Helpers.getDatetimeFromTimestamp(taskDTO.getTimestampTarget()))
                        .datetimeComplete(Helpers.getDatetimeFromTimestamp(taskDTO.getTimestampComplete()))
                        .build()
        ).collect(Collectors.toList());
    }

    public List<TaskModel> getTasksInPod(
            String idPod,
            String idUser,
            String filterText,
            boolean filterIsComplete,
            boolean filterIsNotComplete,
            boolean filterIsStar,
            boolean filterIsNotStar,
            boolean filterIsPin,
            boolean filterIsNotPin
    ) {
        return taskRepository.getTasksInPod(
                idPod,
                idUser,
                filterText,
                filterIsComplete,
                filterIsNotComplete,
                filterIsStar,
                filterIsNotStar,
                filterIsPin,
                filterIsNotPin
        ).stream().map(
                taskDTO -> TaskModel.builder()
                        .id(taskDTO.getId())
                        .name(taskDTO.getName())
                        .description(taskDTO.getDescription())
                        .image(taskDTO.getImage())
                        .numberOfPoints(taskDTO.getNumberOfPoints())
                        .idPod(taskDTO.getIdPod())
                        .isComplete(taskDTO.isComplete())
                        .isStar(taskDTO.isStar())
                        .isPin(taskDTO.isPin())
                        .datetimeCreate(Helpers.getDatetimeFromTimestamp(taskDTO.getTimestampUnix()))
                        .datetimeUpdate(Helpers.getDatetimeFromTimestamp(taskDTO.getTimestampUpdate()))
                        .datetimeTarget(Helpers.getDatetimeFromTimestamp(taskDTO.getTimestampTarget()))
                        .datetimeComplete(Helpers.getDatetimeFromTimestamp(taskDTO.getTimestampComplete()))
                        .build()
        ).collect(Collectors.toList());
    }
}
