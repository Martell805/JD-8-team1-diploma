package ru.skypro.homework.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ResponseWrapperComment { // оставить
   @JsonProperty("count")
   Integer count;
   @JsonProperty("results")
   private List<Comment> results;
}
