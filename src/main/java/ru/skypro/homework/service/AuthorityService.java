package ru.skypro.homework.service;

import ru.skypro.homework.dto.Role;
import ru.skypro.homework.entity.Authority;
import ru.skypro.homework.entity.UserEntity;

public interface AuthorityService {
    Authority addAuthority(UserEntity userEntity, Role role);
}
