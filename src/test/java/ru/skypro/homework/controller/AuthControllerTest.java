package ru.skypro.homework.controller;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.skypro.homework.Generator;
import ru.skypro.homework.dto.LoginReq;
import ru.skypro.homework.dto.NewPassword;
import ru.skypro.homework.dto.RegisterReq;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.entity.Authority;
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.repository.AuthorityRepository;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.impl.AuthServiceImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@TestMethodOrder(MethodOrderer.DisplayName.class)
class AuthControllerTest {

    @Autowired
    private AuthorityRepository authorityRepository;
    @Autowired
    private UserRepository usersRepository;
    @Autowired
    private TestRestTemplate testRestTemplate;
    private final Generator generator = new Generator();
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    public void generateData() {
        int admins = 3;
        int users = 9;

        for (int i = 0; i < admins; i++) {
            UserEntity userEntity = usersRepository.save(generator.genUser(null, "password"));
            authorityRepository.save(generator.genAuthority(userEntity, Role.ADMIN));
        }
        for (int i = 0; i < users; i++) {
            UserEntity userEntity = usersRepository.save(generator.genUser(null, "password"));
            authorityRepository.save(generator.genAuthority(userEntity, Role.USER));
        }
    }

    @AfterEach
    public void clearData() {
        authorityRepository.deleteAll();
        usersRepository.deleteAll();
    }

    @Test
    void contextLoads() {
        assertThat(authorityRepository).isNotNull();
        assertThat(usersRepository).isNotNull();
        assertThat(testRestTemplate).isNotNull();
    }

    @Test
    @DisplayName("200 GET http://localhost:8080/login")
    public void loginTest() {
        UserEntity userExistUsername = usersRepository.findAll().stream().findAny().orElse(null);
        assert userExistUsername != null;
        LoginReq loginReq = new LoginReq();
        loginReq.setUsername(userExistUsername.getUsername());
        loginReq.setPassword("password");
        ResponseEntity<Void> response = testRestTemplate.
                postForEntity("/login", loginReq, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("404, 403 GET http://localhost:8080/login")
    public void loginNegativeTest() {
        String email = generator.generateEmailIfEmpty(null);
        assertThat(usersRepository.findAll().stream()
                .filter(user -> user.getUsername().equals(email))
                .findAny().isEmpty()).isTrue();
        LoginReq loginReq = new LoginReq();
        loginReq.setUsername(email);
        loginReq.setPassword("password");
        ResponseEntity<Void> response = testRestTemplate.
                postForEntity("/login", loginReq, Void.class);
        assertThat(response.getStatusCode()).
                isEqualTo(HttpStatus.NOT_FOUND);

        UserEntity userExistUsername = usersRepository.findAll().stream().findAny().orElse(null);
        assert userExistUsername != null;
        loginReq.setUsername(userExistUsername.getUsername());
        loginReq.setPassword("wrongPassword");
        response = testRestTemplate.
                postForEntity("/login", loginReq, Void.class);
        assertThat(response.getStatusCode()).
                isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("201 GET http://localhost:8080/register")
    public void registerTest() {
        String firstName = "Ivan";
        String lastName = "Ivanov";
        String email = "x@gmail.com";
        String phone = "+79876543210";
        Role role = Role.USER;
        String password = "password";

        List<UserEntity> userList = usersRepository.findAll().stream()
                .filter(user -> user.getUsername().equals(email))
                .collect(Collectors.toList());
        List<Authority> authorityList = authorityRepository.findAll().stream()
                .filter(authority -> authority.getUsername().equals(email))
                .collect(Collectors.toList());
        assertThat(userList.size()).isEqualTo(0);
        assertThat(authorityList.size()).isEqualTo(0);

        RegisterReq registerReq = new RegisterReq(email, password, role, firstName, lastName, phone);

        UserEntity userEntity = generator.genUser(777, email, firstName, lastName,
                phone, LocalDate.now(), null,
                AuthServiceImpl.PASSWORD_PREFIX + passwordEncoder.encode(password), false);

        ResponseEntity<Void> response = testRestTemplate.
                postForEntity("/register", registerReq, Void.class);
        userList = usersRepository.findAll().stream()
                .filter(user1 -> user1.getUsername().equals(email))
                .collect(Collectors.toList());
        authorityList = authorityRepository.findAll().stream()
                .filter(authority -> authority.getUsername().equals(email))
                .collect(Collectors.toList());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(userList.size()).isEqualTo(1);
        assertThat(userList.get(0))
                .usingRecursiveComparison()
                .ignoringFields("id", "password")
                .isEqualTo(userEntity);
        assertThat(
                passwordEncoder.matches(
                        password,
                        userList.get(0).getPassword().substring(AuthServiceImpl.PASSWORD_PREFIX.length())))
                .isTrue();
        assertThat(authorityList.size()).isEqualTo(1);
        assertThat(authorityList.get(0).getAuthority()).isEqualTo(Role.USER.getRole());
    }

    @Test
    @DisplayName("400 GET http://localhost:8080/register")
    public void registerNegativeTest() {
        UserEntity userExistUsername = usersRepository.findAll().stream().findAny().orElse(null);
        assert userExistUsername != null;

        RegisterReq registerReq = new RegisterReq();
        registerReq.setFirstName(userExistUsername.getFirstName());
        registerReq.setLastName(userExistUsername.getLastName());
        registerReq.setPhone(userExistUsername.getPhone());
        registerReq.setRole(Role.USER);
        registerReq.setUsername(userExistUsername.getUsername());
        registerReq.setPassword("password");

        int countUser = usersRepository.findAll().size();
        int countAuth = authorityRepository.findAll().size();

        ResponseEntity<Void> response = testRestTemplate.
                postForEntity("/register", registerReq, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(usersRepository.findAll().size()).isEqualTo(countUser);
        assertThat(authorityRepository.findAll().size()).isEqualTo(countAuth);
    }

    @Test
    @DisplayName("200 GET http://localhost:8080/users/set_password")
    public void changePasswordTest() {
        UserEntity existUser = usersRepository.findAll().stream().findAny().orElse(null);
        assert existUser != null;
        assertThat(usersRepository.findAll().stream()
                .filter(user -> user.getUsername().equals(existUser.getUsername()))
                .count()).isEqualTo(1);
        String oldPass = "password";
        String newPass = "newPassword";
        NewPassword newPassword = new NewPassword();
        newPassword.setCurrentPassword(oldPass);
        newPassword.setNewPassword(newPass);
        ResponseEntity<NewPassword> response = testRestTemplate
                .withBasicAuth(existUser.getUsername(), oldPass)
                .postForEntity("/users/set_password", newPassword, NewPassword.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<UserEntity> users = usersRepository.findAll().stream()
                .filter(user -> user.getUsername().equals(existUser.getUsername()))
                .collect(Collectors.toList());
        assertThat(users.size()).isEqualTo(1);
        assertThat(passwordEncoder
                .matches(newPass, users.get(0).getPassword().substring(AuthServiceImpl.PASSWORD_PREFIX.length())))
                .isTrue();
    }
}