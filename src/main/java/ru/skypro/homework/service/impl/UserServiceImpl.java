package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.NewPassword;
import ru.skypro.homework.dto.RegisterReq;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.dto.User;
import ru.skypro.homework.entity.Authority;
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.exception.AvatarNotFoundException;
import ru.skypro.homework.exception.UserNotFoundException;
import ru.skypro.homework.mapping.UserMapper;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.AvatarService;
import ru.skypro.homework.service.UserService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository usersRepository;
    private final UserMapper userMapper;
    private final AvatarService avatarService;
    private final AuthorityServiceImpl authorityService;

    public Pair<UserEntity, Authority> addUser(RegisterReq registerReq, String password) {
        UserEntity userEntity = userMapper.registerReqToUserEntity(registerReq, password);
        userEntity.setRegDate(LocalDate.now());
        userEntity.setEnabled(true);
        userEntity = usersRepository.save(userEntity);
        Authority authority = authorityService.addAuthority(userEntity, Role.USER);
        return Pair.of(userEntity, authority);
    }

    public User getUsers(String email) {
        return userMapper.userEntityToDto(getUserByEmail(email));
    }

    public UserEntity getUserByEmail(String email) {
        return usersRepository.findByEmail(email).orElseThrow(() -> {
            log.error("getUserByEmail: Не найден пользователь: {}", email);
            return new UserNotFoundException(email);
        });
    }

    @Override
    public Pair<byte[], String> getAvatarMe(String email) {
        return getAvatarDataOfUser(getUserByEmail(email));
    }

    @Override
    public Pair<byte[], String> getAvatarOfUser(Integer userId) {
        UserEntity userEntity = usersRepository.findById(userId).orElseThrow(() -> {
            log.error("getAvatar: Пользователь с ID {} не найден", userId);
            return new UserNotFoundException("Пользователь с ID " + userId + " не найден");
        });
        return getAvatarDataOfUser(userEntity);
    }

    @Override
    public Pair<byte[], String> getAvatarDataOfUser(UserEntity userEntity) {
        if (userEntity.getAvatar() == null) {
            log.error("Исключение! Аватар пользователя c ID " + userEntity.getId() + " = null");
            throw new AvatarNotFoundException();
        }
        return avatarService.getAvatarData(userEntity.getAvatar());
    }

    @Override
    public NewPassword setPassword(NewPassword password) {
        return null;
    }

    @Override
    public User updateUser(String email, User user) {
        UserEntity userEntity = userMapper.userDtoToEntity(user);
        UserEntity newUser = getUserByEmail(email);
        if (userEntity.getEmail() != null) {
            newUser.setEmail(userEntity.getEmail());
        }
        if (userEntity.getPhone() != null) {
            newUser.setPhone(userEntity.getPhone());
        }
        if (userEntity.getFirstName() != null) {
            newUser.setFirstName(userEntity.getFirstName());
        }
        if (userEntity.getLastName() != null) {
            newUser.setLastName(userEntity.getLastName());
        }
        newUser = usersRepository.save(newUser);
        log.info("Пользователь обновлен (id: {})", newUser.getId());
        return userMapper.userEntityToDto(newUser);
    }

    @Override
    public ResponseEntity<Void> updateUserAvatar(String email, MultipartFile image) throws IOException {
        UserEntity userEntity = getUserByEmail(email);
        updateAvatarOfUserEntity(userEntity, image);
        userEntity = usersRepository.save(userEntity);
        if (userEntity.getAvatar() != null && userEntity.getAvatar().getPath() != null
                && Files.exists(Path.of(userEntity.getAvatar().getPath()))) {
            log.info("Аватар пользователя с (id: {}) обновлен", userEntity.getId());
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void updateAvatarOfUserEntity(UserEntity userEntity, MultipartFile image) throws IOException {
        if (userEntity.getAvatar() == null) {
            userEntity.setAvatar(avatarService.addAvatar(image, "user" + userEntity.getId() + "_avatar"));
            log.info("Добавляем аватар пользователю (id: {})", userEntity.getId());
        } else {
            userEntity.setAvatar(avatarService.updateAvatar(userEntity.getAvatar(), image, "user" + userEntity.getId() + "_avatar"));
            log.info("Обновили аватар (id: {}) пользователю (id: {})", userEntity.getAvatar().getId(), userEntity.getId());
        }
    }
}
