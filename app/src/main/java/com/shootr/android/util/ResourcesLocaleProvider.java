package com.shootr.android.util;

import android.content.res.Resources;
import com.shootr.android.domain.utils.LocaleProvider;
import javax.inject.Inject;

public class ResourcesLocaleProvider implements LocaleProvider {

    private Resources resources;

    @Inject public ResourcesLocaleProvider(Resources resources) {
        this.resources = resources;
    }

    @Override public String getLocale() {
        return resources.getConfiguration().locale.toString();
    }
}
