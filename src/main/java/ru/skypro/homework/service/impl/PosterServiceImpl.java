package ru.skypro.homework.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.entity.PosterEntity;
import ru.skypro.homework.exception.PosterNotFoundException;
import ru.skypro.homework.repository.PosterRepository;
import ru.skypro.homework.service.PosterService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static ru.skypro.homework.service.impl.AvatarServiceImpl.getPath;
import static ru.skypro.homework.service.impl.AvatarServiceImpl.getByteStringPair;

@Slf4j
@Service
public class PosterServiceImpl implements PosterService {

    private final String postersDir;
    private final PosterRepository posterRepository;

    public PosterServiceImpl(@Value("${path.to.posters.folder}") String postersDir, PosterRepository posterRepository) {
        this.postersDir = postersDir;
        this.posterRepository = posterRepository;
    }
    @Override
    public PosterEntity addPoster(MultipartFile file, String filename) throws IOException {
        byte[] data = file.getBytes();
        Path path = generatePath(file, filename);
        Files.write(path, data);
        PosterEntity newPoster = posterRepository.save(new PosterEntity(path.toString()));
        log.info("Постер с ID: {} добавлен", newPoster.getId());
        return newPoster;
    }
    @Override
    public Pair<byte[], String> getPosterData(PosterEntity posterEntity) {
        if (posterEntity == null) {
            throw new IllegalArgumentException();
        }
        return getByteStringPair(posterEntity.getId(), log, posterEntity.getPath(), "Постер");
    }
    @Override
    public PosterEntity updatePoster(PosterEntity poster, MultipartFile file, String filename) {
        if (poster == null || poster.getId() == null) {
            throw new IllegalArgumentException();
        }
        Path oldPath = Paths.get(poster.getPath());
        Path newPath = generatePath(file, filename);
        try {
            Files.write(newPath, file.getBytes());
            if (Files.exists(newPath)) {
                poster.setPath(newPath.toString());
                poster = posterRepository.save(poster);
                Files.deleteIfExists(oldPath);
            }
        } catch (IOException ignored) {
            throw new PosterNotFoundException();
        }
        return poster;
    }
    @Override
    public void deletePoster(PosterEntity posterEntity) {
        Path path = Path.of(posterEntity.getPath());
        try {
            Files.deleteIfExists(path);
        } catch (IOException ignored) {
            log.error("Ошибка удаления постера");
        }
        posterRepository.delete(posterEntity);
    }
    @Override
    public Path generatePath(MultipartFile file, String filename) {
        return getPath(file, filename, postersDir);
    }
}
