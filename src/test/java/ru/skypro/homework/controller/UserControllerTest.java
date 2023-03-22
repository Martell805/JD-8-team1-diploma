package ru.skypro.homework.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.skypro.homework.Generator;
import ru.skypro.homework.dto.User;
import ru.skypro.homework.entity.AvatarEntity;
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.mapping.UserMapperImpl;
import ru.skypro.homework.repository.AuthorityRepository;
import ru.skypro.homework.repository.AvatarRepository;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.impl.AuthorityServiceImpl;
import ru.skypro.homework.service.impl.AvatarServiceImpl;
import ru.skypro.homework.service.impl.UserServiceImpl;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@TestMethodOrder(MethodOrderer.DisplayName.MethodName.class)
class UserControllerTest {

    private final String avatarsDir;
    private final String emailForTest = "a@a.ru";
    private MockMvc mockMvc;
    @MockBean
    private UserRepository usersRepository;
    @SpyBean
    private UserServiceImpl userService;
    @SpyBean
    private UserMapperImpl userMapper;
    @MockBean
    private AvatarRepository avatarRepository;
    @SpyBean
    private AvatarServiceImpl avatarService;
    @MockBean
    private AuthorityRepository authorityRepository;
    @SpyBean
    private AuthorityServiceImpl authorityService;
    @InjectMocks
    private UserController userController;
    @Autowired
    private WebApplicationContext context;
    private final ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
    private final Generator generator = new Generator();

    UserControllerTest(@Value("${path.to.avatars.folder}") String avatarsDir) {
        this.avatarsDir = avatarsDir;
    }

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void contextsLoad() {
        assertThat(mockMvc).isNotNull();
        assertThat(usersRepository).isNotNull();
        assertThat(userService).isNotNull();
        assertThat(userMapper).isNotNull();
        assertThat(avatarRepository).isNotNull();
        assertThat(avatarService).isNotNull();
        assertThat(authorityRepository).isNotNull();
        assertThat(authorityService).isNotNull();
        assertThat(userController).isNotNull();
    }

