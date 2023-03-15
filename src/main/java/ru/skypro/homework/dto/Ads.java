package ru.skypro.homework.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Getter
@Setter
@Validated
public class Ads {
    @JsonProperty("pk")
    private Integer id;
    @JsonProperty("image")
    private String imageId;
    @JsonProperty("author")
    @NotNull
    private Integer author;
    @JsonProperty("price")
    @NotNull
    @PositiveOrZero
    private Integer price;
    @JsonProperty("title")
    @NotNull
    private String title;
}
