package ru.skypro.homework.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.skypro.homework.dto.Ads;
import ru.skypro.homework.dto.ResponseWrapperAds;
import ru.skypro.homework.entity.AdsEntity;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AdsMapper {
    @Mapping(target = "id", source = "entity.id")
    @Mapping(target = "title", source = "entity.title")
    @Mapping(target = "imageId", source = "entity")
    @Mapping(target = "author", source = "entity.author.id")
    @Mapping(target = "price", source = "entity.price")
    Ads toDto(AdsEntity entity);

    List<Ads> toAdsDtoList(List<AdsEntity> entityList);

    @Mapping(target = "results", source = "list")
    ResponseWrapperAds mapToResponseWrapperAdsDto(List<Ads> list, Integer count);

    default String mapImageToString(AdsEntity adsEntity) {
        if (adsEntity.getImage() == null || adsEntity.getImage().getId() == null) {
            return null;
        } else {
            return "/ads/" + adsEntity.getId() + "/image/";
        }
    }
}
