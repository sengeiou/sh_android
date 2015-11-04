package com.shootr.mobile.db.manager;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.shootr.mobile.data.entity.LocalSynchronized;
import com.shootr.mobile.data.entity.UserEntity;
import com.shootr.mobile.db.DatabaseContract;
import com.shootr.mobile.db.mappers.UserEntityDBMapper;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;

public class UserManager extends AbstractManager {

    private final UserEntityDBMapper userMapper;

    @Inject
    public UserManager(SQLiteOpenHelper openHelper, UserEntityDBMapper userMapper) {
        super(openHelper);
        this.userMapper = userMapper;
    }

    public void saveUsers(List<UserEntity> userEntities) {
        SQLiteDatabase database = getWritableDatabase();
        try {
            database.beginTransaction();
            for (UserEntity userEntity : userEntities) {
                insertUser(userEntity, database);
            }
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

    public void saveUser(UserEntity user) {
        insertUser(user, getWritableDatabase());
    }

    private void insertUser(UserEntity user, SQLiteDatabase writableDatabase) {
        ContentValues contentValues = userMapper.toContentValues(user);
        if (contentValues.getAsString(DatabaseContract.SyncColumns.DELETED) != null) {
            deleteUser(user);
        } else {
            writableDatabase.insertWithOnConflict(DatabaseContract.UserTable.TABLE,
              null,
              contentValues,
              SQLiteDatabase.CONFLICT_REPLACE);
        }
    }

    public long deleteUser(UserEntity user) {
        String where = DatabaseContract.UserTable.ID + "=?";
        String[] whereArguments = new String[] { user.getIdUser() };
        return getWritableDatabase().delete(DatabaseContract.UserTable.TABLE, where, whereArguments);
    }

    public UserEntity getUserByIdUser(String idUser) {
        String where = DatabaseContract.UserTable.ID + "= ?";
        String[] whereArguments = new String[] { idUser };

        return readUser(where, whereArguments);
    }

    public List<UserEntity> getUsersByIds(List<String> usersIds) {
        int userIdsSize = usersIds.size();
        if (userIdsSize == 0) {
            return Collections.emptyList();
        }

        String whereClause = DatabaseContract.UserTable.ID + " IN (" + createListPlaceholders(userIdsSize) + ")";
        String[] whereArguments = new String[userIdsSize];
        for (int i = 0; i < userIdsSize; i++) {
            whereArguments[i] = String.valueOf(usersIds.get(i));
        }

        return readUsers(whereClause, whereArguments);
    }

    public List<UserEntity> getUsersWatchingSomething(List<String> usersIds) {
        int userIdsSize = usersIds.size();

        if (userIdsSize == 0) {
            return Collections.emptyList();
        }

        String whereSelection = DatabaseContract.UserTable.ID
          + " IN ("
          + createListPlaceholders(userIdsSize)
          + ") AND "
          + DatabaseContract.UserTable.ID_WATCHING_STREAM
          + " IS NOT NULL";

        String[] selectionArguments = new String[userIdsSize];
        for (int i = 0; i < userIdsSize; i++) {
            selectionArguments[i] = String.valueOf(usersIds.get(i));
        }

        return readUsers(whereSelection, selectionArguments);
    }

    public List<UserEntity> searchUsers(String searchString) {
        String stringToSearch = Normalizer.normalize(searchString, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");

        String whereClause = DatabaseContract.UserTable.USER_NAME_NORMALIZED
          + " LIKE '%"
          + stringToSearch
          + "%' OR "
          + DatabaseContract.UserTable.NAME_NORMALIZED
          + " LIKE '%"
          + stringToSearch
          + "%'";

        return readUsers(whereClause, null);
    }

    public List<UserEntity> getUsersNotSynchronized() {
        String whereClause = DatabaseContract.UserTable.SYNCHRONIZED
          + " = '"
          + LocalSynchronized.SYNC_NEW
          + "' "
          + "or "
          + DatabaseContract.UserTable.SYNCHRONIZED
          + " = '"
          + LocalSynchronized.SYNC_UPDATED
          + "' "
          + "or "
          + DatabaseContract.UserTable.WATCHING_SYNCHRONIZED
          + " = '"
          + LocalSynchronized.SYNC_NEW
          + "' "
          + "or "
          + DatabaseContract.UserTable.WATCHING_SYNCHRONIZED
          + " = '"
          + LocalSynchronized.SYNC_UPDATED
          + "' ";

        return readUsers(whereClause, null);
    }

    public UserEntity getUserByUsername(String username) {
        String args = DatabaseContract.UserTable.USER_NAME + "= ?";
        String[] argsString = new String[] { username };

        return readUser(args, argsString);
    }

    @Nullable
    private UserEntity readUser(String whereClause, String[] whereArguments) {
        UserEntity resUser = null;
        Cursor c = getReadableDatabase().query(DatabaseContract.UserTable.TABLE,
          DatabaseContract.UserTable.PROJECTION,
          whereClause,
          whereArguments,
          null,
          null,
          null,
          null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            resUser = userMapper.fromCursor(c);
        }
        c.close();
        return resUser;
    }

    @NonNull
    private List<UserEntity> readUsers(String whereClause, String[] whereArguments) {
        Cursor queryResults = getReadableDatabase().query(DatabaseContract.UserTable.TABLE,
          DatabaseContract.UserTable.PROJECTION,
          whereClause,
          whereArguments,
          null,
          null,
          DatabaseContract.UserTable.USER_NAME + " COLLATE NOCASE");

        List<UserEntity> result = new ArrayList<>(queryResults.getCount());
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
}
