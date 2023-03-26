package ru.skypro.homework.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Validated
public class LoginReq {
    @JsonProperty("password")
    @NotNull
    private String password;
    @JsonProperty("username")
    @NotNull
    private String username;
}
