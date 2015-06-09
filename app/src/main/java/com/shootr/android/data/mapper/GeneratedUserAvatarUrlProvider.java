package com.shootr.android.data.mapper;

import com.shootr.android.BuildConfig;
import javax.inject.Inject;

public class GeneratedUserAvatarUrlProvider implements UserAvatarUrlProvider {

    private static final String SIZE_THUMBNAIL = "thumbnail";
    private static final String URL_PATTERN = BuildConfig.RESOURCES_ENDPOINT_BASE + "users/%s/profileImages/profile.%s";

    @Inject public GeneratedUserAvatarUrlProvider() {
    }

    @Override
    public String thumbnail(String userId) {
        return getUrl(userId, SIZE_THUMBNAIL);
    }

    private String getUrl(String userId, String size) {
        return String.format(URL_PATTERN, userId, size);
    }

}
