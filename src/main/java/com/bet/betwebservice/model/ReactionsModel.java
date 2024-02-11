package com.bet.betwebservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class ReactionsModel {
    private String idReactionTargetEntity;
    private List<UserBubbleReactionModel> userBubblesReaction;
    private int userBubblesReactionTotalNumber;
    private String myReactionType;
}
