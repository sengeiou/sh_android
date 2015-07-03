package com.shootr.android;

import android.content.Context;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.shootr.android.data.dagger.ApplicationContext;
import javax.inject.Inject;

public class FacebookController {

    private final Context appContext;

    @Inject public FacebookController(@ApplicationContext Context appContext) {
        this.appContext = appContext;
    }

    public void logout() {
        if (!FacebookSdk.isInitialized()) {
            FacebookSdk.sdkInitialize(appContext);
        }
        LoginManager.getInstance().logOut();
    }
}
