package com.shootr.mobile.db.manager;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.shootr.mobile.data.entity.PrivateMessageQueueEntity;
import com.shootr.mobile.db.DatabaseContract;
import com.shootr.mobile.db.mappers.MessageQueueDBMapper;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class MessageQueueManager extends AbstractManager {

    private final MessageQueueDBMapper messageQueueDBMapper;

    @Inject public MessageQueueManager(SQLiteOpenHelper dbHelper, MessageQueueDBMapper messageQueueDBMapper) {
        super(dbHelper);
        this.messageQueueDBMapper = messageQueueDBMapper;
    }

    public PrivateMessageQueueEntity saveMessageQueue(PrivateMessageQueueEntity privateMessageQueueEntity) {
        ContentValues contentValues = messageQueueDBMapper.toContentValues(
            privateMessageQueueEntity);
        Long id = getWritableDatabase().insertWithOnConflict(DatabaseContract.MessageQueueTable.TABLE,
          null,
          contentValues,
          SQLiteDatabase.CONFLICT_REPLACE);
        privateMessageQueueEntity.setIdQueue(id);
        return privateMessageQueueEntity;
    }

    public void deleteMessageQueue(PrivateMessageQueueEntity privateMessageQueueEntity) {
        String[] whereArgs = {
          String.valueOf(privateMessageQueueEntity.getIdQueue())
        };
        String whereClause = DatabaseContract.MessageQueueTable.ID_QUEUE + "=?";
        getWritableDatabase().delete(DatabaseContract.MessageQueueTable.TABLE, whereClause, whereArgs);
    }

    public List<PrivateMessageQueueEntity> retrievePendingMessageQueue() {
        List<PrivateMessageQueueEntity> results = new ArrayList<>();
        String where = DatabaseContract.MessageQueueTable.FAILED + "=?";
        String[] whereArgs = new String[] {
          String.valueOf(0)
        };
        Cursor queryResult = getReadableDatabase().query(DatabaseContract.MessageQueueTable.TABLE,
          DatabaseContract.MessageQueueTable.PROJECTION,
          where,
          whereArgs,
          null,
          null,
          null);

        if (queryResult.getCount() > 0) {
            queryResult.moveToFirst();
            do {
                PrivateMessageQueueEntity messageEntity = messageQueueDBMapper.fromCursor(queryResult);
                results.add(messageEntity);
            } while (queryResult.moveToNext());
        }
        queryResult.close();
        return results;
    }

    public PrivateMessageQueueEntity retrieveNextPendingMessage() {
        String where = DatabaseContract.MessageQueueTable.FAILED + "=?";
        String[] whereArgs = new String[] {
          String.valueOf(0)
        };
        Cursor queryResult = getReadableDatabase().query(DatabaseContract.MessageQueueTable.TABLE,
          DatabaseContract.MessageQueueTable.PROJECTION,
          where,
          whereArgs,
          null,
          null,
          null,
          "1");

        PrivateMessageQueueEntity privateMessageQueueEntity = null;
        if (queryResult.getCount() > 0) {
            queryResult.moveToFirst();
            privateMessageQueueEntity = messageQueueDBMapper.fromCursor(queryResult);
        }
        queryResult.close();
        return privateMessageQueueEntity;
    }

    public List<PrivateMessageQueueEntity> retrieveFailedMessageQueues() {
        List<PrivateMessageQueueEntity> results = new ArrayList<>();
        String where = DatabaseContract.MessageQueueTable.FAILED + "=?";
        String[] whereArgs = new String[] {
          String.valueOf(1)
        };
        Cursor queryResult = getReadableDatabase().query(DatabaseContract.MessageQueueTable.TABLE,
          DatabaseContract.MessageQueueTable.PROJECTION,
          where,
          whereArgs,
          null,
          null,
          null);

        if (queryResult.getCount() > 0) {
            queryResult.moveToFirst();
            do {
                PrivateMessageQueueEntity messageEntity = messageQueueDBMapper.fromCursor(queryResult);
                results.add(messageEntity);
            } while (queryResult.moveToNext());
        }
        queryResult.close();
        return results;
    }

    public PrivateMessageQueueEntity retrieveById(Long queuedMessageId) {
        String where = DatabaseContract.ShotQueueTable.ID_QUEUE + "=?";
        String[] whereArgs = new String[] {
          String.valueOf(queuedMessageId)
        };
        Cursor queryResult = getReadableDatabase().query(DatabaseContract.MessageQueueTable.TABLE,
          DatabaseContract.MessageQueueTable.PROJECTION,
          where,
          whereArgs,
          null,
          null,
          null,
          "1");

        PrivateMessageQueueEntity privateMessageQueueEntity = null;
        if (queryResult.getCount() > 0) {
            queryResult.moveToFirst();
            privateMessageQueueEntity = messageQueueDBMapper.fromCursor(queryResult);
        }
        queryResult.close();
        return privateMessageQueueEntity;
    }
}
