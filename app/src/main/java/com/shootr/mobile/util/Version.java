package com.shootr.mobile.util;

import com.shootr.mobile.BuildConfig;

import javax.inject.Inject;

public class Version {
    
    @Inject public Version(){

    }

    public int getDatabaseVersion(){
        return BuildConfig.DATABASE_VERSION;
    }

    public int getVersionCode(){
        return  BuildConfig.VERSION_CODE;
    }

    public String getVersionName(){
        return BuildConfig.VERSION_NAME;
    }
}
