package com.shootr.android.integrationtests;

import android.content.Context;
import com.shootr.android.db.ShootrDbOpenHelper;

public class TestShootrDbOpenHelper extends ShootrDbOpenHelper {

    public TestShootrDbOpenHelper(Context context) {
        super(context, null);
    }

}
