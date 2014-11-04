package gm.mobi.android.db.manager;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import gm.mobi.android.db.GMContract;
import gm.mobi.android.db.GMContract.MatchTable;
import gm.mobi.android.db.GMContract.WatchTable;
import gm.mobi.android.db.mappers.WatchMapper;
import gm.mobi.android.db.objects.UserEntity;
import gm.mobi.android.db.objects.WatchEntity;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class WatchManager extends AbstractManager{


    @Inject WatchMapper watchMapper;

    @Inject
    public WatchManager(WatchMapper watchMapper) {
        this.watchMapper = watchMapper;
    }

    public List<WatchEntity> getWatchesNotEndedOrAdjurnedFromUsers(List<Long> userIds) {
        String query = "SELECT w.* FROM "+WatchTable.TABLE+" w INNER JOIN "+ MatchTable.TABLE+" m ON "
          + "w."+WatchTable.ID_MATCH+" = m."+MatchTable.ID_MATCH
          + " WHERE m."+MatchTable.STATUS+" IN (0,1)"
          + " AND w."+WatchTable.ID_USER+" IN ("+createListPlaceholders(userIds.size())+");";

        String[] whereArguments = new String[userIds.size()];
        for (int i = 0; i < userIds.size(); i++) {
            whereArguments[i] = String.valueOf(userIds.get(i));
        }
        Cursor queryResult = db.rawQuery(query, whereArguments);

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
        long res = 0;
        for (WatchEntity watchEntity : watches) {
            ContentValues contentValues = watchMapper.toContentValues(watchEntity);
            if (contentValues.getAsLong(WatchTable.CSYS_DELETED) != null) {
                res = deleteWatch(watchEntity);
            } else {
                res = db.insertWithOnConflict(WatchTable.TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
                Timber.d("Shot inserted with result: %d", res);
            }
            insertInSync();
        }
    }

    public long deleteWatch(WatchEntity watchEntity){
        long res = 0;
        String args = WatchTable.ID_USER + "=? AND "+ WatchTable.ID_MATCH+"=?";
        String[] stringArgs = new String[]{String.valueOf(watchEntity.getIdUser()), String.valueOf(watchEntity.getIdMatch())};
        Cursor c = db.query(WatchTable.TABLE, WatchTable.PROJECTION, args, stringArgs, null, null, null);
        if (c.getCount() > 0) {
            res = db.delete(WatchTable.TABLE, args, stringArgs);
        }
        c.close();
        return res;
    }

    public void deleteWatches(List<WatchEntity> watchEntities) {
        for (WatchEntity watchEntity : watchEntities) {
            deleteWatch(watchEntity);
        }
    }

    public void deleteAllWatches(Long exceptUserId) {
        db.execSQL("DELETE FROM " + GMContract.WatchTable.TABLE+ " WHERE "+WatchTable.ID_USER+" = "+exceptUserId);
    }

    public void insertInSync(){
        insertInTableSync(WatchTable.TABLE,7,1000,0);
    }

    public List<WatchEntity> getWatchesFromMatches(List<Long> matchIds) {
        String whereSelection = WatchTable.ID_MATCH + " IN (" + createListPlaceholders(matchIds.size())+")";
        String[] whereArguments = new String[matchIds.size()];
        for (int i = 0; i < matchIds.size(); i++) {
            whereArguments[i] = String.valueOf(matchIds.get(i));
        }

        Cursor queryResult =
          db.query(WatchTable.TABLE, WatchTable.PROJECTION, whereSelection, whereArguments, null, null, null);

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
        Cursor queryResult = db.query(WatchTable.TABLE, WatchTable.PROJECTION, whereString, whereArguments, null, null, null);

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
