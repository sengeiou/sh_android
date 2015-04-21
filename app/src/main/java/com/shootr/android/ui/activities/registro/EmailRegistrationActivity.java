package com.shootr.android.ui.activities.registro;

import android.widget.EditText;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.shootr.android.R;
import com.shootr.android.ui.ToolbarDecorator;
import com.shootr.android.ui.activities.BaseToolbarDecoratedActivity;

public class EmailRegistrationActivity extends BaseToolbarDecoratedActivity {


    @InjectView(R.id.registration_email) EditText email;
    @InjectView(R.id.registration_username) EditText username;
    @InjectView(R.id.registration_password) EditText password;

    @Override protected void setupToolbar(ToolbarDecorator toolbarDecorator) {

    }

    @Override protected int getLayoutResource() {
        return R.layout.activity_registration_email;
    }

    @Override protected void initializeViews() {
        ButterKnife.inject(this);
    }

    @Override protected void initializePresenter() {

    }

    @Override protected boolean requiresUserLogin() {
        return false;
    }
}
