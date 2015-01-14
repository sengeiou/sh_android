package com.shootr.android.db.manager;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.database.sqlite.SQLiteOpenHelper;
import com.shootr.android.db.DatabaseContract;
import com.shootr.android.db.DatabaseContract.MatchTable;
import com.shootr.android.db.mappers.MatchMapper;
import com.shootr.android.data.entity.MatchEntity;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class MatchManager extends AbstractManager{

    @Inject MatchMapper matchMapper;

    @Inject public MatchManager(SQLiteOpenHelper openHelper, MatchMapper matchMapper){
        super(openHelper);
        this.matchMapper = matchMapper;
    }

    public MatchEntity getMatchById(Long matchId) {
        String whereSelection = MatchTable.ID_MATCH + " = ?";
        String[] whereArguments = new String[] { String.valueOf(matchId) };

        Cursor queryResult =
          getReadableDatabase().query(MatchTable.TABLE, MatchTable.PROJECTION, whereSelection, whereArguments, null,
            null, null);

        MatchEntity matchEntity = null;
        if (queryResult.getCount() > 0) {
            queryResult.moveToFirst();
            matchEntity = matchMapper.fromCursor(queryResult);
        }
        queryResult.close();
        return matchEntity;
    }

    public List<MatchEntity> getMatchesByIds(List<Long> matchIds) {
        String whereSelection = MatchTable.ID_MATCH + " IN (" + createListPlaceholders(matchIds.size())+")";
        String[] whereArguments = new String[matchIds.size()];
        for (int i = 0; i < matchIds.size(); i++) {
            whereArguments[i] = String.valueOf(matchIds.get(i));
        }

        Cursor queryResult =
          getReadableDatabase().query(MatchTable.TABLE, MatchTable.PROJECTION, whereSelection, whereArguments, null, null, null);

        List<MatchEntity> resultMatches = new ArrayList<>(queryResult.getCount());
        if (queryResult.getCount() > 0) {
            queryResult.moveToFirst();
            do {
                MatchEntity matchEntity = matchMapper.fromCursor(queryResult);
                resultMatches.add(matchEntity);
            } while (queryResult.moveToNext());
        }
        queryResult.close();
        return resultMatches;
    }

    public MatchEntity getNextMatchFromTeam(Long favoriteTeamId) {
        String whereSelection =
          "("+MatchTable.ID_LOCAL_TEAM + "= ? OR " + MatchTable.ID_VISITOR_TEAM + "= ?) AND ("+MatchTable.STATUS+" IN (0, 1))";
        String[] whereArguments = new String[] { String.valueOf(favoriteTeamId), String.valueOf(favoriteTeamId) };
        Cursor queryResult = getReadableDatabase().query(MatchTable.TABLE, MatchTable.PROJECTION, whereSelection, whereArguments, null, null,
          MatchTable.MATCH_DATE, "1");

        MatchEntity matchEntity = null;
        if (queryResult.getCount() > 0) {
            queryResult.moveToFirst();
            matchEntity = matchMapper.fromCursor(queryResult);
        }
        queryResult.close();
        return matchEntity;
    }

    public void saveMatches(List<MatchEntity> matches){
        for(MatchEntity matchEntity: matches){
            saveMatch(matchEntity);
        }
    }

    public void saveMatch(MatchEntity matchEntity) {
        ContentValues contentValues = matchMapper.toContentValues(matchEntity);
        if (contentValues.getAsLong(MatchTable.CSYS_DELETED) != null) {
            deleteMatch(matchEntity);
        } else {
            getWritableDatabase().insertWithOnConflict(MatchTable.TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        }
        insertInSync();
    }

    public long deleteMatch(MatchEntity matchEntity){
        long res = 0;
        String args = MatchTable.ID_MATCH + "=?";
        String[] stringArgs = new String[]{String.valueOf(matchEntity.getIdMatch())};
        Cursor c = getReadableDatabase().query(MatchTable.TABLE, MatchTable.PROJECTION, args, stringArgs, null, null, null);
        if (c.getCount() > 0) {
           res = getWritableDatabase().delete(MatchTable.TABLE, args, stringArgs);
        }
        c.close();
        return res;
    }

    public void deleteMatches(List<MatchEntity> matchEntities) {
        for (MatchEntity matchEntity : matchEntities) {
            deleteMatch(matchEntity);
        }
    }

    public void insertInSync(){
        insertInTableSync(MatchTable.TABLE,10,1000,0);
    }

    public List<MatchEntity> getEndedAndAdjournedMatches() {
        String whereSelection = MatchTable.STATUS + " IN (2,3)";
        Cursor queryResult =
          getReadableDatabase().query(MatchTable.TABLE, MatchTable.PROJECTION, whereSelection, null, null, null, null);

        List<MatchEntity> resultMatches = new ArrayList<>(queryResult.getCount());
        if (queryResult.getCount() > 0) {
            queryResult.moveToFirst();
            do {
                MatchEntity matchEntity = matchMapper.fromCursor(queryResult);
                resultMatches.add(matchEntity);
            } while (queryResult.moveToNext());
        }
        queryResult.close();
        return resultMatches;
    }

    public void deleteAllMatches() {
        getWritableDatabase().execSQL("DELETE FROM "+ DatabaseContract.MatchTable.TABLE);
    }
}
