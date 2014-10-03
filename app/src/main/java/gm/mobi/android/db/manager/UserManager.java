package gm.mobi.android.db.manager;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.SQLException;

import java.util.List;

import javax.inject.Inject;

import gm.mobi.android.db.GMContract;
import gm.mobi.android.db.GMContract.*;
import gm.mobi.android.db.mappers.UserMapper;
import gm.mobi.android.db.objects.TableSync;
import gm.mobi.android.db.objects.User;



public class UserManager {

    SQLiteOpenHelper dbHelper;

    @Inject
    public UserManager(SQLiteOpenHelper dbHelper){
        this.dbHelper = dbHelper;
    }

    /**
     * Retrieve currentUser
     * *
     */
    public User getCurrentUser() {
        User user = null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if(!GeneralManager.isTableEmpty(db,UserTable.TABLE)) {
             Cursor c = db.query(UserTable.TABLE, UserTable.PROJECTION, UserTable.SESSION_TOKEN + " IS NOT NULL", null, null, null, null, "1");
            if (c.getCount() > 0) {
                c.moveToFirst();
                user = UserMapper.fromCursor(c);
            }
            c.close();
        }
        return user;
    }

    /**
     * Insert User list
     */
    public void saveUsers(List<User> userList) throws SQLException {
        long res;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        for (User user : userList) {
            ContentValues contentValues = UserMapper.toContentValues(user);
            if (contentValues.getAsLong(SyncColumns.CSYS_DELETED) != null) {
                res = deleteUser(db, user);
            } else {
                res = db.insertWithOnConflict(GMContract.UserTable.TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
            }
            insertOrUpdateUserInTableSync(db);
        }
        //TODO error handling if(res<0)
    }

    /**
     * Insert a user
     */
    public void saveUser( User user) throws SQLException {

        User currentUser = getCurrentUser();
        ContentValues contentValues = null;
        String[] projection = UserTable.PROJECTION;
        String where = UserTable.ID+"=?";
        contentValues = currentUser!=null ? UserMapper.userToContentValues(user,currentUser) : UserMapper.toContentValues(user);
        String userId = String.valueOf(contentValues.getAsLong(UserTable.ID));
        String[] args = {userId};

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (contentValues.getAsLong(SyncColumns.CSYS_DELETED) != null) {
             deleteUser(db, user);
        }else{
            GeneralManager.insertOrUpdate(db, UserTable.TABLE,contentValues,projection,where,args);
        }
        insertOrUpdateUserInTableSync(db);
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

    /**
     * Insert or Update the entry in sync table
     * **/
     public static long insertOrUpdateUserInTableSync(SQLiteDatabase db){
        TableSync tablesSync = new TableSync();
        tablesSync.setOrder(1); // It's the first table we add in this table, because it's the first data type the application insert in database
        tablesSync.setDirection("OUTPUT");
        tablesSync.setEntity(UserTable.TABLE);
        tablesSync.setMax_timestamp(System.currentTimeMillis());
        if(GeneralManager.isTableEmpty(db, UserTable.TABLE)){
            tablesSync.setMin_timestamp(System.currentTimeMillis());
        }
        return SyncTableManager.insertOrUpdateSyncTable(db,tablesSync);
    }

    /**
     * Retrieve User by idUser
     * */
    public User getUserByIdUser( Long idUser){
       User resUser = null;
       String args = UserTable.ID+"=?";
       String[] argsString =  new String[]{String.valueOf(idUser)};
       SQLiteDatabase db = dbHelper.getReadableDatabase();
       Cursor c = db.query(UserTable.TABLE, UserTable.PROJECTION,args,argsString,null,null,null,null);
        if(c.getCount()>0){
            c.moveToFirst();
            resUser = UserMapper.fromCursor(c);
        }
        c.close();
        return resUser;
    }


}
