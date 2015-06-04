package com.shootr.android.domain.interactor.user;

import com.shootr.android.domain.interactor.Interactor;
import javax.inject.Inject;

public class ConfirmResetPasswordInteractor {

    @Inject
    public ConfirmResetPasswordInteractor() {
    }

    //TODO this is a dummy public contract
    public void confirmResetPassword(String idUser,
      Interactor.CompletedCallback completedCallback,
      Interactor.ErrorCallback errorCallback) {
        completedCallback.onCompleted();
    }
}
