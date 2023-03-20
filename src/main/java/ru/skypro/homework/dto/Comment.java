package ru.skypro.homework.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class Comment {
    @JsonProperty("author")
    private Integer author;
    @JsonProperty("pk")
    private Integer id;
    @JsonProperty("text")
    @NotNull
    private String text;
    @JsonProperty("createdAt")
    private String createdAt;
}
