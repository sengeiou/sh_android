package com.shootr.mobile.db.manager;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.shootr.mobile.data.entity.FavoriteEntity;
import com.shootr.mobile.data.entity.LocalSynchronized;
import com.shootr.mobile.db.DatabaseContract;
import com.shootr.mobile.db.mappers.FavoriteEntityDBMapper;
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
        getWritableDatabase().insertWithOnConflict(DatabaseContract.FavoriteTable.TABLE,
          null,
          favoriteEntityCursorMapper.toContentValues(favoriteEntity),
          SQLiteDatabase.CONFLICT_REPLACE);
        return favoriteEntity;
    }

    public FavoriteEntity getFavoriteByIdStream(String idStream) {
        String whereSelection = DatabaseContract.FavoriteTable.ID_STREAM + " = ? AND " + whereNotDeleted();
        String[] whereArgumens = new String[] { idStream };

        Cursor queryResult = getReadableDatabase().query(DatabaseContract.FavoriteTable.TABLE,
          DatabaseContract.FavoriteTable.PROJECTION,
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
        Cursor queryResult = getReadableDatabase().query(DatabaseContract.FavoriteTable.TABLE,
          DatabaseContract.FavoriteTable.PROJECTION,
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
        String whereClause = DatabaseContract.FavoriteTable.ID_STREAM + " = ?";
        String[] whereArgs = new String[] { streamId };
        getWritableDatabase().delete(DatabaseContract.FavoriteTable.TABLE, whereClause, whereArgs);
    }

    public List<FavoriteEntity> getFavoritesNotSynchronized() {
        Cursor queryResult = getReadableDatabase().query(DatabaseContract.FavoriteTable.TABLE,
          DatabaseContract.FavoriteTable.PROJECTION,
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
        return DatabaseContract.FavoriteTable.SYNCHRONIZED + " <> '" + LocalSynchronized.SYNC_DELETED + "'";
    }

    private String whereNotSynchronized() {
        return DatabaseContract.FavoriteTable.SYNCHRONIZED + " <> '" + LocalSynchronized.SYNC_SYNCHRONIZED + "'";
    }

    public void deleteAll() {
        getWritableDatabase().delete(DatabaseContract.FavoriteTable.TABLE, null, null);
    }
}
