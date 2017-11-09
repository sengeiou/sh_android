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
    private static final String ID_FOLLOWED_USER = DatabaseContract.FollowTable.ID_FOLLOWED_ENTITY;
    private static final String ID_BLOCKED_USER = DatabaseContract.BlockTable.ID_BLOCKED_USER;
    private static final String TYPE = DatabaseContract.FollowTable.TYPE;
    private static final String ID_USER = "IDUSER";

    @Inject public FollowManager(SQLiteOpenHelper openHelper, FollowEntityDBMapper followMapper,
      BlockEntityDBMapper blockMapper) {
        super(openHelper);
        this.followMapper = followMapper;
        this.blockMapper = blockMapper;
    }

    /** Insert a Follow **/
    public void saveFailedFollow(FollowEntity follow) {
        if (follow != null) {
            ContentValues contentValues = followMapper.toContentValues(follow);
            getWritableDatabase().insertWithOnConflict(FOLLOW_TABLE,
              null,
              contentValues,
              SQLiteDatabase.CONFLICT_REPLACE);
        }
    }

    public List<FollowEntity> getFailedFollows() {
        List<FollowEntity> followsToUpdate = new ArrayList<>();
        String whereClause = TYPE + "= ?";
        String[] whereArgs = new String[] { "USER" };
        Cursor c = getReadableDatabase().query(FOLLOW_TABLE,
          DatabaseContract.FollowTable.PROJECTION,
          whereClause,
          whereArgs,
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

    public void removeAllBlocks() {
        getWritableDatabase().delete(BLOCK_TABLE, null, null);
    }

    public void deleteFailedFollows() {
        String whereClause = TYPE + "= ?";
        String[] whereArgs = new String[] { "USER" };
        getWritableDatabase().delete(FOLLOW_TABLE, whereClause, whereArgs);
    }
}
