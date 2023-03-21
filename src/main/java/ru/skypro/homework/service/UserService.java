package ru.skypro.homework.service;

import org.springframework.data.util.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.NewPassword;
import ru.skypro.homework.dto.RegisterReq;
import ru.skypro.homework.dto.User;
import ru.skypro.homework.entity.Authority;
import ru.skypro.homework.entity.UserEntity;

import java.io.IOException;

public interface UserService {
    Pair<UserEntity, Authority> addUser(RegisterReq registerReq, String password);

    Pair<byte[], String> getAvatarDataOfUser(UserEntity userEntity);

    /**
     * Метод установки пароля
     *
     * @param password - новый пароль
     * @return возвращает установленный пароль
     */
    NewPassword setPassword(NewPassword password);

    /**
     * Метод получения DTO пользователя
     *
     * @param username - логин пользователя
     * @return User - DTO
     */
    User getUser(String username);

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
     * Метод обновления изображения пользователя
     */
    ResponseEntity<Void> updateUserAvatar(String username, MultipartFile image) throws IOException;
}
