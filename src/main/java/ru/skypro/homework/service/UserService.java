package ru.skypro.homework.service;

import org.springframework.data.util.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.RegisterReq;
import ru.skypro.homework.dto.User;
import ru.skypro.homework.entity.Authority;
import ru.skypro.homework.entity.UserEntity;

import java.io.IOException;

public interface UserService {

    /**
     * Метод создает/регистрирует нового пользователя
     *
     * @param registerReq - авторизация
     * @param password - пароль
     * @return зарегистрированный пользователь
     */
    Pair<UserEntity, Authority> addUser(RegisterReq registerReq, String password);

    /**
     * Метод получения аватара
     *
     * @param userEntity - пользователь
     */
    Pair<byte[], String> getAvatarDataOfUser(UserEntity userEntity);

    /**
     * Метод получения DTO пользователя
     *
     * @param email - логин пользователя
     * @return User - DTO
     */
    User getUser(String email);

    /**
     * Метод получения Entity пользователя
     *
     * @param email - логин пользователя
     * @return UserEntity
     */
    UserEntity getUserByEmail(String email);

    /**
     * Метод получения аватара пользователя
     *
     * @param email - логин пользователя
     * @return Pair
     */
    Pair<byte[], String> getAvatarMe(String email);

    /**
     * Метод получения аватара пользователя по ID
     *
     * @param userId - ID пользователя
     * @return Pair
     */
    Pair<byte[], String> getAvatarOfUser(Integer userId);

    /**
     * Метод изменения пользователя
     *
     * @param user - пользователь на изменение
     * @return возвращает обновленного пользователя
     */
    User updateUser(String username, User user);

    /**
     * Метод изменения пароля пользователя
     *
     * @param email - логин пользователя на изменение
     * @param password - новый пароль
     * @return возвращает обновленного пользователя
     */
    User updatePassword(String email, String password);

    /**
     * Метод обновления изображения пользователя
     *
     * @param email - логин пользователя на изменение
     * @param image - новый аватар
     */
    ResponseEntity<Void> updateUserAvatar(String email, MultipartFile image) throws IOException;
}
