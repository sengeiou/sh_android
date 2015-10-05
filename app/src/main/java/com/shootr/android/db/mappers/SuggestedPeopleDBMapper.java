package com.shootr.android.db.mappers;

import android.content.ContentValues;
import android.database.Cursor;
import com.shootr.android.data.entity.SuggestedPeopleEntity;
import com.shootr.android.db.DatabaseContract;

public class SuggestedPeopleDBMapper extends GenericDBMapper {

    private final UserEntityDBMapper userEntityDBMapper;

    public SuggestedPeopleDBMapper(UserEntityDBMapper userEntityDBMapper) {
        this.userEntityDBMapper = userEntityDBMapper;
    }

    public SuggestedPeopleEntity fromCursor(Cursor c) {
        SuggestedPeopleEntity entity = new SuggestedPeopleEntity();
        entity.setRelevance(c.getLong(c.getColumnIndex(DatabaseContract.SuggestedPeopleTable.RELEVANCE)));
        userEntityDBMapper.fillEntityWithCursor(entity, c);
        return entity;
    }

    public ContentValues toContentValues(SuggestedPeopleEntity entity) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseContract.SuggestedPeopleTable.RELEVANCE, entity.getRelevance());
        userEntityDBMapper.fillContentValuesWithEntity(cv, entity);
        return cv;
    }
}
