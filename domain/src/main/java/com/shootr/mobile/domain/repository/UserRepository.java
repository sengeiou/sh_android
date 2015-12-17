package com.shootr.mobile.domain.repository;

import com.shootr.mobile.domain.SuggestedPeople;
import com.shootr.mobile.domain.User;
import java.util.List;

public interface UserRepository {

    List<User> getPeople();

    User getUserById(String id);

    User getUserByUsername(String username);

    boolean isFollower(String userId);

    boolean isFollowing(String userId);

    User putUser(User user);

    List<SuggestedPeople> getSuggestedPeople();

    List<User> getAllParticipants(String idStream, Long maxJoinDate);

    List<User> findParticipants(String idStream, String query);

    void updateWatch(User user);

    List<User> getFollowing(String idUser);

    List<User> getFollowers(String idUser);
}
