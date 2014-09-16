package gm.mobi.android.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;


import javax.inject.Inject;

import gm.mobi.android.GolesApplication;
import gm.mobi.android.R;
import gm.mobi.android.db.OpenHelper;
import gm.mobi.android.db.manager.UserManager;
import gm.mobi.android.db.objects.User;
import gm.mobi.android.ui.AppContainer;
import gm.mobi.android.ui.activities.registro.EmailLoginActivity;
import gm.mobi.android.ui.activities.registro.WelcomeLoginActivity;
import gm.mobi.android.ui.base.BaseActivity;

public class MainActivity extends BaseActivity {


    public static final String PREF_IS_USER_REGISTERED = "is_user_registered";

    //TODO recibir parámetros para indicar si viene de registro, login o nueva
    public static Intent getIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!canOpenApp()) {
            finish();
            overridePendingTransition(0, 0);
            startActivity(new Intent(this, WelcomeLoginActivity.class));
            return;
        }
        setContainerContent(R.layout.main_activity);
    }

    public boolean canOpenApp() {
        OpenHelper helper = new OpenHelper(this);

        User signedUser = UserManager.getSignedUser(helper.getReadableDatabase());
        return signedUser != null;
    }
}
