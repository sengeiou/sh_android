package com.shootr.android.domain.interactor.stream;

import com.shootr.android.domain.Stream;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.StreamRepository;
import com.shootr.android.domain.utils.LocaleProvider;
import javax.inject.Inject;

public class GetBlogInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final StreamRepository remoteStreamRepository;
    private final LocaleProvider localeProvider;

    private Callback<Stream> callback;
    private ErrorCallback errorCallback;

    @Inject public GetBlogInteractor(InteractorHandler interactorHandler, PostExecutionThread postExecutionThread,
      @Remote StreamRepository remoteStreamRepository, LocaleProvider localeProvider) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.remoteStreamRepository = remoteStreamRepository;
        this.localeProvider = localeProvider;
    }

    public void obtainBlogStream(Callback<Stream> callback, ErrorCallback errorCallback) {
        this.callback = callback;
        this.errorCallback= errorCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        try {
            notifyLoaded(remoteStreamRepository.getBlogStream(localeProvider.getCountry().toUpperCase(), localeProvider.getLanguage().toUpperCase()));
        } catch (ServerCommunicationException error) {
            notifyError(error);
        }
    }

    private void notifyLoaded(final Stream stream) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                callback.onLoaded(stream);
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
