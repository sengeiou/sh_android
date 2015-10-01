package com.shootr.android.db.manager;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.shootr.android.data.entity.FavoriteEntity;
import com.shootr.android.data.entity.LocalSynchronized;
import com.shootr.android.db.DatabaseContract.FavoriteTable;
import com.shootr.android.db.mappers.FavoriteEntityDBMapper;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class FavoriteManager extends AbstractManager {

    private final FavoriteEntityDBMapper favoriteEntityCursorMapper;

    @Inject
    public FavoriteManager(SQLiteOpenHelper dbHelper, FavoriteEntityDBMapper favoriteEntityCursorMapper) {
        super(dbHelper);
        this.favoriteEntityCursorMapper = favoriteEntityCursorMapper;
    }

    public FavoriteEntity saveFavorite(FavoriteEntity favoriteEntity) {
        getWritableDatabase().insertWithOnConflict(FavoriteTable.TABLE,
          null,
          favoriteEntityCursorMapper.toContentValues(favoriteEntity),
          SQLiteDatabase.CONFLICT_REPLACE);
        return favoriteEntity;
    }

    public FavoriteEntity getFavoriteByIdStream(String idStream) {
        String whereSelection = FavoriteTable.ID_STREAM + " = ? AND " + whereNotDeleted();
        String[] whereArgumens = new String[] { idStream };

        Cursor queryResult = getReadableDatabase().query(FavoriteTable.TABLE,
          FavoriteTable.PROJECTION,
          whereSelection,
          whereArgumens,
          null,
          null,
          null);

        FavoriteEntity result = null;
        if (queryResult.getCount() > 0) {
            queryResult.moveToFirst();
            result = favoriteEntityCursorMapper.fromCursor(queryResult);
        }
        queryResult.close();
        return result;
    }

    public List<FavoriteEntity> getFavorites() {
        Cursor queryResult = getReadableDatabase().query(FavoriteTable.TABLE,
          FavoriteTable.PROJECTION,
          whereNotDeleted(),
          null,
          null,
          null,
          null);

        List<FavoriteEntity> results = new ArrayList<>(queryResult.getCount());
        if (queryResult.getCount() > 0) {
            queryResult.moveToFirst();
            do {
                results.add(favoriteEntityCursorMapper.fromCursor(queryResult));
            } while (queryResult.moveToNext());
        }
        queryResult.close();
        return results;
    }

    public void deleteStreamByIdStream(String streamId) {
        String whereClause = FavoriteTable.ID_STREAM + " = ?";
        String[] whereArgs = new String[] { streamId };
        getWritableDatabase().delete(FavoriteTable.TABLE, whereClause, whereArgs);
    }

    public List<FavoriteEntity> getFavoritesNotSynchronized() {
        Cursor queryResult = getReadableDatabase().query(FavoriteTable.TABLE,
          FavoriteTable.PROJECTION,
          whereNotSynchronized(),
          null,
          null,
          null,
          null);

        List<FavoriteEntity> results = new ArrayList<>(queryResult.getCount());
        if (queryResult.getCount() > 0) {
            queryResult.moveToFirst();
            do {
                results.add(favoriteEntityCursorMapper.fromCursor(queryResult));
            } while (queryResult.moveToNext());
        }
        queryResult.close();
        return results;
    }

    protected String whereNotDeleted() {
        return FavoriteTable.SYNCHRONIZED + " <> '" + LocalSynchronized.SYNC_DELETED + "'";
    }

    private String whereNotSynchronized() {
        return FavoriteTable.SYNCHRONIZED + " <> '" + LocalSynchronized.SYNC_SYNCHRONIZED + "'";
    }

    public void deleteAll() {
        getWritableDatabase().delete(FavoriteTable.TABLE, null, null);
    }
}
