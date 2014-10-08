package gm.mobi.android.db.manager;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import gm.mobi.android.db.GMContract;
import gm.mobi.android.db.GMContract.FollowTable;
import gm.mobi.android.db.mappers.FollowMapper;
import gm.mobi.android.db.objects.Follow;
import gm.mobi.android.db.objects.User;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class FollowManager extends AbstractManager{


    FollowMapper followMapper;
    private static final String CSYS_DELETED = GMContract.SyncColumns.CSYS_DELETED;
    private static final String FOLLOW_TABLE = FollowTable.TABLE;
    private static final String ID_FOLLOWED_USER = FollowTable.ID_FOLLOWED_USER;
    private static final String ID_USER = FollowTable.ID_USER;


    @Inject
    public FollowManager(FollowMapper followMapper){
        this.followMapper = followMapper;
    }

    /**
     * Insert a Follow
     */
    public void saveFollow(Follow follow) throws SQLException {
        if(follow!=null){
            ContentValues contentValues = followMapper.toContentValues(follow);

            if (contentValues.get(CSYS_DELETED) != null) {
                deleteFollow(follow);
            } else {
                db.insertWithOnConflict(FOLLOW_TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
            }
            insertInSync();
        }
    }

    /**
     * Insert a Follow list
     * *
     */
    public void saveFollows(List<Follow> followList) {
        for (Follow follow : followList) {
            ContentValues contentValues = followMapper.toContentValues(follow);

            if (contentValues.getAsLong(CSYS_DELETED) != null) {
                 deleteFollow(follow);
            } else {
                db.insertWithOnConflict(FOLLOW_TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
                Timber.i("Follow inserted ",follow.getIdUser());
            }
        }
       insertInSync();
    }

    /**
     * Retrieve a Following User
     */
    public  List<Long> getUserFollowingIds(Long idUser) throws SQLException {
        List<Long> userIds = new ArrayList<>();
        db.beginTransaction();
        String args = ID_USER+"=?";
        String[] argsString = new String[]{String.valueOf(idUser)};
        if(isTableEmpty(FOLLOW_TABLE)){
            Timber.e("La tabla follow está vacia");
        }
        Cursor c = db.query(GMContract.FollowTable.TABLE, new String[]{ID_FOLLOWED_USER},args,argsString,null,null,null,null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            while (!c.isAfterLast()) {
                userIds.add(c.getLong(0));
                c.moveToNext();
            }
        }
        db.endTransaction();
        c.close();
        userIds.add(idUser);
        return userIds;
    }

    public int getFollowRelationship( User fromUser, User toUser) {
        int resultRelationship = Follow.RELATIONSHIP_NONE;
        String fromUserIdArgument = String.valueOf(fromUser.getIdUser());
        String toUserIdArgument = String.valueOf(toUser.getIdUser());

        String selection = "("
            + ID_USER
            + "=? and "
            + ID_FOLLOWED_USER
            + "=?) OR ("
            + ID_USER
            + "=? and "
            + ID_FOLLOWED_USER
            + "=?)";
        Cursor queryResults = db.query(FOLLOW_TABLE, FollowTable.PROJECTION, selection,
            new String[] {
                fromUserIdArgument, toUserIdArgument, toUserIdArgument, fromUserIdArgument
            }, null, null, null, null);

        if (queryResults.getCount() > 0) {
            queryResults.moveToFirst();
            boolean iFollowHim = false;
            boolean heFollowsMe = false;
            do {
                Follow follow = followMapper.fromCursor(queryResults);
                if (follow != null) {
                    if (follow.getIdUser().equals(fromUser.getIdUser()) && follow.getFollowedUser()
                        .equals(toUser.getIdUser())) {
                        iFollowHim = true;
                    } else if (follow.getIdUser().equals(toUser.getIdUser())
                        && follow.getFollowedUser().equals(fromUser.getIdUser())) {
                        heFollowsMe = true;
                    }

                    if (iFollowHim && heFollowsMe) {
                        resultRelationship = Follow.RELATIONSHIP_BOTH;
                    }else if (iFollowHim) {
                        resultRelationship = Follow.RELATIONSHIP_FOLLOWING;
                    }else if (heFollowsMe) {
                        resultRelationship = Follow.RELATIONSHIP_FOLLOWER;
                    }
                }
            } while (queryResults.moveToNext());
        }
        return resultRelationship;
    }

    /**
     * Delete one Follow
     */
    public long deleteFollow(Follow follow) {
        long res = 0;
        String args = ID_FOLLOWED_USER + "=? AND " + ID_USER + "=?";
        String[] stringArgs = new String[]{String.valueOf(follow.getFollowedUser()), String.valueOf(follow.getIdUser())};

        Cursor c = db.query(FOLLOW_TABLE, GMContract.FollowTable.PROJECTION, args, stringArgs, null, null, null);
        if (c.getCount() > 0) {
            res = db.delete(FOLLOW_TABLE, ID_FOLLOWED_USER + "=? AND " + ID_USER + "=?",
                    new String[]{String.valueOf(follow.getFollowedUser()), String.valueOf(follow.getIdUser())});
        }
        c.close();
        return res;
    }

    public void insertInSync(){
        insertInTableSync(FOLLOW_TABLE, 2,0,0);
    }

}
