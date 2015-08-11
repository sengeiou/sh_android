package com.shootr.android.db.manager;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import com.shootr.android.db.DatabaseContract.NiceShotTable;
import javax.inject.Inject;

public class NiceManager extends AbstractManager {

    @Inject public NiceManager(SQLiteOpenHelper dbHelper) {
        super(dbHelper);
    }

    public void mark(String idShot) {
        ContentValues contentValues = new ContentValues(1);
        contentValues.put(NiceShotTable.ID_SHOT, idShot);
        getWritableDatabase().insert(NiceShotTable.TABLE, null, contentValues);
    }

    public boolean isMarked(String idShot) {
        String where = NiceShotTable.ID_SHOT + " = ?";
        String[] whereArgs = new String[] { idShot };
        Cursor query = getReadableDatabase().query(NiceShotTable.TABLE,
          NiceShotTable.PROJECTION,
          where,
          whereArgs,
          null,
          null,
          null,
          "1");
        boolean result = query.getCount() > 0;
        query.close();
        return result;
    }

    public void unmark(String idShot) {
        String where = NiceShotTable.ID_SHOT + " = ?";
        String[] whereArgs = new String[] { idShot };
        getWritableDatabase().delete(NiceShotTable.TABLE, where, whereArgs);
    }
}
