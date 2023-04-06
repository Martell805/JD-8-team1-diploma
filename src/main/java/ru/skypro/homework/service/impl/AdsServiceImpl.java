package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.Ads;
import ru.skypro.homework.dto.CreateAds;
import ru.skypro.homework.dto.FullAds;
import ru.skypro.homework.dto.ResponseWrapperAds;
import ru.skypro.homework.entity.AdsEntity;
import ru.skypro.homework.entity.PosterEntity;
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.exception.AdsNotFoundException;
import ru.skypro.homework.exception.PosterNotFoundException;
import ru.skypro.homework.mapping.AdsMapper;
import ru.skypro.homework.mapping.CreateAdsMapper;
import ru.skypro.homework.mapping.FullAdsMapper;
import ru.skypro.homework.repository.AdsRepository;
import ru.skypro.homework.service.AdsService;
import ru.skypro.homework.service.CommentService;
import ru.skypro.homework.service.PosterService;
import ru.skypro.homework.service.UserService;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdsServiceImpl implements AdsService {

    private final AdsMapper adsMapper;
    private final CreateAdsMapper createAdsMapper;
    private final FullAdsMapper fullAdsMapper;
    private final AdsRepository adsRepository;
    private final UserService userService;
    private final PosterService posterService;
    private final CommentService commentService;

    @Override
    public Ads addAds(CreateAds properties, MultipartFile image, String email) throws IOException {
        UserEntity author = userService.getUserByEmail(email);
        AdsEntity adsEntity = createAdsMapper.toModel(properties);
        adsEntity.setAuthor(author);
        adsEntity = adsRepository.save(adsEntity);
        PosterEntity poster = posterService.addPoster(image, "poster_Ads" + adsEntity.getId());
        adsEntity.setImage(poster);
        Ads ads = adsMapper.toDto(adsRepository.save(adsEntity));
        log.info("Создали объявление с ID: {}", ads.getId());
        return ads;
    }

    @Override
    public ResponseEntity<Void> deleteAds(Integer id) {
        AdsEntity adsEntity = adsRepository.findById(id).orElseThrow(() -> {
            log.error("Удаление: Объявление с ID {} не найдено.", id);
            return new AdsNotFoundException(id);
        });
        commentService.removeAllCommentsOfAds(adsEntity.getId());
        adsRepository.deleteById(id);
        posterService.deletePoster(adsEntity.getImage());
        log.info("Удалили объявление с ID: {}", id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public Ads updateAds(Integer id, CreateAds createAds) {
        AdsEntity adsEntity = adsRepository.findById(id).orElseThrow(() -> {
            log.error("Обновление: Объявление с ID {} не найдено.", id);
            return new AdsNotFoundException(id);
        });
        adsEntity.setDescription(createAds.getDescription());
        adsEntity.setTitle(createAds.getTitle());
        adsEntity.setPrice(createAds.getPrice());
        Ads ads = adsMapper.toDto(adsRepository.save(adsEntity));
        log.info("Изменили объявление с ID: {}", ads.getId());
        return ads;
    }

    @Override
    public Pair<byte[], String> updatePosterOfAds(Integer id, MultipartFile image) throws IOException {
        AdsEntity adsEntity = adsRepository.findById(id).orElseThrow(() -> {
            log.error("Изменение постера: Объявление с ID {} не найдено.", id);
            return new AdsNotFoundException(id);
        });
        if (adsEntity.getImage() == null) {
            adsEntity.setImage(posterService.addPoster(image, "poster_Ads" + adsEntity.getId()));
        } else {
            adsEntity.setImage(posterService.updatePoster(adsEntity.getImage(), image, "poster_Ads" + adsEntity.getId()));
        }
        adsEntity = adsRepository.save(adsEntity);
        log.info("Обновлён постер объявления с ID {}", id);
        return posterService.getPosterData(adsEntity.getImage());
    }

    @Override
    public FullAds getFullAds(Integer id) {
        return fullAdsMapper.toDto(adsRepository.findById(id).orElseThrow(() -> new AdsNotFoundException(id)));
    }

    @Override
    public ResponseWrapperAds getAllAds() {
        List<Ads> listDto = adsMapper.toAdsDtoList(
                adsRepository.findAll()
        );
        return adsMapper.mapToResponseWrapperAdsDto(listDto, listDto.size());
    }

    @Override
    public ResponseWrapperAds getAdsMe(String email) {

        UserEntity author = userService.getUserByEmail(email);
        ResponseWrapperAds responseWrapperAds = new ResponseWrapperAds();
        responseWrapperAds.setResults(adsMapper.toAdsDtoList(adsRepository.findAdsEntityByAuthor_Id(author.getId())));
        responseWrapperAds.setCount(responseWrapperAds.getResults().size());
        return responseWrapperAds;
    }

    @Override
    public AdsEntity getAds(Integer id) {
        return adsRepository.findById(id).orElseThrow(() -> new AdsNotFoundException(id));
    }

    @Override
    public Pair<byte[], String> getPoster(Integer adsId) {
        AdsEntity ads = adsRepository.findById(adsId).orElseThrow(() -> {
            log.error("Объявление с ID: {} не найдено", adsId);
            return new AdsNotFoundException(adsId);
        });
        if (ads.getImage() == null) {
            log.error("Постер для объявления с ID: {} null", ads.getId());
            throw new PosterNotFoundException();
        }
        return posterService.getPosterData(ads.getImage());
    }
}
