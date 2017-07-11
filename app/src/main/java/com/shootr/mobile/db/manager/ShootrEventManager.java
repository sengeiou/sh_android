package com.shootr.mobile.db.manager;

    import android.content.ContentValues;
    import android.database.Cursor;
    import android.database.sqlite.SQLiteDatabase;
    import android.database.sqlite.SQLiteOpenHelper;
    import com.shootr.mobile.data.entity.ShootrEventEntity;
    import com.shootr.mobile.db.DatabaseContract;
    import com.shootr.mobile.db.mappers.ShootrEventEntityDBMapper;
    import com.shootr.mobile.domain.model.shot.ShotEventType;
    import java.util.ArrayList;
    import java.util.List;
    import javax.inject.Inject;

public class ShotEventManager extends AbstractManager {

  private static final String SHOT_EVENT_TABLE = DatabaseContract.ShotEventTable.TABLE;

  private final ShootrEventEntityDBMapper shootrEventEntityDBMapper;

  @Inject public ShotEventManager(SQLiteOpenHelper dbHelper,
      ShootrEventEntityDBMapper shootrEventEntityDBMapper) {
    super(dbHelper);
    this.shootrEventEntityDBMapper = shootrEventEntityDBMapper;
  }

  public void clickLink(ShootrEventEntity shootrEventEntity) {
    if (shootrEventEntity != null) {
      shootrEventEntity.setType(ShotEventType.SHOT_LINK_CLICK);
      ContentValues contentValues = shootrEventEntityDBMapper.toContentValues(shootrEventEntity);
      getWritableDatabase().insertWithOnConflict(SHOT_EVENT_TABLE, null, contentValues,
          SQLiteDatabase.CONFLICT_REPLACE);
    }
  }

  public void viewHighlightedShot(ShootrEventEntity shootrEventEntity) {
    if (shootrEventEntity != null) {
      shootrEventEntity.setType(ShotEventType.SHOT_VIEW);
      ContentValues contentValues = shootrEventEntityDBMapper.toContentValues(shootrEventEntity);
      getWritableDatabase().insertWithOnConflict(SHOT_EVENT_TABLE, null, contentValues,
          SQLiteDatabase.CONFLICT_REPLACE);
    }
  }

  public void shotDetailViewed(ShootrEventEntity shootrEventEntity) {
    if (shootrEventEntity != null && shootrEventEntity.getIdShot() != null) {
      shootrEventEntity.setType(ShotEventType.SHOT_DETAIL_VIEW);
      ContentValues contentValues = shootrEventEntityDBMapper.toContentValues(shootrEventEntity);
      getWritableDatabase().insertWithOnConflict(SHOT_EVENT_TABLE, null, contentValues,
          SQLiteDatabase.CONFLICT_REPLACE);
    }
  }

  public List<ShootrEventEntity> getEvents() {
    List<ShootrEventEntity> shotEventEntities = new ArrayList<>();
    Cursor c =
        getReadableDatabase().query(SHOT_EVENT_TABLE, DatabaseContract.ShotEventTable.PROJECTION,
            null, null, null, null, null);
    if (c.getCount() > 0) {
      c.moveToFirst();
      do {
        shotEventEntities.add(shootrEventEntityDBMapper.fromCursor(c));
      } while (c.moveToNext());
    }
    c.close();
    return shotEventEntities;
  }

  public void deleteShotEvents() {
    getWritableDatabase().delete(SHOT_EVENT_TABLE, null, null);
  }
}
