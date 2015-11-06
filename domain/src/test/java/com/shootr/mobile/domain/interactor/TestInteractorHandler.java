package com.shootr.mobile.domain.interactor;

public class TestInteractorHandler implements InteractorHandler {

    @Override public void execute(Interactor interactor) {
        try {
            interactor.execute();
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    @Override public void sendUiMessage(Object objectToUi) {
        /* no-op */
    }

    @Override public void stopInteractors() {
        /* no-op */
    }

    @Override public void executeUnique(Runnable clearDBRunnable) {
        /* no-op */
    }
}
