package ru.skypro.homework.service;

import ru.skypro.homework.dto.Role;
import ru.skypro.homework.entity.Authority;
import ru.skypro.homework.entity.UserEntity;

public interface AuthorityService {
    /**
     * Метод назначения роли пользователь/администратор
     *
     * @param userEntity - пользователь
     * @param role - его роль
     * @return возвращает newAuthority
     */
    Authority addAuthority(UserEntity userEntity, Role role);
}
