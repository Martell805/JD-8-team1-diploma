package ru.skypro.homework.mapping;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.skypro.homework.Generator;
import ru.skypro.homework.dto.RegisterReq;
import ru.skypro.homework.dto.User;
import ru.skypro.homework.entity.UserEntity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private final Generator generator = new Generator();
    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Test
    void userDtoToEntityTest() {
        User user = new User();
        user.setId(777);
        user.setEmail("x@gmail.com");
        user.setFirstName("Ибрагим");
        user.setLastName("Туркин");
        user.setPhone("+79998887766");
        user.setRegDate("22/03/2023");

        UserEntity userEntity = userMapper.userDtoToEntity(user);

        assertEquals(userEntity.getId(), 777);
        assertEquals(userEntity.getEmail(), "x@gmail.com");
        assertEquals(userEntity.getFirstName(), "Ибрагим");
        assertEquals(userEntity.getLastName(), "Туркин");
        assertEquals(userEntity.getPhone(), "+79998887766");
        assertEquals(userEntity.getRegDate(), LocalDate.parse("22/03/2023", DateTimeFormatter.ofPattern("d/MM/yyyy")));
        assertEquals(userEntity.getUsername(), "x@gmail.com");
    }

    @Test
    void userEntityToDtoTest() {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(777);
        userEntity.setEmail("x@gmail.com");
        userEntity.setFirstName("Ибрагим");
        userEntity.setLastName("Туркин");
        userEntity.setPhone("+79998887766");
        userEntity.setRegDate(LocalDate.parse("22/03/2023", DateTimeFormatter.ofPattern("d/MM/yyyy")));
        userEntity.setAvatar(generator.genAvatar(777, "/users/777/image"));

        User userDto = userMapper.userEntityToDto(userEntity);
        assertEquals(userDto.getId(), 777);
        assertEquals(userDto.getEmail(), "x@gmail.com");
        assertEquals(userDto.getFirstName(), "Ибрагим");
        assertEquals(userDto.getLastName(), "Туркин");
        assertEquals(userDto.getPhone(), "+79998887766");
        assertEquals(userDto.getRegDate(), "22/03/2023");
        assertEquals(userDto.getImage(), "/users/777/image");
    }

    @Test
    void registerReqToUserEntityTest() {
        RegisterReq user = new RegisterReq();
        user.setUsername("x@gmail.com");
        user.setPassword("password");
        user.setFirstName("Ибрагим");
        user.setLastName("Туркин");
        user.setPhone("+79998887766");

        UserEntity userEntity = userMapper.registerReqToUserEntity(user, "password");

        assertEquals(userEntity.getEmail(), "x@gmail.com");
        assertEquals(userEntity.getUsername(), "x@gmail.com");
        assertEquals(userEntity.getFirstName(), "Ибрагим");
        assertEquals(userEntity.getLastName(), "Туркин");
        assertEquals(userEntity.getPhone(), "+79998887766");
        assertEquals(userEntity.getPassword(), "password");
    }
}