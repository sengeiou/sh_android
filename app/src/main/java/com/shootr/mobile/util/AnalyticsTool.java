package com.shootr.mobile.util;

import android.app.Activity;
import android.content.Context;

public interface AnalyticsTool {

    void init(Context context);

    void setUserId(String userId);

    void analyticsStart(Context context, String name);

    void analyticsStop(Context context, Activity activity);

}
