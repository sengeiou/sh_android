package com.shootr.android.domain.interactor;

public interface InteractorHandler {

    void execute(Interactor interactor);

    void sendUiMessage(Object objectToUi);

    void sendError(Throwable throwable);
}
