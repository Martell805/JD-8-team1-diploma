package ru.skypro.homework.service;

import org.springframework.security.core.Authentication;

public interface VerificationUserService {
    boolean verifySameUser(Integer id, Authentication authentication);
    boolean verifyUsersAds(Integer id, Authentication authentication);
    boolean verifyUsersComment(Integer id, Authentication authentication);
    boolean verifyAdmin(Authentication authentication);
    boolean verifySameUserOrAdmin(Integer id, Authentication authentication);
    boolean verifyUsersAdsOrAdmin(Integer id, Authentication authentication);
    boolean verifyUsersCommentOrAdmin(Integer id, Authentication authentication);
}
