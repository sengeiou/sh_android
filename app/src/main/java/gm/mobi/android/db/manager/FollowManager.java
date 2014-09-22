package gm.mobi.android.db.manager;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;

import gm.mobi.android.db.GMContract;
import gm.mobi.android.db.mappers.FollowMapper;
import gm.mobi.android.db.objects.Follow;

public class FollowManager {

    public static void saveFollow(SQLiteDatabase db, Follow follow) throws SQLException{
        ContentValues contentValues = FollowMapper.toContentValues(follow);
        long res = db.insertWithOnConflict(GMContract.FollowTable.TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
    }
}
