package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.shot.GetShotDetailInteractor;
import com.shootr.mobile.domain.interactor.shot.PinShotInteractor;
import com.shootr.mobile.domain.model.shot.ShotDetail;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.ui.model.mappers.ShotModelMapper;
import com.shootr.mobile.ui.views.PinShotView;
import java.util.Date;
import javax.inject.Inject;

public class PinShotPresenter implements Presenter {

    private final PinShotInteractor pinShotInteractor;
    private final GetShotDetailInteractor getShotDetailInteractor;
    private final SessionRepository sessionRepository;
    private final ShotModelMapper shotModelMapper;
    private PinShotView pinShotView;
    private ShotModel shotModel;
    private boolean hasBeenPaused;

    @Inject
    public PinShotPresenter(PinShotInteractor pinShotInteractor, GetShotDetailInteractor getShotDetailInteractor,
      SessionRepository sessionRepository, ShotModelMapper shotModelMapper) {
        this.pinShotInteractor = pinShotInteractor;
        this.getShotDetailInteractor = getShotDetailInteractor;
        this.sessionRepository = sessionRepository;
        this.shotModelMapper = shotModelMapper;
    }

    protected void setView(PinShotView pinShotView) {
        this.pinShotView = pinShotView;
    }

    protected void setShot(ShotModel shotModel) {
        this.shotModel = shotModel;
    }

    public void initialize(PinShotView pinShotView) {
        setView(pinShotView);
    }

    public void initialize(PinShotView pinShotView, ShotModel shotModel) {
        setView(pinShotView);
        setShot(shotModel);
        setupPinShotButton();
    }

    private void setupPinShotButton() {
        if (canShotBePinned()) {
            pinShotView.showPinShotButton();
        } else {
            pinShotView.hidePinShotButton();
        }
    }

    private boolean canShotBePinned() {
        return shotModel.getHide() != null && shotModel.getHide() != 0 && shotModel.getIdUser()
          .equals(sessionRepository.getCurrentUserId());
    }

    public void pinToProfile(final ShotModel shotModel) {
        pinShotInteractor.pinShot(shotModel.getIdShot(), new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
                shotModel.setHide(new Date().getTime());
                pinShotView.notifyPinnedShot(shotModel);
                pinShotView.showPinned();
                pinShotView.hidePinShotButton();
            }
        });
    }

    @Override public void resume() {
        if (hasBeenPaused) {
            if (shotModel != null) {
                getShotDetailInteractor.loadShotDetail(shotModel.getIdShot(), true,
                    new Interactor.Callback<ShotDetail>() {
                        @Override public void onLoaded(ShotDetail shotDetail) {
                            setShot(shotModelMapper.transform(shotDetail.getShot()));
                            setupPinShotButton();
                        }
                    }, new Interactor.ErrorCallback() {
                        @Override public void onError(ShootrException error) {
                    /* no - op */
                        }
                    });
            }
            hasBeenPaused = false;
        }
    }

    @Override public void pause() {
        hasBeenPaused = true;
    }
}
