package com.shootr.android.db.manager;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.shootr.android.data.entity.Synchronized;
import com.shootr.android.db.DatabaseContract;
import com.shootr.android.db.DatabaseContract.WatchTable;
import com.shootr.android.db.mappers.WatchMapper;
import com.shootr.android.data.entity.WatchEntity;
import com.shootr.android.domain.utils.TimeUtils;
import com.squareup.phrase.Phrase;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class WatchManager extends AbstractManager{


    private final WatchMapper watchMapper;
    private final TimeUtils timeUtils;

    @Inject
    public WatchManager(SQLiteOpenHelper openHelper, WatchMapper watchMapper, TimeUtils timeUtils) {
        super(openHelper);
        this.watchMapper = watchMapper;
        this.timeUtils = timeUtils;
    }

    public List<WatchEntity> getWatchesNotEndedFromUsers(List<Long> userIds) {
        String query = "SELECT w.* FROM "+WatchTable.TABLE+" w INNER JOIN "+ DatabaseContract.EventTable.TABLE+" m ON "
          + "w."+WatchTable.ID_EVENT +" = m."+ DatabaseContract.EventTable.ID_EVENT
          + " WHERE m."+ DatabaseContract.EventTable.END_DATE + ">" + timeUtils.getCurrentTime()
          + " AND w."+WatchTable.ID_USER+" IN ("+createListPlaceholders(userIds.size())+");";

        String[] whereArguments = new String[userIds.size()];
        for (int i = 0; i < userIds.size(); i++) {
            whereArguments[i] = String.valueOf(userIds.get(i));
        }
        Cursor queryResult = getReadableDatabase().rawQuery(query, whereArguments);

        List<WatchEntity> resultWatches = new ArrayList<>(queryResult.getCount());
        if (queryResult.getCount() > 0) {
            queryResult.moveToFirst();
            do {
                WatchEntity watchEntity = watchMapper.fromCursor(queryResult);
                resultWatches.add(watchEntity);
            } while (queryResult.moveToNext());
        }
        queryResult.close();
        return resultWatches;
    }

    public void saveWatches(List<WatchEntity> watches){
        for (WatchEntity watchEntity : watches) {
            saveWatch(watchEntity);
        }
    }
    public void saveWatch(WatchEntity watchEntity){
        ContentValues contentValues = watchMapper.toContentValues(watchEntity);
        if (contentValues.getAsLong(WatchTable.CSYS_DELETED) != null) {
            deleteWatch(watchEntity);
        } else {
            getWritableDatabase().insertWithOnConflict(WatchTable.TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        }
        insertInSync();
    }

    public long deleteWatch(WatchEntity watchEntity){
        long res = 0;
        String args = WatchTable.ID_USER + "=? AND "+ WatchTable.ID_EVENT +"=?";
        String[] stringArgs = new String[]{String.valueOf(watchEntity.getIdUser()), String.valueOf(watchEntity.getIdEvent())};
        Cursor c = getReadableDatabase().query(WatchTable.TABLE, WatchTable.PROJECTION, args, stringArgs, null, null, null);
        if (c.getCount() > 0) {
            res = getWritableDatabase().delete(WatchTable.TABLE, args, stringArgs);
        }
        c.close();
        return res;
    }

    public void deleteWatches(List<WatchEntity> watchEntities) {
        for (WatchEntity watchEntity : watchEntities) {
            deleteWatch(watchEntity);
        }
    }

    public void insertInSync(){
        insertInTableSync(WatchTable.TABLE,7,1000,0);
    }

    public List<WatchEntity> getWatchesByEvent(Long idEvent){
        List<Long> eventIds = new ArrayList<>(1);
        eventIds.add(idEvent);
        List<WatchEntity> watches = getWatchesFromEvents(eventIds);
        return watches;
    }

    public List<WatchEntity> getWatchesFromEvents(List<Long> eventIds) {
        String whereSelection = WatchTable.ID_EVENT
          + " IN (" + createListPlaceholders(eventIds.size())+") AND "+WatchTable.STATUS+"=1";
        String[] whereArguments = new String[eventIds.size()];
        for (int i = 0; i < eventIds.size(); i++) {
            whereArguments[i] = String.valueOf(eventIds.get(i));
        }

        Cursor queryResult =
          getReadableDatabase().query(WatchTable.TABLE, WatchTable.PROJECTION, whereSelection, whereArguments, null, null, null);

        List<WatchEntity> resultWatches = new ArrayList<>(queryResult.getCount());
        if (queryResult.getCount() > 0) {
            queryResult.moveToFirst();
            do {
                WatchEntity watchEntity = watchMapper.fromCursor(queryResult);
                resultWatches.add(watchEntity);
            } while (queryResult.moveToNext());
        }
        queryResult.close();
        return resultWatches;
    }

    public List<WatchEntity> getWatchesRejected() {
        String whereString = WatchTable.STATUS + "=?";
        String[] whereArguments = new String[]{String.valueOf(WatchEntity.STATUS_REJECT)};
        Cursor queryResult = getReadableDatabase().query(WatchTable.TABLE, WatchTable.PROJECTION, whereString, whereArguments, null, null, null);

        List<WatchEntity> resultWatches = new ArrayList<>(queryResult.getCount());
        if (queryResult.getCount() > 0) {
            queryResult.moveToFirst();
            do {
                WatchEntity watchEntity = watchMapper.fromCursor(queryResult);
                resultWatches.add(watchEntity);
            } while (queryResult.moveToNext());
        }
        queryResult.close();
        return resultWatches;
    }

    public WatchEntity getWatching(Long idUser) {
        String whereClause = WatchTable.STATUS + "=? AND " + WatchTable.ID_USER + "=?";
        String[] whereArguments = new String[] { String.valueOf(WatchEntity.STATUS_WATCHING), String.valueOf(idUser) };
        Cursor queryResult =
          getReadableDatabase().query(WatchTable.TABLE, WatchTable.PROJECTION, whereClause, whereArguments, null, null,
            null);
        if (queryResult.getCount() > 0) {
            queryResult.moveToFirst();
            WatchEntity watchEntity = watchMapper.fromCursor(queryResult);
            queryResult.close();
            return watchEntity;
        } else {
            return null;
        }
    }

    public void createUpdateWatch(WatchEntity watchEntity) {
        ContentValues contentValues = watchMapper.toContentValues(watchEntity);
        getWritableDatabase().insertWithOnConflict(WatchTable.TABLE,null,contentValues, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public WatchEntity getWatchByKeys(Long idUser, Long idEvent) {
        WatchEntity watchEntity = null;
        String whereString = WatchTable.ID_USER+"=? AND "+WatchTable.ID_EVENT +"=?";
        String[] whereArguments = new String[]{String.valueOf(idUser),String.valueOf(idEvent)};
        Cursor queryResult = getReadableDatabase().query(WatchTable.TABLE, WatchTable.PROJECTION,whereString,whereArguments,null,null,null);
        if(queryResult.getCount()>0){
            queryResult.moveToFirst();
            watchEntity = watchMapper.fromCursor(queryResult);
        }
        queryResult.close();
        return  watchEntity;
    }

    public WatchEntity getWatchVisibleByUser(Long userId) {
        String whereSelection = WatchTable.ID_USER + " = ? AND " + WatchTable.VISIBLE + " IS ?";
        String[] whereArguments = new String[] { String.valueOf(userId), String.valueOf(WatchEntity.VISIBLE) };

        Cursor queryResult =
          getReadableDatabase().query(WatchTable.TABLE, WatchTable.PROJECTION, whereSelection, whereArguments, null, null, null);

        WatchEntity watchEntity = null;
        if (queryResult.getCount() > 0) {
            queryResult.moveToFirst();
            watchEntity = watchMapper.fromCursor(queryResult);
        }
        queryResult.close();
        return watchEntity;
    }

    public List<WatchEntity> getWatchesNotSynchronized() {
        String whereClause = Phrase.from("{field} = '{n}' or {field} = '{u}'")
          .put("field", WatchTable.CSYS_SYNCHRONIZED)
          .put("n", Synchronized.SYNC_NEW)
          .put("u", Synchronized.SYNC_UPDATED)
          .format().toString();
        Cursor queryResult =
          getReadableDatabase().query(WatchTable.TABLE, WatchTable.PROJECTION, whereClause, null, null, null, null);

        List<WatchEntity> resultWatches = new ArrayList<>(queryResult.getCount());
        if (queryResult.getCount() > 0) {
            queryResult.moveToFirst();
            do {
                WatchEntity watchEntity = watchMapper.fromCursor(queryResult);
                resultWatches.add(watchEntity);
            } while (queryResult.moveToNext());
        }
        queryResult.close();
        return resultWatches;
    }

    public List<WatchEntity> getWatchesSynchronized() {
        String whereClause = Phrase.from("{field} = '{s}' or {field} is null")
          .put("field", WatchTable.CSYS_SYNCHRONIZED)
          .put("s", Synchronized.SYNC_SYNCHRONIZED)
          .format().toString();

        Cursor queryResult =
          getReadableDatabase().query(WatchTable.TABLE, WatchTable.PROJECTION, whereClause, null, null, null, null);

        List<WatchEntity> resultWatches = new ArrayList<>(queryResult.getCount());
        if (queryResult.getCount() > 0) {
            queryResult.moveToFirst();
            do {
                WatchEntity watchEntity = watchMapper.fromCursor(queryResult);
                resultWatches.add(watchEntity);
            } while (queryResult.moveToNext());
        }
        queryResult.close();
        return resultWatches;
    }

    public List<WatchEntity> getWatchesByEventForUsers(List<Long> users, Long idEvent) {
        if (users.isEmpty()) {
            return new ArrayList<>(0);
        }
        String whereClause = Phrase.from("{event_col} = '{id_event}' and {user_col} in ({list})")
          .put("event_col", WatchTable.ID_EVENT)
          .put("user_col", WatchTable.ID_USER)
          .put("id_event", String.valueOf(idEvent))
          .put("list", createListPlaceholders(users.size()))
          .format().toString();

        String[] whereArguments = new String[users.size()];
        for (int i = 0; i < users.size(); i++) {
            whereArguments[i] = String.valueOf(users.get(i));
        }
        Cursor queryResult =
          getReadableDatabase().query(WatchTable.TABLE, WatchTable.PROJECTION, whereClause, whereArguments, null, null,
            null);

        List<WatchEntity> resultWatches = new ArrayList<>(queryResult.getCount());
        if (queryResult.getCount() > 0) {
            queryResult.moveToFirst();
            do {
                WatchEntity watchEntity = watchMapper.fromCursor(queryResult);
                resultWatches.add(watchEntity);
            } while (queryResult.moveToNext());
        }
        queryResult.close();
        return resultWatches;

    }
}
