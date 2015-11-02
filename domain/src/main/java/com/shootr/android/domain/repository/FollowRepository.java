package com.shootr.android.domain.repository;

public interface FollowRepository {

    void follow(String idUser);

    void unfollow(String idUser);

    void block(String idUser);

    void unblock(String idUser);
}
