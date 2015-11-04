package com.shootr.mobile.domain.repository;

import java.util.List;

public interface FollowRepository {

    void follow(String idUser) throws com.shootr.mobile.domain.exception.FollowingBlockedUserException;

    void unfollow(String idUser);

    void block(String idUser);

    void unblock(String idUser);

    List<String> getBlockedIdUsers();
}
