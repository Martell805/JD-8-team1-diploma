package ru.skypro.homework.mapping;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.skypro.homework.dto.Comment;
import ru.skypro.homework.entity.CommentEntity;

import java.util.List;


@Mapper(componentModel = "spring")
public interface  CommentMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "author.id", source = "author")
    @Mapping(target = "text", source = "text")
    @Mapping(target = "createdAt", source = "createdAt")
     CommentEntity commentDtoToEntity(Comment comment);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "author", source = "author.id")
    @Mapping(target = "text", source = "text")
    @Mapping(target = "createdAt", source = "createdAt")
     Comment commentEntityToDto(CommentEntity commentEntity);

    List<CommentEntity> dtoToModel(List<Comment> comments);

    List<Comment> modelToDto (List<CommentEntity> comments);
}
