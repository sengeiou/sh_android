package com.shootr.mobile.db.manager;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.shootr.mobile.data.entity.LocalSynchronized;
import com.shootr.mobile.data.entity.PrivateMessageChannelEntity;
import com.shootr.mobile.db.DatabaseContract;
import com.shootr.mobile.db.mappers.PrivateMessageChannelEntityDBMapper;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class PrivateMessageChannelManager extends AbstractManager {

  private final PrivateMessageChannelEntityDBMapper privateMessageChannelEntityDBMapper;

  @Inject public PrivateMessageChannelManager(SQLiteOpenHelper openHelper,
      PrivateMessageChannelEntityDBMapper privateMessageChannelEntityDBMapper) {
    super(openHelper);
    this.privateMessageChannelEntityDBMapper = privateMessageChannelEntityDBMapper;
  }

  public PrivateMessageChannelEntity getPrivateMessageChannelById(String privateMessageChannelId) {
    String whereSelection =
        DatabaseContract.PrivateMessageChannelTable.ID_PRIVATE_MESSAGE_CHANNEL + " = ?";
    String[] whereArguments = new String[] { String.valueOf(privateMessageChannelId) };

    Cursor queryResult =
        getReadableDatabase().query(DatabaseContract.PrivateMessageChannelTable.TABLE,
            DatabaseContract.PrivateMessageChannelTable.PROJECTION, whereSelection, whereArguments,
            null, null, null);

    PrivateMessageChannelEntity privateMessageChannelEntity = null;
    if (queryResult.getCount() > 0) {
      queryResult.moveToFirst();
      privateMessageChannelEntity = privateMessageChannelEntityDBMapper.fromCursor(queryResult);
    }
    queryResult.close();
    return privateMessageChannelEntity;
  }

  public PrivateMessageChannelEntity getPrivateMessageChannelByIdUser(String idUser) {
    String whereSelection = DatabaseContract.PrivateMessageChannelTable.ID_TARGET_USER + " = ?";
    String[] whereArguments = new String[] { String.valueOf(idUser) };

    Cursor queryResult =
        getReadableDatabase().query(DatabaseContract.PrivateMessageChannelTable.TABLE,
            DatabaseContract.PrivateMessageChannelTable.PROJECTION, whereSelection, whereArguments,
            null, null, null);

    PrivateMessageChannelEntity privateMessageChannelEntity = null;
    if (queryResult.getCount() > 0) {
      queryResult.moveToFirst();
      privateMessageChannelEntity = privateMessageChannelEntityDBMapper.fromCursor(queryResult);
    }
    queryResult.close();
    return privateMessageChannelEntity;
  }

  public void savePrivateMessageChannels(
      List<PrivateMessageChannelEntity> privateMessageChannelEntities) {
    SQLiteDatabase database = getWritableDatabase();
    try {
      database.beginTransaction();
      for (PrivateMessageChannelEntity privateMessageChannelEntity : privateMessageChannelEntities) {
        if (privateMessageChannelEntity.getDeleted() != null) {
          deletePrivateMessageChannel(privateMessageChannelEntity);
        } else {
          ContentValues contentValues =
              privateMessageChannelEntityDBMapper.toContentValues(privateMessageChannelEntity);
          database.insertWithOnConflict(DatabaseContract.PrivateMessageChannelTable.TABLE, null,
              contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        }
      }
      database.setTransactionSuccessful();
    } finally {
      database.endTransaction();
    }
  }

  public void savePrivateMessageChannel(PrivateMessageChannelEntity privateMessageChannelEntity) {
    if (privateMessageChannelEntity.getDeleted() != null) {
      deletePrivateMessageChannel(privateMessageChannelEntity);
    } else {
      ContentValues contentValues =
          privateMessageChannelEntityDBMapper.toContentValues(privateMessageChannelEntity);
      getWritableDatabase().insertWithOnConflict(DatabaseContract.PrivateMessageChannelTable.TABLE,
          null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
    }
  }

  public long deletePrivateMessageChannel(PrivateMessageChannelEntity privateMessageChannelEntity) {
    String idPrivateMessageChannel = privateMessageChannelEntity.getIdPrivateMessageChannel();
    return deletePrivateMessageChannel(idPrivateMessageChannel);
  }

  public long deletePrivateMessageChannel(String idPrivateMessageChannel) {
    long res = 0;
    String args = DatabaseContract.PrivateMessageChannelTable.ID_PRIVATE_MESSAGE_CHANNEL + "=?";
    String[] stringArgs = new String[] { String.valueOf(idPrivateMessageChannel) };
    Cursor c = getReadableDatabase().query(DatabaseContract.PrivateMessageChannelTable.TABLE,
        DatabaseContract.PrivateMessageChannelTable.PROJECTION, args, stringArgs, null, null, null);
    if (c.getCount() > 0) {
      res = getWritableDatabase().delete(DatabaseContract.PrivateMessageChannelTable.TABLE, args,
          stringArgs);
    }
    c.close();
    return res;
  }

  public List<PrivateMessageChannelEntity> getPrivateMessageChannels() {
    String whereSelection = DatabaseContract.PrivateMessageChannelTable.LAST_MESSAGE_TIME
        + " IS NOT NULL AND "
        + DatabaseContract.PrivateMessageChannelTable.DELETED
        + " IS NULL AND ("
        + DatabaseContract.PrivateMessageChannelTable.SYNCHRONIZED
        + " IS NULL OR "
        + DatabaseContract.PrivateMessageChannelTable.SYNCHRONIZED
        + " <> ? )";
    String[] whereArgs = new String[] { LocalSynchronized.SYNC_DELETED };
    Cursor queryResult =
        getReadableDatabase().query(DatabaseContract.PrivateMessageChannelTable.TABLE,
            DatabaseContract.PrivateMessageChannelTable.PROJECTION, whereSelection, null, null,
            null, DatabaseContract.PrivateMessageChannelTable.LAST_MESSAGE_TIME + " DESC", null);

    List<PrivateMessageChannelEntity> resultShots = new ArrayList<>(queryResult.getCount());
    PrivateMessageChannelEntity privateMessageChannelEntity;
    if (queryResult.getCount() > 0) {
      queryResult.moveToFirst();
      do {
        privateMessageChannelEntity = privateMessageChannelEntityDBMapper.fromCursor(queryResult);
        resultShots.add(privateMessageChannelEntity);
      } while (queryResult.moveToNext());
    }
    queryResult.close();
    return resultShots;
  }

  public void removePrivateMessageChannel(String idPrivateMessageChannel) {
    String where = DatabaseContract.PrivateMessageChannelTable.ID_PRIVATE_MESSAGE_CHANNEL + "=?";
    String[] whereArgs = new String[] { idPrivateMessageChannel };
    getWritableDatabase().delete(DatabaseContract.PrivateMessageChannelTable.TABLE, where,
        whereArgs);
  }

  public List<PrivateMessageChannelEntity> getPrivateMessageChannelsNotSynchronized() {
    List<PrivateMessageChannelEntity> privateMessageChannelsToDelete = new ArrayList<>();
    String args = DatabaseContract.SyncColumns.DELETED + " IS NOT NULL";
    Cursor c = getReadableDatabase().query(DatabaseContract.PrivateMessageChannelTable.TABLE,
        DatabaseContract.PrivateMessageChannelTable.PROJECTION, args, null, null, null, null);
    if (c.getCount() > 0) {
      c.moveToFirst();
      do {
        privateMessageChannelsToDelete.add(privateMessageChannelEntityDBMapper.fromCursor(c));
      } while (c.moveToNext());
    }
    c.close();
    return privateMessageChannelsToDelete;
  }

  public void markPrivateMessageChannelDeleted(String channelId) {
    String where = DatabaseContract.PrivateMessageChannelTable.ID_PRIVATE_MESSAGE_CHANNEL + "=?";
    String[] whereArgs = new String[] { String.valueOf(channelId) };
    ContentValues contentValues = new ContentValues();
    contentValues.put(DatabaseContract.PrivateMessageChannelTable.DELETED,
        LocalSynchronized.SYNC_DELETED);
    getWritableDatabase().update(DatabaseContract.PrivateMessageChannelTable.TABLE, contentValues,
        where, whereArgs);
  }
}
