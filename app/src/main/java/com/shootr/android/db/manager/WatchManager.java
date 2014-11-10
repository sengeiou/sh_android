package com.shootr.android.db.manager;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.shootr.android.db.DatabaseContract;
import com.shootr.android.db.DatabaseContract.MatchTable;
import com.shootr.android.db.DatabaseContract.WatchTable;
import com.shootr.android.db.mappers.WatchMapper;
import com.shootr.android.db.objects.MatchEntity;
import com.shootr.android.db.objects.WatchEntity;
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
                Timber.d("Watch inserted with result: %d", res);
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

    public void deleteAllWatches() {
        db.execSQL("DELETE FROM " + DatabaseContract.WatchTable.TABLE);
    }

    public void insertInSync(){
        insertInTableSync(WatchTable.TABLE,7,1000,0);
    }

    public List<WatchEntity> getWatchesByMatch(Long idMatch){
        List<Long> matchIds = new ArrayList<>(1);
        matchIds.add(idMatch);
        List<WatchEntity> watches = getWatchesFromMatches(matchIds);
        return watches;
    }

    public List<WatchEntity> getWatchesFromMatches(List<Long> matchIds) {
        String whereSelection = WatchTable.ID_MATCH + " IN (" + createListPlaceholders(matchIds.size())+") AND "+WatchTable.STATUS+"=1";
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

    public void createUpdateWatch(WatchEntity watchEntity) {
        ContentValues contentValues = watchMapper.toContentValues(watchEntity);
        db.insertWithOnConflict(WatchTable.TABLE,null,contentValues, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public WatchEntity getWatchByKeys(Long idUser, Long idMatch) {
        WatchEntity watchEntity = null;
        String whereString = WatchTable.ID_USER+"=? AND "+WatchTable.ID_MATCH+"=?";
        String[] whereArguments = new String[]{String.valueOf(idUser),String.valueOf(idMatch)};
        Cursor queryResult = db.query(WatchTable.TABLE, WatchTable.PROJECTION,whereString,whereArguments,null,null,null);
        if(queryResult.getCount()>0){
            queryResult.moveToFirst();
            watchEntity = watchMapper.fromCursor(queryResult);
        }
        queryResult.close();
        return  watchEntity;
    }


    /**
     * Check if it exists any data for send to server. This method It is called before request datas
     *
     * **/
    public List<WatchEntity> getDatasForSendToServerInCase(){
        List<WatchEntity> watchesToUpdate = new ArrayList<>();
        String args = WatchTable.CSYS_SYNCHRONIZED+"='N' OR "+WatchTable.CSYS_SYNCHRONIZED+"= 'D' OR "+WatchTable.CSYS_SYNCHRONIZED+"='U'";
        Cursor c = db.query(WatchTable.TABLE, WatchTable.PROJECTION,args,null,null,null,null);
        if(c.getCount()>0){
            c.moveToFirst();
            do{
                watchesToUpdate.add(watchMapper.fromCursor(c));
            }while(c.moveToNext());
        }
        c.close();
        return watchesToUpdate;
    }

    public List<WatchEntity> getWatchesWhereUserNot(Long currentUserId) {
        String sql = "SELECT * FROM "+WatchTable.TABLE+" WHERE "+WatchTable.ID_MATCH+" NOT IN( SELECT "+WatchTable.ID_MATCH +" FROM "+WatchTable.TABLE+" WHERE "+WatchTable.ID_USER+" ="+currentUserId+");";

        Cursor queryResult = db.rawQuery(sql,null);
        List<WatchEntity> resultWatches = new ArrayList<>();
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

    public WatchEntity getWatchByMatchAndUser(Long matchId, Long userId) {
        String whereSelection = WatchTable.ID_MATCH + " = ? AND " + WatchTable.ID_USER + " = ? AND "+WatchTable.STATUS+" IS NOT ?";
        String[] whereArguments = new String[]{String.valueOf(matchId), String.valueOf(userId), String.valueOf(WatchEntity.STATUS_DEFAULT)};

        Cursor queryResult =
                db.query(WatchTable.TABLE, WatchTable.PROJECTION, whereSelection, whereArguments, null, null, null);

        WatchEntity watchEntity = null;
        if (queryResult.getCount() > 0) {
            queryResult.moveToFirst();
            watchEntity = watchMapper.fromCursor(queryResult);
        }
        queryResult.close();
        return watchEntity;
    }

    public Integer getPeopleWatchingInInfo() {
        String sql = "SELECT COUNT(DISTINCT("+WatchTable.ID_USER+")) as NUMBER FROM "+WatchTable.TABLE+" w, "+MatchTable.TABLE+" m WHERE m."+MatchTable.ID_MATCH+"=w."+WatchTable.ID_MATCH+" AND m."+MatchTable.STATUS+"="+MatchEntity.STARTED +" AND w."+WatchTable.STATUS+"="+WatchEntity.STATUS_WATCHING;
        Cursor queryResult = db.rawQuery(sql,null);
        Integer number = 0;
        if (queryResult.getCount() > 0) {
            queryResult.moveToFirst();
            number = queryResult.getInt(queryResult.getColumnIndex("NUMBER"));
        }
        queryResult.close();
       return number;
    }
}
