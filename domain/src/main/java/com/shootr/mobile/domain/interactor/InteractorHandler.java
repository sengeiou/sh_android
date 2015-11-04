package com.shootr.mobile.domain.interactor;

public interface InteractorHandler {

    void execute(Interactor interactor);

    void sendUiMessage(Object objectToUi);

    void stopInteractors();

    void executeUnique(Runnable clearDBRunnable);
}
