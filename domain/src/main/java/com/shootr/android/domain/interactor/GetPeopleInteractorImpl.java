package com.shootr.android.domain.interactor;

import javax.inject.Inject;

public class GetPeopleInteractorImpl implements GetPeopleInteractor {

    private final InteractorHandler interactorHandler;

    @Inject public GetPeopleInteractorImpl(InteractorHandler interactorHandler) {
        this.interactorHandler = interactorHandler;
    }

    @Override public void obtainPeople() {
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Throwable {
        retrievePeopleFromDatabase();
        retrievePeopleFromServer();
        savePeople();
    }

    private void retrievePeopleFromDatabase() {
        interactorHandler.sendUiMessage(null); //TODO
    }

    private void retrievePeopleFromServer() {
        interactorHandler.sendUiMessage(null); //TODO
    }

    private void savePeople() {

    }
}
