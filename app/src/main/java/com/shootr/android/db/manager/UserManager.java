package com.shootr.android.db.manager;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.shootr.android.data.entity.Synchronized;
import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.db.DatabaseContract;
import com.shootr.android.db.DatabaseContract.SyncColumns;
import com.shootr.android.db.DatabaseContract.UserTable;
import com.shootr.android.db.mappers.UserMapper;
import com.squareup.phrase.Phrase;
import java.sql.SQLException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class UserManager extends AbstractManager {

    UserMapper userMapper;
    private SessionRepository sessionRepository;
    private static final String CSYS_SYNCHRONIZED = SyncColumns.CSYS_SYNCHRONIZED;
    private static final String USER_TABLE = UserTable.TABLE;
    private static final String CSYS_DELETED = SyncColumns.CSYS_DELETED;

    @Inject
    public UserManager(SQLiteOpenHelper openHelper, UserMapper userMapper, SessionRepository sessionRepository) {
        super(openHelper);
        this.userMapper = userMapper;
        this.sessionRepository = sessionRepository;
    }

    /**
     * Insert User list
     */
    public void saveUsersAndDeleted(List<UserEntity> users) throws SQLException {
        for (UserEntity user : users) {
            ContentValues contentValues = userMapper.toContentValues(user);
            contentValues.put(CSYS_SYNCHRONIZED, "S");
            if (contentValues.getAsLong(CSYS_DELETED) != null) {
                deleteUser(user);
            } else {
                getWritableDatabase().insertWithOnConflict(USER_TABLE, null, contentValues,SQLiteDatabase.CONFLICT_REPLACE);
            }
            insertInSync();
        }
    }

    public void saveUsers(List<UserEntity> userEntities) {
        for (UserEntity userEntity : userEntities) {
            saveUser(userEntity);
        }
    }

    /**
     * Insert a user
     */
    public void saveUser(UserEntity user) {
        ContentValues contentValues = userMapper.toContentValues(user);
        if (contentValues.getAsLong(CSYS_DELETED) != null) {
            deleteUser(user);
        } else {
            getWritableDatabase().insertWithOnConflict(UserTable.TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        }
        insertInSync();
    }

    public void saveCurrentUser(UserEntity user) throws  SQLException{
        ContentValues contentValues = userMapper.toContentValues(user);
        if (contentValues.getAsLong(CSYS_DELETED) != null) {
            deleteUser(user);
        } else {
            getWritableDatabase().insertWithOnConflict(UserTable.TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        }
        insertInSync();
    }

    /**
     * Delete a user
     */
    public long deleteUser(UserEntity user) {
        long res = 0;
        String args = DatabaseContract.UserTable.ID + "=?";
        String[] stringArgs = new String[] { String.valueOf(user.getIdUser()) };
        Cursor c = getReadableDatabase().query(USER_TABLE, UserTable.PROJECTION, args, stringArgs, null, null, null);
        if (c.getCount() > 0) {
            res = getWritableDatabase().delete(USER_TABLE, UserTable.ID+"=?", new String[] { String.valueOf(user.getIdUser()) });
        }
        c.close();
        return res;
    }

    /**
     * Retrieve User by idUser
     */
    public UserEntity getUserByIdUser(String idUser) {
        UserEntity resUser = null;
        String args = UserTable.ID + "= ?";
        String[] argsString = new String[] { idUser };

        Cursor c = getReadableDatabase().query(USER_TABLE, UserTable.PROJECTION, args, argsString, null, null, null, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            resUser = userMapper.fromCursor(c);
        }
        c.close();
        return resUser;
    }

    public void insertInSync(){
        insertInTableSync(USER_TABLE, 1, 0, 0);
    }

    public List<UserEntity> getUsersByIds(List<String> usersIds) {
        int userIdsSize = usersIds.size();
        List<UserEntity> result = new ArrayList<>(userIdsSize);
        if (userIdsSize == 0) {
            return result;
        }
        String[] selectionArguments = new String[userIdsSize];
        for (int i = 0; i < userIdsSize; i++) {
            selectionArguments[i] = String.valueOf(usersIds.get(i));
        }
        Cursor queryResults = getReadableDatabase().query(UserTable.TABLE, UserTable.PROJECTION,
          UserTable.ID + " IN (" + createListPlaceholders(userIdsSize) + ")", selectionArguments, null, null,
          UserTable.USER_NAME+" COLLATE NOCASE");

        if (queryResults.getCount() > 0) {
            queryResults.moveToFirst();
            do {
                UserEntity user = userMapper.fromCursor(queryResults);
                if (user != null) {
                    result.add(user);
                }
            } while (queryResults.moveToNext());
        }
        queryResults.close();
        return result;
    }

    public List<UserEntity> getUsersWatchingSomething(List<String> usersIds) {
        int userIdsSize = usersIds.size();
        List<UserEntity> result = new ArrayList<>(userIdsSize);

        if (userIdsSize == 0) {
            return result;
        }

        String whereSelection =
          UserTable.ID + " IN (" + createListPlaceholders(userIdsSize) + ") AND " + UserTable.EVENT_ID + " IS NOT NULL";

        String[] selectionArguments = new String[userIdsSize];
        for (int i = 0; i < userIdsSize; i++) {
            selectionArguments[i] = String.valueOf(usersIds.get(i));
        }

        Cursor queryResults = getReadableDatabase().query(UserTable.TABLE, UserTable.PROJECTION,
          whereSelection, selectionArguments, null, null,
          UserTable.NAME);

        if (queryResults.getCount() > 0) {
            queryResults.moveToFirst();
            do {
                UserEntity user = userMapper.fromCursor(queryResults);
                if (user != null) {
                    result.add(user);
                }
            } while (queryResults.moveToNext());
        }
        queryResults.close();
        return result;
    }

    public List<UserEntity> searchUsers(String searchString){
        List<UserEntity> users = new ArrayList<>();
        String stringToSearch = Normalizer.normalize(searchString, Normalizer.Form.NFD)
          .replaceAll("[^\\p{ASCII}]", "");

        String args = UserTable.USER_NAME_NORMALIZED+" LIKE '%"+stringToSearch+"%' OR "+UserTable.NAME_NORMALIZED+" LIKE '%"+stringToSearch+"%'";
          Cursor c = getReadableDatabase().query(UserTable.TABLE, UserTable.PROJECTION, args,null,null,null,UserTable.NAME);

          if(c.getCount()>0){
            c.moveToFirst();
              do {
                  UserEntity user = userMapper.fromCursor(c);
                  if (user != null) {
                      users.add(user);
                  }
              }while(c.moveToNext());
          }
        c.close();
        return users;
    }

    public List<UserEntity> getUsersNotSynchronized() {
        String whereClause = Phrase.from("{field} = '{n}' or {field} = '{u}'")
          .put("field", UserTable.CSYS_SYNCHRONIZED)
          .put("n", Synchronized.SYNC_NEW)
          .put("u", Synchronized.SYNC_UPDATED)
          .format().toString();
        Cursor queryResult =
          getReadableDatabase().query(UserTable.TABLE, UserTable.PROJECTION, whereClause, null, null, null, null);

        List<UserEntity> resultUsers = new ArrayList<>(queryResult.getCount());
        if (queryResult.getCount() > 0) {
            queryResult.moveToFirst();
            do {
                UserEntity watchEntity = userMapper.fromCursor(queryResult);
                resultUsers.add(watchEntity);
            } while (queryResult.moveToNext());
        }
        queryResult.close();
        return resultUsers;
    }

    public UserEntity getUserByUsername(String username) {
        UserEntity resUser = null;
        String args = UserTable.USER_NAME + "= ?";
        String[] argsString = new String[] { username };

        Cursor c = getReadableDatabase().query(USER_TABLE, UserTable.PROJECTION, args, argsString, null, null, null, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            resUser = userMapper.fromCursor(c);
        }
        c.close();
        return resUser;
    }
}
