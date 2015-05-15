package com.shootr.android.ui.base;

import android.content.Intent;
import android.os.Bundle;

import com.shootr.android.data.mapper.UserEntityMapper;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.db.manager.UserManager;
import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.ui.activities.registro.WelcomeLoginActivity;
import javax.inject.Inject;

public class BaseSignedInActivity extends BaseToolbarActivity {

    @Inject UserManager userManager;
    @Inject SessionRepository sessionRepository;
    @Inject UserEntityMapper userEntityMapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    /**
     * Retrieves the current User from database, or redirect to login activity if not found.
     * @return true if there is a user signed in, false if there is not and will open the login screen.
     */
    //TODO refactor: method name not clear
    public boolean restoreSessionOrLogin() {
        if (isSessionActive()) {
            return true;
        } else {
            if (isSessionStored()) {
                restoreSession();
                return true;
            } else {
               finishActivityAndLogin();
                return false;
            }
        }
    }

    public boolean isSessionActive() {
        return sessionRepository.getCurrentUser() != null;
    }

    public boolean isSessionStored() {
        return sessionRepository.getSessionToken() != null &&
          sessionRepository.getCurrentUserId() != null;
    }

    public void restoreSession() {
        UserEntity currentUser = userManager.getUserByIdUser(sessionRepository.getCurrentUserId());
        sessionRepository.setCurrentUser(userEntityMapper.transform(currentUser, currentUser.getIdUser()));
    }

    private void finishActivityAndLogin() {
        finish();
        overridePendingTransition(0, 0);
        startActivity(new Intent(this, WelcomeLoginActivity.class));
    }

}
