package gm.mobi.android.db.manager;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import gm.mobi.android.db.GMContract;
import gm.mobi.android.db.OpenHelper;
import timber.log.Timber;

public class GeneralManager {

    public static void deleteDatabase(Context context) {
        context.deleteDatabase(OpenHelper.DATABASE_NAME);
        Timber.d("Database deleted");
    }


    public static boolean isTableEmpty(SQLiteDatabase db, String entity) {
        boolean res = true;
        String raw_query = "SELECT * FROM "
                + entity;
        Cursor c = db.rawQuery(raw_query, new String[]{});
        if (c.getCount() > 0) res = false;
        c.close();

        return res;
    }


    /**
     * Retrieve row's number for table we need
     * */

    public static long numberOfRows(SQLiteDatabase db, String table){
        long numRows = DatabaseUtils.queryNumEntries(db, table);
        return numRows;
    }
}
