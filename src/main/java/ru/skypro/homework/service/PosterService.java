package ru.skypro.homework.service;

import org.springframework.data.util.Pair;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.entity.PosterEntity;

import java.io.IOException;
import java.nio.file.Path;

public interface PosterService {

    /**
     * Метод добавляет постер и файл изображения
     *
     * @param file - файл изображения
     * @param nameFile - имя файла
     * @return PosterEntity
     */
    PosterEntity addPoster(MultipartFile file, String nameFile) throws IOException;

    /**
     * Метод получения постера
     *
     * @param posterEntity - постер
     */
    Pair<byte[], String> getPosterData(PosterEntity posterEntity);

    /**
     * Метод изменения постера
     *
     * @param poster - постер к изменению
     * @param file - файл изображения
     * @param nameFile - имя файла
     */
    PosterEntity updatePoster(PosterEntity poster, MultipartFile file, String nameFile);

    /**
     * Метод удаления постера и файла с изображением
     *
     * @param image - постер к удалению
     */
    void deletePoster(PosterEntity image);

    /**
     * Метод генерирует путь к постеру (path)
     *
     * @param file - файл изображения
     * @param nameFile - имя файла
     * @return String path
     */
    Path generatePath(MultipartFile file, String nameFile);
}
