package ru.skypro.homework.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@Validated
public class CreateAds {
    @JsonProperty("description")
    @NotNull
    private String description;
    @JsonProperty("title")
    @NotNull
    private String title;
    @JsonProperty("price")
    @NotNull
    @Positive
    private Integer price;
}
