package com.shootr.mobile.db.manager;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.shootr.mobile.data.entity.ShotQueueEntity;
import com.shootr.mobile.db.DatabaseContract;
import com.shootr.mobile.db.mappers.ShotQueueDBMapper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class ShotQueueManager extends AbstractManager {

    private final ShotQueueDBMapper shotQueueDBMapper;

    @Inject public ShotQueueManager(SQLiteOpenHelper dbHelper, ShotQueueDBMapper shotQueueDBMapper) {
        super(dbHelper);
        this.shotQueueDBMapper = shotQueueDBMapper;
    }

    public ShotQueueEntity saveShotQueue(ShotQueueEntity shotQueueEntity) {
        ContentValues contentValues = shotQueueDBMapper.toContentValues(shotQueueEntity);
        Long id = getWritableDatabase().insertWithOnConflict(DatabaseContract.ShotQueueTable.TABLE,
                null,
                contentValues,
                SQLiteDatabase.CONFLICT_REPLACE);
        shotQueueEntity.setIdQueue(id);
        return shotQueueEntity;
    }

    public void deleteShotQueue(ShotQueueEntity shotQueueEntity) {
        String[] whereArgs = {
          String.valueOf(shotQueueEntity.getIdQueue())
        };
        String whereClause = DatabaseContract.ShotQueueTable.ID_QUEUE + "=?";
        getWritableDatabase().delete(DatabaseContract.ShotQueueTable.TABLE, whereClause, whereArgs);
    }

    public List<ShotQueueEntity> retrievePendingShotQueue() {
        List<ShotQueueEntity> results = new ArrayList<>();
        String where = DatabaseContract.ShotQueueTable.FAILED + "=?";
        String[] whereArgs = new String[] {
          String.valueOf(0)
        };
        Cursor queryResult = getReadableDatabase().query(DatabaseContract.ShotQueueTable.TABLE,
          DatabaseContract.ShotQueueTable.PROJECTION,
          where,
          whereArgs,
          null,
          null,
          null);

        if (queryResult.getCount() > 0) {
            queryResult.moveToFirst();
            do {
                ShotQueueEntity shotEntity = shotQueueDBMapper.fromCursor(queryResult);
                results.add(shotEntity);
            } while (queryResult.moveToNext());
        }
        queryResult.close();
        return results;
    }

    public ShotQueueEntity retrieveNextPendingShot() {
        String where = DatabaseContract.ShotQueueTable.FAILED + "=?";
        String[] whereArgs = new String[] {
          String.valueOf(0)
        };
        Cursor queryResult = getReadableDatabase().query(DatabaseContract.ShotQueueTable.TABLE,
          DatabaseContract.ShotQueueTable.PROJECTION,
          where,
          whereArgs,
          null,
          null,
          null,
          "1");

        ShotQueueEntity shotQueueEntity = null;
        if (queryResult.getCount() > 0) {
            queryResult.moveToFirst();
            shotQueueEntity = shotQueueDBMapper.fromCursor(queryResult);
        }
        queryResult.close();
        return shotQueueEntity;
    }

    public List<ShotQueueEntity> retrieveFailedShotQueues() {
        List<ShotQueueEntity> results = new ArrayList<>();
        String where = DatabaseContract.ShotQueueTable.FAILED + "=?";
        String[] whereArgs = new String[] {
          String.valueOf(1)
        };
        Cursor queryResult = getReadableDatabase().query(DatabaseContract.ShotQueueTable.TABLE,
          DatabaseContract.ShotQueueTable.PROJECTION,
          where,
          whereArgs,
          null,
          null,
          null);

        if (queryResult.getCount() > 0) {
            queryResult.moveToFirst();
            do {
                ShotQueueEntity shotEntity = shotQueueDBMapper.fromCursor(queryResult);
                results.add(shotEntity);
            } while (queryResult.moveToNext());
        }
        queryResult.close();
        return results;
    }

    public ShotQueueEntity retrieveById(Long queuedShotId) {
        String where = DatabaseContract.ShotQueueTable.ID_QUEUE + "=?";
        String[] whereArgs = new String[] {
          String.valueOf(queuedShotId)
        };
        Cursor queryResult = getReadableDatabase().query(DatabaseContract.ShotQueueTable.TABLE,
          DatabaseContract.ShotQueueTable.PROJECTION,
          where,
          whereArgs,
          null,
          null,
          null,
          "1");

        ShotQueueEntity shotQueueEntity = null;
        if (queryResult.getCount() > 0) {
            queryResult.moveToFirst();
            shotQueueEntity = shotQueueDBMapper.fromCursor(queryResult);
        }
        queryResult.close();
        return shotQueueEntity;
    }
}
