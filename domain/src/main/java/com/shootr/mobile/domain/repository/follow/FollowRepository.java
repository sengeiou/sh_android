package com.shootr.mobile.domain.repository.follow;

import com.shootr.mobile.domain.exception.FollowingBlockedUserException;
import com.shootr.mobile.domain.model.Follows;
import java.util.List;

public interface FollowRepository {

    void follow(String idUser) throws FollowingBlockedUserException;

    void unfollow(String idUser);

    void block(String idUser);

    void unblock(String idUser);

    List<String> getBlockedIdUsers();

    List<String> getMutualIdUsers();

    Follows getFollowing(String idUser, String[] type, Long maxTimestamp);

    Follows getFollowers(String idUser, String[] type, Long maxTimestamp);
}
