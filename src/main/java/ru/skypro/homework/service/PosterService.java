package ru.skypro.homework.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.skypro.homework.reposutory.PosterRepository;

@Service
@RequiredArgsConstructor
public class PosterService {
    private final PosterRepository posterRepository;
}
