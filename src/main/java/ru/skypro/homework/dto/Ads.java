package ru.skypro.homework.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Ads {
    private Integer id;
    private String imageId;
    private Integer author;
    private Integer price;
    private String title;
}
