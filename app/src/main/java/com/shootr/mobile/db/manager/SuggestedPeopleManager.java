package com.shootr.mobile.db.manager;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.shootr.mobile.data.entity.SuggestedPeopleEntity;
import com.shootr.mobile.db.DatabaseContract;
import com.shootr.mobile.db.mappers.SuggestedPeopleDBMapper;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class SuggestedPeopleManager extends AbstractManager {

    private static final String SUGGESTED_PEOPLE_TABLE = DatabaseContract.SuggestedPeopleTable.TABLE;

    private final SuggestedPeopleDBMapper suggestedPeopleMapper;

    @Inject public SuggestedPeopleManager(SQLiteOpenHelper dbHelper, SuggestedPeopleDBMapper suggestedPeopleMapper) {
        super(dbHelper);
        this.suggestedPeopleMapper = suggestedPeopleMapper;
    }

    public void saveSuggestedPeople(List<SuggestedPeopleEntity> suggestedPeople) {
        SQLiteDatabase database = getWritableDatabase();
        try {
            database.beginTransaction();
            for (SuggestedPeopleEntity suggestedPeopleEntity : suggestedPeople) {
                this.saveSuggestedPeopple(suggestedPeopleEntity);
            }
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

    private void saveSuggestedPeopple(SuggestedPeopleEntity suggestedPeople) {
        ContentValues contentValues = suggestedPeopleMapper.toContentValues(suggestedPeople);
        if (contentValues.getAsLong(DatabaseContract.SyncColumns.DELETED) != null) {
            deleteSuggestedPeople(suggestedPeople);
        } else {
            getWritableDatabase().insertWithOnConflict(DatabaseContract.SuggestedPeopleTable.TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        }
    }

    private long deleteSuggestedPeople(SuggestedPeopleEntity suggestedPeople) {
        long res = 0;
        String args = DatabaseContract.SuggestedPeopleTable.ID + "=?";
        String[] stringArgs = new String[] { String.valueOf(suggestedPeople.getIdUser()) };
        Cursor c = getReadableDatabase().query(SUGGESTED_PEOPLE_TABLE, DatabaseContract.SuggestedPeopleTable.PROJECTION, args, stringArgs, null, null, null);
        if (c.getCount() > 0) {
            res = getWritableDatabase().delete(SUGGESTED_PEOPLE_TABLE, DatabaseContract.SuggestedPeopleTable.ID+"=?", new String[] { String.valueOf(suggestedPeople.getIdUser()) });
        }
        c.close();
        return res;
    }

    public List<SuggestedPeopleEntity> getSuggestedPeople() {
        List<SuggestedPeopleEntity> suggestedPeopleEntities = new ArrayList<>();

        Cursor c = getReadableDatabase().query(DatabaseContract.SuggestedPeopleTable.TABLE, DatabaseContract.SuggestedPeopleTable.PROJECTION, null, null, null, null, null, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            do {
                SuggestedPeopleEntity suggestedPeopleEntity = suggestedPeopleMapper.fromCursor(c);
                if (suggestedPeopleEntity != null) {
                    suggestedPeopleEntities.add(suggestedPeopleEntity);
                }
            } while (c.moveToNext());
        }
        c.close();
        return suggestedPeopleEntities;
    }
}