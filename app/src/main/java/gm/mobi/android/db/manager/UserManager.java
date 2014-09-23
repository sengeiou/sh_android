package gm.mobi.android.db.manager;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.sql.SQLException;
import java.util.List;

import gm.mobi.android.db.GMContract;
import gm.mobi.android.db.GMContract.*;
import gm.mobi.android.db.mappers.FollowMapper;
import gm.mobi.android.db.mappers.UserMapper;
import gm.mobi.android.db.objects.Follow;
import gm.mobi.android.db.objects.TableSync;
import gm.mobi.android.db.objects.User;


public class UserManager {

    /**
     * Retrieve currentUser
     * *
     */
    public static User getCurrentUser(SQLiteDatabase db) {
        User user = null;
        Cursor c = db.query(UserTable.TABLE, UserTable.PROJECTION, UserTable.SESSION_TOKEN + " IS NOT NULL", null, null, null, null, "1");
        if (c.getCount() > 0) {
            c.moveToFirst();
            user = UserMapper.fromCursor(c);
        }
        c.close();
        return user;
    }

    /**
     * Insert User list
     */
    public static void saveUsers(SQLiteDatabase db, List<User> userList) throws SQLException {
        long res;
        for (User user : userList) {
            ContentValues contentValues = UserMapper.toContentValues(user);
            db.beginTransaction();
            if (contentValues.getAsLong(SyncColumns.CSYS_DELETED) != null) {
                res = deleteUser(db, user);
            } else {
                res = db.insertWithOnConflict(GMContract.UserTable.TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
            }
            insertUserInTableSync(db);
            db.endTransaction();
        }
        //TODO error handling if(res<0)
    }

    /**
     * Insert a user
     */
    public static void saveUser(SQLiteDatabase db, User user) throws SQLException {
        ContentValues contentValues = UserMapper.toContentValues(user);
        long res;
        if (contentValues.getAsLong(SyncColumns.CSYS_DELETED) != null) {
            res = deleteUser(db, user);
        } else {
            res = db.insertWithOnConflict(UserTable.TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        }
        insertUserInTableSync(db);
        //TODO error handling? if(res<0)
    }

    /**
     * Delete a user
     */
    public static long deleteUser(SQLiteDatabase db, User user) {
        long res = 0;
        String args = GMContract.UserTable.ID+"=?";
        String[] stringArgs = new String[]{String.valueOf(user.getIdUser())};
        Cursor c = db.query(UserTable.TABLE, UserTable.PROJECTION, args, stringArgs, null, null, null);
        if (c.getCount() > 0) {
            res = db.delete(UserTable.TABLE, UserTable.ID, new String[]{String.valueOf(user.getIdUser())});
        }
        c.close();
        return res;
    }

    public static long insertUserInTableSync(SQLiteDatabase db){
        TableSync tablesSync = new TableSync();
        tablesSync.setOrder(1); // It's the first table we add in this table, because it's the first data type the application insert in database
        tablesSync.setDirection("OUTPUT");
        tablesSync.setEntity(UserTable.TABLE);
        tablesSync.setMax_timestamp(System.currentTimeMillis());
        if(GeneralManager.isTableEmpty(db, UserTable.TABLE)){
            tablesSync.setMin_timestamp(System.currentTimeMillis());
        }
        //We don't have this information already
//        tablesSync.setMaxRows();
//        tablesSync.setMinRows();

        return SyncTableManager.insertOrUpdateSyncTable(db,tablesSync);
    }

}
