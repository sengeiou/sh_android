package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.shot.PinShotInteractor;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.ui.views.PinShotView;
import javax.inject.Inject;

public class PinShotPresenter implements Presenter {

    private final PinShotInteractor pinShotInteractor;
    private final SessionRepository sessionRepository;
    private PinShotView pinShotView;
    private ShotModel shotModel;

    @Inject public PinShotPresenter(PinShotInteractor pinShotInteractor, SessionRepository sessionRepository) {
        this.pinShotInteractor = pinShotInteractor;
        this.sessionRepository = sessionRepository;
    }

    protected void setView(PinShotView pinShotView) {
        this.pinShotView = pinShotView;
    }

    public void initialize(PinShotView pinShotView) {
        setView(pinShotView);
    }

    public void initialize(PinShotView pinShotView, ShotModel shotModel) {
        setView(pinShotView);
        this.shotModel = shotModel;
        setupPinShotButton();
    }

    private void setupPinShotButton() {
        if(canShotBePinned()) {
            pinShotView.showPinShotButton();
        } else {
            pinShotView.hidePinShotButton();
        }
    }

    private boolean canShotBePinned() {
        return shotModel.getHide() != 0L && shotModel.getIdUser().equals(sessionRepository.getCurrentUserId());
    }

    public void pinToProfile(final ShotModel shotModel) {
        pinShotInteractor.pinShot(shotModel.getIdShot(), new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
                shotModel.setHide(0L);
                pinShotView.notifyPinnedShot(shotModel);
                pinShotView.showPinned();
                pinShotView.hidePinShotButton();
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
