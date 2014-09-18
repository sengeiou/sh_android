package gm.mobi.android.db.manager;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;

import gm.mobi.android.db.GMContract;
import gm.mobi.android.db.mappers.ShotMapper;
import gm.mobi.android.db.objects.Shot;

public class ShotManager {


    public static void saveShot(SQLiteDatabase db, Shot shot) throws SQLException {
        ContentValues contentValues = ShotMapper.toContentValues(shot);
        long res = db.insertWithOnConflict(GMContract.ShotTable.TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        //TODO error handling? if(res<0)
    }

}
