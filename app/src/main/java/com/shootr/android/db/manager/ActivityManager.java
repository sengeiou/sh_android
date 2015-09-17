package com.shootr.android.db.manager;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.shootr.android.data.entity.ActivityEntity;
import com.shootr.android.db.DatabaseContract;
import com.shootr.android.db.mappers.ActivityEntityMapper;
import com.shootr.android.domain.ActivityTimelineParameters;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class ActivityManager extends AbstractManager {

    @Inject ActivityEntityMapper activityEntityMapper;

    private static final String ACTIVITY_TABLE = DatabaseContract.ActivityTable.TABLE;

    @Inject protected ActivityManager(SQLiteOpenHelper dbHelper) {
        super(dbHelper);
    }

    public List<ActivityEntity> getActivityTimelineFromParameters(ActivityTimelineParameters parameters) {
        List<String> includedTypes = parameters.getIncludedTypes();

        String typeSelection = DatabaseContract.ActivityTable.TYPE + " IN ("+ createListPlaceholders(includedTypes.size()) +")";

        int whereArgumentsSize = includedTypes.size();
        String[] whereArguments = new String[whereArgumentsSize];

        for (int i = 0; i < includedTypes.size(); i++) {
            whereArguments[i] = includedTypes.get(i);
        }

        String whereClause = typeSelection;

        Cursor queryResult =
          getReadableDatabase().query(ACTIVITY_TABLE, DatabaseContract.ActivityTable.PROJECTION, whereClause, whereArguments, null, null,
            DatabaseContract.ActivityTable.BIRTH +" DESC");

        List<ActivityEntity> resultActivities = new ArrayList<>(queryResult.getCount());
        ActivityEntity activityEntity;
        if (queryResult.getCount() > 0) {
            queryResult.moveToFirst();
            do {
                activityEntity = activityEntityMapper.fromCursor(queryResult);
                resultActivities.add(activityEntity);
            } while (queryResult.moveToNext());
        }
        queryResult.close();
        return resultActivities;
    }

    public void saveActivities(List<ActivityEntity> activityEntities){
        SQLiteDatabase database = getWritableDatabase();
        try{
            database.beginTransaction();
            for (ActivityEntity activityEntity : activityEntities) {
                ContentValues contentValues = activityEntityMapper.toContentValues(activityEntity);
                database.insertWithOnConflict(DatabaseContract.ActivityTable.TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
            }
            database.setTransactionSuccessful();
        }finally {
            database.endTransaction();
        }

    }

    public ActivityEntity getActivity(String activityId) {
        String whereSelection = DatabaseContract.ActivityTable.ID_ACTIVITY + " = ?";
        String[] whereArguments = new String[] { activityId };

        Cursor queryResult = getReadableDatabase().query(ACTIVITY_TABLE,
          DatabaseContract.ActivityTable.PROJECTION,
          whereSelection,
          whereArguments,
          null,
          null,
          null);

        if (queryResult.getCount() > 0) {
            queryResult.moveToFirst();
            ActivityEntity activityEntity = activityEntityMapper.fromCursor(queryResult);
            queryResult.close();
            return activityEntity;
        } else {
            return null;
        }
    }

    public Long getLastModifiedDateForActivity() {
        String order = DatabaseContract.ActivityTable.MODIFIED + " desc";

        Cursor queryResult =
          getReadableDatabase().query(DatabaseContract.ActivityTable.TABLE, DatabaseContract.ActivityTable.PROJECTION, null, null, null, null, order, "1");

        if (queryResult.getCount() > 0) {
            queryResult.moveToFirst();
            ActivityEntity lastActivity = activityEntityMapper.fromCursor(queryResult);
            return lastActivity.getModified().getTime();
        } else {
            return 0L;
        }
    }

    public void deleteActivitiesWithShot(String idShot) {
        String args = DatabaseContract.ActivityTable.ID_SHOT + "=?";
        String[] stringArgs = new String[]{idShot};
        Cursor c = getReadableDatabase().query(DatabaseContract.ActivityTable.TABLE, DatabaseContract.ActivityTable.PROJECTION, args, stringArgs, null, null, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            do {
                getWritableDatabase().delete(DatabaseContract.ActivityTable.TABLE,
                  DatabaseContract.ActivityTable.ID_SHOT,
                  new String[] {});
            } while (c.moveToNext());
        }
        c.close();
    }
}
