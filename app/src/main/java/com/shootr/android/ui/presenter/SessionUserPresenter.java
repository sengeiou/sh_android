package com.shootr.android.ui.presenter;

import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.ui.model.UserModel;
import com.shootr.android.ui.model.mappers.UserModelMapper;
import com.shootr.android.ui.views.SessionUserView;
import javax.inject.Inject;

public class SessionUserPresenter implements Presenter {

    private final SessionRepository sessionRepository;
    private final UserModelMapper userModelMapper;

    private SessionUserView sessionUserView;

    @Inject public SessionUserPresenter(SessionRepository sessionRepository, UserModelMapper userModelMapper) {
        this.sessionRepository = sessionRepository;
        this.userModelMapper = userModelMapper;
    }

    protected void setView(SessionUserView sessionUserView) {
        this.sessionUserView = sessionUserView;
    }

    public void initialize(SessionUserView sessionUserView) {
        setView(sessionUserView);
    }

    public void loadReport(ShotModel shotModel) {
        UserModel userModel = userModelMapper.transform(sessionRepository.getCurrentUser());
        if (userModel.isEmailConfirmed()) {
            sessionUserView.goToReport(sessionRepository.getSessionToken() ,shotModel);
        } else {
            sessionUserView.showConfirmationMessage();
        }
    }

    @Override public void resume() {
        /* no-op */
    }

    @Override public void pause() {
        /* no-op */
    }
}
