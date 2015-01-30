package com.shootr.android.domain.interactor.user;

import com.shootr.android.domain.User;
import com.shootr.android.domain.UserList;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.UserRepository;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.inject.Inject;

public class GetPeopleInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final UserRepository remoteUserRepository;
    private final UserRepository localUserRepository;

    @Inject public GetPeopleInteractor(InteractorHandler interactorHandler, @Remote UserRepository remoteUserRepository,
      @Local UserRepository localUserRepository) {
        this.interactorHandler = interactorHandler;
        this.remoteUserRepository = remoteUserRepository;
        this.localUserRepository = localUserRepository;
    }

    public void obtainPeople() {
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Throwable {
        obtainPeopleFromRepository(localUserRepository);
        obtainPeopleFromRepository(remoteUserRepository);
    }

    private void obtainPeopleFromRepository(UserRepository userRepository) {
        List<User> userList = userRepository.getPeople();
        userList = reorderPeopleByUsername(userList);
        interactorHandler.sendUiMessage(new UserList(userList));
    }

    private List<User> reorderPeopleByUsername(List<User> userList) {
        Collections.sort(userList, new UsernameComparator());
        return userList;
    }

    static class UsernameComparator implements Comparator<User> {
        @Override public int compare(User user1, User user2) {
            return user1.getUsername()
              .compareToIgnoreCase(user2.getUsername());
        }

    }

}
