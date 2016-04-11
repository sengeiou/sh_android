package com.shootr.mobile.ui.base;

import android.content.Intent;
import android.os.Bundle;
import com.shootr.mobile.data.entity.UserEntity;
import com.shootr.mobile.data.mapper.UserEntityMapper;
import com.shootr.mobile.db.manager.UserManager;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.ui.activities.registro.LoginSelectionActivity;
import javax.inject.Inject;

public class BaseSignedInActivity extends BaseToolbarActivity {

    @Inject UserManager userManager;
    @Inject SessionRepository sessionRepository;
    @Inject UserEntityMapper userEntityMapper;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Retrieves the current User from database, or redirect to login activity if not found.
     *
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
        return sessionRepository.getSessionToken() != null && sessionRepository.getCurrentUserId() != null;
    }

    public void restoreSession() {
        UserEntity currentUser = userManager.getUserByIdUser(sessionRepository.getCurrentUserId());
        sessionRepository.setCurrentUser(userEntityMapper.transform(currentUser, currentUser.getIdUser()));
    }

    private void finishActivityAndLogin() {
        finish();
        overridePendingTransition(0, 0);
        startActivity(new Intent(this, LoginSelectionActivity.class));
    }
}
