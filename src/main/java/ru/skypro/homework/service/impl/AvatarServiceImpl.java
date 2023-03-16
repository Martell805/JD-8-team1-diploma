package ru.skypro.homework.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.entity.AvatarEntity;
import ru.skypro.homework.exception.AvatarNotFoundException;
import ru.skypro.homework.exception.PosterNotFoundException;
import ru.skypro.homework.repository.AvatarRepository;
import ru.skypro.homework.service.AvatarService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Service
@Slf4j
public class AvatarServiceImpl implements AvatarService {
    private final String avatarsDir;
    private final AvatarRepository avatarRepository;

    public AvatarServiceImpl(
            @Value("${path.to.avatars.folder}") String avatarDir, AvatarRepository avatarRepository) {
        this.avatarsDir = avatarDir;
        this.avatarRepository = avatarRepository;
    }

    /**
     * Метод добавляет аватар и файл изображения
     *
     * @param file - файл изображения
     * @param filename - имя файла
     * @return AvatarEntity
     */
    @Override
    public AvatarEntity addAvatar(MultipartFile file, String filename) throws IOException {
        byte[] data = file.getBytes();
        Path path = generatePath(file, filename);
        Files.write(path, data);
        AvatarEntity newAvatar = avatarRepository.save(new AvatarEntity(path.toString()));
        log.info("Аватар с ID: {} добавлен", newAvatar.getId());
        return newAvatar;
    }

    /**
     * Метод получения аватара
     *
     * @param avatarEntity - аватар
     */
    @Override
    public Pair<byte[], String> getAvatarData(AvatarEntity avatarEntity) {
        if (avatarEntity == null) {
            throw new IllegalArgumentException();
        }
        return getByteStringPair(avatarEntity.getId(), log, avatarEntity.getPath(), "Аватар");
    }

    /**
     * Метод изменения аватара
     *
     * @param avatar - постер к изменению
     * @param file - файл изображения
     * @param filename - имя файла
     */
    @Override
    public AvatarEntity updateAvatar(AvatarEntity avatar, MultipartFile file, String filename) {
        if (avatar == null || avatar.getId() == null) {
            throw new IllegalArgumentException();
        }
        Path oldPath = Paths.get(avatar.getPath());
        Path newPath = generatePath(file, filename);
        try {
            Files.write(newPath, file.getBytes());
            if (Files.exists(newPath)) {
                avatar.setPath(newPath.toString());
                avatar = avatarRepository.save(avatar);
                Files.deleteIfExists(oldPath);
            }
        } catch (IOException ignored) {
            throw new AvatarNotFoundException();
        }
        return avatar;
    }

    /**
     * Метод удаления аватара и файла с изображением
     *
     * @param avatarEntity - постер к удалению
     */
    @Override
    public void deleteAvatar(AvatarEntity avatarEntity) {
        Path path = Path.of(avatarEntity.getPath());
        try {
            Files.deleteIfExists(path);
        } catch (IOException ignored) {
            log.error("Ошибка удаления аватара");
        }
        avatarRepository.delete(avatarEntity);
    }

    /**
     * Метод генерирует путь к аватару (path)
     *
     * @param file     - файл изображения
     * @param filename - имя файла
     * @return String path
     */
    @Override
    public Path generatePath(MultipartFile file, String filename) {
        return getPath(file, filename, avatarsDir);
    }

    /**
     * Метод получения пути к файлу
     */
    static Path getPath(MultipartFile file, String filename, String imageDir) {
        String extension, path;
        int count = 0;
        Path resultingPath = Paths.get(imageDir);
        if (file.getOriginalFilename() == null) {
            extension = ".jpg";
        } else {
            extension = Optional.ofNullable(file.getOriginalFilename())
                    .map(fileName -> fileName.substring(file.getOriginalFilename().lastIndexOf('.')))
                    .orElse("");
        }
        do {
            path = filename + "_" + count++ + extension;
        } while (Files.exists(resultingPath.resolve(path)));
        return resultingPath.resolve(path);
    }

    /**
     * Метод чтения файла изображения
     */
    static Pair<byte[], String> getByteStringPair(Integer id, Logger log, String path, String objectName) {
        if (id == null) {
            log.error(objectName + " или его ID = null");
            throw new IllegalArgumentException();
        }
        try {
            log.debug("Читаем байты по адресу: {}", path);
            return Pair.of(Files.readAllBytes(Paths.get(path)), MediaType.IMAGE_JPEG_VALUE);
        } catch (IOException ignored) {
            log.error(objectName + " с ID: {}. Отсутствует файл.", id);
            throw new PosterNotFoundException();
        } catch (NullPointerException e) {
            log.error(objectName + " с ID: {}. Отсутствует путь.", id);
            throw new PosterNotFoundException();
        }
    }
}
