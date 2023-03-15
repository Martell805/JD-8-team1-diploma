package ru.skypro.homework.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public class Comment { // оставить
    @JsonProperty("author")
    private Integer author;
    @JsonProperty("pk")
    private Integer id;
    @JsonProperty("text")
    private String text;
    @JsonProperty("createdAt")
    private Date createdAt;
}
