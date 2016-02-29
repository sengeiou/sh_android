package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.shot.PinShotInteractor;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.ui.views.PinShotView;
import javax.inject.Inject;

public class PinShotPresenter implements Presenter {

    private final PinShotInteractor pinShotInteractor;
    private PinShotView pinShotView;

    @Inject public PinShotPresenter(PinShotInteractor pinShotInteractor) {
        this.pinShotInteractor = pinShotInteractor;
    }

    protected void setView(PinShotView pinShotView) {
        this.pinShotView = pinShotView;
    }

    public void initialize(PinShotView pinShotView) {
        setView(pinShotView);
    }

    public void pinToProfile(final ShotModel shotModel) {
        pinShotInteractor.pinShot(shotModel.getIdShot(), new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
                shotModel.setHide(0L);
                pinShotView.notifyPinnedShot(shotModel);
                pinShotView.showPinned();
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
