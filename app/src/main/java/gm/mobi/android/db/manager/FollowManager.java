package gm.mobi.android.db.manager;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import gm.mobi.android.db.GMContract;
import gm.mobi.android.db.mappers.FollowMapper;
import gm.mobi.android.db.objects.Follow;

public class FollowManager {

    public static void saveFollow(SQLiteDatabase db, Follow follow) throws SQLException{
        ContentValues contentValues = FollowMapper.toContentValues(follow);
        long res = db.insertWithOnConflict(GMContract.FollowTable.TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public static void saveFollows(SQLiteDatabase db, List<Follow> followList){
        for(Follow follow: followList){
            ContentValues contentValues = FollowMapper.toContentValues(follow);
            db.beginTransaction();
            db.insertWithOnConflict(GMContract.FollowTable.TABLE,null,contentValues,SQLiteDatabase.CONFLICT_REPLACE);
            db.endTransaction();
        }
    }

    public static List<Integer> getUserFollowingIds(SQLiteDatabase db, Integer idUser) throws SQLException{
        List<Integer> userIds = new ArrayList<Integer>();
        SQLiteQueryBuilder qBuilder = new SQLiteQueryBuilder();

        String raw_query = "SELECT "+ GMContract.FollowTable.ID_FOLLOWED_USER+" as IDFOLLOWING FROM "
                + GMContract.FollowTable.TABLE
                + " WHERE"
                +  GMContract.UserTable.ID+" = "+idUser;
        Cursor c = db.rawQuery(raw_query, new String[]{});
        int count = c.getCount();
        if(count > 0) {
            c.moveToFirst();
            while(!c.isAfterLast()) {
                userIds.add(c.getColumnIndex("IDFOLLOWING"));
                c.moveToNext();
            }
        }

        c.close();

    return userIds;
    }
}
