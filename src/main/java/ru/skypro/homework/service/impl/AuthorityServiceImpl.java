package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.entity.Authority;
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.repository.AuthorityRepository;
import ru.skypro.homework.service.AuthorityService;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthorityServiceImpl implements AuthorityService {
    private final AuthorityRepository authorityRepository;

    @Override
    public Authority addAuthority(UserEntity userEntity, Role role) {
        Authority authority = new Authority();
        authority.setAuthority(role.getRole());
        authority.setUsername(userEntity.getUsername());
        Authority newAuthority = authorityRepository.save(authority);
        log.info("Создали Authority {} пользователя {}",
                newAuthority.getAuthority(), newAuthority.getUsername());
        return newAuthority;
    }
}
