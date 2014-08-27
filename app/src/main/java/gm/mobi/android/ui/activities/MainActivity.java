package gm.mobi.android.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;


import gm.mobi.android.R;
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

        if (canOpenApp()) {
            setContentView(R.layout.main_activity);
        } else {
            finish();
            overridePendingTransition(0, 0);
            startActivity(new Intent(this, WelcomeLoginActivity.class));
        }
    }

    public boolean canOpenApp() {
        return PreferenceManager.getDefaultSharedPreferences(this).getBoolean(PREF_IS_USER_REGISTERED, false);
    }
}
