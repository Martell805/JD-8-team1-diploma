package ru.skypro.homework.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.util.Pair;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.RegisterReq;
import ru.skypro.homework.entity.Authority;
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.exception.UserNotFoundException;
import ru.skypro.homework.service.AuthService;
import ru.skypro.homework.service.UserService;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    private final UserDetailsManager manager;
    private final PasswordEncoder encoder;
    private final UserService userService;
    public final static String PAS_PREFIX = "{bcrypt}";

    public AuthServiceImpl(@Qualifier("jdbcUserDetailsManager") UserDetailsManager manager, UserService userService) {
        this.userService = userService;
        this.manager = manager;
        this.encoder = new BCryptPasswordEncoder();
    }

    @Override
    public boolean login(String userName, String password) {
        if (!manager.userExists(userName)) {
            log.error("Ошибка входа, пользователь {} не найден", userName);
            throw new UserNotFoundException(userName);
        }
        UserDetails userDetails = manager.loadUserByUsername(userName);
        String encryptedPassword = userDetails.getPassword();
        log.debug("pass: {}", encryptedPassword);
        String encryptedPasswordWithoutEncryptionType = encryptedPassword.substring(8);
        log.debug("pass: {}", encryptedPasswordWithoutEncryptionType);
        boolean isLoggedIn = encoder.matches(password, encryptedPasswordWithoutEncryptionType);
        if (isLoggedIn) {
            log.info("Пользователь успешно вошел - {}", userName);
        } else {
            log.warn("Ошибка входа. Не верный пароль пользователя {} ", userName);
        }
        return isLoggedIn;
    }

    @Override
    public boolean register(RegisterReq registerReq) {
        if (manager.userExists(registerReq.getUsername())) {
            log.error("Пользователь {} уже существует", registerReq.getUsername());
            return false;
        }
        Pair<UserEntity, Authority> pair =
                userService.addUser(registerReq, PAS_PREFIX + encoder.encode(registerReq.getPassword()));
        if (pair != null
                && pair.getFirst().getUsername() != null
                && pair.getFirst().getUsername().equals(registerReq.getUsername())) {
            log.info("Пользователь {} зарегистрирован", registerReq.getUsername());
            return true;
        } else {
            return false;
        }
    }
}
