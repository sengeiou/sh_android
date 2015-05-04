package com.shootr.android.data.mapper;

import com.shootr.android.BuildConfig;
import javax.inject.Inject;

public class UserAvatarUrlBuilder {

    private static final String SIZE_THUMBNAIL = "thumbnail";
    private static final String URL_PATTERN = BuildConfig.RESOURCES_ENDPOINT_BASE + "users/%d/profileImages/profile.%s";

    @Inject public UserAvatarUrlBuilder() {
    }

    public String thumbnail(String userId) {
        return getUrl(userId, SIZE_THUMBNAIL);
    }

    private String getUrl(String userId, String size) {
        return String.format(URL_PATTERN, userId, size);
    }
}
