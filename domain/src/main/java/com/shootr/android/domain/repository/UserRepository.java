package com.shootr.android.domain.repository;

import com.shootr.android.domain.SuggestedPeople;
import com.shootr.android.domain.User;
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
}
