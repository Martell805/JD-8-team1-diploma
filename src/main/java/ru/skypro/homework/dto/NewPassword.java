package ru.skypro.homework.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class NewPassword { // оставить
    @JsonProperty("newPassword")
    @NotNull
    private String newPassword;
    @JsonProperty("currentPassword")
    @NotNull
    private String currentPassword;
}
