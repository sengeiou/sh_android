package com.shootr.android.util;

import android.content.Context;
import android.content.res.Resources;
import android.telephony.TelephonyManager;
import com.shootr.android.data.dagger.ApplicationContext;
import com.shootr.android.domain.utils.LocaleProvider;
import javax.inject.Inject;

public class ResourcesLocaleProvider implements LocaleProvider {

    private final Resources resources;
    private final TelephonyManager telephonyManager;

    @Inject
    public ResourcesLocaleProvider(Resources resources, @ApplicationContext Context context) {
        this.resources = resources;
        this.telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    }

    @Override
    public String getLocale() {
        return resources.getConfiguration().locale.toString();
    }

    @Override
    public String getLanguage() {
        return resources.getConfiguration().locale.getLanguage();
    }

    @Override
    public String getCountry() {
        String country = telephonyManager.getNetworkCountryIso();
        if (country != null) {
            return country;
        }
        country = telephonyManager.getSimCountryIso();
        if (country != null) {
            return country;
        }
        country = resources.getConfiguration().locale.getCountry();

        return country;
    }
}
