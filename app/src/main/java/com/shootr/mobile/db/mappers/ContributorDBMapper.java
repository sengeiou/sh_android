package com.shootr.mobile.db.mappers;

import android.content.ContentValues;
import android.database.Cursor;
import com.shootr.mobile.data.entity.ContributorEntity;
import com.shootr.mobile.db.DatabaseContract;

public class ContributorDBMapper extends GenericDBMapper {

  public static final String ID_USER = DatabaseContract.ContributorTable.ID_USER;
  public static final String ID_STREAM = DatabaseContract.ContributorTable.ID_STREAM;
  public static final String ID_CONTRIBUTOR = DatabaseContract.ContributorTable.ID_CONTRIBUTOR;

  public ContributorEntity fromCursor(Cursor c) {
    ContributorEntity contributor = new ContributorEntity();
    contributor.setIdUser(c.getString(c.getColumnIndex(ID_USER)));
    contributor.setIdStream(c.getString(c.getColumnIndex(ID_STREAM)));
    contributor.setIdContributor(c.getString(c.getColumnIndex(ID_CONTRIBUTOR)));
    return contributor;
  }

  public ContentValues toContentValues(ContributorEntity contributor) {
    ContentValues cv = new ContentValues();
    cv.put(ID_USER, contributor.getIdUser());
    cv.put(ID_STREAM, contributor.getIdStream());
    cv.put(ID_CONTRIBUTOR, contributor.getIdContributor());
    return cv;
  }
}
