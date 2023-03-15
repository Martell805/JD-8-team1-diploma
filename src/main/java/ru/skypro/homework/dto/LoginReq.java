package ru.skypro.homework.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Getter
@Validated
public class LoginReq {
    @JsonProperty("password")
    @NotNull
    private String password;
    @JsonProperty("username")
    @NotNull
    private String username;
}
