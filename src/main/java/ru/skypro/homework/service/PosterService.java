package ru.skypro.homework.service;

import org.springframework.data.util.Pair;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.entity.PosterEntity;

import java.io.IOException;
import java.nio.file.Path;

public interface PosterService {

    PosterEntity addPoster(MultipartFile file, String nameFile) throws IOException;

    Pair<byte[], String> getPosterData(PosterEntity posterEntity);

    PosterEntity updatePoster(PosterEntity poster, MultipartFile file, String nameFile);

    void deletePoster(PosterEntity image);

    Path generatePath(MultipartFile file, String nameFile);
}
