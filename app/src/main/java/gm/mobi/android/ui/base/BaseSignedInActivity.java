package gm.mobi.android.ui.base;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import gm.mobi.android.GolesApplication;
import gm.mobi.android.db.manager.UserManager;
import gm.mobi.android.db.objects.User;
import gm.mobi.android.ui.activities.registro.WelcomeLoginActivity;
import javax.inject.Inject;

public class BaseSignedInActivity extends BaseActivity {

    @Inject UserManager userManager;
    @Inject SQLiteOpenHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Retrieves the current User from database, or redirect to login activity if not found.
     * @return true if there is a user signed in, false if there is not and will open the login screen.
     */
    public boolean restoreSessionOrLogin() {

        GolesApplication app = GolesApplication.get(this);
        if (app.getCurrentUser() != null) {
            return true;
        } else {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            userManager.setDataBase(db);
            User currentUser = userManager.getCurrentUser();
            db.close();
            if (currentUser != null) {
                app.setCurrentUser(currentUser);
                return true;
            } else {
                finish();
                overridePendingTransition(0, 0);
                startActivity(new Intent(this, WelcomeLoginActivity.class));
                return false;
            }
        }
    }

    /**
     * Utility method to get the current user from the Application object.
     * WARNING: Make sure to call {link @restoreSessionOrLogin} before using this method,
     * and stop execution if login is required.
     *
     * @return Currently signed in User
     */
    public User getCurrentUser() {
        return GolesApplication.get(this).getCurrentUser();
    }

}
