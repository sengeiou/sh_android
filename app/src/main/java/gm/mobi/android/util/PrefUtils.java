package gm.mobi.android.util;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefUtils {

    public static final String PREF_MOCK_CURRENT_TIME = "mock_current_time";

    public static SharedPreferences getMockPreferences(Context context) {
        return context.getSharedPreferences("mock_data", Context.MODE_PRIVATE);
    }


}
