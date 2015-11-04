package com.shootr.mobile.domain.repository;

import java.util.List;

public interface UserRepository {

    List<com.shootr.mobile.domain.User> getPeople();

    com.shootr.mobile.domain.User getUserById(String id);

    com.shootr.mobile.domain.User getUserByUsername(String username);

    boolean isFollower(String userId);

    boolean isFollowing(String userId);

    com.shootr.mobile.domain.User putUser(com.shootr.mobile.domain.User user);

    List<com.shootr.mobile.domain.SuggestedPeople> getSuggestedPeople();

    List<com.shootr.mobile.domain.User> getAllParticipants(String idStream, Long maxJoinDate);

    List<com.shootr.mobile.domain.User> findParticipants(String idStream, String query);

    void updateWatch(com.shootr.mobile.domain.User user);
}
