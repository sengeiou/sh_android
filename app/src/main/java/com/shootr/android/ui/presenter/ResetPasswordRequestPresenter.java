package com.shootr.android.ui.presenter;

import com.shootr.android.db.mappers.ForgotPasswordMapper;
import com.shootr.android.domain.ForgotPasswordResult;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.user.ResetPasswordInteractor;
import com.shootr.android.ui.model.ForgotPasswordUserModel;
import com.shootr.android.ui.model.mappers.ForgotPasswordUserModelMapper;
import com.shootr.android.ui.views.ResetPasswordRequestView;
import javax.inject.Inject;

public class ResetPasswordRequestPresenter implements Presenter {

    private final ResetPasswordInteractor resetPasswordInteractor;
    private final ForgotPasswordUserModelMapper forgotPasswordUserModelMapper;

    private ResetPasswordRequestView resetPasswordRequestView;

    @Inject
    public ResetPasswordRequestPresenter(ResetPasswordInteractor resetPasswordInteractor,
      ForgotPasswordUserModelMapper forgotPasswordUserModelMapper) {
        this.resetPasswordInteractor = resetPasswordInteractor;
        this.forgotPasswordUserModelMapper = forgotPasswordUserModelMapper;
    }

    protected void setView(ResetPasswordRequestView resetPasswordRequestView) {
        this.resetPasswordRequestView = resetPasswordRequestView;
    }

    public void initialize(ResetPasswordRequestView resetPasswordRequestView) {
        setView(resetPasswordRequestView);
        resetPasswordRequestView.disableNextButton();
    }

    public void next() {
        resetPasswordInteractor.attempResetPassword(resetPasswordRequestView.getUsernameOrEmail(), //
          new Interactor.Callback<ForgotPasswordResult>() {
              @Override
              public void onLoaded(ForgotPasswordResult forgotPasswordResult) {
                  ForgotPasswordUserModel model = forgotPasswordUserModelMapper.transform(forgotPasswordResult);
                  resetPasswordRequestView.navigateToResetPasswordConfirmation(model);
              }
          }, //
          new Interactor.ErrorCallback() {
              @Override
              public void onError(ShootrException error) {
                  //TODO
              }
          });
    }

    public void onUsernameOrEmailChanged(String usernameOrEmail) {
        if (!filterInput(usernameOrEmail).isEmpty()) {
            resetPasswordRequestView.enableNextButton();
        } else {
            resetPasswordRequestView.disableNextButton();
        }
    }

    private String filterInput(String inputText) {
        return inputText.trim();
    }

    @Override
    public void resume() {
        /* no-op */
    }

    @Override
    public void pause() {
        /* no-op */
    }
}
