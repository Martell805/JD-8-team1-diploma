package ru.skypro.homework.service;

import org.springframework.security.core.Authentication;
import ru.skypro.homework.dto.Comment;
import ru.skypro.homework.dto.ResponseWrapperComment;
import ru.skypro.homework.entity.CommentEntity;
import ru.skypro.homework.exception.AdsNotFoundException;
import ru.skypro.homework.exception.CommentNotFoundException;

public interface CommentService {
    /**
     * Добавление нового комментария к объявлению.
     *
     * @param id             - id объявления;
     * @param comment        - {@link Comment};
     * @param authentication - аутентификация пользователя;
     * @return Созданный комментарий;
     * @throws AdsNotFoundException - не найдено объявление
     */
    Comment addComment(Integer id, Comment comment, Authentication authentication);
    /**
     * Обновление комментария.
     *
     * @param adId      - id объявления;
     * @param commentId - id комментария;
     * @param comment   - {@link Comment};
     * @return Обновленный комментарий;
     * @throws CommentNotFoundException - не найден комментарий
     */
    Comment updateComment(Integer adId, Integer commentId, Comment comment, String authorName);

    /**
     * Получения комментария по id.
     *
     * @param adId - id объявления;
     * @param commentId - id комментария;
     * @return Найденый комментарий;
     * @throws CommentNotFoundException - не найден комментарий
     */
    Comment getComment(Integer adId, Integer commentId);

    /**
     * Получения сущьности комментария по id.
     *
     * @param id - id комментария;
     * @return Найденый комментарий;
     * @throws CommentNotFoundException - не найден комментарий
     */
    CommentEntity getCommentEntityById(Integer id);

    /**
     * Удаление комментария по id.
     *
     * @param adId - id объявления;
     * @param commentId - id комментария;
     * @throws CommentNotFoundException - не найден комментарий
     */
    void deleteComment(Integer adId, Integer commentId);
    /**
     * Получения списка все комментариев к объявлению.
     *
     * @param id - id объявления;
     * @return Список всех комментариев.
     */
    ResponseWrapperComment getAllCommentsByAd(Integer id);

    /**
     * Метод удаляет все комментарии к объявлению по его ID, используя nativeQuery
     *
     * @param id - ID объявления
     */
    void removeAllCommentsOfAds(Integer id);
}
