package com.shootr.mobile.db.manager;

    import android.content.ContentValues;
    import android.database.Cursor;
    import android.database.sqlite.SQLiteDatabase;
    import android.database.sqlite.SQLiteOpenHelper;
    import com.shootr.mobile.data.entity.ShotEventEntity;
    import com.shootr.mobile.db.DatabaseContract;
    import com.shootr.mobile.db.mappers.ShotEventEntityDBMapper;
    import com.shootr.mobile.domain.model.shot.ShotEventType;
    import java.util.ArrayList;
    import java.util.List;
    import javax.inject.Inject;

public class ShotEventManager extends AbstractManager {

  private static final String SHOT_EVENT_TABLE = DatabaseContract.ShotEventTable.TABLE;

  private final ShotEventEntityDBMapper shotEventEntityDBMapper;

  @Inject public ShotEventManager(SQLiteOpenHelper dbHelper,
      ShotEventEntityDBMapper shotEventEntityDBMapper) {
    super(dbHelper);
    this.shotEventEntityDBMapper = shotEventEntityDBMapper;
  }

  public void clickLink(ShotEventEntity shotEventEntity) {
    if (shotEventEntity != null) {
      shotEventEntity.setType(ShotEventType.SHOT_LINK_CLICK);
      ContentValues contentValues = shotEventEntityDBMapper.toContentValues(shotEventEntity);
      getWritableDatabase().insertWithOnConflict(SHOT_EVENT_TABLE, null, contentValues,
          SQLiteDatabase.CONFLICT_REPLACE);
    }
  }

  public void viewHighlightedShot(ShotEventEntity shotEventEntity) {
    if (shotEventEntity != null) {
      shotEventEntity.setType(ShotEventType.SHOT_VIEW);
      ContentValues contentValues = shotEventEntityDBMapper.toContentValues(shotEventEntity);
      getWritableDatabase().insertWithOnConflict(SHOT_EVENT_TABLE, null, contentValues,
          SQLiteDatabase.CONFLICT_REPLACE);
    }
  }

  public void shotDetailViewed(ShotEventEntity shotEventEntity) {
    if (shotEventEntity != null && shotEventEntity.getIdShot() != null) {
      shotEventEntity.setType(ShotEventType.SHOT_DETAIL_VIEW);
      ContentValues contentValues = shotEventEntityDBMapper.toContentValues(shotEventEntity);
      getWritableDatabase().insertWithOnConflict(SHOT_EVENT_TABLE, null, contentValues,
          SQLiteDatabase.CONFLICT_REPLACE);
    }
  }

  public List<ShotEventEntity> getEvents() {
    List<ShotEventEntity> shotEventEntities = new ArrayList<>();
    Cursor c =
        getReadableDatabase().query(SHOT_EVENT_TABLE, DatabaseContract.ShotEventTable.PROJECTION,
            null, null, null, null, null);
    if (c.getCount() > 0) {
      c.moveToFirst();
      do {
        shotEventEntities.add(shotEventEntityDBMapper.fromCursor(c));
      } while (c.moveToNext());
    }
    c.close();
    return shotEventEntities;
  }

  public void deleteShotEvents() {
    getWritableDatabase().delete(SHOT_EVENT_TABLE, null, null);
  }
}
