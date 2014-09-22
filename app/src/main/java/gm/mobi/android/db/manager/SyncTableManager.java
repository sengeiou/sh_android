package gm.mobi.android.db.manager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import gm.mobi.android.db.GMContract;
import gm.mobi.android.util.TimeUtils;

public class SyncTableManager {

    public static int NUMDAYS = 7;
    public static Long getLastModifiedDate(Context context, SQLiteDatabase db, String entity) {
        Long lastDateModified = null;

//        Cursor c = db.query(GMContract.TablesSync.TABLE, GMContract.TablesSync.PROJECTION, GMContract.TablesSync.ENTITY + " = " + entity, null, null, null, null, "1");
//        if (c.getCount() > 0) {
//            c.moveToFirst();
//            lastDateModified = c.getLong(c.getColumnIndex(GMContract.TablesSync.MAX_TIMESTAMP));
//        } else {
            lastDateModified = TimeUtils.getNDaysAgo(context,NUMDAYS);
//        }
//        c.close();
        return lastDateModified;
    }
}
