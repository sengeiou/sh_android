package com.shootr.mobile.domain.interactor.stream;

import com.shootr.mobile.domain.User;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

/**
 * Gives the number of people watching the stream the current user has visible.
 */
public class WatchNumberInteractor implements Interactor{

    public static final int NO_STREAM = -1;
    private String idStream;
    private Callback callback;

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final UserRepository remoteUserRepository;
    private final UserRepository localUserRepository;

    @Inject public WatchNumberInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread,
      @com.shootr.mobile.domain.repository.Remote UserRepository remoteUserRepository,
      @Local UserRepository localUserRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.remoteUserRepository = remoteUserRepository;
        this.localUserRepository = localUserRepository;
    }

    public void loadWatchNumber(String idStream, Callback callback) {
        this.idStream = idStream;
        this.callback = callback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        List<User> people = getRemotePeopleOrFallbackToLocal();
        List<User> watchers = filterUsersWatchingStream(people, idStream);
        notifyLoaded(watchers.size());
    }

    protected List<User> filterUsersWatchingStream(List<User> people, String idStream) {
        List<User> watchers = new ArrayList<>();
        for (User user : people) {
            if (idStream.equals(user.getIdWatchingStream())) {
                watchers.add(user);
            }
        }
        return watchers;
    }

    private void notifyLoaded(final Integer countIsWatching) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                callback.onLoaded(countIsWatching);
            }
        });
    }

    private List<User> getRemotePeopleOrFallbackToLocal() {
        try {
            return remoteUserRepository.getPeople();
        } catch (com.shootr.mobile.domain.exception.ServerCommunicationException networkError) {
            return localUserRepository.getPeople();
        }
    }

    public interface Callback {

        void onLoaded(Integer count);

    }
}
