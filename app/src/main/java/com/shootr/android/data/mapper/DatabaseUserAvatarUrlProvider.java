package com.shootr.android.data.mapper;

import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.db.manager.UserManager;
import javax.inject.Inject;

public class DatabaseUserAvatarUrlProvider implements UserAvatarUrlProvider {

    private final UserManager userManager;

    @Inject public DatabaseUserAvatarUrlProvider(UserManager userManager) {
        this.userManager = userManager;
    }

    @Override
    public String thumbnail(String userId) {
        UserEntity user = userManager.getUserByIdUser(userId);
        if (user != null) {
            return user.getPhoto();
        } else {
            return null;
        }
    }


}
