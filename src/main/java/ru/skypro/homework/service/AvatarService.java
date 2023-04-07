package ru.skypro.homework.service;

import org.springframework.data.util.Pair;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.entity.AvatarEntity;

import java.io.IOException;
import java.nio.file.Path;

public interface AvatarService {
    /**
     * Метод добавляет аватар и файл изображения
     *
     * @param file - файл изображения
     * @param filename - имя файла
     * @return AvatarEntity
     */
    AvatarEntity addAvatar(MultipartFile file, String filename) throws IOException;

    /**
     * Метод получения аватара
     *
     * @param avatarEntity - аватар
     */
    Pair<byte[], String> getAvatarData(AvatarEntity avatarEntity);

    /**
     * Метод изменения аватара
     *
     * @param avatar - постер к изменению
     * @param file - файл изображения
     * @param filename - имя файла
     */
    AvatarEntity updateAvatar(AvatarEntity avatar, MultipartFile file, String filename);

    /**
     * Метод удаления аватара и файла с изображением
     *
     * @param avatarEntity - постер к удалению
     */
    void deleteAvatar(AvatarEntity avatarEntity);

    /**
     * Метод генерирует путь к аватару (path)
     *
     * @param file     - файл изображения
     * @param filename - имя файла
     * @return String path
     */
    Path generatePath(MultipartFile file, String filename);
}
