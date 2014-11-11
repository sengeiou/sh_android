package com.shootr.android.db.manager;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.shootr.android.data.SessionManager;
import com.shootr.android.db.DatabaseContract;
import com.shootr.android.db.DatabaseContract.SyncColumns;
import com.shootr.android.db.DatabaseContract.UserTable;
import com.shootr.android.db.mappers.UserMapper;
import com.shootr.android.db.objects.UserEntity;
import java.sql.SQLException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class UserManager extends AbstractManager {

    UserMapper userMapper;
    private SessionManager sessionManager;
    private static final String CSYS_SYNCHRONIZED = SyncColumns.CSYS_SYNCHRONIZED;
    private static final String USER_TABLE = UserTable.TABLE;
    private static final String CSYS_DELETED = SyncColumns.CSYS_DELETED;

    @Inject
    public UserManager(UserMapper userMapper, SessionManager sessionManager) {
        this.userMapper = userMapper;
        this.sessionManager = sessionManager;
    }

    /**
     * Insert User list
     */
    public void saveUsersFromServer(List<UserEntity> users) throws SQLException {
        for (UserEntity user : users) {
            ContentValues contentValues = userMapper.toContentValues(user);
            contentValues.put(CSYS_SYNCHRONIZED, "S");
            if (contentValues.getAsLong(CSYS_DELETED) != null) {
                deleteUser(user);
            } else {
                db.insertWithOnConflict(USER_TABLE, null, contentValues,SQLiteDatabase.CONFLICT_REPLACE);
            }
            insertInSync();
        }

        //TODO error handling  if res<0
    }

    /**
     * Insert a user
     */
    public void saveUser(UserEntity user) {
        UserEntity currentUser = sessionManager.getCurrentUser();

        if(!user.getIdUser().equals(currentUser.getIdUser())) {
            ContentValues contentValues = userMapper.toContentValues(user);
            if (contentValues.getAsLong(CSYS_DELETED) != null) {
                deleteUser(user);
            } else {
                db.insertWithOnConflict(UserTable.TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
            }
        }else{
            updateCurrentUser(currentUser,user);
        }
        insertInSync();
    }

    public void updateCurrentUser(UserEntity currentUser, UserEntity user){
        currentUser.setNumFollowers(user.getNumFollowers());
        currentUser.setNumFollowings(user.getNumFollowings());
        currentUser.setBio(user.getBio());
        currentUser.setFavoriteTeamName(user.getFavoriteTeamName());
        currentUser.setName(user.getName());
        currentUser.setFavoriteTeamId(user.getFavoriteTeamId());
        currentUser.setPhoto(user.getPhoto());
        currentUser.setUserName(user.getUserName());
        currentUser.setPoints(user.getPoints());
        db.update(UserTable.TABLE,userMapper.toContentValues(currentUser),"idUser=?",new String[]{String.valueOf(currentUser.getIdUser())});
    }


    public void saveCurrentUser(UserEntity user) throws  SQLException{
        ContentValues contentValues = userMapper.toContentValues(user);
        if (contentValues.getAsLong(CSYS_DELETED) != null) {
            deleteUser(user);
        } else {
            db.insertWithOnConflict(UserTable.TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
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
        Cursor c = db.query(USER_TABLE, UserTable.PROJECTION, args, stringArgs, null, null, null);
        if (c.getCount() > 0) {
            res = db.delete(USER_TABLE, UserTable.ID+"=?", new String[] { String.valueOf(user.getIdUser()) });
        }
        c.close();
        return res;
    }

    /**
     * Retrieve User by idUser
     */
    public UserEntity getUserByIdUser(Long idUser) {
        UserEntity resUser = null;
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

    public List<UserEntity> getUsersByIds(List<Long> usersIds) {
        int userIdsSize = usersIds.size();
        List<UserEntity> result = new ArrayList<>(userIdsSize);
        if (userIdsSize == 0) {
            return result;
        }
        String[] selectionArguments = new String[userIdsSize];
        for (int i = 0; i < userIdsSize; i++) {
            selectionArguments[i] = String.valueOf(usersIds.get(i));
        }
        Cursor queryResults = db.query(UserTable.TABLE, UserTable.PROJECTION,
          UserTable.ID + " IN (" + createListPlaceholders(userIdsSize) + ")", selectionArguments, null, null,
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
          Cursor c = db.query(UserTable.TABLE, UserTable.PROJECTION, args,null,null,null,UserTable.NAME);

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
}
