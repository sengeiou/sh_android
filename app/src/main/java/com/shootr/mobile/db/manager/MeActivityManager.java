package com.shootr.mobile.db.manager;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.shootr.mobile.data.entity.ActivityEntity;
import com.shootr.mobile.db.DatabaseContract;
import com.shootr.mobile.db.mappers.MeActivityEntityDBMapper;
import com.shootr.mobile.domain.model.activity.ActivityTimelineParameters;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class MeActivityManager extends AbstractManager {

  @Inject MeActivityEntityDBMapper meActivityEntityDBMapper;

  private static final String ACTIVITY_TABLE = DatabaseContract.MeActivityTable.TABLE;

  @Inject protected MeActivityManager(SQLiteOpenHelper dbHelper) {
    super(dbHelper);
  }

  public List<ActivityEntity> getActivityTimelineFromParameters(
      ActivityTimelineParameters parameters) {

    Cursor queryResult =
        getReadableDatabase().query(ACTIVITY_TABLE, DatabaseContract.MeActivityTable.PROJECTION,
            null, null, null, null,
            DatabaseContract.MeActivityTable.BIRTH + " DESC", parameters.getLimit().toString());

    List<ActivityEntity> resultActivities = new ArrayList<>(queryResult.getCount());
    ActivityEntity activityEntity;
    if (queryResult.getCount() > 0) {
      queryResult.moveToFirst();
      do {
        activityEntity = meActivityEntityDBMapper.fromCursor(queryResult);
        resultActivities.add(activityEntity);
      } while (queryResult.moveToNext());
    }
    queryResult.close();
    return resultActivities;
  }

  public void saveActivities(List<ActivityEntity> activityEntities) {
    SQLiteDatabase database = getWritableDatabase();
    try {
      database.beginTransaction();
      for (ActivityEntity activityEntity : activityEntities) {
        ContentValues contentValues = meActivityEntityDBMapper.toContentValues(activityEntity);
        database.insertWithOnConflict(DatabaseContract.MeActivityTable.TABLE, null, contentValues,
            SQLiteDatabase.CONFLICT_REPLACE);
      }
      database.setTransactionSuccessful();
    } finally {
      database.endTransaction();
    }
  }

  public ActivityEntity getActivity(String activityId) {
    String whereSelection = DatabaseContract.MeActivityTable.ID_ACTIVITY + " = ?";
    String[] whereArguments = new String[] { activityId };

    Cursor queryResult =
        getReadableDatabase().query(ACTIVITY_TABLE, DatabaseContract.MeActivityTable.PROJECTION,
            whereSelection, whereArguments, null, null, null);

    if (queryResult.getCount() > 0) {
      queryResult.moveToFirst();
      ActivityEntity activityEntity = meActivityEntityDBMapper.fromCursor(queryResult);
      queryResult.close();
      return activityEntity;
    } else {
      return null;
    }
  }

  public Long getLastModifiedDateForActivity() {
    String order = DatabaseContract.MeActivityTable.MODIFIED + " desc";

    Cursor queryResult = getReadableDatabase().query(DatabaseContract.MeActivityTable.TABLE,
        DatabaseContract.MeActivityTable.PROJECTION, null, null, null, null, order, "1");

    if (queryResult.getCount() > 0) {
      queryResult.moveToFirst();
      ActivityEntity lastActivity = meActivityEntityDBMapper.fromCursor(queryResult);
      return lastActivity.getModified().getTime();
    } else {
      return 0L;
    }
  }

  public void deleteActivitiesWithShot(String idShot) {
    String args = DatabaseContract.MeActivityTable.ID_SHOT + "=?";
    String[] stringArgs = new String[] { idShot };
    Cursor c = getReadableDatabase().query(DatabaseContract.MeActivityTable.TABLE,
        DatabaseContract.MeActivityTable.PROJECTION, args, stringArgs, null, null, null);
    if (c.getCount() > 0) {
      c.moveToFirst();
      do {
        getWritableDatabase().delete(DatabaseContract.MeActivityTable.TABLE,
            DatabaseContract.MeActivityTable.ID_SHOT, new String[] {});
      } while (c.moveToNext());
    }
    c.close();
  }

  public void updateFollowOnStreamActivities(boolean isFollow, String idStream)
  {
    String whereClause = DatabaseContract.MeActivityTable.ID_STREAM + " = ?";
    String[] whereArguments = new String[] { String.valueOf(idStream) };
    ContentValues values = new ContentValues(1);
    values.put(DatabaseContract.MeActivityTable.IS_FOLLOWING, isFollow ? 1 : 0);
    getWritableDatabase().update(DatabaseContract.MeActivityTable.TABLE, values, whereClause,
        whereArguments);
  }
}
