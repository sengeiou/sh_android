package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.User;
import com.shootr.mobile.domain.UserList;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.UserRepository;
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

    @Override public void execute() throws Exception {
        obtainLocalPeople();
        obtainRemotePeople();
    }

    private void obtainLocalPeople() {
        List<User> userList = localUserRepository.getPeople();
        if (!userList.isEmpty()) {
            userList = reorderPeopleByUsername(userList);
            interactorHandler.sendUiMessage(new UserList(userList));
        }
    }

    private void obtainRemotePeople() {
        try {
            List<User> userList = remoteUserRepository.getPeople();
            userList = reorderPeopleByUsername(userList);
            interactorHandler.sendUiMessage(new UserList(userList));
        } catch (ServerCommunicationException networkError) {
            /* no-op */
        }
    }

    private List<User> reorderPeopleByUsername(List<User> userList) {
        Collections.sort(userList, new UsernameComparator());
        return userList;
    }

    static class UsernameComparator implements Comparator<User> {

        @Override public int compare(User user1, User user2) {
            return user1.getUsername().compareToIgnoreCase(user2.getUsername());
        }
    }
}
