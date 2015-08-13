package com.shootr.android.db.manager;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import com.shootr.android.db.DatabaseContract;
import com.shootr.android.db.DatabaseContract.NiceShotTable;
import com.shootr.android.domain.exception.NiceAlreadyMarkedException;
import com.shootr.android.domain.exception.NiceNotMarkedException;
import javax.inject.Inject;

public class NiceManager extends AbstractManager {

    public static final int RESULT_ERROR_OCCURRED = -1;

    @Inject public NiceManager(SQLiteOpenHelper dbHelper) {
        super(dbHelper);
    }

    public void mark(String idShot) throws NiceAlreadyMarkedException {
        ContentValues contentValues = new ContentValues(1);
        contentValues.put(NiceShotTable.ID_SHOT, idShot);
        long operationResult = getWritableDatabase().insert(NiceShotTable.TABLE, null, contentValues);
        if (operationResult == RESULT_ERROR_OCCURRED) {
            throw new NiceAlreadyMarkedException();
        }
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

    public void unmark(String idShot) throws NiceNotMarkedException {
        String where = NiceShotTable.ID_SHOT + " = ?";
        String[] whereArgs = new String[] { idShot };
        int deletedRows = getWritableDatabase().delete(NiceShotTable.TABLE, where, whereArgs);
        if (deletedRows <= 0) {
            throw new NiceNotMarkedException();
        }
    }
}
