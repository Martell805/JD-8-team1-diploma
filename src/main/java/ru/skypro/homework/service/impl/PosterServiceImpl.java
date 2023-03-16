package ru.skypro.homework.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.Poster;
import ru.skypro.homework.entity.PosterEntity;
import ru.skypro.homework.exception.PosterNotFoundException;
import ru.skypro.homework.mapping.PosterMapper;
import ru.skypro.homework.repository.PosterRepository;
import ru.skypro.homework.service.PosterService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class PosterServiceImpl implements PosterService {

    private final String postersDir;
    private final PosterRepository posterRepository;
    private final PosterMapper posterMapping;

    public PosterServiceImpl(@Value("${path.to.posters.folder}") String postersDir, PosterRepository posterRepository, PosterMapper posterMapping) {
        this.postersDir = postersDir;
        this.posterRepository = posterRepository;
        this.posterMapping = posterMapping;
    }

    public PosterEntity getById(Integer id) {
        return posterRepository.findById(id).orElseThrow(PosterNotFoundException::new);
    }

    public PosterEntity getByAdsId(Integer adsId) {
        return posterRepository.findByAdsId(adsId).orElseThrow(PosterNotFoundException::new);
    }

    public PosterEntity savePoster(PosterEntity posterEntity) {
        return posterRepository.save(posterEntity);
    }

    public PosterEntity savePoster(Poster poster) {
        return posterRepository.save(fromDTOToEntity(poster));
    }

    public void deletePosterById(Integer id) {
        posterRepository.deleteById(id);
    }

    public Poster fromEntityToDTO(PosterEntity PosterEntity) {
        return posterMapping.toDto(PosterEntity);
    }

    public PosterEntity fromDTOToEntity(Poster poster) {
        return posterMapping.toEntity(poster);
    }

    public List<Poster> fromEntityListToDTOList(List<PosterEntity> posterEntities) {
        return posterMapping.toDtoList(posterEntities);
    }

    public List<PosterEntity> fromDTOListToEntityList(List<Poster> posters) {
        return posterMapping.toEntityList(posters);
    }

    public Pair<byte[], String> getPosterData(PosterEntity posterEntity) {
        if (posterEntity == null || posterEntity.getId() == null) {
            log.error("Постер или ID постера null");
            throw new IllegalArgumentException();
        }
        try {
            log.debug("Читаем байты по адресу: {}", posterEntity.getPath());
            return Pair.of(Files.readAllBytes(Paths.get(posterEntity.getPath())), MediaType.IMAGE_JPEG_VALUE);
        } catch (IOException ignored) {
            log.error("Отсутствует файл постера с ID: {}", posterEntity.getId());
            throw new PosterNotFoundException();
        } catch (NullPointerException e) {
            log.error("Отсутствует путь в постере с ID: {}", posterEntity.getId());
            throw new PosterNotFoundException();
        }
    }

    /**
     * Метод добавляет постер - картинку
     *
     * @param file - файл изображения
     * @param filename - имя файла
     * @return PosterEntity
     */
    public PosterEntity addPoster(MultipartFile file, String filename) throws IOException {
        byte[] data = file.getBytes();
        Path path = generatePath(file, filename);
        Files.write(path, data);
        PosterEntity newPoster = posterRepository.save(new PosterEntity(path.toString()));
        log.info("Постер с ID: {} добавлен", newPoster.getId());
        return newPoster;
    }

    @Override
    public PosterEntity updatePoster(PosterEntity poster, MultipartFile file, String filename) {
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

    /**
     * Метод генерирует путь к постеру (path)
     *
     * @param file - файл изображения
     * @param filename - имя файла     
     * @return String
     */
    public Path generatePath(MultipartFile file, String filename) {
        String extension, path;
        int count = 0;
        Path resultingPath = Paths.get(postersDir);
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
     * Метод удаления постера и файла с изображением
     *
     * @param posterEntity - постер к удалению
     */
    @Override
    public void deletePoster(PosterEntity posterEntity) {
        Path path = Path.of(posterEntity.getPath());
        try {
            Files.deleteIfExists(path);
        } catch (IOException ignored) {
            log.error("Ошибка удаления постера");
        }
        posterRepository.delete(posterEntity);
        posterRepository.findById(posterEntity.getId());
    }
}
