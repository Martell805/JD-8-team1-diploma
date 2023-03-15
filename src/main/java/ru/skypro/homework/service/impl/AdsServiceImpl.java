package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.Ads;
import ru.skypro.homework.dto.CreateAds;
import ru.skypro.homework.dto.FullAds;
import ru.skypro.homework.dto.ResponseWrapperAds;
import ru.skypro.homework.entity.AdsEntity;
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.exception.AdsNotFoundException;
import ru.skypro.homework.exception.PosterNotFoundException;
import ru.skypro.homework.mapping.AdsMapper;
import ru.skypro.homework.mapping.CreateAdsMapper;
import ru.skypro.homework.mapping.FullAdsMapper;
import ru.skypro.homework.repository.AdsRepository;
import ru.skypro.homework.service.AdsService;
import ru.skypro.homework.service.PosterService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdsServiceImpl implements AdsService {

    private final AdsMapper adsMapper;
    private final CreateAdsMapper createAdsMapper;
    private final FullAdsMapper fullAdsMapper;
    private final AdsRepository adsRepository;
    private final UserServiceImpl userServiceImpl;
    private final PosterService posterService;

    @Override
    public Ads addAds(CreateAds properties, MultipartFile image, String email) {

        // mapping from dto to entity
        UserEntity author = userServiceImpl.getUserByEmail(email);
        AdsEntity adsEntity = createAdsMapper.toModel(properties);
        adsEntity.setAuthor(author);
        //TODO картинка
        return adsMapper.toDto(adsRepository.save(adsEntity));
    }

    @Override
    public void deleteAds(Integer id) {
        AdsEntity adsEntity = adsRepository.findById(id).orElseThrow(() -> new AdsNotFoundException(id));
        adsRepository.deleteById(id);
    }

    @Override
    public Ads updateAds(Integer id, CreateAds createAds) {
        AdsEntity adsEntity = adsRepository.findById(id).orElseThrow(() -> new AdsNotFoundException(id));
        adsEntity.setDescription(createAds.getDescription());
        adsEntity.setTitle(createAds.getTitle());
        adsEntity.setPrice(createAds.getPrice());
        return adsMapper.toDto(adsRepository.save(adsEntity));
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

        UserEntity author = userServiceImpl.getUserByEmail(email);
        ResponseWrapperAds responseWrapperAds = new ResponseWrapperAds();
        responseWrapperAds.setResults(adsMapper.toAdsDtoList(adsRepository.findAdsEntityByAuthor_Id(author.getId())));
        responseWrapperAds.setCount(responseWrapperAds.getResults().size());
        return responseWrapperAds;
    }

    /**
     * Метод получает постер для об объявления по его ID
     *
     * @param adsId ID объявления
     * @return poster
     */
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
