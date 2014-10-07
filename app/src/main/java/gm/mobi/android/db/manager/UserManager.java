package gm.mobi.android.db.manager;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import gm.mobi.android.db.GMContract;
import gm.mobi.android.db.GMContract.*;
import gm.mobi.android.db.mappers.UserMapper;
import gm.mobi.android.db.objects.User;

public class UserManager extends AbstractManager {

    UserMapper userMapper;
    private static final String USER_TABLE = UserTable.TABLE;
    private static final String CSYS_DELETED = SyncColumns.CSYS_DELETED;
    @Inject
    public UserManager(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    /**
     * Retrieve currentUser
     * *
     */
    public User getCurrentUser() {
        User user = null;
        if (!isTableEmpty(USER_TABLE)) {
            Cursor c =
              db.query(USER_TABLE, UserTable.PROJECTION, UserTable.SESSION_TOKEN + " IS NOT NULL", null, null,
                null, null, "1");
            if (c.getCount() > 0) {
                c.moveToFirst();
                user = userMapper.fromCursor(c);
            }
            c.close();
        }
        return user;
    }

    /**
     * Insert User list
     */
    public void saveUsers(List<User> users) throws SQLException {
        for (User user : users) {
            ContentValues contentValues = userMapper.toContentValues(user);
            if (contentValues.getAsLong(CSYS_DELETED) != null) {
                deleteUser(user);
            } else {
                db.insertWithOnConflict(USER_TABLE, null, contentValues,SQLiteDatabase.CONFLICT_REPLACE);
            }
            insertInSync();
        }

        //TODO error handling if(res<0)
    }

    /**
     * Insert a user
     */
    public void saveUser(User user) throws SQLException {

        User currentUser = getCurrentUser();
        ContentValues contentValues = null;
        String[] projection = UserTable.PROJECTION;
        String where = UserTable.ID + "=?";
        contentValues =
          currentUser != null ? userMapper.userToContentValues(user, currentUser) : userMapper.toContentValues(user);
        String userId = String.valueOf(contentValues.getAsLong(UserTable.ID));
        String[] args = { userId };
        if (contentValues.getAsLong(CSYS_DELETED) != null) {
            deleteUser(user);
        } else {
            insertOrUpdate(USER_TABLE, contentValues, projection, where, args);
        }
        insertInSync();
        //TODO error handling? if(res<0)
    }

    /**
     * Delete a user
     */
    public long deleteUser(User user) {
        long res = 0;
        String args = GMContract.UserTable.ID + "=?";
        String[] stringArgs = new String[] { String.valueOf(user.getIdUser()) };
        Cursor c = db.query(USER_TABLE, UserTable.PROJECTION, args, stringArgs, null, null, null);
        if (c.getCount() > 0) {
            res = db.delete(USER_TABLE, UserTable.ID, new String[] { String.valueOf(user.getIdUser()) });
        }
        c.close();
        return res;
    }

    /**
     * Retrieve User by idUser
     */
    public User getUserByIdUser(Long idUser) {
        User resUser = null;
        String args = UserTable.ID + "=?";
        String[] argsString = new String[] { String.valueOf(idUser) };

        Cursor c = db.query(USER_TABLE, UserTable.PROJECTION, args, argsString, null, null, null, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            resUser = userMapper.fromCursor(c);
        }
        c.close();
        return resUser;
    }

    public void insertInSync(){
        insertInTableSync(USER_TABLE,1,0,0);
    }
    public List<User> getUsersByIds(List<Long> usersIds) {
        List<User> result = new ArrayList<>(usersIds.size());
        String[] selectionArguments = new String[usersIds.size()];
        for (int i = 0; i < usersIds.size(); i++) {
            selectionArguments[i] = String.valueOf(usersIds.get(i));
        }
        Cursor queryResults = db.query(UserTable.TABLE, UserTable.PROJECTION,
          UserTable.ID + " IN (" + createListPlaceholders(usersIds.size()) + ")", selectionArguments, null, null,
          UserTable.NAME);

        if (queryResults.getCount() > 0) {
            queryResults.moveToFirst();
            do {
                User user = userMapper.fromCursor(queryResults);
                if (user != null) {
                    result.add(user);
                }
            } while (queryResults.moveToNext());
        }
        return result;
    }
}
