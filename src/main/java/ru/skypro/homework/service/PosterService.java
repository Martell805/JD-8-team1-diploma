package ru.skypro.homework.service;

import org.springframework.data.util.Pair;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.entity.PosterEntity;

import java.io.IOException;
import java.nio.file.Path;

public interface PosterService {
    Pair<byte[], String> getPosterData(PosterEntity posterEntity);

    PosterEntity addPoster(MultipartFile file, String nameFile) throws IOException;

    PosterEntity updatePoster(PosterEntity poster, MultipartFile file, String nameFile);

    Path generatePath(MultipartFile file, String nameFile);

    void deletePoster(PosterEntity image);
}
