package com.shootr.android.data.mapper;

import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.db.manager.UserManager;
import javax.inject.Inject;

public class DatabaseUserAvatarUrlProvider implements UserAvatarUrlProvider {

    private final UserManager userManager;
    private final GeneratedUserAvatarUrlProvider generatedUserAvatarUrlProvider;

    @Inject public DatabaseUserAvatarUrlProvider(UserManager userManager,
      GeneratedUserAvatarUrlProvider generatedUserAvatarUrlProvider) {
        this.userManager = userManager;
        this.generatedUserAvatarUrlProvider = generatedUserAvatarUrlProvider;
    }

    @Override
    public String thumbnail(String userId) {
        UserEntity user = userManager.getUserByIdUser(userId);
        if (user != null) {
            return user.getPhoto();
        } else {
            return generatedUserAvatarUrlProvider.thumbnail(userId);
        }
    }


}
