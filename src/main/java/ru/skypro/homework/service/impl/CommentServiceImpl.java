package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.Comment;
import ru.skypro.homework.dto.ResponseWrapperComment;
import ru.skypro.homework.entity.CommentEntity;
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.exception.AdsNotFoundException;
import ru.skypro.homework.exception.CommentNotFoundException;
import ru.skypro.homework.exception.UserForbiddenException;
import ru.skypro.homework.exception.UserNotFoundException;
import ru.skypro.homework.mapping.CommentMapper;
import ru.skypro.homework.repository.AdsRepository;
import ru.skypro.homework.repository.CommentRepository;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.CommentService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final AdsRepository adsRepository;
    private final UserRepository userRepository;

    @Override
    public Comment addComment(Integer id, Comment comment, Authentication authentication) {

        CommentEntity newComment = commentMapper.commentDtoToEntity(comment);
        newComment.setAuthor(userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UserNotFoundException(authentication.getName())));
        newComment.setAds(adsRepository.findById(id)
                .orElseThrow(() -> new AdsNotFoundException(id)));
        newComment.setCreatedAt(LocalDateTime.now());

        return commentMapper.commentEntityToDto(commentRepository.save(newComment));
    }

    @Override
    public Comment getComment(Integer adId, Integer commentId) {
        return commentMapper.commentEntityToDto(findComment(adId, commentId));
    }

    @Override
    public CommentEntity getCommentEntityById(Integer id) {
        return commentRepository.findById(id).orElseThrow(() -> new CommentNotFoundException(id));
    }

    @Override
    public ResponseWrapperComment getAllCommentsByAd(Integer id) {
        List<Comment> comments = commentMapper.modelToDtoList(
                commentRepository.findAllByAds_Id(id)
        );
        return commentMapper.mapToResponseWrapperComments(comments, comments.size());
    }

    @Override
    public Comment updateComment(Integer adId, Integer commentId, Comment comment, String authorName) {
        CommentEntity findComment = findComment(adId, commentId);

        UserEntity findUser = userRepository.findByEmail(authorName)
                .orElseThrow(() -> new UserNotFoundException(authorName));

        if (findComment.getAuthor().getId().equals(findUser.getId())) {
            findComment.setText(comment.getText());
            findComment.setCreatedAt(LocalDateTime.now());
            return commentMapper.commentEntityToDto(commentRepository.save(findComment));
        } else {
            throw new UserForbiddenException(findUser.getId());
        }
    }

    @Override
    public void deleteComment(Integer adId, Integer commentId) {
        findComment(adId, commentId);
        commentRepository.deleteById(commentId);
        log.info("Удаление: Комментарий с ID {} удален", commentId);
    }


    @Override
    public void removeAllCommentsOfAds(Integer id) {
        commentRepository.deleteAllByAdsId(id);
        log.info("Удалили все комментарии к объявлению с ID: {}", id);
    }

    private CommentEntity findComment(Integer adId, Integer commentId) {
        return commentRepository.findByAds_IdAndId(adId, commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));
    }
}
