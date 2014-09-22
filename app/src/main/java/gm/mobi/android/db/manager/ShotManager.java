package gm.mobi.android.db.manager;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;
import java.util.List;

import gm.mobi.android.db.GMContract;
import gm.mobi.android.db.mappers.ShotMapper;
import gm.mobi.android.db.objects.Shot;

public class ShotManager {

    public static void saveShot(SQLiteDatabase db, Shot follow) throws SQLException {
        ContentValues contentValues = ShotMapper.toContentValues(follow);
        long res = db.insertWithOnConflict(GMContract.ShotTable.TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public static void saveShots(SQLiteDatabase db, List<Shot> shotList){
        for(Shot shot: shotList){
            ContentValues contentValues = ShotMapper.toContentValues(shot);
            db.beginTransaction();
            db.insertWithOnConflict(GMContract.ShotTable.TABLE,null,contentValues,SQLiteDatabase.CONFLICT_REPLACE);
            db.endTransaction();
        }
    }
}
