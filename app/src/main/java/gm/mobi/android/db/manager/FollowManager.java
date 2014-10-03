package gm.mobi.android.db.manager;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;

import gm.mobi.android.db.objects.User;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import gm.mobi.android.db.GMContract.FollowTable;
import gm.mobi.android.db.GMContract;
import gm.mobi.android.db.mappers.FollowMapper;
import gm.mobi.android.db.objects.Follow;
import gm.mobi.android.db.objects.TableSync;
import timber.log.Timber;

public class FollowManager {


    SQLiteOpenHelper dbHelper;

    @Inject
    public FollowManager(SQLiteOpenHelper dbHelper){
        this.dbHelper = dbHelper;
    }

    /**
     * Insert a Follow
     */
    public void saveFollow(Follow follow) throws SQLException {

        if(follow!=null){
            ContentValues contentValues = FollowMapper.toContentValues(follow);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            if (contentValues.get(GMContract.SyncColumns.CSYS_DELETED) != null) {
                deleteFollow(db, follow);
            } else {
                db.insertWithOnConflict(GMContract.FollowTable.TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
            }
            insertFollowInTableSync(db);
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
    public static List<Long> getUserFollowingIds(SQLiteDatabase db, Long idUser) throws SQLException {
        List<Long> userIds = new ArrayList<>();
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
                userIds.add(c.getLong(0));
                c.moveToNext();
            }
        }
        db.endTransaction();
        c.close();
        userIds.add(idUser);
        return userIds;
    }

    public int getFollowRelationship( User fromUser, User toUser) {
        int resultRelationship = Follow.RELATIONSHIP_NONE;
        String fromUserIdArgument = String.valueOf(fromUser.getIdUser());
        String toUserIdArgument = String.valueOf(toUser.getIdUser());

        String selection = "("
            + FollowTable.ID_USER
            + "=? and "
            + FollowTable.ID_FOLLOWED_USER
            + "=?) OR ("
            + FollowTable.ID_USER
            + "=? and "
            + FollowTable.ID_FOLLOWED_USER
            + "=?)";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor queryResults = db.query(FollowTable.TABLE, FollowTable.PROJECTION, selection,
            new String[] {
                fromUserIdArgument, toUserIdArgument, toUserIdArgument, fromUserIdArgument
            }, null, null, null, null);

        if (queryResults.getCount() > 0) {
            queryResults.moveToFirst();
            boolean iFollowHim = false;
            boolean heFollowsMe = false;
            do {
                Follow follow = FollowMapper.fromCursor(queryResults);
                if (follow != null) {
                    if (follow.getIdUser().equals(fromUser.getIdUser()) && follow.getFollowedUser()
                        .equals(toUser.getIdUser())) {
                        iFollowHim = true;
                    } else if (follow.getIdUser().equals(toUser.getIdUser())
                        && follow.getFollowedUser().equals(fromUser.getIdUser())) {
                        heFollowsMe = true;
                    }

                    if (iFollowHim && heFollowsMe) {
                        resultRelationship = Follow.RELATIONSHIP_BOTH;
                    }else if (iFollowHim) {
                        resultRelationship = Follow.RELATIONSHIP_FOLLOWING;
                    }else if (heFollowsMe) {
                        resultRelationship = Follow.RELATIONSHIP_FOLLOWER;
                    }
                }
            } while (queryResults.moveToNext());
        }
        return resultRelationship;
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
