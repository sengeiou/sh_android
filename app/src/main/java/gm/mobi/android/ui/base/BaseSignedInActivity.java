package gm.mobi.android.ui.base;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import gm.mobi.android.GolesApplication;
import gm.mobi.android.data.SessionManager;
import gm.mobi.android.data.SessionManagerImpl;
import gm.mobi.android.db.manager.UserManager;
import gm.mobi.android.db.objects.UserEntity;
import gm.mobi.android.ui.activities.registro.WelcomeLoginActivity;
import javax.inject.Inject;

public class BaseSignedInActivity extends BaseActivity {

    @Inject UserManager userManager;
    @Inject SQLiteOpenHelper dbHelper;
    @Inject SessionManager sessionManager;

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
        return sessionManager.getCurrentUser() != null;
    }

    public boolean isSessionStored() {
        return sessionManager.getSessionToken() != null && sessionManager.getCurrentUserId() > 0L;
    }

    public void restoreSession() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        userManager.setDataBase(db);
        UserEntity currentUser = userManager.getUserByIdUser(sessionManager.getCurrentUserId());
        db.close();
        sessionManager.setCurrentUser(currentUser);
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
        return sessionManager.getCurrentUser();
    }

}
