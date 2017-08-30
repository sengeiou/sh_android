package com.shootr.mobile.domain.repository.user;

import com.shootr.mobile.domain.exception.EmailAlreadyExistsException;
import com.shootr.mobile.domain.exception.UsernameAlreadyExistsException;
import com.shootr.mobile.domain.model.stream.Stream;
import com.shootr.mobile.domain.model.user.SuggestedPeople;
import com.shootr.mobile.domain.model.user.User;
import java.io.IOException;
import java.util.List;

public interface UserRepository {

    List<User> getPeople();

    void synchronizeFollow();

    User getUserById(String id);

    User getUserForAnalythicsById(String id);

    User getUserByUsername(String username);

    boolean isFollower(String userId);

    boolean isFollowing(String userId);

    User putUser(User user);

    List<SuggestedPeople> getSuggestedPeople(String locale);

    List<User> getAllParticipants(String idStream, Long maxJoinDate);

    List<User> findParticipants(String idStream, String query);

    Stream updateWatch(User user);

    List<User> getFollowing(String idUser, Integer page, Integer pageSize);

    List<User> getFollowers(String idUser, Integer page, Integer pageSize);

    List<User> getLocalPeople(String idUser);

    List<User> getLocalPeopleFromIdStream(String idStream);

    void updateUserProfile(User updatedUserEntity)
      throws EmailAlreadyExistsException, UsernameAlreadyExistsException;

    List<User> findFriends(String searchString, Integer pageOffset, String locale) throws IOException;

    void forceUpdatePeople();

    List<String> getFollowingIds(String userId);

    void mute(String idUser);

    void unMute(String idUser);
}
