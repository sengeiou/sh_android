package com.shootr.android.domain.interactor.event;

import com.shootr.android.domain.Shot;
import com.shootr.android.domain.User;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.ShotRepository;
import com.shootr.android.domain.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class GetEventMediaInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final ShotRepository shotRepository;
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;

    private String idEvent;
    private Callback callback;

    @Inject
    public GetEventMediaInteractor(InteractorHandler interactorHandler, PostExecutionThread postExecutionThread,
      @Remote ShotRepository shotRepository, @Remote UserRepository userRepository, SessionRepository sessionRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.shotRepository = shotRepository;
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
    }

    public void getEventMedia(String idEvent, Callback callback) {
        this.idEvent = idEvent;
        this.callback = callback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Throwable {
        String currentUserId = sessionRepository.getCurrentUserId();
        List<User> people = userRepository.getPeople();
        List<String> peopleIds = new ArrayList<>();
        List<Shot> shots = new ArrayList<>();
        for (User user : people) {
            peopleIds.add(user.getIdUser());
        }
        peopleIds.add(currentUserId);
        for (String userId : peopleIds) {
            shots.addAll(shotRepository.getMediaByIdEvent(idEvent, userId));
        }
        notifyLoaded(shots);
    }

    private void notifyLoaded(final List<Shot> shots) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                callback.onLoaded(shots);
            }
        });
    }

}
