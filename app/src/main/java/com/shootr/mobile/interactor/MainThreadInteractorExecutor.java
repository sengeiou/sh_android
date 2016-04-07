package com.shootr.mobile.interactor;

import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;

import javax.inject.Inject;

import timber.log.Timber;

public class MainThreadInteractorExecutor implements InteractorHandler {

    @Inject
    public MainThreadInteractorExecutor() {
    }

    @Override
    public void execute(Interactor interactor) {
        String interactorName = interactor.getClass().getSimpleName();
        Timber.d("-> Running %s in main thread", interactorName);
        long start = System.currentTimeMillis();
        try {
            interactor.execute();
        } catch (Exception unhandledException) {
            Timber.e(unhandledException,
              "Unhandled exception while running %s. If this is an expected exception, it should be handled inside the Interactor.",
              interactorName);
            throw new RuntimeException(unhandledException);
        } finally {
            long end = System.currentTimeMillis();
            long executionTimeMillis = end - start;
            Timber.d("<- Finished %s in %dms", interactorName, executionTimeMillis);
            if (executionTimeMillis > 10) {
                Timber.w(
                  "%s took more than 10 milliseconds. Maybe this interactor is doing too much work for the Main Thread?",
                  interactorName);
            }
        }
    }

    @Override
    public void sendUiMessage(Object objectToUi) {
        throw new IllegalStateException("sendUiMessage not available for the MainThreadInteractorExecutor");
    }

    @Override
    public void stopInteractors() {
        throw new IllegalStateException("stopInteractors not available for the MainThreadInteractorExecutor");
    }

    @Override
    public void executeUnique(Runnable clearDBRunnable) {
        throw new IllegalStateException("executeUnique not available for the MainThreadInteractorExecutor");
    }
}
