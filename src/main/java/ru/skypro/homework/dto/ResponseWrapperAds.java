package ru.skypro.homework.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ResponseWrapperAds {
    @JsonProperty("count")
    private Integer count;
    @JsonProperty("results")
    private List<Ads> results;
}
