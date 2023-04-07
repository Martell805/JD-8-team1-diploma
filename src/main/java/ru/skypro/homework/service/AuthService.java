package ru.skypro.homework.service;

import ru.skypro.homework.dto.NewPassword;
import ru.skypro.homework.dto.RegisterReq;

public interface AuthService {
    /**
     * Метод авторизации пользователя.
     *
     * @param userName email пользователя
     * @param password пароль пользователя
     * @return - boolean
     */
    boolean login(String userName, String password);

    /**
     * Метод регистрации пользователя.
     *
     * @param registerReq данные регистрационной формы
     * @return - boolean
     */
    boolean register(RegisterReq registerReq);

    /**
     * Метод смены пароля пользователя.
     *
     * @param body тело запроса
     * @return - boolean
     */
    boolean changePassword(NewPassword body);
}
