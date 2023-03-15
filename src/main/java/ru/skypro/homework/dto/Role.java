package ru.skypro.homework.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Role {
    USER("USER"), ADMIN("ADMIN");

    private final String role;
}
