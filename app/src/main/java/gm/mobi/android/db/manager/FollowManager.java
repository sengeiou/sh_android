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


    /**
     * Insert a Follow
     */
    public static void saveFollow(SQLiteDatabase db, Follow follow) throws SQLException {
        long res;
        ContentValues contentValues = FollowMapper.toContentValues(follow);
        if (contentValues.get(GMContract.SyncColumns.CSYS_DELETED) != null) {
            res = deleteFollow(db, follow);
        } else {
            res = db.insertWithOnConflict(GMContract.FollowTable.TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        }
    }

    /**
     * Insert a Follow list
     * *
     */
    public static void saveFollows(SQLiteDatabase db, List<Follow> followList) {
        long res;
        for (Follow follow : followList) {
            ContentValues contentValues = FollowMapper.toContentValues(follow);
            db.beginTransaction();
            if (contentValues.getAsLong(GMContract.SyncColumns.CSYS_DELETED) != null) {
                res = deleteFollow(db, follow);
            } else {
                res = db.insertWithOnConflict(GMContract.FollowTable.TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
            }
            db.endTransaction();
        }
    }

    /**
     * Retrieve a Following User
     */
    public static List<Integer> getUserFollowingIds(SQLiteDatabase db, Integer idUser) throws SQLException {
        List<Integer> userIds = new ArrayList<>();

        String raw_query = "SELECT " + GMContract.FollowTable.ID_FOLLOWED_USER + " as IDFOLLOWING FROM "
                + GMContract.FollowTable.TABLE
                + " WHERE "
                + GMContract.UserTable.ID + " = " + idUser;
        Cursor c = db.rawQuery(raw_query, new String[]{});
        if (c.getCount() > 0) {
            c.moveToFirst();
            while (!c.isAfterLast()) {
                userIds.add(c.getColumnIndex("IDFOLLOWING"));
                c.moveToNext();
            }
        }
        c.close();
        return userIds;
    }

    /**
     * Delete one Follow
     */
    public static long deleteFollow(SQLiteDatabase db, Follow follow) {
        return db.delete(GMContract.FollowTable.TABLE, GMContract.FollowTable.ID_FOLLOWED_USER + "=? AND " + GMContract.FollowTable.ID_USER + "=?",
                new String[]{String.valueOf(follow.getFollowedUser()), String.valueOf(follow.getIdUser())});
    }
}
