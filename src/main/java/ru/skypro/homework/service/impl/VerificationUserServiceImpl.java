package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.skypro.homework.entity.AdsEntity;
import ru.skypro.homework.entity.CommentEntity;
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.service.AdsService;
import ru.skypro.homework.service.CommentService;
import ru.skypro.homework.service.UserService;
import ru.skypro.homework.service.VerificationUserService;
import java.util.Collection;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Slf4j
@RequiredArgsConstructor
@Service
public class VerificationUserServiceImpl implements VerificationUserService {
    private final UserService userService;
    private final AdsService adsService;
    private final CommentService commentService;

    @Override
    public boolean verifySameUser(Integer id, Authentication authentication) {
        UserDetails principal = (UserDetails) authentication.getPrincipal();
        UserEntity user = userService.getUserByEmail(principal.getUsername());

        return id.equals(user.getId());
    }

    @Override
    public boolean verifyUsersAds(Integer id, Authentication authentication) {
        UserDetails principal = (UserDetails) authentication.getPrincipal();
        UserEntity user = userService.getUserByEmail(principal.getUsername());

        AdsEntity ads = adsService.getAds(id);

        return ads.getAuthor().getId().equals(user.getId());
    }

    @Override
    public boolean verifyUsersComment(Integer id, Authentication authentication) {
        UserDetails principal = (UserDetails) authentication.getPrincipal();
        UserEntity user = userService.getUserByEmail(principal.getUsername());

        CommentEntity comment = commentService.getCommentEntityById(id);

        return comment.getAuthor().getId().equals(user.getId());
    }

    @Override
    public boolean verifyAdmin(Authentication authentication) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        SimpleGrantedAuthority authority = (SimpleGrantedAuthority) authorities.toArray()[0];

        return ((SimpleGrantedAuthority) authorities.toArray()[0]).getAuthority().equals("ADMIN");
    }

    @Override
    public boolean verifySameUserOrAdmin(Integer id, Authentication authentication) {
        return  verifyAdmin(authentication) || verifySameUser(id, authentication);
    }

    @Override
    public boolean verifyUsersAdsOrAdmin(Integer id, Authentication authentication) {
        return  verifyAdmin(authentication) || verifyUsersAds(id, authentication);
    }

    @Override
    public boolean verifyUsersCommentOrAdmin(Integer id, Authentication authentication) {
        return  verifyAdmin(authentication) || verifyUsersComment(id, authentication);
    }
}
