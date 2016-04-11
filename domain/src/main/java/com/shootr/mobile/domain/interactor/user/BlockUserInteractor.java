package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.interactor.OnCompletedObserver;
import com.shootr.mobile.domain.repository.FollowRepository;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.Remote;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;

import static com.shootr.mobile.domain.utils.Preconditions.checkNotNull;

public class BlockUserInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final FollowRepository localFollowRepository;
    private final FollowRepository remoteFollowRepository;

    private String idUser;
    private CompletedCallback callback;
    private ErrorCallback errorCallback;

    @Inject public BlockUserInteractor(InteractorHandler interactorHandler, PostExecutionThread postExecutionThread,
      @Local FollowRepository localFollowRepository, @Remote FollowRepository remoteFollowRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.localFollowRepository = localFollowRepository;
        this.remoteFollowRepository = remoteFollowRepository;
    }

    public void block(String idUser, CompletedCallback callback, ErrorCallback errorCallback) {
        this.idUser = checkNotNull(idUser);
        this.callback = callback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        try {
            subscribeOnCompletedObserverToObservable(remoteBlockObservable());
            subscribeOnCompletedObserverToObservable(localBlockObservable());
            notifyCompleted();
        } catch (ServerCommunicationException error) {
            notifyError(error);
        }
    }

    private Observable<Void> localBlockObservable() {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override public void call(Subscriber<? super Void> subscriber) {
                localFollowRepository.block(idUser);
                subscriber.onCompleted();
            }
        });
    }

    private Observable<Void> remoteBlockObservable() {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override public void call(Subscriber<? super Void> subscriber) {
                remoteFollowRepository.block(idUser);
                subscriber.onCompleted();
                notifyCompleted();
            }
        });
    }

    private void subscribeOnCompletedObserverToObservable(Observable<Void> observable) {
        observable.subscribe(new OnCompletedObserver<Void>() {
            @Override public void onError(Throwable error) {
                localFollowRepository.unblock(idUser);
                if (error instanceof ServerCommunicationException) {
                    notifyError((ServerCommunicationException) error);
                }
            }
        });
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
