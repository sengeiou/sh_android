package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.domain.exception.FollowingBlockedUserException;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.interactor.OnCompletedObserver;
import com.shootr.mobile.domain.repository.FollowRepository;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.user.UserRepository;
import com.shootr.mobile.domain.service.user.CannotFollowBlockedUserException;
import javax.inject.Inject;
import rx.Observable;
import rx.Subscriber;

import static com.shootr.mobile.domain.utils.Preconditions.checkNotNull;

public class FollowInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final FollowRepository localFollowRepository;
    private final FollowRepository remoteFollowRepository;
    private final UserRepository remoteUserRepository;
    private final UserRepository localUserRepository;

    private String idUser;
    private CompletedCallback callback;
    private ErrorCallback errorCallback;

    @Inject public FollowInteractor(InteractorHandler interactorHandler, PostExecutionThread postExecutionThread,
      @Local FollowRepository localFollowRepository, @Remote FollowRepository remoteFollowRepository,
      @Remote UserRepository remoteUserRepository, @Local UserRepository localUserRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.localFollowRepository = localFollowRepository;
        this.remoteFollowRepository = remoteFollowRepository;
        this.remoteUserRepository = remoteUserRepository;
        this.localUserRepository = localUserRepository;
    }

    public void follow(String idUser, CompletedCallback callback, ErrorCallback errorCallback) {
        this.idUser = checkNotNull(idUser);
        this.callback = callback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        subscribeOnCompletedObserverToObservable(remoteFollowObservable());
        subscribeOnCompletedObserverToObservable(localFollowObservable());
        subscribeOnCompletedObserverToObservable(ensureUserExistInLocalObservable());
    }

    private Observable<Void> localFollowObservable() {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override public void call(Subscriber<? super Void> subscriber) {
                try {
                    localFollowRepository.follow(idUser);
                } catch (FollowingBlockedUserException e) {
                    /* no-op */
                }
                subscriber.onCompleted();
            }
        });
    }

    private Observable<Void> remoteFollowObservable() {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override public void call(Subscriber<? super Void> subscriber) {
                try {
                    remoteFollowRepository.follow(idUser);
                    notifyCompleted();
                } catch (FollowingBlockedUserException error) {
                    notifyError(new CannotFollowBlockedUserException(error));
                }
                subscriber.onCompleted();
            }
        });
    }

    private Observable<Void> ensureUserExistInLocalObservable() {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override public void call(Subscriber<? super Void> subscriber) {
                ensureUserExistInLocal();
                subscriber.onCompleted();
            }
        });
    }

    private void subscribeOnCompletedObserverToObservable(Observable<Void> observable) {
        observable.subscribe(new OnCompletedObserver<Void>() {
            @Override public void onError(Throwable error) {
                if (error instanceof FollowingBlockedUserException) {
                    notifyError(new CannotFollowBlockedUserException(error));
                }
            }
        });
    }

    protected void ensureUserExistInLocal() {
        try {
            if (localUserRepository.getUserById(idUser) == null) {
                User user = remoteUserRepository.getUserById(idUser);
                localUserRepository.putUser(user);
            }
        } catch (ServerCommunicationException e) {
            /* bad luck: will have unconsistent data for a short period of time */
        }
    }

    private void notifyCompleted() {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                callback.onCompleted();
            }
        });
    }

    private void notifyError(final ShootrException error) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                errorCallback.onError(error);
            }
        });
    }
}
