package com.shootr.mobile.util;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import com.shootr.mobile.domain.User;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.user.GetUserByUsernameInteractor;
import com.shootr.mobile.ui.activities.ProfileContainerActivity;
import timber.log.Timber;

public class ProfileOpener {

    private Context context;
    private Intent intent;

    private GetUserByUsernameInteractor getUserByUsernameInteractor;

    public ProfileOpener(Context context, GetUserByUsernameInteractor getUserByUsernameInteractor) {
        this.context = context;
        this.getUserByUsernameInteractor = getUserByUsernameInteractor;
    }

    public void openUserProfileFromUsername(String username) {
        getUserByUsernameInteractor.searchUserByUsername(username, new Interactor.Callback<User>() {
            @Override public void onLoaded(User user) {
                if (user != null) {
                    startProfileContainerActivity(user.getIdUser());
                } else {
                    showNotification(context);
                }
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                Timber.e(error, "Error while searching user by username");
            }
        });
    }

    private void showNotification(Context context) {
        Toast.makeText(context, "User not found", Toast.LENGTH_LONG).show();
    }

    private void startProfileContainerActivity(String idUser) {
        intent = ProfileContainerActivity.getIntent(context, idUser);
    }

    public Intent getIntent() {
        return intent;
    }
}
