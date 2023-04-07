package ru.skypro.homework.mapping;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.skypro.homework.dto.Comment;
import ru.skypro.homework.dto.ResponseWrapperComment;
import ru.skypro.homework.entity.CommentEntity;

import java.util.List;


@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "author.id", source = "author")
    @Mapping(target = "text", source = "text")
    @Mapping(target = "createdAt", source = "createdAt", dateFormat = "yyyy-MM-dd HH:mm")
    CommentEntity commentDtoToEntity(Comment comment);

    @Mapping(target = "authorFirstName", source = "author.firstName")
    @Mapping(target = "authorAvatar", source = "commentEntity")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "author", source = "author.id")
    @Mapping(target = "text", source = "text")
    @Mapping(target = "createdAt", source = "createdAt", dateFormat = "yyyy-MM-dd HH:mm")
    Comment commentEntityToDto(CommentEntity commentEntity);

    default String mapAvatarToString(CommentEntity commentEntity) {
        if (commentEntity.getAuthor() == null || commentEntity.getAuthor().getAvatar() == null) {
            return null;
        } else {
            return "/users/" + commentEntity.getAuthor().getId() + "/image";
        }
    }

    @Mapping(target = "results", source = "list")
    ResponseWrapperComment mapToResponseWrapperComments(List<Comment> list, Integer count);

    List<Comment> modelToDtoList(List<CommentEntity> comments);
}
