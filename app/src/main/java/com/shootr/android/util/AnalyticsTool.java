package com.shootr.android.util;

import android.content.Context;

public interface AnalyticsTool {

    void init(Context context);

    interface Factory {

        AnalyticsTool create();

    }
}
