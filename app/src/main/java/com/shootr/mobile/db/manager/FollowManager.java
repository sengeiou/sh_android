package com.shootr.mobile.db.manager;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.shootr.mobile.data.entity.BlockEntity;
import com.shootr.mobile.data.entity.FollowEntity;
import com.shootr.mobile.db.DatabaseContract;
import com.shootr.mobile.db.mappers.BlockEntityDBMapper;
import com.shootr.mobile.db.mappers.FollowEntityDBMapper;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class FollowManager extends AbstractManager {

    FollowEntityDBMapper followMapper;
    BlockEntityDBMapper blockMapper;
    private static final String FOLLOW_TABLE = DatabaseContract.FollowTable.TABLE;
    private static final String BLOCK_TABLE = DatabaseContract.BlockTable.TABLE;
    private static final String ID_FOLLOWED_USER = DatabaseContract.FollowTable.ID_FOLLOWED_USER;
    private static final String ID_BLOCKED_USER = DatabaseContract.BlockTable.ID_BLOCKED_USER;
    private static final String ID_USER = DatabaseContract.FollowTable.ID_USER;

    @Inject public FollowManager(SQLiteOpenHelper openHelper, FollowEntityDBMapper followMapper,
      BlockEntityDBMapper blockMapper) {
        super(openHelper);
        this.followMapper = followMapper;
        this.blockMapper = blockMapper;
    }

    /** Insert a Follow **/
    public void saveFollow(FollowEntity follow) {
        if (follow != null) {
            ContentValues contentValues = followMapper.toContentValues(follow);
            getWritableDatabase().insertWithOnConflict(FOLLOW_TABLE,
              null,
              contentValues,
              SQLiteDatabase.CONFLICT_REPLACE);
        }
    }

    /**
     * Insert a Follow list from Server datas
     * *
     */
    public void saveFollowsFromServer(List<FollowEntity> followList) {
        SQLiteDatabase database = getWritableDatabase();
        try {
            database.beginTransaction();
            for (FollowEntity follow : followList) {
                ContentValues contentValues = followMapper.toContentValues(follow);
                if (contentValues.getAsLong(DatabaseContract.SyncColumns.DELETED) != null) {
                    deleteFollow(follow.getIdFollowedUser(), follow.getIdUser(), database);
                } else {
                    contentValues.put(DatabaseContract.SyncColumns.SYNCHRONIZED, "S");
                    database.insertWithOnConflict(FOLLOW_TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
                }
            }
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

    public FollowEntity getFollowByUserIds(String idUserWhoFollow, String idUserFollowed) {
        String args = ID_USER
          + "=? AND "
          + ID_FOLLOWED_USER
          + " =? AND ("
          + DatabaseContract.SyncColumns.DELETED
          + " IS NULL OR "
          + DatabaseContract.SyncColumns.SYNCHRONIZED
          + " = 'D')";
        String[] argsString = new String[] { String.valueOf(idUserWhoFollow), String.valueOf(idUserFollowed) };
        FollowEntity follow = null;
        Cursor c = getReadableDatabase().query(DatabaseContract.FollowTable.TABLE,
          DatabaseContract.FollowTable.PROJECTION,
          args,
          argsString,
          null,
          null,
          null,
          null);
        if (c.getCount() > 0) {
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

        String args = ID_USER + "=? AND " + DatabaseContract.SyncColumns.DELETED + " IS NULL";
        String[] argsString = new String[] { String.valueOf(idUser) };
        Cursor c = getReadableDatabase().query(DatabaseContract.FollowTable.TABLE,
          DatabaseContract.FollowTable.PROJECTION,
          args,
          argsString,
          null,
          null,
          null,
          null);

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

    /**
     * Delete one Follow
     */

    public long deleteFollow(String followedUser, String idUser) {
        String whereClause = ID_FOLLOWED_USER + "=? AND " + ID_USER + "=?";
        String[] whereArgs = new String[] { followedUser, idUser };
        return getWritableDatabase().delete(FOLLOW_TABLE, whereClause, whereArgs);
    }

    private long deleteFollow(String followedUser, String idUser, SQLiteDatabase database) {
        String whereClause = ID_FOLLOWED_USER + "=? AND " + ID_USER + "=?";
        String[] whereArgs = new String[] { followedUser, idUser };
        return database.delete(FOLLOW_TABLE, whereClause, whereArgs);
    }

    public List<FollowEntity> getFollowsNotSynchronized() {
        List<FollowEntity> followsToUpdate = new ArrayList<>();
        String args = DatabaseContract.SyncColumns.SYNCHRONIZED
          + "='N' OR "
          + DatabaseContract.SyncColumns.SYNCHRONIZED
          + "= 'D' OR "
          + DatabaseContract.SyncColumns.SYNCHRONIZED
          + "='U'";
        Cursor c = getReadableDatabase().query(FOLLOW_TABLE,
          DatabaseContract.FollowTable.PROJECTION,
          args,
          null,
          null,
          null,
          null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            do {
                followsToUpdate.add(followMapper.fromCursor(c));
            } while (c.moveToNext());
        }
        c.close();
        return followsToUpdate;
    }

    public void saveBlock(BlockEntity block) {
        if (block != null) {
            ContentValues contentValues = blockMapper.toContentValues(block);
            getWritableDatabase().insertWithOnConflict(BLOCK_TABLE,
              null,
              contentValues,
              SQLiteDatabase.CONFLICT_REPLACE);
        }
    }

    public long deleteBlock(String currentUserId, String idBlockedUser) {
        String whereClause = ID_BLOCKED_USER + "=? AND " + ID_USER + "=?";
        String[] whereArgs = new String[] { idBlockedUser, currentUserId };
        return getWritableDatabase().delete(BLOCK_TABLE, whereClause, whereArgs);
    }

    public void saveBlockedsFromServer(List<BlockEntity> blockeds) {
        SQLiteDatabase database = getWritableDatabase();
        try {
            database.beginTransaction();
            for (BlockEntity block : blockeds) {
                ContentValues contentValues = blockMapper.toContentValues(block);
                database.insertWithOnConflict(BLOCK_TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
            }
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

    public List<BlockEntity> getBlockeds() {
        List<BlockEntity> blockeds = new ArrayList<>();
        Cursor c = getReadableDatabase().query(BLOCK_TABLE,
          DatabaseContract.BlockTable.PROJECTION,
          null,
          null,
          null,
          null,
          null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            do {
                blockeds.add(blockMapper.fromCursor(c));
            } while (c.moveToNext());
        }
        c.close();
        return blockeds;
    }

    public List<String> getMutuals() {
        List<String> mutuals = new ArrayList<>();
        String args = DatabaseContract.FollowTable.IS_FRIEND + " <> 0";
        Cursor c = getReadableDatabase().query(DatabaseContract.FollowTable.TABLE,
          DatabaseContract.FollowTable.PROJECTION,
          args,
          null,
          null,
          null,
          null,
          null);

        if (c.getCount() > 0) {
            c.moveToFirst();
            while (!c.isAfterLast()) {
                mutuals.add(c.getString(c.getColumnIndex(ID_FOLLOWED_USER)));
                c.moveToNext();
            }
        }
        c.close();
        return mutuals;
    }

    public void removeAllBlocks() {
        getWritableDatabase().delete(BLOCK_TABLE, null, null);
    }
}
