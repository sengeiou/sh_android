package com.shootr.mobile.ui.debug.debugactions;

import com.shootr.mobile.ui.activities.registro.EmailLoginActivity;
import com.shootr.mobile.ui.debug.ContextualDebugActions;

public class LoginDebugAction extends ContextualDebugActions.DebugAction<EmailLoginActivity> {

    private String username;
    private String password;

    public LoginDebugAction(String username, String password) {
        super(EmailLoginActivity.class);
        this.username = username;
        this.password = password;
    }

    @Override public String name() {
        return "Login with " + username;
    }

    @Override public void run(EmailLoginActivity activity) {
        activity.emailUsername.setText(username);
        activity.password.setText(password);
        activity.password.requestFocus();
        activity.onLoginWithEmailButtonClick();
    }
}
