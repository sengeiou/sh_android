package gm.mobi.android.db.manager;

import android.content.Context;

import gm.mobi.android.db.OpenHelper;
import timber.log.Timber;

public class GeneralManager {

    public static void deleteDatabase(Context context) {
        context.deleteDatabase(OpenHelper.DATABASE_NAME);
        Timber.d("Database deleted");
    }
}
