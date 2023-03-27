package ru.skypro.homework.service;

import org.springframework.security.core.Authentication;

public interface VerificationUserService {
    /**
     * Показывает, изменяет ли пользователь сам себя
     *
     * @param id - id пользователя
     * @param authentication - объект {@link Authentication} из SpringSecurity
     * @return Pair
     * */
    boolean verifySameUser(Integer id, Authentication authentication);
    /**
     * Показывает, изменяет ли пользователь своё объявление
     *
     * @param id - id объявления
     * @param authentication - объект {@link Authentication} из SpringSecurity
     * @return Pair
     * */
    boolean verifyUsersAds(Integer id, Authentication authentication);
    /**
     * Показывает, изменяет ли пользователь свой комментарий
     *
     * @param id - id комментария
     * @param authentication - объект {@link Authentication} из SpringSecurity
     * @return Pair
     * */
    boolean verifyUsersComment(Integer id, Authentication authentication);
    /**
     * Показывает, является ли пользователь администратором
     *
     * @param authentication - объект {@link Authentication} из SpringSecurity
     * @return Pair
     * */
    boolean verifyAdmin(Authentication authentication);
    /**
     * Показывает, изменяет ли пользователь сам себя или является администратором
     *
     * @param id - id пользователя
     * @param authentication - объект {@link Authentication} из SpringSecurity
     * @return Pair
     * */
    boolean verifySameUserOrAdmin(Integer id, Authentication authentication);
    /**
     * Показывает, изменяет ли пользователь своё объявление или является администратором
     *
     * @param id - id объявления
     * @param authentication - объект {@link Authentication} из SpringSecurity
     * @return Pair
     * */
    boolean verifyUsersAdsOrAdmin(Integer id, Authentication authentication);
    /**
     * Показывает, изменяет ли пользователь свой комментарий или является администратором
     *
     * @param id - id комментария
     * @param authentication - объект {@link Authentication} из SpringSecurity
     * @return Pair
     * */
    boolean verifyUsersCommentOrAdmin(Integer id, Authentication authentication);
}
