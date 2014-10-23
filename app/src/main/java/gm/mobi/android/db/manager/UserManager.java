package gm.mobi.android.db.manager;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import gm.mobi.android.db.GMContract;
import gm.mobi.android.db.GMContract.SyncColumns;
import gm.mobi.android.db.GMContract.UserTable;
import gm.mobi.android.db.mappers.UserMapper;
import gm.mobi.android.db.objects.UserEntity;
import java.sql.SQLException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class UserManager extends AbstractManager {

    UserMapper userMapper;
    private String CSYS_SYNCHRONIZED = SyncColumns.CSYS_SYNCHRONIZED;
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
    public UserEntity getCurrentUser() {
        UserEntity user = null;
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

        //TODO error handling if(res<0)
    }

    /**
     * Insert a user
     */
    public void saveUser(UserEntity user) throws SQLException {
        UserEntity userEntity = user;
        UserEntity currentUser = getCurrentUser();
        if(!user.getIdUser().equals(currentUser.getIdUser())) {
            ContentValues contentValues = userMapper.toContentValues(user);
            String userId = String.valueOf(contentValues.getAsLong(UserTable.ID));
            String[] args = { userId };
            if (contentValues.getAsLong(CSYS_DELETED) != null) {
                deleteUser(user);
            } else {
                db.insertWithOnConflict(UserTable.TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
            }
        }else{
            currentUser.setNumFollowers(user.getNumFollowers());
                currentUser.setNumFollowings(user.getNumFollowings());
                currentUser.setBio(user.getBio());
                currentUser.setFavoriteTeamName(user.getFavoriteTeamName());
                currentUser.setName(user.getName());
                currentUser.setFavoriteTeamId(user.getFavoriteTeamId());
                currentUser.setPhoto(user.getPhoto());
                currentUser.setUserName(user.getUserName());
                currentUser.setPoints(user.getPoints());
                db.update(UserTable.TABLE,userMapper.currentUserToContentValues(currentUser),"idUser=?",new String[]{String.valueOf(currentUser.getIdUser())});
        }
        insertInSync();
        //TODO error handling? if(res<0)
    }

    public void saveCurrentUser(UserEntity user) throws  SQLException{
        ContentValues contentValues = userMapper.currentUserToContentValues(user);
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
        String args = GMContract.UserTable.ID + "=?";
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
        return users;
    }
}
