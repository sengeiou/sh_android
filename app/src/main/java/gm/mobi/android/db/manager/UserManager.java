package gm.mobi.android.db.manager;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;
import java.util.List;

import gm.mobi.android.db.GMContract;
import gm.mobi.android.db.GMContract.*;
import gm.mobi.android.db.mappers.FollowMapper;
import gm.mobi.android.db.mappers.UserMapper;
import gm.mobi.android.db.objects.Follow;
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
        //TODO error handling? if(res<0)
    }

    /**
     * Delete a user
     */
    public static long deleteUser(SQLiteDatabase db, User user) {
        return db.delete(UserTable.TABLE, UserTable.ID + " = ", new String[]{String.valueOf(String.valueOf(user.getIdUser()))});
    }

}
