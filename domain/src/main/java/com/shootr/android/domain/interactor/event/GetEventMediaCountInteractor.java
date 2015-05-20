package com.shootr.android.domain.interactor.event;

import com.shootr.android.domain.User;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.ShotRepository;
import com.shootr.android.domain.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class GetEventMediaCountInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final ShotRepository shotRepository;
    private final UserRepository userRepository;

    private String idEvent;
    private String idUser;
    private Callback callback;

    @Inject public GetEventMediaCountInteractor(InteractorHandler interactorHandler, PostExecutionThread postExecutionThread,
      @Remote ShotRepository shotRepository, @Remote UserRepository userRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.shotRepository = shotRepository;
        this.userRepository = userRepository;
    }

    public void getEventMediaCount(String idEvent, String idUser, Callback callback) {
        this.idEvent = idEvent;
        this.callback = callback;
        this.idUser = idUser;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Throwable {
        List<User> people = userRepository.getPeople();
        List<String> peopleIds = new ArrayList<>();
        for (User user : people) {
            peopleIds.add(user.getIdUser());
        }
        peopleIds.add(idUser);
        Integer mediaCountByIdEvent = 0;
        for (String userId : peopleIds) {
            mediaCountByIdEvent += shotRepository.getMediaCountByIdEvent(idEvent, userId);
        }
        notifyLoaded(mediaCountByIdEvent);
    }

    private void notifyLoaded(final Integer mediaCount) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                callback.onLoaded(mediaCount);
            }
        });
    }
}
