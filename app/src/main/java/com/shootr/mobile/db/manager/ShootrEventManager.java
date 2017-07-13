package com.shootr.mobile.db.manager;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.shootr.mobile.data.entity.ShootrEventEntity;
import com.shootr.mobile.db.DatabaseContract;
import com.shootr.mobile.db.mappers.ShootrEventEntityDBMapper;
import com.shootr.mobile.domain.model.shot.ShootrEventType;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class ShootrEventManager extends AbstractManager {

  private static final String SHOT_EVENT_TABLE = DatabaseContract.ShootrEventTable.TABLE;

  private final ShootrEventEntityDBMapper shootrEventEntityDBMapper;

  @Inject public ShootrEventManager(SQLiteOpenHelper dbHelper,
      ShootrEventEntityDBMapper shootrEventEntityDBMapper) {
    super(dbHelper);
    this.shootrEventEntityDBMapper = shootrEventEntityDBMapper;
  }

  public void clickLink(ShootrEventEntity shootrEventEntity) {
    shootrEventEntity.setType(ShootrEventType.SHOT_LINK_CLICK);
    putShootrEventEntity(shootrEventEntity);
  }

  public void viewHighlightedShot(ShootrEventEntity shootrEventEntity) {
    shootrEventEntity.setType(ShootrEventType.SHOT_VIEW);
    putShootrEventEntity(shootrEventEntity);
  }

  public void shotDetailViewed(ShootrEventEntity shootrEventEntity) {
    shootrEventEntity.setType(ShootrEventType.SHOT_DETAIL_VIEW);
    putShootrEventEntity(shootrEventEntity);
  }

  public List<ShootrEventEntity> getEvents() {
    List<ShootrEventEntity> shotEventEntities = new ArrayList<>();
    Cursor c =
        getReadableDatabase().query(SHOT_EVENT_TABLE, DatabaseContract.ShootrEventTable.PROJECTION,
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

  public void viewUserProfileEvent(ShootrEventEntity shootrEventEntity) {
    shootrEventEntity.setType(ShootrEventType.USER_PROFILE_VIEWED);
    putShootrEventEntity(shootrEventEntity);
  }

  private void putShootrEventEntity(ShootrEventEntity shootrEventEntity) {
    if (shootrEventEntity != null && shootrEventEntity.getId() != null) {
      ContentValues contentValues = shootrEventEntityDBMapper.toContentValues(shootrEventEntity);
      getWritableDatabase().insertWithOnConflict(SHOT_EVENT_TABLE, null, contentValues,
          SQLiteDatabase.CONFLICT_REPLACE);
    }
  }
}
