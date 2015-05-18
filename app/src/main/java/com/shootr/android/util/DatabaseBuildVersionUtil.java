package com.shootr.android.util;

import com.shootr.android.BuildConfig;
import javax.inject.Inject;

public class DatabaseBuildVersionUtil {

    @Inject public DatabaseBuildVersionUtil(){

    }

    public int getDatabaseVersionFromBuild(){
        return BuildConfig.DATABASE_VERSION;
    }

}
