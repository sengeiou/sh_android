package com.shootr.mobile.domain.repository.user;

import com.shootr.mobile.domain.exception.EmailAlreadyExistsException;
import com.shootr.mobile.domain.exception.UsernameAlreadyExistsException;
import com.shootr.mobile.domain.model.stream.Stream;
import com.shootr.mobile.domain.model.user.SuggestedPeople;
import com.shootr.mobile.domain.model.user.User;
import java.io.IOException;
import java.util.List;

public interface UserRepository {

    User getUserById(String id);

    User getUserForAnalythicsById(String id);

    User getUserByUsername(String username);

    User putUser(User user);

    List<SuggestedPeople> getSuggestedPeople(String locale);

    List<User> getAllParticipants(String idStream, Long maxJoinDate);

    List<User> findParticipants(String idStream, String query);

    Stream updateWatch(User user);

    List<User> getLocalPeople(String idUser);

    void updateUserProfile(User updatedUserEntity)
      throws EmailAlreadyExistsException, UsernameAlreadyExistsException;

    List<User> findFriends(String searchString, Integer pageOffset, String locale) throws IOException;

    void mute(String idUser);

    void unMute(String idUser);

    void updateSuggestedPeopleCache(List<SuggestedPeople> suggestedPeopleList);

    void acceptTerms();
}
