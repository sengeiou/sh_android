package com.shootr.android.ui.presenter;

import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.shot.DeleteShotInteractor;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.ui.model.UserModel;
import com.shootr.android.ui.model.mappers.UserModelMapper;
import com.shootr.android.ui.views.ReportShotView;
import javax.inject.Inject;

public class ReportShotPresenter implements Presenter {

    private final DeleteShotInteractor deleteShotInteractor;
    private final SessionRepository sessionRepository;
    private final UserModelMapper userModelMapper;

    private ReportShotView reportShotView;

    @Inject public ReportShotPresenter(DeleteShotInteractor deleteShotInteractor, SessionRepository sessionRepository,
      UserModelMapper userModelMapper) {
        this.deleteShotInteractor = deleteShotInteractor;
        this.sessionRepository = sessionRepository;
        this.userModelMapper = userModelMapper;
    }

    protected void setView(ReportShotView reportShotView) {
        this.reportShotView = reportShotView;
    }

    public void initialize(ReportShotView reportShotView) {
        setView(reportShotView);
    }

    public void report(ShotModel shotModel) {
        UserModel userModel = userModelMapper.transform(sessionRepository.getCurrentUser());
        if (userModel.isEmailConfirmed()) {
            reportShotView.goToReport(sessionRepository.getSessionToken() ,shotModel);
        } else {
            reportShotView.showEmailNotConfirmedError();
        }
    }

    public void onShotLongPressed(ShotModel shotModel) {
        if (sessionRepository.getCurrentUserId().equals(shotModel.getIdUser())) {
            reportShotView.showHolderContextMenu(shotModel);
        } else {
            reportShotView.showContextMenu(shotModel);
        }

    }

    public void deleteShot(ShotModel shotModel) {
        deleteShotInteractor.deleteShot(shotModel.getIdShot(), new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
                // TODO mostrar feedback?
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                // TODO mostrar error
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
