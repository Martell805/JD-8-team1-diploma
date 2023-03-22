package ru.skypro.homework;

import com.github.javafaker.Faker;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.entity.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class Generator {

    private final Faker faker = new Faker();
    private final Random random = new Random();
    private final PasswordEncoder encoder = new BCryptPasswordEncoder();

    public UserEntity genUser(Integer id, String email, String firstName,
                              String lastName, String phone, LocalDate regDate,
                              AvatarEntity avatarEntity, String password, boolean needGenerate) {
        if (needGenerate) {
            id = generateIdIfEmpty(id);
            firstName = generateFirstNameIfEmpty(firstName);
            lastName = generateLastNameIfEmpty(lastName);
            email = generateEmailIfEmpty(email);
            phone = generatePhoneIfEmpty(phone);
            regDate = generateDate(LocalDate.now().minusYears(1), LocalDate.now());
            password = generatePasswordIfEmpty(password, true);
        }
        UserEntity userEntity = new UserEntity();
        userEntity.setId(id);
        userEntity.setEmail(email);
        userEntity.setFirstName(firstName);
        userEntity.setLastName(lastName);
        userEntity.setPhone(phone);
        userEntity.setUsername(email);
        userEntity.setRegDate(regDate);
        userEntity.setAvatar(avatarEntity);
        userEntity.setPassword(password);
        userEntity.setEnabled(true);

        return userEntity;
    }

    public UserEntity genUser(AvatarEntity avatarEntity, String pass) {
        return genUser(null, null, null, null, null, null,
                avatarEntity, pass, true);
    }

    public AvatarEntity genAvatar(int id, String path) {
        AvatarEntity avatarEntity = new AvatarEntity();
        avatarEntity.setId(id);
        avatarEntity.setPath(path);

        return avatarEntity;
    }

    public String generatePasswordIfEmpty(String password, boolean bcrypt) {
        if (password == null) {
            password = faker.internet().password();
        }
        if (bcrypt) {
            CharSequence charSequence = new StringBuilder(password);
            return "{bcrypt}" + encoder.encode(charSequence);
        } else {
            return password;
        }
    }

    private String getPathImageNameNotExist(String dirToCopy) {
        String filePath;
        do {
            filePath = faker.file().fileName(
                    dirToCopy, null, "jpg", null);
        } while (Files.exists(Path.of(filePath)));
        return filePath;
    }

    public AvatarEntity generateAvatarIfNull(AvatarEntity avatar, String dirForAvatars, String dirToCopyOrNull) throws IOException {
        if (avatar == null) {
            avatar = new AvatarEntity();
            avatar.setId(generateIdIfEmpty(null));
            if (dirForAvatars == null || dirForAvatars.length() == 0) {
                avatar.setPath(faker.file().fileName());
            } else {
                List<String> pathsOfFiles = getPathsOfFiles(dirForAvatars);
                String pathRandom = pathsOfFiles.get(random.nextInt(pathsOfFiles.size()));
                if (dirToCopyOrNull != null) {
                    File file = new File(dirToCopyOrNull);
                    if (!Files.exists(file.toPath())) {
                        file.mkdirs();
                    }
                    String filePath = getPathImageNameNotExist(dirToCopyOrNull);
                    Files.copy(Path.of(pathRandom), Path.of(filePath));
                    avatar.setPath(filePath);
                } else {
                    avatar.setPath(pathRandom);
                }
            }
        }
        return avatar;
    }

    public byte[] generateDataFileOfImageFromDir(String dirForImages) {
        List<String> pathsOfFiles = getPathsOfFiles(dirForImages);
        try {
            return Files.readAllBytes(Path.of(pathsOfFiles.get(random.nextInt(pathsOfFiles.size()))));
        } catch (IOException ignored) {
        }
        return faker.avatar().image().getBytes();
    }

    public List<String> getPathsOfFiles(String path) {
        File dir = new File(path);
        File[] files = dir.listFiles();
        assert files != null;
        return Arrays.stream(files).map(file -> path + "/" + file.getName()).collect(Collectors.toList());
    }

    public LocalDate generateDate(LocalDate startInclusive, LocalDate endExclusive) {
        long startEpochDay = startInclusive.toEpochDay();
        long endEpochDay = endExclusive.toEpochDay();
        long randomDay = ThreadLocalRandom
                .current()
                .nextLong(startEpochDay, endEpochDay);
        return LocalDate.ofEpochDay(randomDay);
    }

    public Authority genAuthority(UserEntity userEntity, Role role) {
        Authority authority = new Authority();
        if (userEntity == null || userEntity.getUsername() == null) {
            authority.setUsername(faker.name().username());
        } else {
            authority.setUsername(userEntity.getUsername());
        }
        if (role == null) {
            authority.setAuthority(genRoleIfEmpty(null).toString());
        } else {
            authority.setAuthority(role.getRole());
        }
        authority.setAuthority(authority.getAuthority());
        authority.setId(generateIdIfEmpty(null));
        return authority;
    }

    public Role genRoleIfEmpty(Role role) {
        if (role == null) {
            Role[] roleList = Role.values();
            return roleList[random.nextInt(roleList.length)];
        }
        return role;
    }

    public String generatePhoneIfEmpty(String phone) {
        if (phone == null || phone.length() == 0) {
            StringBuilder sb = new StringBuilder("+79");
            for (int i = 0; i < 9; i++) {
                sb.append(random.nextInt(10));
            }
            return sb.toString();
        }
        return phone;
    }

    public String generateFirstNameIfEmpty(String name) {
        if (name == null || name.length() == 0) {
            return faker.name().firstName();
        }
        return name.substring(0, 29);
    }

    public String generateLastNameIfEmpty(String name) {
        if (name == null || name.length() == 0) {
            return faker.name().lastName();
        }
        return name.substring(0, 29);
    }

    public String generateEmailIfEmpty(String email) {
        if (email == null || email.length() == 0) {
            return faker.internet().emailAddress();
        }
        return email;
    }

    public Integer generateIdIfEmpty(Integer id) {
        if (id == null || id < 0) {
            int idTemp = -1;
            while (idTemp < 0) {
                idTemp = random.nextInt();
            }
            return idTemp;
        }
        return id;
    }
}
