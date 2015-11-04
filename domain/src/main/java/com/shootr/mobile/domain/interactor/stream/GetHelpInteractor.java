package com.shootr.mobile.domain.interactor.stream;

import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.utils.LocaleProvider;
import javax.inject.Inject;

public class GetHelpInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final com.shootr.mobile.domain.repository.StreamRepository remoteStreamRepository;
    private final LocaleProvider localeProvider;

    private Callback<com.shootr.mobile.domain.Stream> callback;
    private ErrorCallback errorCallback;

    @Inject public GetHelpInteractor(InteractorHandler interactorHandler, PostExecutionThread postExecutionThread,
      @com.shootr.mobile.domain.repository.Remote
      com.shootr.mobile.domain.repository.StreamRepository remoteStreamRepository, LocaleProvider localeProvider) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.remoteStreamRepository = remoteStreamRepository;
        this.localeProvider = localeProvider;
    }

    public void obtainHelpStream(Callback<com.shootr.mobile.domain.Stream> callback, ErrorCallback errorCallback) {
        this.callback = callback;
        this.errorCallback= errorCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        try {
            notifyLoaded(remoteStreamRepository.getHelpStream(localeProvider.getCountry(), localeProvider.getLanguage()));
        } catch (com.shootr.mobile.domain.exception.ServerCommunicationException error) {
            notifyError(error);
        }
    }

    private void notifyLoaded(final com.shootr.mobile.domain.Stream stream) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                callback.onLoaded(stream);
            }
        });
    }

    private void notifyError(final com.shootr.mobile.domain.exception.ShootrException error) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                errorCallback.onError(error);
            }
        });
    }

}
