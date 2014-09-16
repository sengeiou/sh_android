package gm.mobi.android.db.manager;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;

import gm.mobi.android.db.GMContract.*;
import gm.mobi.android.db.mappers.UserMapper;
import gm.mobi.android.db.objects.User;

public class UserManager {

    public static User getSignedUser(SQLiteDatabase db) {
        User user = null;
        Cursor c = db.query(UserTable.TABLE, UserTable.PROJECTION, UserTable.SESSION_TOKEN + " IS NOT NULL", null, null, null, null, "1");
        if (c.getCount() > 0) {
            c.moveToFirst();
            user = UserMapper.fromCursor(c);
        }
        c.close();

        return user;
    }

    public static void saveUser(SQLiteDatabase db, User u) throws SQLException{
        ContentValues contentValues = UserMapper.toContentValues(u);
        long res = db.insertWithOnConflict(UserTable.TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        //TODO error handling? if(res<0)
    }

}
