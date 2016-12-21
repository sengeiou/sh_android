package com.shootr.mobile.db.manager;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.shootr.mobile.data.entity.PrivateMessageEntity;
import com.shootr.mobile.db.DatabaseContract;
import com.shootr.mobile.db.DatabaseContract.PrivateMessageTable;
import com.shootr.mobile.db.mappers.PrivateMessageEntityDBMapper;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class PrivateMessageManager extends AbstractManager {

  @Inject PrivateMessageEntityDBMapper privateMessageEntityMapper;

  private static final String PRIVATE_MESSAGE_TABLE = PrivateMessageTable.TABLE;

  @Inject public PrivateMessageManager(SQLiteOpenHelper openHelper,
      PrivateMessageEntityDBMapper privateMessageEntityMapper) {
    super(openHelper);
    this.privateMessageEntityMapper = privateMessageEntityMapper;
  }

  public void savePrivateMessage(PrivateMessageEntity privateMessageEntity) {
    insertPrivateMessage(privateMessageEntity, getWritableDatabase());
  }

  public void savePrivateMessages(List<PrivateMessageEntity> privateMessageList) {
    SQLiteDatabase database = getWritableDatabase();
    try {
      database.beginTransaction();
      for (PrivateMessageEntity privateMessage : privateMessageList) {
        insertPrivateMessage(privateMessage, database);
      }
      database.setTransactionSuccessful();
    } finally {
      database.endTransaction();
    }
  }

  public List<PrivateMessageEntity> getPrivateMessageByIdChannel(
      String idChannel) {
    String privateMessageChannelSelection = PrivateMessageTable.ID_PRIVATE_MESSAGE_CHANNEL + " = ?";

    String[] whereArguments = new String[1];
    whereArguments[0] = String.valueOf(idChannel);
    String whereClause = privateMessageChannelSelection;

    Cursor queryResult =
        getReadableDatabase().query(PrivateMessageTable.TABLE, PrivateMessageTable.PROJECTION,
            whereClause, whereArguments, null, null, PrivateMessageTable.BIRTH + " DESC", "150");

    List<PrivateMessageEntity> resultShots = new ArrayList<>(queryResult.getCount());
    PrivateMessageEntity privateMessageEntity;
    if (queryResult.getCount() > 0) {
      queryResult.moveToFirst();
      do {
        privateMessageEntity = privateMessageEntityMapper.fromCursor(queryResult);
        resultShots.add(privateMessageEntity);
      } while (queryResult.moveToNext());
    }
    queryResult.close();
    return resultShots;
  }

  public long deletePrivateMessage(String idPrivateMessage) {
    String where = PrivateMessageTable.ID_PRIVATE_MESSAGE + "=?";
    String[] whereArgs = new String[] { idPrivateMessage };
    return (long) getWritableDatabase().delete(PRIVATE_MESSAGE_TABLE, where, whereArgs);
  }

  private void insertPrivateMessage(PrivateMessageEntity privateMessage,
      SQLiteDatabase writableDatabase) {
    ContentValues contentValues = privateMessageEntityMapper.toContentValues(privateMessage);
    if (contentValues.getAsLong(DatabaseContract.SyncColumns.DELETED) != null) {
      deletePrivateMessage(privateMessage.getIdPrivateMessage());
    } else {
      writableDatabase.insertWithOnConflict(PRIVATE_MESSAGE_TABLE, null, contentValues,
          SQLiteDatabase.CONFLICT_REPLACE);
    }
  }

  public void deleteShotsByIdPrivateMessageChannel(String idPrivateMessageChannel) {
    SQLiteDatabase database = getWritableDatabase();
    try {
      database.beginTransaction();

      String where = PrivateMessageTable.ID_PRIVATE_MESSAGE_CHANNEL + "=?";
      String[] whereArgs = new String[] { idPrivateMessageChannel };
      getWritableDatabase().delete(PRIVATE_MESSAGE_TABLE, where, whereArgs);

      database.setTransactionSuccessful();
    } finally {
      database.endTransaction();
    }
  }
}
