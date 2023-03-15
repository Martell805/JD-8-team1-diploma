package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
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
import java.time.LocalDate;
import java.util.List;


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
        newComment.setCreatedAt(LocalDate.now());

        return commentMapper.commentEntityToDto(commentRepository.save(newComment));
    }

    @Override
    public Comment updateComment(Integer adId, Integer commentId, Comment comment, Authentication authentication) {
        CommentEntity findComment = findComment(adId, commentId);

        UserEntity findUser = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UserNotFoundException(authentication.getName()));

        if (findComment.getAuthor().getId().equals(findUser.getId())) {
            findComment.setText(comment.getText());
            findComment.setCreatedAt(LocalDate.now());
            return commentMapper.commentEntityToDto(commentRepository.save(findComment));

        } else {
            throw new UserForbiddenException(findUser.getId());
        }
    }

    @Override
    public Comment getComment(Integer adId, Integer commentId) {
        return commentMapper.commentEntityToDto(findComment(adId, commentId));
    }

    @Override
    public void deleteComment(Integer adId, Integer commentId) {
        findComment(adId, commentId);
        commentRepository.deleteById(commentId);
    }
    @Override
    public ResponseWrapperComment getAllCommentsByAd(Integer id) {
        List<CommentEntity> comments = commentRepository.findAllByAds_Id(id);
        ResponseWrapperComment findComments = new ResponseWrapperComment();
        findComments.setResults(commentMapper.modelToDto(comments));
        findComments.setCount(comments.size());
        return findComments;
    }
    private CommentEntity findComment(Integer adId, Integer commentId) {
        return commentRepository.findByAds_IdAndId(adId, commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));
    }
}
