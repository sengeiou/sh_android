package com.shootr.android.ui.presenter;

import com.shootr.android.domain.User;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.stream.GetAllParticipantsInteractor;
import com.shootr.android.ui.views.AllParticipantsView;
import com.shootr.android.util.ErrorMessageFactory;
import java.util.List;
import javax.inject.Inject;

public class AllParticipantsPresenter implements Presenter {

    private final GetAllParticipantsInteractor getAllParticipantsInteractor;
    private final ErrorMessageFactory errorMessageFactory;

    private AllParticipantsView allParticipantsView;

    @Inject public AllParticipantsPresenter(GetAllParticipantsInteractor getAllParticipantsInteractor,
      ErrorMessageFactory errorMessageFactory) {
        this.getAllParticipantsInteractor = getAllParticipantsInteractor;
        this.errorMessageFactory = errorMessageFactory;
    }

    protected void setView(AllParticipantsView allParticipantsView) {
        this.allParticipantsView = allParticipantsView;
    }

    public void initialize(AllParticipantsView allParticipantsView, String idStream) {
        this.setView(allParticipantsView);
        this.loadAllParticipants(idStream);
    }

    private void loadAllParticipants(String idStream) {
        getAllParticipantsInteractor.obtainAllParticipants(idStream, new Interactor.Callback<List<User>>() {
            @Override public void onLoaded(List<User> users) {
                //TODO THINGS
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                allParticipantsView.showError(errorMessageFactory.getMessageForError(error));
            }
        });
    }

    @Override public void resume() {
        /* no-op */
    }

    @Override public void pause() {
        /* no-op */
    }
}
