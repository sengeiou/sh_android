package com.shootr.mobile.util;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.telephony.TelephonyManager;
import com.shootr.mobile.data.dagger.ApplicationContext;
import com.shootr.mobile.domain.utils.LocaleProvider;
import javax.inject.Inject;

public class ResourcesLocaleProvider implements LocaleProvider {

    private final Resources resources;
    private final TelephonyManager telephonyManager;

    @Inject
    public ResourcesLocaleProvider(Resources resources, @ApplicationContext Context context) {
        this.resources = resources;
        this.telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    }

    @Override public String getLocale() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return resources.getConfiguration().getLocales().get(0).toString();
        } else {
            return resources.getConfiguration().locale.toString();
        }
    }

    @Override public String getLanguage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return resources.getConfiguration().getLocales().get(0).getLanguage();
        } else {
            return resources.getConfiguration().locale.getLanguage();
        }
    }

    @Override
    public String getCountry() {
        String country = telephonyManager.getNetworkCountryIso();
        if (country != null && !country.isEmpty()) {
            return country.toUpperCase();
        }
        country = telephonyManager.getSimCountryIso();
        if (country != null && !country.isEmpty()) {
            return country;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            country = resources.getConfiguration().getLocales().get(0).getCountry();
        } else {
            country = resources.getConfiguration().locale.getCountry();
        }
        return country;
    }
}
