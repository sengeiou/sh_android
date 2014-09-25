package gm.mobi.android.db.manager;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import gm.mobi.android.db.GMContract;
import gm.mobi.android.db.mappers.FollowMapper;
import gm.mobi.android.db.objects.Follow;
import gm.mobi.android.db.objects.TableSync;
import timber.log.Timber;

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
        insertFollowInTableSync(db);
    }

    /**
     * Insert a Follow list
     * *
     */
    public static void saveFollows(SQLiteDatabase db, List<Follow> followList) {
        long res;
        for (Follow follow : followList) {
            ContentValues contentValues = FollowMapper.toContentValues(follow);

            if (contentValues.getAsLong(GMContract.SyncColumns.CSYS_DELETED) != null) {
                res = deleteFollow(db, follow);
            } else {
                res = db.insertWithOnConflict(GMContract.FollowTable.TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
                Timber.i("Follow inserted ",follow.getIdUser());
            }
        }
        insertFollowInTableSync(db);
    }

    /**
     * Retrieve a Following User
     */
    public static List<Integer> getUserFollowingIds(SQLiteDatabase db, Integer idUser) throws SQLException {
        List<Integer> userIds = new ArrayList<>();
        db.beginTransaction();
        String args = GMContract.FollowTable.ID_USER+"=?";
        String[] argsString = new String[]{String.valueOf(idUser)};
        if(GeneralManager.isTableEmpty(db, GMContract.FollowTable.TABLE)){
            Timber.e("La tabla follow está vacia");
        }
        Cursor c = db.query(GMContract.FollowTable.TABLE, new String[]{GMContract.FollowTable.ID_FOLLOWED_USER},args,argsString,null,null,null,null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            while (!c.isAfterLast()) {
                userIds.add(c.getInt(0));
                c.moveToNext();
            }
        }
        db.endTransaction();
        c.close();
        userIds.add(idUser);
        return userIds;
    }

    /**
     * Delete one Follow
     */
    public static long deleteFollow(SQLiteDatabase db, Follow follow) {
        long res = 0;
        String args = GMContract.FollowTable.ID_FOLLOWED_USER + "=? AND " + GMContract.FollowTable.ID_USER + "=?";
        String[] stringArgs = new String[]{String.valueOf(follow.getFollowedUser()), String.valueOf(follow.getIdUser())};
        Cursor c = db.query(GMContract.FollowTable.TABLE, GMContract.FollowTable.PROJECTION, args, stringArgs, null, null, null);
        if (c.getCount() > 0) {
            res = db.delete(GMContract.FollowTable.TABLE, GMContract.FollowTable.ID_FOLLOWED_USER + "=? AND " + GMContract.FollowTable.ID_USER + "=?",
                    new String[]{String.valueOf(follow.getFollowedUser()), String.valueOf(follow.getIdUser())});
        }
        c.close();
        return res;
    }

    public static long insertFollowInTableSync(SQLiteDatabase db){
        TableSync tablesSync = new TableSync();
        tablesSync.setOrder(2); // It's the second data type the application insert in database
        tablesSync.setDirection("BOTH");
        tablesSync.setEntity(GMContract.FollowTable.TABLE);
        tablesSync.setMax_timestamp(System.currentTimeMillis());
        if(GeneralManager.isTableEmpty(db, GMContract.FollowTable.TABLE)){
            tablesSync.setMin_timestamp(System.currentTimeMillis());
        }
        //We don't have this information already
//        tablesSync.setMaxRows();
//        tablesSync.setMinRows();

        return SyncTableManager.insertOrUpdateSyncTable(db,tablesSync);
    }
}
