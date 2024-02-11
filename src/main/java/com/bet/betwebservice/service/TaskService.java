package com.bet.betwebservice.service;

import com.bet.betwebservice.common.*;
import com.bet.betwebservice.dao.*;
import com.bet.betwebservice.dto.NumberOfPointsInTasksCompletedOverTimeVisualizationDTO;
import com.bet.betwebservice.dto.TaskIndividualPropertiesDTO;
import com.bet.betwebservice.dto.TaskSharedPropertiesDTO;
import com.bet.betwebservice.entity.*;
import com.bet.betwebservice.model.*;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import software.amazon.awssdk.core.sync.RequestBody;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class TaskService {
    private TaskRepository taskRepository;
    private UserRepository userRepository;
    private PodRepository podRepository;
    private TaskUserTaskCompleteRepository taskUserTaskCompleteRepository;
    private TaskUserTaskStarRepository taskUserTaskStarRepository;
    private TaskUserTaskPinRepository taskUserTaskPinRepository;
    private TaskUserTaskNoteRepository taskUserTaskNoteRepository;
    private TaskUserTaskReactionRepository taskUserTaskReactionRepository;
    private TaskUserTaskCommentReactionRepository taskUserTaskCommentReactionRepository;
    private TaskUserTaskCommentReplyReactionRepository taskUserTaskCommentReplyReactionRepository;
    private TaskUserTaskCommentRepository taskUserTaskCommentRepository;
    private TaskUserTaskCommentReplyRepository taskUserTaskCommentReplyRepository;

    public TaskService(
        TaskRepository taskRepository, 
        UserRepository userRepository,
        PodRepository podRepository,
        TaskUserTaskCompleteRepository taskUserTaskCompleteRepository,
        TaskUserTaskStarRepository taskUserTaskStarRepository,
        TaskUserTaskPinRepository taskUserTaskPinRepository,
        TaskUserTaskNoteRepository taskUserTaskNoteRepository,
        TaskUserTaskReactionRepository taskUserTaskReactionRepository,
        TaskUserTaskCommentReactionRepository taskUserTaskCommentReactionRepository,
        TaskUserTaskCommentReplyReactionRepository taskUserTaskCommentReplyReactionRepository,
        TaskUserTaskCommentRepository taskUserTaskCommentRepository,
        TaskUserTaskCommentReplyRepository taskUserTaskCommentReplyRepository
    ) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.podRepository = podRepository;
        this.taskUserTaskCompleteRepository = taskUserTaskCompleteRepository;
        this.taskUserTaskStarRepository = taskUserTaskStarRepository;
        this.taskUserTaskPinRepository = taskUserTaskPinRepository;
        this.taskUserTaskNoteRepository = taskUserTaskNoteRepository;
        this.taskUserTaskReactionRepository = taskUserTaskReactionRepository;
        this.taskUserTaskCommentReactionRepository = taskUserTaskCommentReactionRepository;
        this.taskUserTaskCommentReplyReactionRepository = taskUserTaskCommentReplyReactionRepository;
        this.taskUserTaskCommentRepository = taskUserTaskCommentRepository;
        this.taskUserTaskCommentReplyRepository = taskUserTaskCommentReplyRepository;
    }

    public List<UserBubbleModel> getUserBubblesTaskComplete(String idTask, String idUser) {
        Set<UUID> userIdsFollowedByGivenUser = new HashSet<>(this.userRepository.getUserIdsFollowedByGivenUser(idUser));
        Set<UUID> userIds_FollowRequestSentByGivenUser_NotYetAccepted = new HashSet<>(this.userRepository.getUserIds_FollowRequestSentByGivenUser_NotYetAccepted(idUser));
        ZoneId userTimeZoneZoneId = Utilities.getUserTimeZoneZoneId(idUser, this.userRepository);
        List<UserBubbleModel> userBubblesTaskComplete = this.userRepository.getUserBubblesTaskComplete(idTask).stream().map(userBubbleDTOTaskComplete -> {
            return UserBubbleModel.builder()
                .id(userBubbleDTOTaskComplete.getId())
                .name(userBubbleDTOTaskComplete.getName())
                .username(userBubbleDTOTaskComplete.getUsername())
                .imageLink(Utilities.getPresignedUrl(Constants.S3_FOLDER_NAME_USER_IMAGE, userBubbleDTOTaskComplete.getIdImageKey()))
                .timestampToSortBy(userBubbleDTOTaskComplete.getTimestampToSortBy())
                .datetimeDateOnlyLabel(Utilities.getDatetimeDateOnlyFromTimestamp(userTimeZoneZoneId, userBubbleDTOTaskComplete.getTimestampToSortBy()))
                .datetimeDateAndTimeLabel(Utilities.getDatetimeDateAndTimeFromTimestamp(userTimeZoneZoneId, userBubbleDTOTaskComplete.getTimestampToSortBy()))
                .isFollowedByMe(userIdsFollowedByGivenUser.contains(userBubbleDTOTaskComplete.getId()))
                .isFollowRequestSentNotYetAccepted(userIds_FollowRequestSentByGivenUser_NotYetAccepted.contains(userBubbleDTOTaskComplete.getId()))
                .isMe(idUser.equals(userBubbleDTOTaskComplete.getId().toString()))
                .build();
        }).collect(Collectors.toList());
        Collections.sort(userBubblesTaskComplete, new Comparator<UserBubbleModel>() {
            public int compare(UserBubbleModel userBubbleTaskComplete1, UserBubbleModel userBubbleTaskComplete2) {
                if (userBubbleTaskComplete1.isMe() == true && userBubbleTaskComplete2.isMe() == false) {
                    return -1;
                } else if (userBubbleTaskComplete1.isMe() == false && userBubbleTaskComplete2.isMe() == true) {
                    return 1;
                }
                if (userBubbleTaskComplete1.isFollowedByMe() == true && userBubbleTaskComplete2.isFollowedByMe() == false) {
                    return -1;
                } else if (userBubbleTaskComplete1.isFollowedByMe() == false && userBubbleTaskComplete2.isFollowedByMe() == true) {
                    return 1;
                }
                return userBubbleTaskComplete1.getUsername().compareTo(userBubbleTaskComplete2.getUsername());
            }
        });
        return userBubblesTaskComplete;
    }

    public Page<TaskModel> getTasksPersonal(
            String idUser,
            String filterNameOrDescription,
            boolean filterIsCompleteIndividual,
            boolean filterIsNotCompleteIndividual,
            boolean filterIsStarIndividual,
            boolean filterIsNotStarIndividual,
            boolean filterIsPinIndividual,
            boolean filterIsNotPinIndividual,
            Pageable pageable
    ) {
        List<TaskSharedPropertiesDTO> tasksPersonal_SharedProperties = this.taskRepository.getTasksPersonal_SharedProperties(idUser, filterNameOrDescription);
        List<TaskIndividualPropertiesDTO> tasksPersonal_IndividualProperties = this.taskRepository.getTasksPersonal_IndividualProperties(idUser, filterNameOrDescription);
        Page<TaskModel> tasksPage = this._getTaskModelFromSharedAndIndividualProperties(
                idUser,
                tasksPersonal_SharedProperties, 
                tasksPersonal_IndividualProperties,
                filterIsCompleteIndividual,
                filterIsNotCompleteIndividual,
                filterIsStarIndividual,
                filterIsNotStarIndividual,
                filterIsPinIndividual,
                filterIsNotPinIndividual,
                pageable
        );
        return tasksPage;
    }

    public Page<TaskModel> getTasksAssociatedWithPod(
            String idPod,
            String idUser,
            String filterNameOrDescription,
            boolean filterIsCompleteIndividual,
            boolean filterIsNotCompleteIndividual,
            boolean filterIsStarIndividual,
            boolean filterIsNotStarIndividual,
            boolean filterIsPinIndividual,
            boolean filterIsNotPinIndividual,
            Pageable pageable
    ) {
        List<TaskSharedPropertiesDTO> tasksAssociatedWithPod_SharedProperties = this.taskRepository.getTasksAssociatedWithPod_SharedProperties(idPod, filterNameOrDescription);
        List<TaskIndividualPropertiesDTO> tasksAssociatedWithPod_IndividualProperties = this.taskRepository.getTasksAssociatedWithPod_IndividualProperties(idPod, idUser, filterNameOrDescription);
        Page<TaskModel> tasksPage = this._getTaskModelFromSharedAndIndividualProperties(
                idUser,
                tasksAssociatedWithPod_SharedProperties, 
                tasksAssociatedWithPod_IndividualProperties,
                filterIsCompleteIndividual,
                filterIsNotCompleteIndividual,
                filterIsStarIndividual,
                filterIsNotStarIndividual,
                filterIsPinIndividual,
                filterIsNotPinIndividual,
                pageable
        );
        return tasksPage;
    }

    public Page<TaskModel> getTasksAssociatedWithStamp(
            String idStamp,
            String idUser,
            String filterNameOrDescription,
            boolean filterIsCompleteIndividual,
            boolean filterIsNotCompleteIndividual,
            boolean filterIsStarIndividual,
            boolean filterIsNotStarIndividual,
            boolean filterIsPinIndividual,
            boolean filterIsNotPinIndividual,
            Pageable pageable
    ) {
        List<TaskSharedPropertiesDTO> tasksAssociatedWithStamp_SharedProperties = this.taskRepository.getTasksAssociatedWithStamp_SharedProperties(idStamp, filterNameOrDescription);
        List<TaskIndividualPropertiesDTO> tasksAssociatedWithStamp_IndividualProperties = this.taskRepository.getTasksAssociatedWithStamp_IndividualProperties(idStamp, idUser, filterNameOrDescription);
        Page<TaskModel> tasksPage = this._getTaskModelFromSharedAndIndividualProperties(
                idUser,
                tasksAssociatedWithStamp_SharedProperties, 
                tasksAssociatedWithStamp_IndividualProperties,
                filterIsCompleteIndividual,
                filterIsNotCompleteIndividual,
                filterIsStarIndividual,
                filterIsNotStarIndividual,
                filterIsPinIndividual,
                filterIsNotPinIndividual,
                pageable
        );
        return tasksPage;
    }

    public Page<TaskModel> getPinnedTasksAssociatedWithUser(
            String idUserProfile,
            String idUser,
            String filterNameOrDescription,
            boolean filterIsCompleteIndividual,
            boolean filterIsNotCompleteIndividual,
            boolean filterIsStarIndividual,
            boolean filterIsNotStarIndividual,
            boolean filterIsPinIndividual,
            boolean filterIsNotPinIndividual,
            Pageable pageable
    ) {
        List<TaskSharedPropertiesDTO> tasksAssociatedWithUser_SharedProperties = this.taskRepository.getPinnedTasksAssociatedWithUser_SharedProperties(idUserProfile, filterNameOrDescription);
        List<TaskIndividualPropertiesDTO> tasksAssociatedWithUser_IndividualProperties = this.taskRepository.getPinnedTasksAssociatedWithUser_IndividualProperties(idUserProfile, filterNameOrDescription);
        Page<TaskModel> tasksPage = this._getTaskModelFromSharedAndIndividualProperties(
                idUser,
                tasksAssociatedWithUser_SharedProperties, 
                tasksAssociatedWithUser_IndividualProperties,
                filterIsCompleteIndividual,
                filterIsNotCompleteIndividual,
                filterIsStarIndividual,
                filterIsNotStarIndividual,
                filterIsPinIndividual,
                filterIsNotPinIndividual,
                pageable
        );
        return tasksPage;
    }

    public NumberOfPointsInTasksCompletedOverTimeVisualizationModel getNumberOfPointsInTasksCompletedOverTimeVisualizationPersonal(String idUser) {
        ZoneId userTimeZoneZoneId = Utilities.getUserTimeZoneZoneId(idUser, this.userRepository);
        String dateStartAsString =  Utilities.getDatetimeDateOnlyFromTimestamp(userTimeZoneZoneId, this.userRepository.getTimestampUserCreateAccount(idUser));
        List<NumberOfPointsInTasksCompletedOverTimeVisualizationDTO> numberOfPointsInTasksCompletedOverTimeList = this.taskRepository.getNumberOfPointsInTasksCompletedOverTimeVisualizationPersonal(idUser);
        NumberOfPointsInTasksCompletedOverTimeVisualizationModel numberOfPointsInTasksCompletedOverTimeModel = this._getNumberOfPointsInTasksCompletedOverTimeVisualization(
                numberOfPointsInTasksCompletedOverTimeList,
                dateStartAsString,
                userTimeZoneZoneId,
                true
        );
        return numberOfPointsInTasksCompletedOverTimeModel;
    }

    public NumberOfPointsInTasksCompletedOverTimeVisualizationModel getNumberOfPointsInTasksCompletedOverTimeVisualizationAssociatedWithPod(String idPod, String idUser) {
        ZoneId userTimeZoneZoneId = Utilities.getUserTimeZoneZoneId(idUser, this.userRepository);
        // Integer timestampUserJoinPod = this.userRepository.getTimestampUserJoinPod(idPod, idUser);
        Integer timestampUserJoinPod = this.userRepository.getTimestampUserFirstCompleteAnyTaskAssociatedWithPod(idPod, idUser);
        boolean isHeatmapRelevantOverride = Utilities.isValidTimestamp(timestampUserJoinPod);
        String dateStartAsString;
        if (isHeatmapRelevantOverride) {
            dateStartAsString = Utilities.getDatetimeDateOnlyFromTimestamp(userTimeZoneZoneId, timestampUserJoinPod);
        } else {
            dateStartAsString = LocalDate.now(userTimeZoneZoneId).format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        }
        List<NumberOfPointsInTasksCompletedOverTimeVisualizationDTO> numberOfPointsInTasksCompletedOverTimeList = this.taskRepository.getNumberOfPointsInTasksCompletedOverTimeVisualizationAssociatedWithPod(idPod, idUser);
        NumberOfPointsInTasksCompletedOverTimeVisualizationModel numberOfPointsInTasksCompletedOverTimeModel = this._getNumberOfPointsInTasksCompletedOverTimeVisualization(
                numberOfPointsInTasksCompletedOverTimeList,
                dateStartAsString,
                userTimeZoneZoneId,
                isHeatmapRelevantOverride
        );
        return numberOfPointsInTasksCompletedOverTimeModel;
    }

    public NumberOfPointsInTasksCompletedOverTimeVisualizationModel getNumberOfPointsInTasksCompletedOverTimeVisualizationAssociatedWithStamp(String idStamp, String idUser) {
        ZoneId userTimeZoneZoneId = Utilities.getUserTimeZoneZoneId(idUser, this.userRepository);
        Integer timestampUserFirstCompleteAnyTaskAssociatedWithStamp = this.userRepository.getTimestampUserFirstCompleteAnyTaskAssociatedWithStamp(idStamp, idUser);
        boolean isHeatmapRelevantOverride = Utilities.isValidTimestamp(timestampUserFirstCompleteAnyTaskAssociatedWithStamp);
        String dateStartAsString;
        if (isHeatmapRelevantOverride) {
            dateStartAsString =  Utilities.getDatetimeDateOnlyFromTimestamp(userTimeZoneZoneId, timestampUserFirstCompleteAnyTaskAssociatedWithStamp);
        } else {
            dateStartAsString = LocalDate.now(userTimeZoneZoneId).format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        }
        List<NumberOfPointsInTasksCompletedOverTimeVisualizationDTO> numberOfPointsInTasksCompletedOverTimeList = this.taskRepository.getNumberOfPointsInTasksCompletedOverTimeVisualizationAssociatedWithStamp(idStamp, idUser);
        NumberOfPointsInTasksCompletedOverTimeVisualizationModel numberOfPointsInTasksCompletedOverTimeModel = this._getNumberOfPointsInTasksCompletedOverTimeVisualization(
                numberOfPointsInTasksCompletedOverTimeList,
                dateStartAsString,
                userTimeZoneZoneId,
                isHeatmapRelevantOverride
        );
        return numberOfPointsInTasksCompletedOverTimeModel;
    }

    public NumberOfPointsInTasksCompletedOverTimeVisualizationModel _getNumberOfPointsInTasksCompletedOverTimeVisualization(
        List<NumberOfPointsInTasksCompletedOverTimeVisualizationDTO> numberOfPointsInTasksCompletedOverTimeList,
        String dateStartAsString,
        ZoneId userTimeZoneZoneId,
        boolean isHeatmapRelevantOverride
    ) {
        /**
         * get day of week in label for aggregate daily line chart
         */
        Map<String, List<NumberOfPointsInTasksCompletedOverTimeVisualizationDTO>> dateLabelToNumberOfPointsInTasksCompletedOverTimeVisualizationListMap = new HashMap<>();

        for (int i = 0; i < numberOfPointsInTasksCompletedOverTimeList.size(); i++) {
            NumberOfPointsInTasksCompletedOverTimeVisualizationDTO numberOfPointsInTasksCompletedOverTimeEntry = numberOfPointsInTasksCompletedOverTimeList.get(i);
            Integer timestampOfTaskComplete = numberOfPointsInTasksCompletedOverTimeEntry.getTimestamp();
            if (Utilities.isValidTimestamp(timestampOfTaskComplete)) {
                String dateLabel = Utilities.getDatetimeDateOnlyFromTimestamp(userTimeZoneZoneId, timestampOfTaskComplete);
                List<NumberOfPointsInTasksCompletedOverTimeVisualizationDTO> numberOfPointsInTasksCompletedOverTimeListValue = dateLabelToNumberOfPointsInTasksCompletedOverTimeVisualizationListMap.getOrDefault(dateLabel, new ArrayList<>());
                numberOfPointsInTasksCompletedOverTimeListValue.add(numberOfPointsInTasksCompletedOverTimeEntry);
                dateLabelToNumberOfPointsInTasksCompletedOverTimeVisualizationListMap.put(dateLabel, numberOfPointsInTasksCompletedOverTimeListValue);
            }
        }

        List<NumberOfPointsInTasksCompletedOverTimeVisualizationModel.HeatmapChartDataPoint> dataHeatmapChart = new ArrayList<>();
        List<NumberOfPointsInTasksCompletedOverTimeVisualizationModel.LineChartDataPoint> dataLineChart_AggregateDay_NotCumulative = new ArrayList<>();
        List<NumberOfPointsInTasksCompletedOverTimeVisualizationModel.LineChartDataPoint> dataLineChart_AggregateWeek_NotCumulative = new ArrayList<>();
        List<NumberOfPointsInTasksCompletedOverTimeVisualizationModel.LineChartDataPoint> dataLineChart_AggregateMonth_NotCumulative = new ArrayList<>();
        List<NumberOfPointsInTasksCompletedOverTimeVisualizationModel.LineChartDataPoint> dataLineChart_AggregateDay_Cumulative = new ArrayList<>();
        List<NumberOfPointsInTasksCompletedOverTimeVisualizationModel.LineChartDataPoint> dataLineChart_AggregateWeek_Cumulative = new ArrayList<>();
        List<NumberOfPointsInTasksCompletedOverTimeVisualizationModel.LineChartDataPoint> dataLineChart_AggregateMonth_Cumulative = new ArrayList<>();

        DateTimeFormatter dtf_yyyyMMdd = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        DateTimeFormatter dtf_MMdd = DateTimeFormatter.ofPattern("MM/dd");
        String dateEndAsString = LocalDate.now(userTimeZoneZoneId).format(dtf_yyyyMMdd);
        // HeatmapChart (start same month, 1 year ago, on the Sunday of the week including the same day of month, end on the next Saturday)
        String dateEndSundayAsString = LocalDate.now(userTimeZoneZoneId).format(dtf_yyyyMMdd);
        while (LocalDate.parse(dateEndSundayAsString, dtf_yyyyMMdd).getDayOfWeek().getValue() != 6) {
            dateEndSundayAsString = LocalDate.parse(dateEndSundayAsString, dtf_yyyyMMdd).plusDays(1).format(dtf_yyyyMMdd);
        }
        String HEATMAP_CHART_COLOR_DEFAULT = "#FFFFFF";
        String dateIteratorAsStringHeatmapChart = LocalDate.parse(dateEndAsString, dtf_yyyyMMdd).minusYears(1).format(dtf_yyyyMMdd);
        while (LocalDate.parse(dateIteratorAsStringHeatmapChart, dtf_yyyyMMdd).getDayOfWeek().getValue() != 7) {
            dateIteratorAsStringHeatmapChart = LocalDate.parse(dateIteratorAsStringHeatmapChart, dtf_yyyyMMdd).minusDays(1).format(dtf_yyyyMMdd);
        }
        List<Integer> totalNumberOfPointsInSingleDayForColorThresholdsList = new ArrayList<>();
        while (LocalDate.parse(dateIteratorAsStringHeatmapChart, dtf_yyyyMMdd).compareTo(LocalDate.parse(dateEndSundayAsString, dtf_yyyyMMdd)) <= 0) {
            List<NumberOfPointsInTasksCompletedOverTimeVisualizationDTO> numberOfPointsInTasksCompletedOverTimeListValue = dateLabelToNumberOfPointsInTasksCompletedOverTimeVisualizationListMap.getOrDefault(dateIteratorAsStringHeatmapChart, new ArrayList<>());
            boolean isAfterOrEqualToRelevantStartDate = LocalDate.parse(dateIteratorAsStringHeatmapChart, dtf_yyyyMMdd).isAfter(LocalDate.parse(dateStartAsString, dtf_yyyyMMdd)) || 
                LocalDate.parse(dateIteratorAsStringHeatmapChart, dtf_yyyyMMdd).isEqual(LocalDate.parse(dateStartAsString, dtf_yyyyMMdd));
            if (isAfterOrEqualToRelevantStartDate) {
                int numberOfPoints = 0;
                for (int i = 0; i < numberOfPointsInTasksCompletedOverTimeListValue.size(); i++) {
                    numberOfPoints += numberOfPointsInTasksCompletedOverTimeListValue.get(i).getNumberOfPoints();
                }
                totalNumberOfPointsInSingleDayForColorThresholdsList.add(numberOfPoints);
            }
            dateIteratorAsStringHeatmapChart = LocalDate.parse(dateIteratorAsStringHeatmapChart, dtf_yyyyMMdd).plusDays(1).format(dtf_yyyyMMdd);
        }
        Collections.sort(totalNumberOfPointsInSingleDayForColorThresholdsList);
        String DEFAULT_COLOR_IRRELEVANT = "#e5e5e5";
        int colorP75to100UB = 0;
        int colorP75to100LB = 0;
        int colorP50to75UB = 0;
        int colorP50to75LB = 0;
        int colorP25to50UB = 0;
        int colorP25to50LB = 0;
        int colorP0to25UB = 0;
        int colorP0to25LB = 0;
        if (totalNumberOfPointsInSingleDayForColorThresholdsList.size() > 0) {
            colorP75to100UB = totalNumberOfPointsInSingleDayForColorThresholdsList.get(totalNumberOfPointsInSingleDayForColorThresholdsList.size()-1);
            colorP75to100LB = totalNumberOfPointsInSingleDayForColorThresholdsList.get(3*totalNumberOfPointsInSingleDayForColorThresholdsList.size()/4);
            colorP50to75UB = colorP75to100LB-1;
            colorP50to75LB = Math.min(colorP50to75UB, totalNumberOfPointsInSingleDayForColorThresholdsList.get(totalNumberOfPointsInSingleDayForColorThresholdsList.size()/2));
            colorP25to50UB = colorP50to75LB-1;
            colorP25to50LB = Math.min(colorP25to50UB, totalNumberOfPointsInSingleDayForColorThresholdsList.get(totalNumberOfPointsInSingleDayForColorThresholdsList.size()/4));
            colorP0to25UB = colorP25to50LB-1;
            colorP0to25LB = Math.min(colorP0to25UB, totalNumberOfPointsInSingleDayForColorThresholdsList.get(0));
        }
        dateIteratorAsStringHeatmapChart = LocalDate.parse(dateEndAsString, dtf_yyyyMMdd).minusYears(1).format(dtf_yyyyMMdd);
        while (LocalDate.parse(dateIteratorAsStringHeatmapChart, dtf_yyyyMMdd).getDayOfWeek().getValue() != 7) {
            dateIteratorAsStringHeatmapChart = LocalDate.parse(dateIteratorAsStringHeatmapChart, dtf_yyyyMMdd).minusDays(1).format(dtf_yyyyMMdd);
        }
        while (LocalDate.parse(dateIteratorAsStringHeatmapChart, dtf_yyyyMMdd).compareTo(LocalDate.parse(dateEndSundayAsString, dtf_yyyyMMdd)) <= 0) {
            List<NumberOfPointsInTasksCompletedOverTimeVisualizationDTO> numberOfPointsInTasksCompletedOverTimeListValue = dateLabelToNumberOfPointsInTasksCompletedOverTimeVisualizationListMap.getOrDefault(dateIteratorAsStringHeatmapChart, new ArrayList<>());
            int numberOfTasksComplete = numberOfPointsInTasksCompletedOverTimeListValue.size();
            int numberOfPoints = 0;
            for (int i = 0; i < numberOfPointsInTasksCompletedOverTimeListValue.size(); i++) {
                numberOfPoints += numberOfPointsInTasksCompletedOverTimeListValue.get(i).getNumberOfPoints();
            }
            String color = HEATMAP_CHART_COLOR_DEFAULT;
            if (numberOfTasksComplete == 0) {
                color = HEATMAP_CHART_COLOR_DEFAULT;
            } else if (numberOfPoints >= colorP0to25LB && numberOfPoints <= colorP0to25UB) {
                color = "#ccfff3";
            } else if (numberOfPoints >= colorP25to50LB && numberOfPoints <= colorP25to50UB) {
                color = "#ccfff3";
            } else if (numberOfPoints >= colorP50to75LB && numberOfPoints <= colorP50to75UB) {
                color = "#99ffe8";
            } else if (numberOfPoints >= colorP75to100LB && numberOfPoints <= colorP75to100UB) {
                color = "#89e5d0";
            }
            // // random to experiment with different color patterns: ["#ccfff3", "#99ffe8", "#89e5d0"]
            // List<String> randomColors = new ArrayList<>(Arrays.asList(HEATMAP_CHART_COLOR_DEFAULT, "#ccfff3", "#ccfff3",  "#99ffe8", "#99ffe8", "#89e5d0", "#89e5d0"));
            // color = randomColors.get(new Random().nextInt(randomColors.size()));
            boolean isAfterOrEqualToRelevantStartDate = isHeatmapRelevantOverride &&
                    (LocalDate.parse(dateIteratorAsStringHeatmapChart, dtf_yyyyMMdd).isAfter(LocalDate.parse(dateStartAsString, dtf_yyyyMMdd)) ||
                    LocalDate.parse(dateIteratorAsStringHeatmapChart, dtf_yyyyMMdd).isEqual(LocalDate.parse(dateStartAsString, dtf_yyyyMMdd)));
            boolean isToday = LocalDate.parse(dateIteratorAsStringHeatmapChart, dtf_yyyyMMdd).isEqual(LocalDate.parse(dateEndAsString, dtf_yyyyMMdd));
            boolean isBeforeOrEqualToRelevantEndDate = LocalDate.parse(dateIteratorAsStringHeatmapChart, dtf_yyyyMMdd).isBefore(LocalDate.parse(dateEndAsString, dtf_yyyyMMdd)) || isToday;
            NumberOfPointsInTasksCompletedOverTimeVisualizationModel.HeatmapChartDataPoint heatmapChartDataPointEntry = NumberOfPointsInTasksCompletedOverTimeVisualizationModel.HeatmapChartDataPoint.builder()
                .numberOfPoints(numberOfPoints)
                .numberOfTasksComplete(numberOfTasksComplete)
                .color(isAfterOrEqualToRelevantStartDate && isBeforeOrEqualToRelevantEndDate ? color : DEFAULT_COLOR_IRRELEVANT)
                .dateLabel(dateIteratorAsStringHeatmapChart)
                .dayOfWeek(LocalDate.parse(dateIteratorAsStringHeatmapChart, dtf_yyyyMMdd).getDayOfWeek().toString())
                .isAfterOrEqualToRelevantStartDate(isAfterOrEqualToRelevantStartDate)
                .isBeforeOrEqualToRelevantEndDate(isBeforeOrEqualToRelevantEndDate)
                .isToday(isToday)
                .build();
            dataHeatmapChart.add(heatmapChartDataPointEntry);
            dateIteratorAsStringHeatmapChart = LocalDate.parse(dateIteratorAsStringHeatmapChart, dtf_yyyyMMdd).plusDays(1).format(dtf_yyyyMMdd);
        }

        // LineChart - AGGREGATE DAY - 7 days ago (1 full week)
        String dateIteratorAsStringLineChartAggregateDay = LocalDate.parse(dateEndAsString, dtf_yyyyMMdd).minusWeeks(1).format(dtf_yyyyMMdd);
        int dataLineChart_AggregateDay_Cumulative_numberOfPoints = 0;
        while (LocalDate.parse(dateIteratorAsStringLineChartAggregateDay, dtf_yyyyMMdd).compareTo(LocalDate.parse(dateEndAsString, dtf_yyyyMMdd)) <= 0) {
            List<NumberOfPointsInTasksCompletedOverTimeVisualizationDTO> numberOfPointsInTasksCompletedOverTimeListValue = dateLabelToNumberOfPointsInTasksCompletedOverTimeVisualizationListMap.getOrDefault(dateIteratorAsStringLineChartAggregateDay, new ArrayList<>());
            String dateLabel = LocalDate.parse(dateIteratorAsStringLineChartAggregateDay, dtf_yyyyMMdd).format(dtf_MMdd);
            int numberOfPoints = 0;
            for (int i = 0; i < numberOfPointsInTasksCompletedOverTimeListValue.size(); i++) {
                numberOfPoints += numberOfPointsInTasksCompletedOverTimeListValue.get(i).getNumberOfPoints();
            }
            dataLineChart_AggregateDay_Cumulative_numberOfPoints += numberOfPoints;
            // Not Cumulative
            NumberOfPointsInTasksCompletedOverTimeVisualizationModel.LineChartDataPoint dataLineChart_AggregateDay_NotCumulativeEntry = NumberOfPointsInTasksCompletedOverTimeVisualizationModel.LineChartDataPoint.builder()
                .numberOfPoints(numberOfPoints)
                .dateLabel(dateLabel)
                .build();
            dataLineChart_AggregateDay_NotCumulative.add(dataLineChart_AggregateDay_NotCumulativeEntry);
            // Cumulative
            NumberOfPointsInTasksCompletedOverTimeVisualizationModel.LineChartDataPoint dataLineChart_AggregateDay_CumulativeEntry = NumberOfPointsInTasksCompletedOverTimeVisualizationModel.LineChartDataPoint.builder()
                .numberOfPoints(dataLineChart_AggregateDay_Cumulative_numberOfPoints)
                .dateLabel(dateLabel)
                .build();
            dataLineChart_AggregateDay_Cumulative.add(dataLineChart_AggregateDay_CumulativeEntry);
            dateIteratorAsStringLineChartAggregateDay = LocalDate.parse(dateIteratorAsStringLineChartAggregateDay, dtf_yyyyMMdd).plusDays(1).format(dtf_yyyyMMdd);
        }

        // LineChart - AGGREGATE WEEK - Sunday to Sunday (start on the Sunday of the week in previous month which includes this day of month)
        String dateIteratorAsStringLineChartAggregateWeek = LocalDate.parse(dateEndAsString, dtf_yyyyMMdd).minusMonths(1).format(dtf_yyyyMMdd);
        while (LocalDate.parse(dateIteratorAsStringLineChartAggregateWeek, dtf_yyyyMMdd).getDayOfWeek().getValue() != 7) {
            dateIteratorAsStringLineChartAggregateWeek = LocalDate.parse(dateIteratorAsStringLineChartAggregateWeek, dtf_yyyyMMdd).minusDays(1).format(dtf_yyyyMMdd);
        }
        int dataLineChart_AggregateWeek_Cumulative_numberOfPoints = 0;
        while (LocalDate.parse(dateIteratorAsStringLineChartAggregateWeek, dtf_yyyyMMdd).compareTo(LocalDate.parse(dateEndAsString, dtf_yyyyMMdd)) <= 0) {
            String dateIteratorStartAsString = dateIteratorAsStringLineChartAggregateWeek;
            String dateIteratorEndAsString = LocalDate.parse(dateIteratorAsStringLineChartAggregateWeek, dtf_yyyyMMdd).plusWeeks(1).minusDays(1).compareTo(LocalDate.parse(dateEndAsString, dtf_yyyyMMdd)) < 0 ?
                LocalDate.parse(dateIteratorAsStringLineChartAggregateWeek, dtf_yyyyMMdd).plusWeeks(1).minusDays(1).format(dtf_yyyyMMdd) : LocalDate.parse(dateEndAsString, dtf_yyyyMMdd).format(dtf_yyyyMMdd);
            String dateLabel = new StringBuilder()
                .append(LocalDate.parse(dateIteratorStartAsString, dtf_yyyyMMdd).format(dtf_MMdd))
                .append("-")
                .append(LocalDate.parse(dateIteratorEndAsString, dtf_yyyyMMdd).format(dtf_MMdd))
                .toString();
            String dateIteratorByDayAsString = dateIteratorStartAsString;
            int numberOfPoints = 0;
            while (LocalDate.parse(dateIteratorByDayAsString, dtf_yyyyMMdd).compareTo(LocalDate.parse(dateIteratorEndAsString, dtf_yyyyMMdd)) <= 0) {
                List<NumberOfPointsInTasksCompletedOverTimeVisualizationDTO> numberOfPointsInTasksCompletedOverTimeListValue = dateLabelToNumberOfPointsInTasksCompletedOverTimeVisualizationListMap.getOrDefault(dateIteratorByDayAsString, new ArrayList<>());
                for (int i = 0; i < numberOfPointsInTasksCompletedOverTimeListValue.size(); i++) {
                    numberOfPoints += numberOfPointsInTasksCompletedOverTimeListValue.get(i).getNumberOfPoints();
                }
                dateIteratorByDayAsString = LocalDate.parse(dateIteratorByDayAsString, dtf_yyyyMMdd).plusDays(1).format(dtf_yyyyMMdd);
            }
            dataLineChart_AggregateWeek_Cumulative_numberOfPoints += numberOfPoints;
            // Not Cumulative
            NumberOfPointsInTasksCompletedOverTimeVisualizationModel.LineChartDataPoint dataLineChart_AggregateWeek_NotCumulativeEntry = NumberOfPointsInTasksCompletedOverTimeVisualizationModel.LineChartDataPoint.builder()
                .numberOfPoints(numberOfPoints)
                .dateLabel(dateLabel)
                .build();
            dataLineChart_AggregateWeek_NotCumulative.add(dataLineChart_AggregateWeek_NotCumulativeEntry);
            // Cumulative
            NumberOfPointsInTasksCompletedOverTimeVisualizationModel.LineChartDataPoint dataLineChart_AggregateWeek_CumulativeEntry = NumberOfPointsInTasksCompletedOverTimeVisualizationModel.LineChartDataPoint.builder()
                .numberOfPoints(dataLineChart_AggregateWeek_Cumulative_numberOfPoints)
                .dateLabel(dateLabel)
                .build();
            dataLineChart_AggregateWeek_Cumulative.add(dataLineChart_AggregateWeek_CumulativeEntry);
            dateIteratorAsStringLineChartAggregateWeek = LocalDate.parse(dateIteratorAsStringLineChartAggregateWeek, dtf_yyyyMMdd).plusWeeks(1).format(dtf_yyyyMMdd);
        }

        // TODO: AGGREGATE MONTH (start same month, 1 year ago, on the 1st of the month)
        String dateIteratorAsStringLineChartAggregateMonth = LocalDate.of(
            LocalDate.parse(dateEndAsString, dtf_yyyyMMdd).getYear()-1,
            LocalDate.parse(dateEndAsString, dtf_yyyyMMdd).getMonth().getValue(),
            1
        ).format(dtf_yyyyMMdd);
        int dataLineChart_AggregateMonth_Cumulative_numberOfPoints = 0;
        while (LocalDate.parse(dateIteratorAsStringLineChartAggregateMonth, dtf_yyyyMMdd).compareTo(LocalDate.parse(dateEndAsString, dtf_yyyyMMdd)) <= 0) {
            String dateIteratorStartAsString = dateIteratorAsStringLineChartAggregateMonth;
            String dateIteratorEndAsString = LocalDate.parse(dateIteratorAsStringLineChartAggregateMonth, dtf_yyyyMMdd).plusMonths(1).minusDays(1).compareTo(LocalDate.parse(dateEndAsString, dtf_yyyyMMdd)) < 0 ?
                LocalDate.parse(dateIteratorAsStringLineChartAggregateMonth, dtf_yyyyMMdd).plusMonths(1).minusDays(1).format(dtf_yyyyMMdd) : LocalDate.parse(dateEndAsString, dtf_yyyyMMdd).format(dtf_yyyyMMdd);
            String dateLabel = new StringBuilder()
                    .append(LocalDate.parse(dateIteratorStartAsString, dtf_yyyyMMdd).format(dtf_MMdd))
                    .append("-")
                    .append(LocalDate.parse(dateIteratorEndAsString, dtf_yyyyMMdd).format(dtf_MMdd))
                    .toString();
            String dateIteratorByDayAsString = dateIteratorStartAsString;
            int numberOfPoints = 0;
            while (LocalDate.parse(dateIteratorByDayAsString, dtf_yyyyMMdd).compareTo(LocalDate.parse(dateIteratorEndAsString, dtf_yyyyMMdd)) <= 0) {
                List<NumberOfPointsInTasksCompletedOverTimeVisualizationDTO> numberOfPointsInTasksCompletedOverTimeListValue = dateLabelToNumberOfPointsInTasksCompletedOverTimeVisualizationListMap.getOrDefault(dateIteratorByDayAsString, new ArrayList<>());
                for (int i = 0; i < numberOfPointsInTasksCompletedOverTimeListValue.size(); i++) {
                    numberOfPoints += numberOfPointsInTasksCompletedOverTimeListValue.get(i).getNumberOfPoints();
                }
                dateIteratorByDayAsString = LocalDate.parse(dateIteratorByDayAsString, dtf_yyyyMMdd).plusDays(1).format(dtf_yyyyMMdd);
            }
            dataLineChart_AggregateMonth_Cumulative_numberOfPoints += numberOfPoints;
            // Not Cumulative
            NumberOfPointsInTasksCompletedOverTimeVisualizationModel.LineChartDataPoint dataLineChart_AggregateMonth_NotCumulativeEntry = NumberOfPointsInTasksCompletedOverTimeVisualizationModel.LineChartDataPoint.builder()
                .numberOfPoints(numberOfPoints)
                .dateLabel(dateLabel)
                .build();
            dataLineChart_AggregateMonth_NotCumulative.add(dataLineChart_AggregateMonth_NotCumulativeEntry);
            // Cumulative
            NumberOfPointsInTasksCompletedOverTimeVisualizationModel.LineChartDataPoint dataLineChart_AggregateMonth_CumulativeEntry = NumberOfPointsInTasksCompletedOverTimeVisualizationModel.LineChartDataPoint.builder()
                .numberOfPoints(dataLineChart_AggregateMonth_Cumulative_numberOfPoints)
                .dateLabel(dateLabel)
                .build();
            dataLineChart_AggregateMonth_Cumulative.add(dataLineChart_AggregateMonth_CumulativeEntry);
            dateIteratorAsStringLineChartAggregateMonth = LocalDate.parse(dateIteratorAsStringLineChartAggregateMonth, dtf_yyyyMMdd).plusMonths(1).format(dtf_yyyyMMdd);
        }

        NumberOfPointsInTasksCompletedOverTimeVisualizationModel numberOfPointsInTasksCompletedOverTimeModel = NumberOfPointsInTasksCompletedOverTimeVisualizationModel.builder()
            .dataHeatmapChart(dataHeatmapChart)
            .dataLineChart_AggregateDay_NotCumulative(dataLineChart_AggregateDay_NotCumulative)
            .dataLineChart_AggregateDay_Cumulative(dataLineChart_AggregateDay_Cumulative)
            .dataLineChart_AggregateWeek_NotCumulative(dataLineChart_AggregateWeek_NotCumulative)
            .dataLineChart_AggregateWeek_Cumulative(dataLineChart_AggregateWeek_Cumulative)
            .dataLineChart_AggregateMonth_NotCumulative(dataLineChart_AggregateMonth_NotCumulative)
            .dataLineChart_AggregateMonth_Cumulative(dataLineChart_AggregateMonth_Cumulative)
            .build();
        return numberOfPointsInTasksCompletedOverTimeModel;

    }

    public TaskModel updateTask(
            String idUser,
            JsonNode requestBody
    ) throws Exception {
        if (
            idUser == null ||
            !requestBody.has(RequestBodyKeys.UPDATE_TASK_ID)
        ) {
            throw new Exception("Error: invalid input"); // TODO create an exception class
        }
        String taskId = RequestFormatterCommon.fUUID(requestBody.get(RequestBodyKeys.UPDATE_TASK_ID));
        if (
            !RequestValidatorTask.id(taskId)
        ) {
            throw new Exception("Error: invalid input"); // TODO create an exception class
        }

        Optional<TaskEntity> taskEntityOptional = this.taskRepository.findById(UUID.fromString(taskId));
        if (!taskEntityOptional.isPresent()) {
            throw new Exception("Error: invalid input");
        }
        TaskEntity taskEntity = taskEntityOptional.get();
        if (requestBody.has(RequestBodyKeys.UPDATE_TASK_NAME)) {
            String taskName = RequestFormatterCommon.fString(requestBody.get(RequestBodyKeys.UPDATE_TASK_NAME));
            if (!RequestValidatorTask.name(taskName)) {
                throw new Exception("Error: invalid input");
            }
            boolean isNoChange = (taskName == null && taskEntity.getName() == null) || 
                (taskName != null && taskName.equals(taskEntity.getName())
            );
            if (!isNoChange) {
                taskEntity.setName(taskName);
                taskEntity.setTimestampUpdate((int) Instant.now().getEpochSecond());
                this.taskRepository.save(taskEntity);
            }
        }
        if (requestBody.has(RequestBodyKeys.UPDATE_TASK_DESCRIPTION)) {
            String taskDescription = RequestFormatterCommon.fString(requestBody.get(RequestBodyKeys.UPDATE_TASK_DESCRIPTION));
            if (!RequestValidatorTask.description(taskDescription)) {
                throw new Exception("Error: invalid input");
            }
            boolean isNoChange = (taskDescription == null && taskEntity.getDescription() == null) || 
                (taskDescription != null && taskDescription.equals(taskEntity.getDescription())
            );
            if (!isNoChange) {
                taskEntity.setDescription(taskDescription);
                taskEntity.setTimestampUpdate((int) Instant.now().getEpochSecond());
                this.taskRepository.save(taskEntity);
            }
        }
        if (requestBody.has(RequestBodyKeys.UPDATE_TASK_IMAGE)) {
            String taskImageAsBase64String = RequestFormatterCommon.fString(requestBody.get(RequestBodyKeys.UPDATE_TASK_IMAGE));
            if (!RequestValidatorTask.imageAsBase64String(taskImageAsBase64String)) {
                throw new Exception("Error: invalid input");
            }
            boolean isNoChange = taskImageAsBase64String == null && taskEntity.getIdImageKey() == null;
            if (!isNoChange) {
                if (taskImageAsBase64String == null) {
                    taskEntity.setIdImageKey(null);
                    this.taskRepository.save(taskEntity);
                } else {
                    UUID taskImageKeyId = UUID.randomUUID();
                    byte[] imageAsByteArray = java.util.Base64.getDecoder().decode(taskImageAsBase64String.substring(taskImageAsBase64String.indexOf(",") + 1));
                    RequestBody imageAsByteArrayRequestBody = RequestBody.fromBytes(imageAsByteArray);
                    Utilities.putObjectInS3(Constants.S3_FOLDER_NAME_TASK_IMAGE, taskImageKeyId, imageAsByteArrayRequestBody);
                    taskEntity.setIdImageKey(taskImageKeyId);
                    this.taskRepository.save(taskEntity);
                }
            }
        }
        if (requestBody.has(RequestBodyKeys.UPDATE_TASK_NUMBER_OF_POINTS)) {
            int taskNumberOfPoints = requestBody.get(RequestBodyKeys.UPDATE_TASK_NUMBER_OF_POINTS).asInt();
            if (!RequestValidatorTask.numberOfPoints(taskNumberOfPoints)) {
                throw new Exception("Error: invalid input");
            }
            boolean isNoChange = taskNumberOfPoints == taskEntity.getNumberOfPoints();
            if (!isNoChange) {
                taskEntity.setNumberOfPoints(taskNumberOfPoints);
                taskEntity.setTimestampUpdate((int) Instant.now().getEpochSecond());
                this.taskRepository.save(taskEntity);
            }
        }
        if (requestBody.has(RequestBodyKeys.UPDATE_TASK_DATETIME_TARGET)) {
            String taskDatetimeTarget = RequestFormatterCommon.fString(requestBody.get(RequestBodyKeys.UPDATE_TASK_DATETIME_TARGET));
            if (!RequestValidatorTask.datetimeTarget(taskDatetimeTarget)) {
                throw new Exception("Error: invalid input");
            }
            boolean isNoChange = (
                (taskDatetimeTarget == null && taskEntity.getDatetimeTarget() == null) ||
                taskDatetimeTarget == taskEntity.getDatetimeTarget()
            );
            if (!isNoChange) {
                taskEntity.setDatetimeTarget(taskDatetimeTarget);
                taskEntity.setTimestampUpdate((int) Instant.now().getEpochSecond());
                this.taskRepository.save(taskEntity);
            }
        }
        if (requestBody.has(RequestBodyKeys.UPDATE_TASK_IS_COMPLETE)) {
            boolean taskIsComplete = RequestFormatterCommon.fBoolean(requestBody.get(RequestBodyKeys.UPDATE_TASK_IS_COMPLETE));
            List<TaskUserTaskCompleteEntity> taskUserTaskCompleteEntities = this.taskUserTaskCompleteRepository.findByIdTaskIdUser(taskId, idUser);
            if (taskIsComplete) {
                boolean isNoChange = taskUserTaskCompleteEntities.size() > 0;
                if (!isNoChange) {
                    TaskUserTaskCompleteEntity taskUserTaskCompleteEntity = new TaskUserTaskCompleteEntity();
                    taskUserTaskCompleteEntity.setIdTask(UUID.fromString(taskId));
                    taskUserTaskCompleteEntity.setIdUser(UUID.fromString(idUser));
                    taskUserTaskCompleteEntity.setTimestampUnix((int) Instant.now().getEpochSecond());
                    this.taskUserTaskCompleteRepository.save(taskUserTaskCompleteEntity);
                }
            } else {
                boolean isNoChange = taskUserTaskCompleteEntities.size() == 0;
                if (!isNoChange) {
                    TaskUserTaskCompleteEntity taskUserTaskCompleteEntity = taskUserTaskCompleteEntities.get(0);
                    this.taskUserTaskCompleteRepository.delete(taskUserTaskCompleteEntity);
                }
            }
        }
        if (requestBody.has(RequestBodyKeys.UPDATE_TASK_IS_STAR)) {
            boolean taskIsStar = RequestFormatterCommon.fBoolean(requestBody.get(RequestBodyKeys.UPDATE_TASK_IS_STAR));
            List<TaskUserTaskStarEntity> taskUserTaskStarEntities = this.taskUserTaskStarRepository.findByIdTaskIdUser(taskId, idUser);
            if (taskIsStar) {
                boolean isNoChange = taskUserTaskStarEntities.size() > 0;
                if (!isNoChange) {
                    TaskUserTaskStarEntity taskUserTaskStarEntity = new TaskUserTaskStarEntity();
                    taskUserTaskStarEntity.setIdTask(UUID.fromString(taskId));
                    taskUserTaskStarEntity.setIdUser(UUID.fromString(idUser));
                    taskUserTaskStarEntity.setTimestampUnix((int) Instant.now().getEpochSecond());
                    this.taskUserTaskStarRepository.save(taskUserTaskStarEntity);
                }
            } else {
                boolean isNoChange = taskUserTaskStarEntities.size() == 0;
                if (!isNoChange) {
                    this.taskUserTaskStarRepository.delete(taskUserTaskStarEntities.get(0));
                }
            }
        }
        if (requestBody.has(RequestBodyKeys.UPDATE_TASK_IS_PIN)) {
            boolean taskIsPin = RequestFormatterCommon.fBoolean(requestBody.get(RequestBodyKeys.UPDATE_TASK_IS_PIN));
            List<TaskUserTaskPinEntity> taskUserTaskPinEntities = this.taskUserTaskPinRepository.findByIdTaskIdUser(taskId, idUser);
            if (taskIsPin) {
                boolean isNoChange = taskUserTaskPinEntities.size() > 0;
                if (!isNoChange) {
                    TaskUserTaskPinEntity taskUserTaskPinEntity = new TaskUserTaskPinEntity();
                    taskUserTaskPinEntity.setIdTask(UUID.fromString(taskId));
                    taskUserTaskPinEntity.setIdUser(UUID.fromString(idUser));
                    taskUserTaskPinEntity.setTimestampUnix((int) Instant.now().getEpochSecond());
                    this.taskUserTaskPinRepository.save(taskUserTaskPinEntity);
                }
            } else {
                boolean isNoChange = taskUserTaskPinEntities.size() == 0;
                if (!isNoChange) {
                    this.taskUserTaskPinRepository.delete(taskUserTaskPinEntities.get(0));
                }
            }
        }
        if (requestBody.has(RequestBodyKeys.UPDATE_TASK_NOTE_TEXT)) {
            String taskNoteText = RequestFormatterCommon.fString(requestBody.get(RequestBodyKeys.UPDATE_TASK_NOTE_TEXT));
            if (!RequestValidatorTask.noteText(taskNoteText)) {
                throw new Exception("Error: invalid input");
            }
            TaskUserTaskNoteEntity taskUserTaskNoteEntity = this.taskUserTaskNoteRepository.findByIdTaskIdUser(taskId, idUser).size() > 0 ?
                this.taskUserTaskNoteRepository.findByIdTaskIdUser(taskId, idUser).get(0) :
                new TaskUserTaskNoteEntity();
            boolean isNoChange = (taskNoteText == null && taskUserTaskNoteEntity.getNoteText() == null) || 
                (taskNoteText != null && taskNoteText.equals(taskUserTaskNoteEntity.getNoteText())
            );
            if (!isNoChange) {
                if (taskUserTaskNoteEntity.getTimestampUnix() == null) {
                    taskUserTaskNoteEntity.setTimestampUnix((int) Instant.now().getEpochSecond());
                }
                taskUserTaskNoteEntity.setIdTask(UUID.fromString(taskId));
                taskUserTaskNoteEntity.setIdUser(UUID.fromString(idUser));
                taskUserTaskNoteEntity.setNoteText(taskNoteText);
                if (taskNoteText == null) {
                    taskUserTaskNoteEntity.setTimestampNoteText(null);
                } else {
                    taskUserTaskNoteEntity.setTimestampNoteText((int) Instant.now().getEpochSecond());
                }
                if (taskUserTaskNoteEntity.getNoteText() == null && taskUserTaskNoteEntity.getIdNoteImageKey() == null) {
                    this.taskUserTaskNoteRepository.delete(taskUserTaskNoteEntity);
                } else {
                    this.taskUserTaskNoteRepository.save(taskUserTaskNoteEntity);
                }
            }
        }
        if (requestBody.has(RequestBodyKeys.UPDATE_TASK_NOTE_IMAGE)) {
            String taskNoteImageAsBase64String = RequestFormatterCommon.fString(requestBody.get(RequestBodyKeys.UPDATE_TASK_NOTE_IMAGE));
            if (!RequestValidatorTask.imageAsBase64String(taskNoteImageAsBase64String)) {
                throw new Exception("Error: invalid input");
            }
            TaskUserTaskNoteEntity taskUserTaskNoteEntity = this.taskUserTaskNoteRepository.findByIdTaskIdUser(taskId, idUser).size() > 0 ?
                this.taskUserTaskNoteRepository.findByIdTaskIdUser(taskId, idUser).get(0) :
                new TaskUserTaskNoteEntity();
            boolean isNoChange = taskNoteImageAsBase64String == null && taskEntity.getIdImageKey() == null;
            if (!isNoChange) {
                if (taskUserTaskNoteEntity.getTimestampUnix() == null) {
                    taskUserTaskNoteEntity.setTimestampUnix((int) Instant.now().getEpochSecond());
                }
                taskUserTaskNoteEntity.setIdTask(UUID.fromString(taskId));
                taskUserTaskNoteEntity.setIdUser(UUID.fromString(idUser));
                if (taskNoteImageAsBase64String == null) {
                    taskUserTaskNoteEntity.setIdNoteImageKey(null);
                    taskUserTaskNoteEntity.setTimestampNoteImage(null);
                } else {
                    UUID taskNoteImageKeyId = UUID.randomUUID();
                    byte[] imageAsByteArray = java.util.Base64.getDecoder().decode(taskNoteImageAsBase64String.substring(taskNoteImageAsBase64String.indexOf(",") + 1));
                    RequestBody imageAsByteArrayRequestBody = RequestBody.fromBytes(imageAsByteArray);
                    Utilities.putObjectInS3(Constants.S3_FOLDER_NAME_TASK_NOTE_IMAGE, taskNoteImageKeyId, imageAsByteArrayRequestBody);
                    taskUserTaskNoteEntity.setIdNoteImageKey(taskNoteImageKeyId);
                    taskUserTaskNoteEntity.setTimestampNoteImage((int) Instant.now().getEpochSecond());
                }
                if (taskUserTaskNoteEntity.getNoteText() == null && taskUserTaskNoteEntity.getIdNoteImageKey() == null) {
                    this.taskUserTaskNoteRepository.delete(taskUserTaskNoteEntity);
                } else {
                    this.taskUserTaskNoteRepository.save(taskUserTaskNoteEntity);
                }
            }
        }

        boolean taskIsComplete = this.taskUserTaskCompleteRepository.findByIdTaskIdUser(taskId, idUser).size() > 0;
        boolean taskIsStar = this.taskUserTaskStarRepository.findByIdTaskIdUser(taskId, idUser).size() > 0;
        boolean taskIsPin = this.taskUserTaskPinRepository.findByIdTaskIdUser(taskId, idUser).size() > 0;
        Integer taskTimestampComplete = this.taskUserTaskCompleteRepository.findByIdTaskIdUser(taskId, idUser).size() > 0 ? 
            this.taskUserTaskCompleteRepository.findByIdTaskIdUser(taskId, idUser).get(0).getTimestampUnix() :
            null;
        String noteText = this.taskUserTaskNoteRepository.findByIdTaskIdUser(taskId, idUser).size() > 0 ? 
            this.taskUserTaskNoteRepository.findByIdTaskIdUser(taskId, idUser).get(0).getNoteText() :
            null;
        UUID noteImageKey = this.taskUserTaskNoteRepository.findByIdTaskIdUser(taskId, idUser).size() > 0 ? 
            this.taskUserTaskNoteRepository.findByIdTaskIdUser(taskId, idUser).get(0).getIdNoteImageKey() :
            null;
        List<UserBubbleModel> userBubblesTaskComplete = this.getUserBubblesTaskComplete(taskId.toString(), idUser);
        ZoneId userTimeZoneZoneId = Utilities.getUserTimeZoneZoneId(idUser, this.userRepository);
        Set<UUID> podIdsUserIsMemberOf = new HashSet<>(this.podRepository.getPodIdsUserIsMemberOf(idUser));
        TaskModel task = TaskModel.builder()
            .id(UUID.fromString(taskId))
            .name(taskEntity.getName())
            .description(taskEntity.getDescription())
            .imageLink(Utilities.getPresignedUrl(Constants.S3_FOLDER_NAME_TASK_IMAGE, taskEntity.getIdImageKey()))
            .numberOfPoints(taskEntity.getNumberOfPoints())
            .idPod(taskEntity.getIdPod())
            .isComplete(taskIsComplete)
            .isStar(taskIsStar)
            .isPin(taskIsPin)
            .noteText(noteText)
            .noteImageLink(Utilities.getPresignedUrl(Constants.S3_FOLDER_NAME_TASK_NOTE_IMAGE, noteImageKey))
            .datetimeCreate(Utilities.getDatetimeDateOnlyFromTimestamp(userTimeZoneZoneId, taskEntity.getTimestampUnix()))
            .datetimeUpdate(Utilities.getDatetimeDateOnlyFromTimestamp(userTimeZoneZoneId, taskEntity.getTimestampUpdate()))
            .datetimeTarget(taskEntity.getDatetimeTarget())
            .datetimeComplete(Utilities.getDatetimeDateOnlyFromTimestamp(userTimeZoneZoneId, taskTimestampComplete))
            .userBubblesTaskComplete(userBubblesTaskComplete.subList(0, Math.min(userBubblesTaskComplete.size(), 3)))
            .userBubblesTaskCompleteTotalNumber(userBubblesTaskComplete.size())
            .isMemberOfTaskPod(taskEntity.getIdPod() == null || podIdsUserIsMemberOf.contains(taskEntity.getIdPod()))
            .build();
        return task;
    }

    public void createTask(
            @RequestParam String idUser,
            JsonNode requestBody
    ) throws Exception {
       if (
           idUser == null ||
           !requestBody.has(RequestBodyKeys.CREATE_TASK_NAME) ||
           !requestBody.has(RequestBodyKeys.CREATE_TASK_NUMBER_OF_POINTS)
       ) {
           throw new Exception("Error: invalid input"); // TODO create an exception class
       }
       String taskIdUserCreate = idUser;
       String taskName = RequestFormatterCommon.fString(requestBody.get(RequestBodyKeys.CREATE_TASK_NAME));
       int taskNumberOfPoints = requestBody.get(RequestBodyKeys.CREATE_TASK_NUMBER_OF_POINTS).asInt();
       if (
           !RequestValidatorTask.id(taskIdUserCreate) ||
           !RequestValidatorTask.name(taskName) ||
           !RequestValidatorTask.numberOfPoints(taskNumberOfPoints)
       ) {
           throw new Exception("Error: invalid input"); // TODO create an exception class
       }
       // save processed input
       TaskEntity taskEntity = new TaskEntity();
       taskEntity.setIdUserCreate(UUID.fromString(idUser));
       taskEntity.setTimestampUnix((int) Instant.now().getEpochSecond());
       taskEntity.setName(taskName);
       taskEntity.setNumberOfPoints(taskNumberOfPoints);
       if (requestBody.has(RequestBodyKeys.CREATE_TASK_DESCRIPTION)) {
           String taskDescription = RequestFormatterCommon.fString(requestBody.get(RequestBodyKeys.CREATE_TASK_DESCRIPTION));
           if (!RequestValidatorTask.description(taskDescription)) {
               throw new Exception("Error: invalid input");
           }
           taskEntity.setDescription(taskDescription);
       }
       if (requestBody.has(RequestBodyKeys.CREATE_TASK_ID_POD)) {
           String taskIdPod = RequestFormatterCommon.fUUID(requestBody.get(RequestBodyKeys.CREATE_TASK_ID_POD));
           if (!RequestValidatorTask.description(taskIdPod)) {
               throw new Exception("Error: invalid input");
           }
           taskEntity.setIdPod(UUID.fromString(taskIdPod));
       }
       if (requestBody.has(RequestBodyKeys.CREATE_TASK_DATETIME_TARGET)) {
           String taskDatetimeTarget = RequestFormatterCommon.fString(requestBody.get(RequestBodyKeys.CREATE_TASK_DATETIME_TARGET));
           if (!RequestValidatorTask.datetimeTarget(taskDatetimeTarget)) {
               throw new Exception("Error: invalid input");
           }
           taskEntity.setDatetimeTarget(taskDatetimeTarget);
       }
       this.taskRepository.save(taskEntity);
    }

    private Page<TaskModel> _getTaskModelFromSharedAndIndividualProperties(
        String idUser,
        List<TaskSharedPropertiesDTO> tasks_SharedProperties,
        List<TaskIndividualPropertiesDTO> tasks_IndividualProperties,
        boolean filterIsCompleteIndividual,
        boolean filterIsNotCompleteIndividual,
        boolean filterIsStarIndividual,
        boolean filterIsNotStarIndividual,
        boolean filterIsPinIndividual,
        boolean filterIsNotPinIndividual,
        Pageable pageable
    ) {
        HashMap<UUID, TaskSharedPropertiesDTO> idTaskToTaskSharedPropertiesDTOMap = new HashMap<>();
        HashMap<UUID, TaskIndividualPropertiesDTO> idTaskToTaskIndividualPropertiesDTOMap = new HashMap<>();

        for (int i = 0; i < tasks_SharedProperties.size(); i++) {
            idTaskToTaskSharedPropertiesDTOMap.put(tasks_SharedProperties.get(i).getId(), tasks_SharedProperties.get(i));
        }
        for (int i = 0; i < tasks_IndividualProperties.size(); i++) {
            idTaskToTaskIndividualPropertiesDTOMap.put(tasks_IndividualProperties.get(i).getId(), tasks_IndividualProperties.get(i));
        }
        List<TaskModel> taskList = new ArrayList<>();
        ZoneId userTimeZoneZoneId = Utilities.getUserTimeZoneZoneId(idUser, this.userRepository);
        for (UUID idTask : idTaskToTaskSharedPropertiesDTOMap.keySet()) {
            TaskSharedPropertiesDTO taskSharedPropertiesEntry = idTaskToTaskSharedPropertiesDTOMap.get(idTask);
            TaskIndividualPropertiesDTO taskIndividualPropertiesEntry = idTaskToTaskIndividualPropertiesDTOMap.get(idTask);
            Set<UUID> podIdsUserIsMemberOf = new HashSet<>(this.podRepository.getPodIdsUserIsMemberOf(idUser));
            TaskModel task = TaskModel.builder()
                        .id(taskSharedPropertiesEntry.getId())
                        .name(taskSharedPropertiesEntry.getName())
                        .description(taskSharedPropertiesEntry.getDescription())
                        .imageLink(Utilities.getPresignedUrl(Constants.S3_FOLDER_NAME_TASK_IMAGE, taskSharedPropertiesEntry.getIdImageKey()))
                        .numberOfPoints(taskSharedPropertiesEntry.getNumberOfPoints())
                        .idPod(taskSharedPropertiesEntry.getIdPod())
                        .isComplete(taskIndividualPropertiesEntry.isComplete())
                        .isStar(taskIndividualPropertiesEntry.isStar())
                        .isPin(taskIndividualPropertiesEntry.isPin())
                        .noteText(taskIndividualPropertiesEntry.getNoteText())
                        .noteImageLink(Utilities.getPresignedUrl(Constants.S3_FOLDER_NAME_TASK_NOTE_IMAGE, taskIndividualPropertiesEntry.getIdNoteImageKey()))
                        .datetimeCreate(Utilities.getDatetimeDateOnlyFromTimestamp(userTimeZoneZoneId, taskSharedPropertiesEntry.getTimestampUnix()))
                        .datetimeUpdate(Utilities.getDatetimeDateOnlyFromTimestamp(userTimeZoneZoneId, taskSharedPropertiesEntry.getTimestampUpdate()))
                        .datetimeTarget(taskSharedPropertiesEntry.getDatetimeTarget())
                        .datetimeComplete(Utilities.getDatetimeDateOnlyFromTimestamp(userTimeZoneZoneId, taskIndividualPropertiesEntry.getTimestampComplete()))
                        .isMemberOfTaskPod(taskSharedPropertiesEntry.getIdPod() == null || podIdsUserIsMemberOf.contains(taskSharedPropertiesEntry.getIdPod()))
                        .build();
            taskList.add(task);
        }

        // filter
        Set<UUID> podIdsUserIsMemberOf = new HashSet<>(this.podRepository.getPodIdsUserIsMemberOf(idUser));
        Set<UUID> podIdsPublicPod = new HashSet<>(this.podRepository.getPodIdsPublicPod());
        taskList = taskList.stream().filter(task -> {
            boolean isUserAllowedAccessViewTask = task.getIdPod() == null;
            if (task.getIdPod() != null) {
                isUserAllowedAccessViewTask = podIdsPublicPod.contains(task.getIdPod()) || podIdsUserIsMemberOf.contains(task.getIdPod());
            }
            return isUserAllowedAccessViewTask &&
                (task.isComplete() == filterIsCompleteIndividual || task.isComplete() != filterIsNotCompleteIndividual) &&
                (task.isStar() == filterIsStarIndividual || task.isStar() != filterIsNotStarIndividual) &&
                (task.isPin() == filterIsPinIndividual || task.isPin() != filterIsNotPinIndividual);
        }).collect(Collectors.toList());

        for (TaskModel taskModel : taskList) {
            UUID taskId = taskModel.getId();
            if (taskModel.getIdPod() != null) {
                List<UserBubbleModel> userBubblesTaskComplete = this.getUserBubblesTaskComplete(taskId.toString(), idUser);
                taskModel.setUserBubblesTaskComplete(userBubblesTaskComplete.subList(0, Math.min(userBubblesTaskComplete.size(), 3)));
                taskModel.setUserBubblesTaskCompleteTotalNumber(userBubblesTaskComplete.size());
            }
        }
        // sort
        Collections.sort(taskList, new Comparator<TaskModel>() {
            public int compare(TaskModel task1, TaskModel task2) {
                if (task1.isComplete() == true && task2.isComplete() == false) {
                    return 1;
                } else if (task1.isComplete() == false && task2.isComplete() == true) {
                    return -1;
                }
                Integer task1TimestampCreate = idTaskToTaskSharedPropertiesDTOMap.get(task1.getId()).getTimestampUnix();
                Integer task1TimestampUpdate = idTaskToTaskSharedPropertiesDTOMap.get(task1.getId()).getTimestampUpdate();
                Integer task2TimestampCreate = idTaskToTaskSharedPropertiesDTOMap.get(task2.getId()).getTimestampUnix();
                Integer task2TimestampUpdate = idTaskToTaskSharedPropertiesDTOMap.get(task2.getId()).getTimestampUpdate();
                int task1TimestampCreateOrUpdate = task1TimestampUpdate != null ? task1TimestampUpdate : task1TimestampCreate;
                int task2TimestampCreateOrUpdate = task2TimestampUpdate != null ? task2TimestampUpdate : task2TimestampCreate;
                return task2TimestampCreateOrUpdate - task1TimestampCreateOrUpdate;
            }
        });

        Page<TaskModel> tasksPage = new PageImpl<TaskModel>(taskList, pageable, taskList.size());
        return tasksPage;
    }


    public void deleteTask(
            String idUser,
            JsonNode requestBody
    ) throws Exception {
        if (
            idUser == null ||
            !requestBody.has(RequestBodyKeys.UPDATE_TASK_ID)
        ) {
            throw new Exception("Error: invalid input"); // TODO create an exception class
        }
        String taskId = RequestFormatterCommon.fUUID(requestBody.get(RequestBodyKeys.UPDATE_TASK_ID));
        if (
            !RequestValidatorTask.id(taskId)
        ) {
            throw new Exception("Error: invalid input"); // TODO create an exception class
        }

        Optional<TaskEntity> taskEntityOptional = this.taskRepository.findById(UUID.fromString(taskId));
        if (!taskEntityOptional.isPresent()) {
            throw new Exception("Error: invalid input");
        }
        TaskEntity taskEntity = taskEntityOptional.get();
        taskEntity.setArchived(true);
        this.taskRepository.save(taskEntity);
    }

    public void updateTaskReaction(
        @RequestParam String idUser,
        JsonNode requestBody
    ) throws Exception {
        if (
            idUser == null ||
            !requestBody.has(RequestBodyKeys.UPDATE_TASK_REACTION_ID_TASK)
        ) {
            throw new Exception("Error: invalid input"); // TODO create an exception class
        }
        String idTask = RequestFormatterCommon.fUUID(requestBody.get(RequestBodyKeys.UPDATE_TASK_REACTION_ID_TASK));
        String reactionType = RequestFormatterCommon.fString(requestBody.get(RequestBodyKeys.UPDATE_TASK_REACTION_REACTION_TYPE));
        if (
            !RequestValidatorUser.id(idUser) ||
            !RequestValidatorTask.id(idTask) ||
            !RequestValidatorTask.reactionType(reactionType)
        ) {
            throw new Exception("Error: invalid input"); // TODO create an exception class
        }
        List<TaskUserTaskReactionEntity> taskUserTaskReactionEntityList = this.taskUserTaskReactionRepository.findByIdTaskIdUser(idTask, idUser);
        if (taskUserTaskReactionEntityList.size() > 0) {
            TaskUserTaskReactionEntity taskUserTaskReactionEntity = taskUserTaskReactionEntityList.get(0);
            if (reactionType == null) {
                this.taskUserTaskReactionRepository.delete(taskUserTaskReactionEntity);
            } else {
                taskUserTaskReactionEntity.setReactionType(reactionType);
                this.taskUserTaskReactionRepository.save(taskUserTaskReactionEntity);
            }
        } else {
            if (reactionType == null) {
                return;
            } else {
                TaskUserTaskReactionEntity taskUserTaskReactionEntity = new TaskUserTaskReactionEntity();
                taskUserTaskReactionEntity.setIdTask(UUID.fromString(idTask));
                taskUserTaskReactionEntity.setIdUser(UUID.fromString(idUser));
                taskUserTaskReactionEntity.setReactionType(reactionType);
                taskUserTaskReactionEntity.setTimestampUnix((int) Instant.now().getEpochSecond());
                this.taskUserTaskReactionRepository.save(taskUserTaskReactionEntity);
            }
        }
    }

    public void updateTaskCommentReaction(
        @RequestParam String idUser,
        JsonNode requestBody
    ) throws Exception {
        if (
            idUser == null ||
            !requestBody.has(RequestBodyKeys.UPDATE_TASK_COMMENT_REACTION_ID_TASK_COMMENT)
        ) {
            throw new Exception("Error: invalid input"); // TODO create an exception class
        }
        String idTaskComment = RequestFormatterCommon.fUUID(requestBody.get(RequestBodyKeys.UPDATE_TASK_COMMENT_REACTION_ID_TASK_COMMENT));
        String reactionType = RequestFormatterCommon.fString(requestBody.get(RequestBodyKeys.UPDATE_TASK_REACTION_REACTION_TYPE));
        if (
            !RequestValidatorUser.id(idUser) ||
            !RequestValidatorTask.idTaskComment(idTaskComment) ||
            !RequestValidatorTask.reactionType(reactionType)
        ) {
            throw new Exception("Error: invalid input"); // TODO create an exception class
        }
        List<TaskUserTaskCommentReactionEntity> taskUserTaskCommentReactionEntityList = this.taskUserTaskCommentReactionRepository.findByIdTaskCommentIdUser(idTaskComment, idUser);
        if (taskUserTaskCommentReactionEntityList.size() > 0) {
            TaskUserTaskCommentReactionEntity taskUserTaskCommentReactionEntity = taskUserTaskCommentReactionEntityList.get(0);
            if (reactionType == null) {
                this.taskUserTaskCommentReactionRepository.delete(taskUserTaskCommentReactionEntity);
            } else {
                taskUserTaskCommentReactionEntity.setReactionType(reactionType);
                this.taskUserTaskCommentReactionRepository.save(taskUserTaskCommentReactionEntity);
            }
        } else {
            if (reactionType == null) {
                return;
            } else {
                TaskUserTaskCommentReactionEntity taskUserTaskCommentReactionEntity = new TaskUserTaskCommentReactionEntity();
                taskUserTaskCommentReactionEntity.setIdTaskComment(UUID.fromString(idTaskComment));
                taskUserTaskCommentReactionEntity.setIdUser(UUID.fromString(idUser));
                taskUserTaskCommentReactionEntity.setReactionType(reactionType);
                taskUserTaskCommentReactionEntity.setTimestampUnix((int) Instant.now().getEpochSecond());
                this.taskUserTaskCommentReactionRepository.save(taskUserTaskCommentReactionEntity);
            }
        }
    }

    public void updateTaskCommentReplyReaction(
        @RequestParam String idUser,
        JsonNode requestBody
    ) throws Exception {
        if (
            idUser == null ||
            !requestBody.has(RequestBodyKeys.UPDATE_TASK_COMMENT_REPLY_REACTION_ID_TASK_COMMENT_REPLY)
        ) {
            throw new Exception("Error: invalid input"); // TODO create an exception class
        }
        String idTaskCommentReply = RequestFormatterCommon.fUUID(requestBody.get(RequestBodyKeys.UPDATE_TASK_COMMENT_REPLY_REACTION_ID_TASK_COMMENT_REPLY));
        String reactionType = RequestFormatterCommon.fString(requestBody.get(RequestBodyKeys.UPDATE_TASK_REACTION_REACTION_TYPE));
        if (
            !RequestValidatorUser.id(idUser) ||
            !RequestValidatorTask.idTaskCommentReply(idTaskCommentReply) ||
            !RequestValidatorTask.reactionType(reactionType)
        ) {
            throw new Exception("Error: invalid input"); // TODO create an exception class
        }
        List<TaskUserTaskCommentReplyReactionEntity> taskUserTaskCommentReplyReactionEntityList = this.taskUserTaskCommentReplyReactionRepository.findByIdTaskCommentReplyIdUser(idTaskCommentReply, idUser);
        if (taskUserTaskCommentReplyReactionEntityList.size() > 0) {
            TaskUserTaskCommentReplyReactionEntity taskUserTaskCommentReplyReactionEntity = taskUserTaskCommentReplyReactionEntityList.get(0);
            if (reactionType == null) {
                this.taskUserTaskCommentReplyReactionRepository.delete(taskUserTaskCommentReplyReactionEntity);
            } else {
                taskUserTaskCommentReplyReactionEntity.setReactionType(reactionType);
                this.taskUserTaskCommentReplyReactionRepository.save(taskUserTaskCommentReplyReactionEntity);
            }
        } else {
            if (reactionType == null) {
                return;
            } else {
                TaskUserTaskCommentReplyReactionEntity taskUserTaskCommentReplyReactionEntity = new TaskUserTaskCommentReplyReactionEntity();
                taskUserTaskCommentReplyReactionEntity.setIdTaskCommentReply(UUID.fromString(idTaskCommentReply));
                taskUserTaskCommentReplyReactionEntity.setIdUser(UUID.fromString(idUser));
                taskUserTaskCommentReplyReactionEntity.setReactionType(reactionType);
                taskUserTaskCommentReplyReactionEntity.setTimestampUnix((int) Instant.now().getEpochSecond());
                this.taskUserTaskCommentReplyReactionRepository.save(taskUserTaskCommentReplyReactionEntity);
            }
        }
    }

    public void createTaskComment(
        @RequestParam String idUser,
        JsonNode requestBody
    ) throws Exception {
        if (
            idUser == null ||
            !requestBody.has(RequestBodyKeys.CREATE_TASK_COMMENT_ID_TASK)
        ) {
            throw new Exception("Error: invalid input"); // TODO create an exception class
        }
        String idTask = RequestFormatterCommon.fUUID(requestBody.get(RequestBodyKeys.CREATE_TASK_COMMENT_ID_TASK));
        if (
            !RequestValidatorUser.id(idUser) ||
            !RequestValidatorTask.id(idTask)
        ) {
            throw new Exception("Error: invalid input"); // TODO create an exception class
        }
        if (requestBody.has(RequestBodyKeys.CREATE_TASK_COMMENT_COMMENT_TEXT)) {
            String commentText = RequestFormatterCommon.fString(requestBody.get(RequestBodyKeys.CREATE_TASK_COMMENT_COMMENT_TEXT));
            if (!RequestValidatorTask.commentText(commentText)) {
                throw new Exception("Error: invalid input");
            }
            TaskUserTaskCommentEntity taskUserTaskCommentEntity = new TaskUserTaskCommentEntity();
            taskUserTaskCommentEntity.setTimestampUnix((int) Instant.now().getEpochSecond());
            taskUserTaskCommentEntity.setIdTask(UUID.fromString(idTask));
            taskUserTaskCommentEntity.setIdUser(UUID.fromString(idUser));
            taskUserTaskCommentEntity.setText(true);
            taskUserTaskCommentEntity.setCommentText(commentText);
            this.taskUserTaskCommentRepository.save(taskUserTaskCommentEntity);
        }
        if (requestBody.has(RequestBodyKeys.CREATE_TASK_COMMENT_COMMENT_IMAGE)) {
            String commentImageAsBase64String = RequestFormatterCommon.fString(requestBody.get(RequestBodyKeys.CREATE_TASK_COMMENT_COMMENT_IMAGE));
            if (!RequestValidatorTask.commentImageAsBase64String(commentImageAsBase64String)) {
                throw new Exception("Error: invalid input");
            }
            TaskUserTaskCommentEntity taskUserTaskCommentEntity = new TaskUserTaskCommentEntity();
            taskUserTaskCommentEntity.setTimestampUnix((int) Instant.now().getEpochSecond());
            taskUserTaskCommentEntity.setIdTask(UUID.fromString(idTask));
            taskUserTaskCommentEntity.setIdUser(UUID.fromString(idUser));
            taskUserTaskCommentEntity.setImage(true);
            UUID taskCommentImageKeyId = UUID.randomUUID();
            byte[] imageAsByteArray = java.util.Base64.getDecoder().decode(commentImageAsBase64String.substring(commentImageAsBase64String.indexOf(",") + 1));
            RequestBody imageAsByteArrayRequestBody = RequestBody.fromBytes(imageAsByteArray);
            Utilities.putObjectInS3(Constants.S3_FOLDER_NAME_TASK_COMMENT_IMAGE, taskCommentImageKeyId, imageAsByteArrayRequestBody);
            taskUserTaskCommentEntity.setIdCommentImageKey(taskCommentImageKeyId);
            this.taskUserTaskCommentRepository.save(taskUserTaskCommentEntity);
        }
    }

    public void createTaskCommentReply(
        @RequestParam String idUser,
        JsonNode requestBody
    ) throws Exception {
        if (
            idUser == null ||
            !requestBody.has(RequestBodyKeys.CREATE_TASK_COMMENT_REPLY_ID_TASK_COMMENT)
        ) {
            throw new Exception("Error: invalid input"); // TODO create an exception class
        }
        String idTaskComment = RequestFormatterCommon.fUUID(requestBody.get(RequestBodyKeys.CREATE_TASK_COMMENT_REPLY_ID_TASK_COMMENT));
        if (
            !RequestValidatorUser.id(idUser) ||
            !RequestValidatorTask.idTaskComment(idTaskComment)
        ) {
            throw new Exception("Error: invalid input"); // TODO create an exception class
        }
        if (requestBody.has(RequestBodyKeys.CREATE_TASK_COMMENT_REPLY_COMMENT_TEXT)) {
            String commentReplyText = RequestFormatterCommon.fString(requestBody.get(RequestBodyKeys.CREATE_TASK_COMMENT_REPLY_COMMENT_TEXT));
            if (!RequestValidatorTask.commentReplyText(commentReplyText)) {
                throw new Exception("Error: invalid input");
            }
            TaskUserTaskCommentReplyEntity taskUserTaskCommentReplyEntity = new TaskUserTaskCommentReplyEntity();
            taskUserTaskCommentReplyEntity.setTimestampUnix((int) Instant.now().getEpochSecond());
            taskUserTaskCommentReplyEntity.setIdTaskComment(UUID.fromString(idTaskComment));
            taskUserTaskCommentReplyEntity.setIdUser(UUID.fromString(idUser));
            taskUserTaskCommentReplyEntity.setText(true);
            taskUserTaskCommentReplyEntity.setCommentReplyText(commentReplyText);
            this.taskUserTaskCommentReplyRepository.save(taskUserTaskCommentReplyEntity);
        }
        if (requestBody.has(RequestBodyKeys.CREATE_TASK_COMMENT_REPLY_COMMENT_IMAGE)) {
            String commentReplyImageAsBase64String = RequestFormatterCommon.fString(requestBody.get(RequestBodyKeys.CREATE_TASK_COMMENT_REPLY_COMMENT_IMAGE));
            if (!RequestValidatorTask.commentReplyImageAsBase64String(commentReplyImageAsBase64String)) {
                throw new Exception("Error: invalid input");
            }
            TaskUserTaskCommentReplyEntity taskUserTaskCommentReplyEntity = new TaskUserTaskCommentReplyEntity();
            taskUserTaskCommentReplyEntity.setTimestampUnix((int) Instant.now().getEpochSecond());
            taskUserTaskCommentReplyEntity.setIdTaskComment(UUID.fromString(idTaskComment));
            taskUserTaskCommentReplyEntity.setIdUser(UUID.fromString(idUser));
            taskUserTaskCommentReplyEntity.setImage(true);
            UUID taskCommentImageKeyId = UUID.randomUUID();
            byte[] imageAsByteArray = java.util.Base64.getDecoder().decode(commentReplyImageAsBase64String.substring(commentReplyImageAsBase64String.indexOf(",") + 1));
            RequestBody imageAsByteArrayRequestBody = RequestBody.fromBytes(imageAsByteArray);
            Utilities.putObjectInS3(Constants.S3_FOLDER_NAME_TASK_COMMENT_REPLY_IMAGE, taskCommentImageKeyId, imageAsByteArrayRequestBody);
            taskUserTaskCommentReplyEntity.setIdCommentReplyImageKey(taskCommentImageKeyId);
            this.taskUserTaskCommentReplyRepository.save(taskUserTaskCommentReplyEntity);
        }
    }
    
    public ReactionsModel getTaskReactions(
        @RequestParam String idUser,
        JsonNode requestBody
    ) throws Exception {
        if (
            idUser == null ||
            !requestBody.has(RequestBodyKeys.GET_TASK_REACTIONS_ID_TASK) ||
            !requestBody.has(RequestBodyKeys.GET_TASK_REACTIONS_NUMBER_OF_REACTIONS_LIMIT)
        ) {
            throw new Exception("Error: invalid input"); // TODO create an exception class
        }
        String idTask = RequestFormatterCommon.fUUID(requestBody.get(RequestBodyKeys.GET_TASK_REACTIONS_ID_TASK));
        Integer numberOfReactionsLimit = RequestFormatterCommon.fInteger(requestBody.get(RequestBodyKeys.GET_TASK_REACTIONS_NUMBER_OF_REACTIONS_LIMIT));
        if (
            !RequestValidatorUser.id(idUser) ||
            !RequestValidatorTask.id(idTask) ||
            !RequestValidatorTask.numberOfReactionsLimit(numberOfReactionsLimit)
        ) {
            throw new Exception("Error: invalid input"); // TODO create an exception class
        }
        Set<UUID> userIdsFollowedByGivenUser = new HashSet<>(this.userRepository.getUserIdsFollowedByGivenUser(idUser));
        Set<UUID> userIds_FollowRequestSentByGivenUser_NotYetAccepted = new HashSet<>(this.userRepository.getUserIds_FollowRequestSentByGivenUser_NotYetAccepted(idUser));
        ZoneId userTimeZoneZoneId = Utilities.getUserTimeZoneZoneId(idUser, this.userRepository);
        List<UserBubbleReactionModel> userBubblesReactionTask = this.taskRepository.getUserBubblesReactionTask(idTask).stream().map(userBubbleReactionTaskReactionDTO -> {
            UserEntity userEntityOfReaction = this.userRepository.findById(userBubbleReactionTaskReactionDTO.getIdUser()).get();
            return UserBubbleReactionModel.builder()
                .id(userEntityOfReaction.getId())
                .name(userEntityOfReaction.getName())
                .username(userEntityOfReaction.getUsername())
                .imageLink(Utilities.getPresignedUrl(Constants.S3_FOLDER_NAME_USER_IMAGE, userEntityOfReaction.getIdImageKey()))
                .timestampToSortBy(userBubbleReactionTaskReactionDTO.getTimestampToSortBy())
                .datetimeDateOnlyLabel(Utilities.getDatetimeDateOnlyFromTimestamp(userTimeZoneZoneId, userBubbleReactionTaskReactionDTO.getTimestampToSortBy()))
                .datetimeDateAndTimeLabel(Utilities.getDatetimeDateAndTimeFromTimestamp(userTimeZoneZoneId, userBubbleReactionTaskReactionDTO.getTimestampToSortBy()))
                .isFollowedByMe(userIdsFollowedByGivenUser.contains(userBubbleReactionTaskReactionDTO.getIdUser()))
                .isFollowRequestSentNotYetAccepted(userIds_FollowRequestSentByGivenUser_NotYetAccepted.contains(userBubbleReactionTaskReactionDTO.getIdUser()))
                .isMe(idUser.equals(userBubbleReactionTaskReactionDTO.getIdUser().toString()))
                .reactionType(userBubbleReactionTaskReactionDTO.getReactionType())
                .build();
        }).collect(Collectors.toList());
        Collections.sort(userBubblesReactionTask, new Comparator<UserBubbleReactionModel>() {
            public int compare(UserBubbleReactionModel userBubbleReaction1, UserBubbleReactionModel userBubbleReaction2) {
                if (userBubbleReaction1.isMe() == true && userBubbleReaction2.isMe() == false) {
                    return -1;
                } else if (userBubbleReaction1.isMe() == false && userBubbleReaction2.isMe() == true) {
                    return 1;
                }
                if (userBubbleReaction1.isFollowedByMe() == true && userBubbleReaction2.isFollowedByMe() == false) {
                    return -1;
                } else if (userBubbleReaction1.isFollowedByMe() == false && userBubbleReaction2.isFollowedByMe() == true) {
                    return 1;
                }
                return userBubbleReaction1.getUsername().compareTo(userBubbleReaction2.getUsername());
            }
        });
        String myReactionTask = this.taskUserTaskReactionRepository.findByIdTaskIdUser(idTask, idUser).size() == 0 ? 
            null : 
            this.taskUserTaskReactionRepository.findByIdTaskIdUser(idTask, idUser).get(0).getReactionType();
        return ReactionsModel.builder()
            .idReactionTargetEntity(idTask)
            .userBubblesReaction(
                numberOfReactionsLimit == null || numberOfReactionsLimit <= 0 ?
                userBubblesReactionTask :
                userBubblesReactionTask.subList(0, Math.min(userBubblesReactionTask.size(), numberOfReactionsLimit))
            )
            .userBubblesReactionTotalNumber(userBubblesReactionTask.size())
            .myReactionType(myReactionTask)
            .build();
    }

    public ReactionsModel getTaskCommentReactions(
        @RequestParam String idUser,
        JsonNode requestBody
    ) throws Exception {
        if (
            idUser == null ||
            !requestBody.has(RequestBodyKeys.GET_TASK_COMMENT_REACTIONS_ID_TASK_COMMENT) ||
            !requestBody.has(RequestBodyKeys.GET_TASK_COMMENT_REACTIONS_NUMBER_OF_REACTIONS_LIMIT)
        ) {
            throw new Exception("Error: invalid input"); // TODO create an exception class
        }
        String idTaskComment = RequestFormatterCommon.fUUID(requestBody.get(RequestBodyKeys.GET_TASK_COMMENT_REACTIONS_ID_TASK_COMMENT));
        Integer numberOfReactionsLimit = RequestFormatterCommon.fInteger(requestBody.get(RequestBodyKeys.GET_TASK_COMMENT_REACTIONS_NUMBER_OF_REACTIONS_LIMIT));
        if (
            !RequestValidatorUser.id(idUser) ||
            !RequestValidatorTask.id(idTaskComment) ||
            !RequestValidatorTask.numberOfReactionsLimit(numberOfReactionsLimit)
        ) {
            throw new Exception("Error: invalid input"); // TODO create an exception class
        }
        Set<UUID> userIdsFollowedByGivenUser = new HashSet<>(this.userRepository.getUserIdsFollowedByGivenUser(idUser));
        Set<UUID> userIds_FollowRequestSentByGivenUser_NotYetAccepted = new HashSet<>(this.userRepository.getUserIds_FollowRequestSentByGivenUser_NotYetAccepted(idUser));
        ZoneId userTimeZoneZoneId = Utilities.getUserTimeZoneZoneId(idUser, this.userRepository);
        List<UserBubbleReactionModel> userBubblesReactionTaskComment = this.taskRepository.getUserBubblesReactionTaskComment(idTaskComment).stream().map(userBubbleReactionTaskReactionDTO -> {
            UserEntity userEntityOfReaction = this.userRepository.findById(userBubbleReactionTaskReactionDTO.getIdUser()).get();
            return UserBubbleReactionModel.builder()
                .id(userEntityOfReaction.getId())
                .name(userEntityOfReaction.getName())
                .username(userEntityOfReaction.getUsername())
                .imageLink(Utilities.getPresignedUrl(Constants.S3_FOLDER_NAME_USER_IMAGE, userEntityOfReaction.getIdImageKey()))
                .timestampToSortBy(userBubbleReactionTaskReactionDTO.getTimestampToSortBy())
                .datetimeDateOnlyLabel(Utilities.getDatetimeDateOnlyFromTimestamp(userTimeZoneZoneId, userBubbleReactionTaskReactionDTO.getTimestampToSortBy()))
                .datetimeDateAndTimeLabel(Utilities.getDatetimeDateAndTimeFromTimestamp(userTimeZoneZoneId, userBubbleReactionTaskReactionDTO.getTimestampToSortBy()))
                .isFollowedByMe(userIdsFollowedByGivenUser.contains(userBubbleReactionTaskReactionDTO.getIdUser()))
                .isFollowRequestSentNotYetAccepted(userIds_FollowRequestSentByGivenUser_NotYetAccepted.contains(userBubbleReactionTaskReactionDTO.getIdUser()))
                .isMe(idUser.equals(userBubbleReactionTaskReactionDTO.getIdUser().toString()))
                .reactionType(userBubbleReactionTaskReactionDTO.getReactionType())
                .build();
        }).collect(Collectors.toList());
        Collections.sort(userBubblesReactionTaskComment, new Comparator<UserBubbleReactionModel>() {
            public int compare(UserBubbleReactionModel userBubbleReaction1, UserBubbleReactionModel userBubbleReaction2) {
                if (userBubbleReaction1.isMe() == true && userBubbleReaction2.isMe() == false) {
                    return -1;
                } else if (userBubbleReaction1.isMe() == false && userBubbleReaction2.isMe() == true) {
                    return 1;
                }
                if (userBubbleReaction1.isFollowedByMe() == true && userBubbleReaction2.isFollowedByMe() == false) {
                    return -1;
                } else if (userBubbleReaction1.isFollowedByMe() == false && userBubbleReaction2.isFollowedByMe() == true) {
                    return 1;
                }
                return userBubbleReaction1.getUsername().compareTo(userBubbleReaction2.getUsername());
            }
        });
        String myReactionTaskComment = this.taskUserTaskCommentReactionRepository.findByIdTaskCommentIdUser(idTaskComment, idUser).size() == 0 ? 
            null : 
            this.taskUserTaskCommentReactionRepository.findByIdTaskCommentIdUser(idTaskComment, idUser).get(0).getReactionType();
        return ReactionsModel.builder()
            .idReactionTargetEntity(idTaskComment)
            .userBubblesReaction(
                numberOfReactionsLimit == null || numberOfReactionsLimit <= 0 ?
                userBubblesReactionTaskComment :
                userBubblesReactionTaskComment.subList(0, Math.min(userBubblesReactionTaskComment.size(), numberOfReactionsLimit))
            )
            .userBubblesReactionTotalNumber(userBubblesReactionTaskComment.size())
            .myReactionType(myReactionTaskComment)
            .build();
    }

    public ReactionsModel getTaskCommentReplyReactions(
        @RequestParam String idUser,
        JsonNode requestBody
    ) throws Exception {
        if (
            idUser == null ||
            !requestBody.has(RequestBodyKeys.GET_TASK_COMMENT_REPLY_REACTIONS_ID_TASK_COMMENT_REPLY) ||
            !requestBody.has(RequestBodyKeys.GET_TASK_COMMENT_REPLY_REACTIONS_NUMBER_OF_REACTIONS_LIMIT)
        ) {
            throw new Exception("Error: invalid input"); // TODO create an exception class
        }
        String idTaskCommentReply = RequestFormatterCommon.fUUID(requestBody.get(RequestBodyKeys.GET_TASK_COMMENT_REPLY_REACTIONS_ID_TASK_COMMENT_REPLY));
        Integer numberOfReactionsLimit = RequestFormatterCommon.fInteger(requestBody.get(RequestBodyKeys.GET_TASK_COMMENT_REPLY_REACTIONS_NUMBER_OF_REACTIONS_LIMIT));
        if (
            !RequestValidatorUser.id(idUser) ||
            !RequestValidatorTask.id(idTaskCommentReply) ||
            !RequestValidatorTask.numberOfReactionsLimit(numberOfReactionsLimit)
        ) {
            throw new Exception("Error: invalid input"); // TODO create an exception class
        }
        Set<UUID> userIdsFollowedByGivenUser = new HashSet<>(this.userRepository.getUserIdsFollowedByGivenUser(idUser));
        Set<UUID> userIds_FollowRequestSentByGivenUser_NotYetAccepted = new HashSet<>(this.userRepository.getUserIds_FollowRequestSentByGivenUser_NotYetAccepted(idUser));
        ZoneId userTimeZoneZoneId = Utilities.getUserTimeZoneZoneId(idUser, this.userRepository);
        List<UserBubbleReactionModel> userBubblesReactionTaskCommentReply = this.taskRepository.getUserBubblesReactionTaskCommentReply(idTaskCommentReply).stream().map(userBubbleReactionTaskReplyReactionDTO -> {
            UserEntity userEntityOfReaction = this.userRepository.findById(userBubbleReactionTaskReplyReactionDTO.getIdUser()).get();
            return UserBubbleReactionModel.builder()
                .id(userEntityOfReaction.getId())
                .name(userEntityOfReaction.getName())
                .username(userEntityOfReaction.getUsername())
                .imageLink(Utilities.getPresignedUrl(Constants.S3_FOLDER_NAME_USER_IMAGE, userEntityOfReaction.getIdImageKey()))
                .timestampToSortBy(userBubbleReactionTaskReplyReactionDTO.getTimestampToSortBy())
                .datetimeDateOnlyLabel(Utilities.getDatetimeDateOnlyFromTimestamp(userTimeZoneZoneId, userBubbleReactionTaskReplyReactionDTO.getTimestampToSortBy()))
                .datetimeDateAndTimeLabel(Utilities.getDatetimeDateAndTimeFromTimestamp(userTimeZoneZoneId, userBubbleReactionTaskReplyReactionDTO.getTimestampToSortBy()))
                .isFollowedByMe(userIdsFollowedByGivenUser.contains(userBubbleReactionTaskReplyReactionDTO.getId()))
                .isFollowRequestSentNotYetAccepted(userIds_FollowRequestSentByGivenUser_NotYetAccepted.contains(userBubbleReactionTaskReplyReactionDTO.getId()))
                .isMe(idUser.equals(userBubbleReactionTaskReplyReactionDTO.getId().toString()))
                .reactionType(userBubbleReactionTaskReplyReactionDTO.getReactionType())
                .build();
        }).collect(Collectors.toList());
        Collections.sort(userBubblesReactionTaskCommentReply, new Comparator<UserBubbleReactionModel>() {
            public int compare(UserBubbleReactionModel userBubbleReaction1, UserBubbleReactionModel userBubbleReaction2) {
                if (userBubbleReaction1.isMe() == true && userBubbleReaction2.isMe() == false) {
                    return -1;
                } else if (userBubbleReaction1.isMe() == false && userBubbleReaction2.isMe() == true) {
                    return 1;
                }
                if (userBubbleReaction1.isFollowedByMe() == true && userBubbleReaction2.isFollowedByMe() == false) {
                    return -1;
                } else if (userBubbleReaction1.isFollowedByMe() == false && userBubbleReaction2.isFollowedByMe() == true) {
                    return 1;
                }
                return userBubbleReaction1.getUsername().compareTo(userBubbleReaction2.getUsername());
            }
        });
        String myReactionTaskCommentReply = this.taskUserTaskCommentReplyReactionRepository.findByIdTaskCommentReplyIdUser(idTaskCommentReply, idUser).size() == 0 ? 
            null : 
            this.taskUserTaskCommentReplyReactionRepository.findByIdTaskCommentReplyIdUser(idTaskCommentReply, idUser).get(0).getReactionType();
        return ReactionsModel.builder()
            .idReactionTargetEntity(idTaskCommentReply)
            .userBubblesReaction(
                numberOfReactionsLimit == null || numberOfReactionsLimit <= 0 ?
                userBubblesReactionTaskCommentReply :
                userBubblesReactionTaskCommentReply.subList(0, Math.min(userBubblesReactionTaskCommentReply.size(), numberOfReactionsLimit))
            )
            .userBubblesReactionTotalNumber(userBubblesReactionTaskCommentReply.size())
            .myReactionType(myReactionTaskCommentReply)
            .build();
    }

    public List<TaskCommentModel> getTaskComments(
        @RequestParam String idUser,
        JsonNode requestBody
    ) throws Exception {
        if (
            idUser == null ||
            !requestBody.has(RequestBodyKeys.GET_TASK_COMMENTS_ID_TASK)
        ) {
            throw new Exception("Error: invalid input"); // TODO create an exception class
        }
        String idTask = RequestFormatterCommon.fUUID(requestBody.get(RequestBodyKeys.GET_TASK_COMMENTS_ID_TASK));
        if (
            !RequestValidatorUser.id(idUser) ||
            !RequestValidatorTask.id(idTask)
        ) {
            throw new Exception("Error: invalid input"); // TODO create an exception class
        }

        // get reactions
        Set<UUID> userIdsFollowedByGivenUser = new HashSet<>(this.userRepository.getUserIdsFollowedByGivenUser(idUser));
        Set<UUID> userIds_FollowRequestSentByGivenUser_NotYetAccepted = new HashSet<>(this.userRepository.getUserIds_FollowRequestSentByGivenUser_NotYetAccepted(idUser));
        ZoneId userTimeZoneZoneId = Utilities.getUserTimeZoneZoneId(idUser, this.userRepository);
        List<TaskCommentModel> taskCommentModelList = this.taskRepository.getTaskComments(idTask).stream().map(taskCommentDTO -> {
            String idTaskComment = taskCommentDTO.getId().toString();
            UserEntity taskCommentModelUserEntity = this.userRepository.findById(taskCommentDTO.getIdUser()).get();
            // get reactions
            List<UserBubbleReactionModel> userBubblesReactionTaskComment = this.taskRepository.getUserBubblesReactionTaskComment(idTaskComment).stream().map(userBubbleReactionTaskReactionDTO -> {
                UserEntity userEntityOfReaction = this.userRepository.findById(userBubbleReactionTaskReactionDTO.getIdUser()).get();
                return UserBubbleReactionModel.builder()
                    .id(userEntityOfReaction.getId())
                    .name(userEntityOfReaction.getName())
                    .username(userEntityOfReaction.getUsername())
                    .imageLink(Utilities.getPresignedUrl(Constants.S3_FOLDER_NAME_USER_IMAGE, userEntityOfReaction.getIdImageKey()))
                    .timestampToSortBy(userBubbleReactionTaskReactionDTO.getTimestampToSortBy())
                    .datetimeDateOnlyLabel(Utilities.getDatetimeDateOnlyFromTimestamp(userTimeZoneZoneId, userBubbleReactionTaskReactionDTO.getTimestampToSortBy()))
                    .datetimeDateAndTimeLabel(Utilities.getDatetimeDateAndTimeFromTimestamp(userTimeZoneZoneId, userBubbleReactionTaskReactionDTO.getTimestampToSortBy()))
                    .isFollowedByMe(userIdsFollowedByGivenUser.contains(userBubbleReactionTaskReactionDTO.getIdUser()))
                    .isFollowRequestSentNotYetAccepted(userIds_FollowRequestSentByGivenUser_NotYetAccepted.contains(userBubbleReactionTaskReactionDTO.getIdUser()))
                    .isMe(idUser.equals(userBubbleReactionTaskReactionDTO.getIdUser().toString()))
                    .reactionType(userBubbleReactionTaskReactionDTO.getReactionType())
                    .build();
            }).collect(Collectors.toList());
            Collections.sort(userBubblesReactionTaskComment, new Comparator<UserBubbleReactionModel>() {
                public int compare(UserBubbleReactionModel userBubbleReaction1, UserBubbleReactionModel userBubbleReaction2) {
                    if (userBubbleReaction1.isMe() == true && userBubbleReaction2.isMe() == false) {
                        return -1;
                    } else if (userBubbleReaction1.isMe() == false && userBubbleReaction2.isMe() == true) {
                        return 1;
                    }
                    if (userBubbleReaction1.isFollowedByMe() == true && userBubbleReaction2.isFollowedByMe() == false) {
                        return -1;
                    } else if (userBubbleReaction1.isFollowedByMe() == false && userBubbleReaction2.isFollowedByMe() == true) {
                        return 1;
                    }
                    return userBubbleReaction1.getUsername().compareTo(userBubbleReaction2.getUsername());
                }
            });
            String myReactionTaskComment = this.taskUserTaskCommentReactionRepository.findByIdTaskCommentIdUser(idTaskComment, idUser).size() == 0 ? 
                null : 
                this.taskUserTaskCommentReactionRepository.findByIdTaskCommentIdUser(idTaskComment, idUser).get(0).getReactionType();
            ReactionsModel taskCommentReactions = ReactionsModel.builder()
                .idReactionTargetEntity(idTaskComment)
                .userBubblesReaction(userBubblesReactionTaskComment.subList(0, Math.min(userBubblesReactionTaskComment.size(), 3)))
                .userBubblesReactionTotalNumber(userBubblesReactionTaskComment.size())
                .myReactionType(myReactionTaskComment)
                .build();
            return TaskCommentModel.builder()
                    .idTaskComment(taskCommentDTO.getId().toString())
                    .idUser(taskCommentModelUserEntity.getId().toString())
                    .username(taskCommentModelUserEntity.getUsername())
                    .userImageLink(Utilities.getPresignedUrl(Constants.S3_FOLDER_NAME_USER_IMAGE, taskCommentModelUserEntity.getIdImageKey()))
                    .timestampToSortBy(taskCommentDTO.getTimestampToSortBy())
                    .datetimeDateAndTimeLabel(Utilities.getDatetimeDateAndTimeFromTimestamp(userTimeZoneZoneId, taskCommentDTO.getTimestampToSortBy()))
                    .isText(taskCommentDTO.isText())
                    .commentText(taskCommentDTO.getCommentText())
                    .isImage(taskCommentDTO.isImage())
                    .commentImageLink(Utilities.getPresignedUrl(Constants.S3_FOLDER_NAME_TASK_COMMENT_IMAGE, taskCommentDTO.getIdTaskCommentImageKey()))
                    .numberOfReplies(this.taskRepository.getTaskCommentReplies(idTaskComment).size())
                    .reactions(taskCommentReactions)
                    .build();
        }).collect(Collectors.toList());
        
        Collections.sort(taskCommentModelList, new Comparator<TaskCommentModel>() {
            public int compare(TaskCommentModel taskCommentModel1, TaskCommentModel taskCommentModel2) {
                if (taskCommentModel1.getTimestampToSortBy() > taskCommentModel2.getTimestampToSortBy()) {
                    return 1;
                } else if (taskCommentModel1.getTimestampToSortBy() < taskCommentModel2.getTimestampToSortBy()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
        return taskCommentModelList;
    }

    public List<TaskCommentReplyModel> getTaskCommentReplies(
        @RequestParam String idUser,
        JsonNode requestBody
    ) throws Exception {
        if (
            idUser == null ||
            !requestBody.has(RequestBodyKeys.GET_TASK_COMMENT_REPLIES_ID_TASK_COMMENT)
        ) {
            throw new Exception("Error: invalid input"); // TODO create an exception class
        }
        String idTaskComment = RequestFormatterCommon.fUUID(requestBody.get(RequestBodyKeys.GET_TASK_COMMENT_REPLIES_ID_TASK_COMMENT));
        if (
            !RequestValidatorUser.id(idUser) ||
            !RequestValidatorTask.id(idTaskComment)
        ) {
            throw new Exception("Error: invalid input"); // TODO create an exception class
        }

        Set<UUID> userIdsFollowedByGivenUser = new HashSet<>(this.userRepository.getUserIdsFollowedByGivenUser(idUser));
        Set<UUID> userIds_FollowRequestSentByGivenUser_NotYetAccepted = new HashSet<>(this.userRepository.getUserIds_FollowRequestSentByGivenUser_NotYetAccepted(idUser));
        ZoneId userTimeZoneZoneId = Utilities.getUserTimeZoneZoneId(idUser, this.userRepository);

        List<TaskCommentReplyModel> taskCommentReplyModelList = this.taskRepository.getTaskCommentReplies(idTaskComment).stream().map(taskCommentReplyDTO -> {
            String idTaskCommentReply = taskCommentReplyDTO.getId().toString();
            UserEntity taskCommentReplyUserEntity = this.userRepository.findById(taskCommentReplyDTO.getIdUser()).get();

            List<UserBubbleReactionModel> userBubblesReactionTaskCommentReply = this.taskRepository.getUserBubblesReactionTaskCommentReply(idTaskCommentReply).stream().map(userBubbleReactionTaskReplyReactionDTO -> {
                UserEntity userEntityOfReaction = this.userRepository.findById(userBubbleReactionTaskReplyReactionDTO.getIdUser()).get();
                return UserBubbleReactionModel.builder()
                    .id(userEntityOfReaction.getId())
                    .name(userEntityOfReaction.getName())
                    .username(userEntityOfReaction.getUsername())
                    .imageLink(Utilities.getPresignedUrl(Constants.S3_FOLDER_NAME_USER_IMAGE, userEntityOfReaction.getIdImageKey()))
                    .timestampToSortBy(userBubbleReactionTaskReplyReactionDTO.getTimestampToSortBy())
                    .datetimeDateOnlyLabel(Utilities.getDatetimeDateOnlyFromTimestamp(userTimeZoneZoneId, userBubbleReactionTaskReplyReactionDTO.getTimestampToSortBy()))
                    .datetimeDateAndTimeLabel(Utilities.getDatetimeDateAndTimeFromTimestamp(userTimeZoneZoneId, userBubbleReactionTaskReplyReactionDTO.getTimestampToSortBy()))
                    .isFollowedByMe(userIdsFollowedByGivenUser.contains(userBubbleReactionTaskReplyReactionDTO.getId()))
                    .isFollowRequestSentNotYetAccepted(userIds_FollowRequestSentByGivenUser_NotYetAccepted.contains(userBubbleReactionTaskReplyReactionDTO.getId()))
                    .isMe(idUser.equals(userBubbleReactionTaskReplyReactionDTO.getId().toString()))
                    .reactionType(userBubbleReactionTaskReplyReactionDTO.getReactionType())
                    .build();
            }).collect(Collectors.toList());
            Collections.sort(userBubblesReactionTaskCommentReply, new Comparator<UserBubbleReactionModel>() {
                public int compare(UserBubbleReactionModel userBubbleReaction1, UserBubbleReactionModel userBubbleReaction2) {
                    if (userBubbleReaction1.isMe() == true && userBubbleReaction2.isMe() == false) {
                        return -1;
                    } else if (userBubbleReaction1.isMe() == false && userBubbleReaction2.isMe() == true) {
                        return 1;
                    }
                    if (userBubbleReaction1.isFollowedByMe() == true && userBubbleReaction2.isFollowedByMe() == false) {
                        return -1;
                    } else if (userBubbleReaction1.isFollowedByMe() == false && userBubbleReaction2.isFollowedByMe() == true) {
                        return 1;
                    }
                    return userBubbleReaction1.getUsername().compareTo(userBubbleReaction2.getUsername());
                }
            });
            String myReactionTaskCommentReply = this.taskUserTaskCommentReplyReactionRepository.findByIdTaskCommentReplyIdUser(idTaskCommentReply, idUser).size() == 0 ? 
                null : 
                this.taskUserTaskCommentReplyReactionRepository.findByIdTaskCommentReplyIdUser(idTaskCommentReply, idUser).get(0).getReactionType();
            ReactionsModel taskCommentReplyReactions = ReactionsModel.builder()
                .idReactionTargetEntity(idTaskCommentReply)
                .userBubblesReaction(userBubblesReactionTaskCommentReply.subList(0, Math.min(userBubblesReactionTaskCommentReply.size(), 3)))
                .userBubblesReactionTotalNumber(userBubblesReactionTaskCommentReply.size())
                .myReactionType(myReactionTaskCommentReply)
                .build();
            return TaskCommentReplyModel.builder()
                .idTaskCommentReply(idTaskCommentReply)
                .idUser(taskCommentReplyUserEntity.getId().toString())
                .username(taskCommentReplyUserEntity.getUsername())
                .userImageLink(Utilities.getPresignedUrl(Constants.S3_FOLDER_NAME_USER_IMAGE, taskCommentReplyUserEntity.getIdImageKey()))
                .timestampToSortBy(taskCommentReplyDTO.getTimestampToSortBy())
                .datetimeDateAndTimeLabel(Utilities.getDatetimeDateAndTimeFromTimestamp(userTimeZoneZoneId, taskCommentReplyDTO.getTimestampToSortBy()))
                .isText(taskCommentReplyDTO.isText())
                .commentReplyText(taskCommentReplyDTO.getCommentText())
                .isImage(taskCommentReplyDTO.isImage())
                .commentReplyImageLink(Utilities.getPresignedUrl(Constants.S3_FOLDER_NAME_TASK_COMMENT_REPLY_IMAGE, taskCommentReplyDTO.getIdTaskCommentImageKey()))
                .reactions(taskCommentReplyReactions)
                .build();
        }).collect(Collectors.toList());
    
        Collections.sort(taskCommentReplyModelList, new Comparator<TaskCommentReplyModel>() {
            public int compare(TaskCommentReplyModel taskCommentReply1, TaskCommentReplyModel taskCommentReply2) {
                if (taskCommentReply1.getTimestampToSortBy() > taskCommentReply2.getTimestampToSortBy()) {
                    return 1;
                } else if (taskCommentReply1.getTimestampToSortBy() < taskCommentReply2.getTimestampToSortBy()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
        return taskCommentReplyModelList;
    }
}
