package com.shootr.android.ui.presenter;

import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.ui.model.UserModel;
import com.shootr.android.ui.model.mappers.UserModelMapper;
import com.shootr.android.ui.views.ReportShotView;
import javax.inject.Inject;

public class ReportShotPresenter implements Presenter {

    private final SessionRepository sessionRepository;
    private final UserModelMapper userModelMapper;

    private ReportShotView reportShotView;

    @Inject public ReportShotPresenter(SessionRepository sessionRepository, UserModelMapper userModelMapper) {
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

    @Override public void resume() {
        /* no-op */
    }

    @Override public void pause() {
        /* no-op */
    }
}
