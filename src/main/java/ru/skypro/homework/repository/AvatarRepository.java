package ru.skypro.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skypro.homework.entity.AvatarEntity;
import java.util.Optional;

@Repository
public interface AvatarRepository extends JpaRepository<AvatarEntity, Integer> {
    Optional<AvatarEntity> findByUserId(Integer userId);
}
