package com.shootr.android.db.manager;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.shootr.android.data.entity.FollowEntity;
import com.shootr.android.db.DatabaseContract;
import com.shootr.android.db.DatabaseContract.FollowTable;
import com.shootr.android.db.mappers.FollowEntityDBMapper;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class FollowManager extends AbstractManager{


    FollowEntityDBMapper followMapper;
    private static final String FOLLOW_TABLE = FollowTable.TABLE;
    private static final String ID_FOLLOWED_USER = FollowTable.ID_FOLLOWED_USER;
    private static final String ID_USER = FollowTable.ID_USER;


    @Inject
    public FollowManager(SQLiteOpenHelper openHelper, FollowEntityDBMapper followMapper){
        super(openHelper);
        this.followMapper = followMapper;
    }

    /**
     * Insert a Follow from Server datas
     */
    public void saveFollowFromServer(FollowEntity follow) throws SQLException {
        long id = 0;
        if(follow!=null){
            ContentValues contentValues = followMapper.toContentValues(follow);

            if (contentValues.get(DatabaseContract.SyncColumns.DELETED) != null) {
                deleteFollow(follow);
            } else {
                contentValues.put(DatabaseContract.SyncColumns.SYNCHRONIZED, "S");
                id = getWritableDatabase().insertWithOnConflict(FOLLOW_TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
            }
        }
        if(id!=-1){
            insertInSync();
        }
    }


    /** Insert a Follow **/
    public void saveFollow(FollowEntity follow) {
        if(follow!=null){
            ContentValues contentValues = followMapper.toContentValues(follow);
            getWritableDatabase().insertWithOnConflict(FOLLOW_TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
         }
    }

    /**
     * Insert a Follow list from Server datas
     * *
     */
    public void saveFollowsFromServer(List<FollowEntity> followList) {
        for (FollowEntity follow : followList) {
            ContentValues contentValues = followMapper.toContentValues(follow);
            if (contentValues.getAsLong(DatabaseContract.SyncColumns.DELETED) != null) {
                 deleteFollow(follow);
            } else {
                contentValues.put(DatabaseContract.SyncColumns.SYNCHRONIZED,"S");
                getWritableDatabase().insertWithOnConflict(FOLLOW_TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
            }
        }
       insertInSync();
    }

    public FollowEntity getFollowByUserIds(String idUserWhoFollow, String idUserFollowed){
        String args = ID_USER +"=? AND "+ ID_FOLLOWED_USER+" =? AND ("+ DatabaseContract.SyncColumns.DELETED+" IS NULL OR "+DatabaseContract.SyncColumns.SYNCHRONIZED+" = 'D')";
        String[] argsString = new String[]{String.valueOf(idUserWhoFollow), String.valueOf(idUserFollowed)};
        FollowEntity follow = null;
        Cursor  c = getReadableDatabase().query(DatabaseContract.FollowTable.TABLE, FollowTable.PROJECTION,args,argsString,null,null,null,null);
        if(c.getCount()>0){
            c.moveToFirst();
            follow = followMapper.fromCursor(c);
        }
        c.close();
        return follow;
    }

    /**
     * Retrieve a Following User
     */
    public List<String> getUserFollowingIds(String idUser) {
        List<String> userIds = new ArrayList<>();

        String args = ID_USER+"=? AND "+DatabaseContract.SyncColumns.DELETED +" IS NULL";
        String[] argsString = new String[]{String.valueOf(idUser)};
        if(isTableEmpty(FOLLOW_TABLE)){
            Timber.e("La tabla follow está vacia");
        }
        Cursor c = getReadableDatabase().query(DatabaseContract.FollowTable.TABLE, FollowTable.PROJECTION,args,argsString,null,null,null,null);

        if (c.getCount() > 0) {
            c.moveToFirst();
            while (!c.isAfterLast()) {
                userIds.add(c.getString(c.getColumnIndex(ID_FOLLOWED_USER)));
                c.moveToNext();
            }
        }
        c.close();
        return userIds;
    }

    public List<String> getUserFollowingIdsWithOwnUser(String idUser) throws SQLException{
        List<String> userIds = getUserFollowingIds(idUser);
        userIds.add(idUser);
        return userIds;
    }

    public int getFollowRelationshipState(String idFromUser, String idToUser) {
        int resultRelationship = FollowEntity.RELATIONSHIP_NONE;
        String fromUserIdArgument = String.valueOf(idFromUser);
        String toUserIdArgument = String.valueOf(idToUser);

        String selection = "("
            + ID_USER
            + "=? and "
            + ID_FOLLOWED_USER
            + "=?) OR ("
            + ID_USER
            + "=? and "
            + ID_FOLLOWED_USER
            + "=?)";
        Cursor queryResults = getReadableDatabase().query(FOLLOW_TABLE, FollowTable.PROJECTION, selection,
            new String[] {
                fromUserIdArgument, toUserIdArgument, toUserIdArgument, fromUserIdArgument
            }, null, null, null, null);

        if (queryResults.getCount() > 0) {
            queryResults.moveToFirst();
            boolean iFollowHim = false;
            boolean heFollowsMe = false;
            do {
                FollowEntity follow = followMapper.fromCursor(queryResults);
                if (follow != null) {
                    if (follow.getIdUser().equals(idFromUser) && follow.getFollowedUser()
                        .equals(idToUser)) {
                        iFollowHim = true;
                    } else if (follow.getIdUser().equals(idToUser)
                        && follow.getFollowedUser().equals(idFromUser)) {
                        heFollowsMe = true;
                    }

                    if (iFollowHim && heFollowsMe) {
                        resultRelationship = FollowEntity.RELATIONSHIP_BOTH;
                    }else if (iFollowHim) {
                        resultRelationship = FollowEntity.RELATIONSHIP_FOLLOWING;
                    }else if (heFollowsMe) {
                        resultRelationship = FollowEntity.RELATIONSHIP_FOLLOWER;
                    }
                }
            } while (queryResults.moveToNext());
        }
        queryResults.close();
        return resultRelationship;
    }

    public int doIFollowHimState(Long idCurrentUser, Long idUser){
        int resultRelationship = FollowEntity.RELATIONSHIP_NONE;
        if(idCurrentUser.equals(idUser)){
            return FollowEntity.RELATIONSHIP_OWN;
        }else {
            String fromUserIdArgument = String.valueOf(idCurrentUser);
            String toUserIdArgument = String.valueOf(idUser);
            String selection = "(" + ID_USER + "=? and " + ID_FOLLOWED_USER + "=?)";
            Cursor queryResults = getReadableDatabase().query(FOLLOW_TABLE, FollowTable.PROJECTION, selection, new String[] { fromUserIdArgument, toUserIdArgument }, null, null, null, null);
            if (queryResults.getCount() > 0) {
                queryResults.moveToFirst();
                do {
                    FollowEntity follow = followMapper.fromCursor(queryResults);
                    if (follow != null && follow.getDeleted() == null) {
                        resultRelationship = FollowEntity.RELATIONSHIP_FOLLOWING;
                    }else if(follow!=null && follow.getDeleted() !=null){
                        resultRelationship = FollowEntity.RELATIONSHIP_NONE;
                    }
                } while (queryResults.moveToNext());
            }
            queryResults.close();
        }
        return resultRelationship;
    }

    /**
     * Delete one Follow
     */
    public long deleteFollow(FollowEntity follow) {
        return deleteFollow(follow.getFollowedUser(), follow.getIdUser());
    }

    public long deleteFollow(String followedUser, String idUser) {
        String whereClause = ID_FOLLOWED_USER + "=? AND " + ID_USER + "=?";
        String[] whereArgs = new String[]{followedUser, idUser};
        return getWritableDatabase().delete(FOLLOW_TABLE, whereClause, whereArgs);
    }

    public void insertInSync(){
        insertInTableSync(FOLLOW_TABLE, 2,0,0);
    }


    public List<FollowEntity> getFollowsNotSynchronized(){
        List<FollowEntity> followsToUpdate = new ArrayList<>();
        String args = DatabaseContract.SyncColumns.SYNCHRONIZED+"='N' OR "+DatabaseContract.SyncColumns.SYNCHRONIZED+"= 'D' OR "+DatabaseContract.SyncColumns.SYNCHRONIZED+"='U'";
        Cursor c = getReadableDatabase().query(FOLLOW_TABLE, FollowTable.PROJECTION,args,null,null,null,null);
        if(c.getCount()>0){
            c.moveToFirst();
            do{
                followsToUpdate.add(followMapper.fromCursor(c));
            }while(c.moveToNext());
        }
        c.close();
        return followsToUpdate;
    }
}
