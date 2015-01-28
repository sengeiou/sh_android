package com.shootr.android.domain.interactor.user;

import com.shootr.android.domain.User;
import com.shootr.android.domain.UserList;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.UserRepository;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.inject.Inject;

public class GetPeopleInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final UserRepository userRepository;

    @Inject public GetPeopleInteractor(InteractorHandler interactorHandler, UserRepository userRepository) {
        this.interactorHandler = interactorHandler;
        this.userRepository = userRepository;
    }

    public void obtainPeople() {
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Throwable {
        userRepository.getPeople(new UserRepository.UserListCallback() {
            @Override public void onLoaded(List<User> userList) {
                userList = reorderPeopleByUsername(userList);
                interactorHandler.sendUiMessage(new UserList(userList));
            }
        });
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
