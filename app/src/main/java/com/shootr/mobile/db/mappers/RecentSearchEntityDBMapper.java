package com.shootr.mobile.db.mappers;

import android.content.ContentValues;
import android.database.Cursor;
import com.shootr.mobile.data.entity.RecentSearchEntity;
import com.shootr.mobile.data.entity.StreamEntity;
import com.shootr.mobile.data.entity.UserEntity;
import com.shootr.mobile.db.DatabaseContract;
import com.shootr.mobile.domain.model.SearchableType;
import javax.inject.Inject;

public class RecentSearchEntityDBMapper extends GenericDBMapper {

  @Inject public RecentSearchEntityDBMapper() {
  }

  public ContentValues toContentValues(RecentSearchEntity recentSearchEntity) {
    ContentValues contentValues = new ContentValues();
    fillContentValues(recentSearchEntity, contentValues);
    return contentValues;
  }

  private void fillContentValues(RecentSearchEntity recentSearchEntity,
      ContentValues contentValues) {
    if (recentSearchEntity.getStream().getIdStream() != null) {
      contentValues.put(DatabaseContract.RecentSearchTable.ID_SEARCH_ITEM,
          recentSearchEntity.getStream().getIdStream());
      contentValues.put(DatabaseContract.RecentSearchTable.ITEM_TYPE, SearchableType.STREAM);
    } else {
      contentValues.put(DatabaseContract.RecentSearchTable.ID_SEARCH_ITEM,
          recentSearchEntity.getUser().getIdUser());
      contentValues.put(DatabaseContract.RecentSearchTable.ITEM_TYPE, SearchableType.USER);
    }
    contentValues.put(DatabaseContract.RecentSearchTable.VISIT_DATE,
        recentSearchEntity.getVisitDate());
  }

  public RecentSearchEntity fromCursor(Cursor c) {
    RecentSearchEntity recentSearchEntity = new RecentSearchEntity();
    fillSearchEntity(c, recentSearchEntity);
    return recentSearchEntity;
  }

  private void fillSearchEntity(Cursor c, RecentSearchEntity recentSearchEntity) {

    int type = c.getInt(c.getColumnIndex(DatabaseContract.RecentSearchTable.ITEM_TYPE));
    if (type == 0) {
      StreamEntity stream = new StreamEntity();
      stream.setIdStream(
          c.getString(c.getColumnIndex(DatabaseContract.RecentSearchTable.ID_SEARCH_ITEM)));
      recentSearchEntity.setStream(stream);
    } else {
      UserEntity user = new UserEntity();
      user.setIdUser(
          c.getString(c.getColumnIndex(DatabaseContract.RecentSearchTable.ID_SEARCH_ITEM)));
      recentSearchEntity.setUser(user);
    }
    recentSearchEntity.setVisitDate(
        c.getLong(c.getColumnIndex(DatabaseContract.RecentSearchTable.VISIT_DATE)));
  }
}
