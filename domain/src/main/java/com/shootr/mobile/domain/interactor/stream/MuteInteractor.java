package com.shootr.mobile.domain.interactor.stream;

import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.interactor.OnCompletedObserver;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.MuteRepository;
import com.shootr.mobile.domain.repository.Remote;
import javax.inject.Inject;
import rx.Observable;
import rx.Subscriber;

import static com.shootr.mobile.domain.utils.Preconditions.checkNotNull;

public class MuteInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final MuteRepository localMuteRepository;
    private final MuteRepository remoteMuteRepository;

    private String idStream;
    private CompletedCallback callback;
    private ErrorCallback errorCallback;

    @Inject public MuteInteractor(InteractorHandler interactorHandler, PostExecutionThread postExecutionThread,
      @Local MuteRepository localMuteRepository, @Remote MuteRepository remoteMuteRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.localMuteRepository = localMuteRepository;
        this.remoteMuteRepository = remoteMuteRepository;
    }

    public void mute(String idStream, CompletedCallback callback, ErrorCallback errorCallback) {
        this.idStream = checkNotNull(idStream);
        this.callback = callback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        subscribeOnCompletedObserverToObservable(remoteMuteObservable());
        subscribeOnCompletedObserverToObservable(localMuteObservable());
    }

    private Observable<Void> localMuteObservable() {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override public void call(Subscriber<? super Void> subscriber) {
                localMuteRepository.mute(idStream);
                subscriber.onCompleted();
            }
        });
    }

    private Observable<Void> remoteMuteObservable() {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override public void call(Subscriber<? super Void> subscriber) {
                remoteMuteRepository.mute(idStream);
                notifyCompleted();
                subscriber.onCompleted();
            }
        });
    }

    private void subscribeOnCompletedObserverToObservable(Observable<Void> observable) {
        observable.subscribe(new OnCompletedObserver<Void>() {
            @Override public void onError(Throwable error) {
                if (error instanceof ServerCommunicationException) {
                    notifyError(new ServerCommunicationException(error));
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