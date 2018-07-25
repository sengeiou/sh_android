package com.shootr.mobile.domain.utils;

public interface LocaleProvider {

    String getLocale();

    String getLanguage();

    String getCountry();

    String getLocaleInLanguageTag();
}
