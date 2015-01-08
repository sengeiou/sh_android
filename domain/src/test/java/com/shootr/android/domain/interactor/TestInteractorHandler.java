package com.shootr.android.domain.interactor;

public class TestInteractorHandler implements InteractorHandler {

    private MessageReceiver receiver;

    @Override public void execute(Interactor interactor) {
        try {
            interactor.execute();
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    @Override public void sendUiMessage(Object objectToUi) {
        if (receiver != null) {
            receiver.receive(objectToUi);
        }
    }

    public void setReceiver(MessageReceiver receiver) {
        this.receiver = receiver;
    }

    interface MessageReceiver {

        void receive(Object objectToUi);
    }
}
