package com.bet.betwebservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
@Entity
public class TaskCommentDTO {
    @Id
    private UUID id;
    private Integer timestampToSortBy;
    private UUID idUser;
    @JsonProperty(value="isText")
    private boolean isText;
    private String commentText;
    @JsonProperty(value="isImage")
    private boolean isImage;
    private UUID idTaskCommentImageKey;
}
