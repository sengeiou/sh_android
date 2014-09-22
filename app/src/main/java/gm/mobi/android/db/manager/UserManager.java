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

    public static void saveUsers(SQLiteDatabase db, List<User> userList) throws  SQLException{
        for(User user: userList){
            ContentValues contentValues = UserMapper.toContentValues(user);
            db.beginTransaction();
            db.insertWithOnConflict(GMContract.UserTable.TABLE,null,contentValues,SQLiteDatabase.CONFLICT_REPLACE);
            db.endTransaction();
        }
    }

    public static void saveUser(SQLiteDatabase db, User u) throws SQLException {
        ContentValues contentValues = UserMapper.toContentValues(u);
        long res = db.insertWithOnConflict(UserTable.TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);

        if(res<0){

            //TODO error handling? if(res<0)
        }
    }

}
