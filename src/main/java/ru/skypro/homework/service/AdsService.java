package ru.skypro.homework.service;

import org.springframework.data.util.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.Ads;
import ru.skypro.homework.dto.CreateAds;
import ru.skypro.homework.dto.FullAds;
import ru.skypro.homework.dto.ResponseWrapperAds;
import ru.skypro.homework.exception.AdsNotFoundException;

import java.io.IOException;

/**
 * Сервис объявлений
 */

public interface AdsService {

    /**
     * Добавляет новое объявление
     *
     * @param properties     {@link CreateAds}
     * @param image
     * @param username
     * @return Созданное объявление
     */
    Ads addAds(CreateAds properties, MultipartFile image, String username) throws IOException;

    /**
     * Удаляет запись из БД по id
     *
     * @param id
     * @throws AdsNotFoundException исключение, если запись с id не найдена
     */
    ResponseEntity<Void> deleteAds(Integer id);

    /**
     * Изменяет объявление
     *
     * @param id
     * @param createAds
     * @return
     */
    Ads updateAds(Integer id, CreateAds createAds);

    /**
     * Получает объявление по id
     *
     * @param id
     * @return
     */
    FullAds getFullAds(Integer id);

    /**
     * Получаем все объявления
     *
     * @return
     */
    ResponseWrapperAds getAllAds();

    /**
     * Возвращает объявления конкретного пользователя
     *
     * @param username
     * @return
     */
    ResponseWrapperAds getAdsMe(String username);

    Pair<byte[], String> getPoster(Integer idAds);

    Pair<byte[], String> updatePosterOfAds(Integer adsId, MultipartFile image) throws IOException;
}

