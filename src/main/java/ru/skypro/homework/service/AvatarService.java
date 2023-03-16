package ru.skypro.homework.service;

import org.springframework.data.util.Pair;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.entity.AvatarEntity;

import java.io.IOException;
import java.nio.file.Path;

public interface AvatarService {
    AvatarEntity addAvatar(MultipartFile file, String filename) throws IOException;

    Pair<byte[], String> getAvatarData(AvatarEntity avatarEntity);

    AvatarEntity updateAvatar(AvatarEntity avatar, MultipartFile file, String filename);

    void deleteAvatar(AvatarEntity avatarEntity);

    Path generatePath(MultipartFile file, String filename);
}
