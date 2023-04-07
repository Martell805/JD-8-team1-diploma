package ru.skypro.homework.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.skypro.homework.dto.FullAds;
import ru.skypro.homework.entity.AdsEntity;

@Mapper(componentModel = "spring")
public interface FullAdsMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "authorFirstName", source = "author.firstName")
    @Mapping(target = "authorLastName", source = "author.lastName")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "email", source = "author.email")
    @Mapping(target = "image", source = "entity")
    @Mapping(target = "phone", source = "author.phone")
    @Mapping(target = "price", source = "price")
    @Mapping(target = "title", source = "title")
    FullAds toDto(AdsEntity entity);

    default String mapImageToString(AdsEntity adsEntity) {
        if (adsEntity.getImage() == null || adsEntity.getImage().getId() == null) {
            return null;
        } else {
            return "/ads/" + adsEntity.getId() + "/image/";
        }
    }
}
