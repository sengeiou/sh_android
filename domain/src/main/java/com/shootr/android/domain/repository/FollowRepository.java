package com.shootr.android.domain.repository;

import com.shootr.android.domain.exception.FollowingBlockedUserException;
import java.util.List;

public interface FollowRepository {

    void follow(String idUser) throws FollowingBlockedUserException;

    void unfollow(String idUser);

    void block(String idUser);

    void unblock(String idUser);

    List<String> getBlockedIdUsers();
}
