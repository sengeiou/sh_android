package com.shootr.android.ui.base;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;

import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.db.manager.UserManager;
import com.shootr.android.domain.UserEntity;
import com.shootr.android.ui.activities.registro.WelcomeLoginActivity;
import javax.inject.Inject;

public class BaseSignedInActivity extends BaseActivity {

    @Inject UserManager userManager;
    @Inject SQLiteOpenHelper dbHelper;
    @Inject SessionRepository sessionRepository;

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
        return sessionRepository.getSessionToken() != null && sessionRepository.getCurrentUserId() > 0L;
    }

    public void restoreSession() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        UserEntity currentUser = userManager.getUserByIdUser(sessionRepository.getCurrentUserId());
        sessionRepository.setCurrentUser(currentUser);
    }

    private void finishActivityAndLogin() {
        finish();
        overridePendingTransition(0, 0);
        startActivity(new Intent(this, WelcomeLoginActivity.class));
    }


    /**
     * Utility method to get the current user from the Application object.
     * WARNING: Make sure to call {link @restoreSessionOrLogin} before using this method,
     * and stop execution if login is required.
     *
     * @return Currently signed in User
     */
    public UserEntity getCurrentUser() {
        return sessionRepository.getCurrentUser();
    }

}