    @Test
    @DisplayName("200 GET http://localhost:8080/users/me")
    @WithMockUser(username = emailForTest, authorities = "USER")
    void getUserTest() throws Exception {
        UserEntity user = generator.genUser(null, null);

        User userDto = userMapper.userEntityToDto(user);
        String userDtoJSON = objectWriter.writeValueAsString(userDto);
        when(usersRepository.findByEmail(emailForTest)).thenReturn(Optional.of(user));

        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders
                        .get("http://localhost:8080/users/me")
                        .accept(MediaType.APPLICATION_JSON);
        ResultActions resultActions = mockMvc.perform(builder);
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().json(userDtoJSON));
    }

    @Test
    @DisplayName("200 GET http://localhost:8080/users/me/image")
    @WithMockUser(username = emailForTest, authorities = {"USER"})
    void getAvatarTest() throws Exception {
        AvatarEntity avatar = generator.generateAvatarIfNull(null, avatarsDir, null);
        avatar.setId(777);
        UserEntity user = generator.genUser(avatar, null);

        when(usersRepository.findByEmail(emailForTest)).thenReturn(Optional.of(user));
        when(avatarRepository.findById(avatar.getId())).thenReturn(Optional.of(avatar));

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .get("http://localhost:8080/users/me/image")
                .contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        MvcResult mvcResult = mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andReturn();
        assertThat(mvcResult.getResponse().getContentAsByteArray())
                .isEqualTo(Files.readAllBytes(Path.of(avatar.getPath())));
    }

    @Test
    @DisplayName("200 GET http://localhost:8080/users/{idUser}/image")
    @WithMockUser(username = emailForTest, authorities = {"USER"})
    void getAvatarOfUserTest() throws Exception {
        AvatarEntity avatar = generator.generateAvatarIfNull(null, avatarsDir, null);
        avatar.setId(777);
        UserEntity userEntity = generator.genUser(null, null);
        userEntity.setId(777);
        userEntity.setAvatar(avatar);

        when(usersRepository.findById(userEntity.getId())).thenReturn(Optional.of(userEntity));
        when(avatarRepository.findById(avatar.getId())).thenReturn(Optional.of(avatar));

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .get("http://localhost:8080/users/" + userEntity.getId() + "/image")
                .contentType(MediaType.MULTIPART_FORM_DATA);
        MvcResult mvcResult = mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andReturn();
        assertThat(mvcResult.getResponse().getContentAsByteArray())
                .isEqualTo(Files.readAllBytes(Path.of(avatar.getPath())));
    }

    @Test
    @DisplayName("200 PATCH http://localhost:8080/users/me")
    @WithMockUser(username = emailForTest, authorities = {"USER"})
    void updateUserTest() throws Exception {
        UserEntity oldUser = generator.genUser(null, null);
        UserEntity newUser = generator.genUser(null, null);
        newUser.setId(oldUser.getId());
        newUser.setEmail("Updated@mail.ru");
        newUser.setFirstName("updatedUser");

        User oldUserDto = userMapper.userEntityToDto(oldUser);
        User newUserDto = userMapper.userEntityToDto(newUser);

        when(usersRepository.findByEmail(emailForTest)).thenReturn(Optional.of(oldUser));
        when(usersRepository.save(any(UserEntity.class))).thenReturn(newUser);
        newUser = usersRepository.save(newUser);

        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders
                        .patch("http://localhost:8080/users/me", oldUser)
                        .content(objectWriter.writeValueAsString(oldUserDto))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .with(csrf());

        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().json(objectWriter.writeValueAsString(newUserDto)));
        verify(usersRepository, times(1)).save(newUser);
    }

    @Test
    @DisplayName("404 PATCH http://localhost:8080/users/me")
    @WithMockUser(username = emailForTest, authorities = {"USER"})
    void updateUserNegativeTest() throws Exception {
        UserEntity user = generator.genUser(null, null);
        User userDto = userMapper.userEntityToDto(user);

        when(usersRepository.findByEmail(emailForTest)).thenReturn(Optional.empty());
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders
                        .patch("http://localhost:8080/users/me", user)
                        .content(objectWriter.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .with(csrf());
        mockMvc.perform(builder)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("200 PATCH http://localhost:8080/users/me/image")
    @WithMockUser(username = emailForTest, authorities = {"USER"})
    void updateUserAvatarTest() throws Exception {
        List<String> files = generator.getPathsOfFiles(avatarsDir);
        AvatarEntity avatar = new AvatarEntity();
        avatar.setId(777);
        UserEntity userEntity = generator.genUser(avatar, null);
        userEntity.setEmail(emailForTest);

        assertThat(generator.getPathsOfFiles(avatarsDir).size() >= 2).isTrue(); //для теста нужно >= 2 файлов
        byte[] data1 = new byte[0];
        byte[] data2 = data1;
        while (Arrays.equals(data1, data2)) {
            data1 = generator.generateDataFileOfImageFromDir(avatarsDir);
            data2 = generator.generateDataFileOfImageFromDir(avatarsDir);
        }
        String pathStr1 = avatarsDir + "/avatar_for_test.jpg";
        Path path1 = Path.of(pathStr1);
        if (!Files.exists(path1)) {
            Files.write(path1, data1);
        }
        avatar.setPath(pathStr1);

        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "image",
                "image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                data2);
        when(usersRepository.save(userEntity)).thenReturn(userEntity);
        when(usersRepository.findByEmail(userEntity.getEmail())).thenReturn(Optional.of(userEntity));
        when(avatarRepository.findById(avatar.getId())).thenReturn(Optional.of(avatar));
        when(avatarRepository.save(avatar)).thenReturn(avatar);

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .multipart("http://localhost:8080/users/me/image")
                .file(mockMultipartFile)
                .accept(MediaType.MULTIPART_FORM_DATA_VALUE)
                .with(csrf());
        mockMvc.perform(builder.with(request -> {
                    request.setMethod("PATCH");
                    return request;
                }))
                .andExpect(status().isOk());

        List<String> filesNew = generator.getPathsOfFiles(avatarsDir);
        assertThat(files.size() + 1).isEqualTo(filesNew.size()); //появился еще 1 файл?

        String newFile = filesNew.stream()
                .filter(s -> !files.contains(s))
                .findAny().orElse(null);
        assert newFile != null;
        Path newPath = Path.of(newFile);
        assertThat(Files.readAllBytes(newPath)).isEqualTo(data2); //новый аватар сохранен?
        assertThat(Files.exists(path1)).isFalse(); //старый аватар удален?
        Files.deleteIfExists(newPath);

        userEntity.setAvatar(null);
        when(usersRepository.save(userEntity)).thenReturn(userEntity);
        when(avatarRepository.save(any(AvatarEntity.class))).thenReturn(avatar);
        mockMvc.perform(builder)
                .andExpect(status().isOk());
        assertThat(Files.readAllBytes(newPath)).isEqualTo(data2); //новый аватар сохранен?
        assertThat(Files.exists(path1)).isFalse(); //старый аватар удален?
        Files.deleteIfExists(newPath);
        verify(avatarRepository, times(1)).save(avatar);
        verify(avatarRepository, times(2)).save(any(AvatarEntity.class));
        verify(usersRepository, times(2)).save(userEntity);
    }

    @Test
    @DisplayName("404 PATCH http://localhost:8080/users/me/image")
    @WithMockUser(username = emailForTest, authorities = {"USER"})
    void updateUserAvatarNegativeTest() throws Exception {
        when(usersRepository.findByEmail(any())).thenReturn(Optional.empty());
        MockMultipartFile mockMultipartFile = new MockMultipartFile("image", "image.jpg",
                MediaType.IMAGE_JPEG_VALUE, new byte[0]);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .multipart("http://localhost:8080/users/me/image")
                .file(mockMultipartFile)
                .accept(MediaType.MULTIPART_FORM_DATA)
                .with(csrf());
        ResultActions resultActions = mockMvc.perform(
                builder.with(request -> {
                    request.setMethod("PATCH");
                    return request;
                }));
        resultActions
                .andExpect(status().isNotFound());
    }
}