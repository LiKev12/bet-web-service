package com.bet.betwebservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class NumberOfPointsInTasksCompletedOverTimeVisualizationModel {
    private List<HeatmapChartDataPoint> dataHeatmapChart;
    private List<LineChartDataPoint> dataLineChart_AggregateDay_NotCumulative;
    private List<LineChartDataPoint> dataLineChart_AggregateDay_Cumulative;
    private List<LineChartDataPoint> dataLineChart_AggregateWeek_NotCumulative;
    private List<LineChartDataPoint> dataLineChart_AggregateWeek_Cumulative;   
    private List<LineChartDataPoint> dataLineChart_AggregateMonth_NotCumulative;
    private List<LineChartDataPoint> dataLineChart_AggregateMonth_Cumulative;

    @Data
    @AllArgsConstructor
    @Builder
    public static class HeatmapChartDataPoint {
        private int numberOfPoints;
        private int numberOfTasksComplete;
        private String color;
        private String dateLabel;
        private String dayOfWeek;
        @JsonProperty(value="isAfterOrEqualToRelevantStartDate")
        private boolean isAfterOrEqualToRelevantStartDate;
        @JsonProperty(value="isBeforeOrEqualToRelevantEndDate")
        private boolean isBeforeOrEqualToRelevantEndDate;
        @JsonProperty(value="isToday")
        private boolean isToday;
    }

    @Data
    @AllArgsConstructor
    @Builder
    public static class LineChartDataPoint {
        private int numberOfPoints;
        private String dateLabel;
    }
}
