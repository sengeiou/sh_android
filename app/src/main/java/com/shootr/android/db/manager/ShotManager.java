package com.shootr.android.db.manager;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.shootr.android.data.entity.ShotEntity;
import com.shootr.android.db.DatabaseContract;
import com.shootr.android.db.DatabaseContract.ShotTable;
import com.shootr.android.db.mappers.ShotEntityMapper;
import com.shootr.android.db.mappers.UserMapper;
import com.shootr.android.domain.ActivityTimelineParameters;
import com.shootr.android.domain.EventTimelineParameters;
import com.shootr.android.domain.ShotType;
import com.shootr.android.ui.model.mappers.ShotEntityModelMapper;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class ShotManager extends  AbstractManager{

    @Inject ShotEntityMapper shotEntityMapper;
    @Inject UserMapper userMapper;
    @Inject ShotEntityModelMapper shotVOMapper;

    private static final String SHOT_TABLE = ShotTable.TABLE;
    private static final String CSYS_DELETED = DatabaseContract.SyncColumns.CSYS_DELETED;
    private static final String CSYS_BIRTH = DatabaseContract.SyncColumns.CSYS_BIRTH;

    @Inject
    public ShotManager(SQLiteOpenHelper openHelper, ShotEntityMapper shotEntityMapper, UserMapper userMapper, ShotEntityModelMapper shotVOMapper){
        super(openHelper);
        this.shotEntityMapper = shotEntityMapper;
        this.userMapper = userMapper;
        this.shotVOMapper = shotVOMapper;
    }

    /**
     * Insert a Shot
     */
    public void saveShot(ShotEntity shot) throws SQLException {
        ContentValues contentValues = shotEntityMapper.toContentValues(shot);
        if (contentValues.getAsLong(CSYS_DELETED) != null) {
            deleteShot(shot);
        } else {
            getWritableDatabase().insertWithOnConflict(SHOT_TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        }
        insertInSync();
    }

    public List<ShotEntity> getLatestShotsFromIdUser(String idUser, Long latestShotsNumber) {
        List<ShotEntity> latestShots = new ArrayList<>();
        String whereSelection = ShotTable.ID_USER + " = ?";
        String[] whereArguments = new String[]{String.valueOf(idUser)};

        Cursor queryResult =
          getReadableDatabase().query(ShotTable.TABLE,
            ShotTable.PROJECTION,
            whereSelection,
            whereArguments,
            null,
            null,
            ShotTable.CSYS_BIRTH + " DESC",
            String.valueOf(latestShotsNumber));

        ShotEntity shotEntity;
        if (queryResult.getCount() > 0) {
            queryResult.moveToFirst();
            do {
                shotEntity = shotEntityMapper.fromCursor(queryResult);
                latestShots.add(shotEntity);
            } while (queryResult.moveToNext());
        }
        queryResult.close();
        return latestShots;
    }

    /**
     * Insert a shot list
     */
    public void saveShots(List<ShotEntity> shotList) {
        SQLiteDatabase database = getWritableDatabase();
        for (ShotEntity shot : shotList) {
            if (shot.getCsysDeleted() != null) {
                deleteShot(shot);
            } else {
                ContentValues contentValues = shotEntityMapper.toContentValues(shot);
                database.insertWithOnConflict(ShotTable.TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
            }
        }
    }

    /**
     * Delete a shot
     */
    public long deleteShot(ShotEntity shot) {
        long res = 0;
        String args = DatabaseContract.ShotTable.ID_SHOT + "=?";
        String[] stringArgs = new String[]{String.valueOf(shot.getIdShot())};
        Cursor c = getReadableDatabase().query(SHOT_TABLE, DatabaseContract.ShotTable.PROJECTION, args, stringArgs, null, null, null);
        if (c.getCount() > 0) {
            res = getWritableDatabase().delete(SHOT_TABLE, DatabaseContract.ShotTable.ID_SHOT, new String[]{String.valueOf(shot.getIdShot())});
        }
        c.close();
        return res;
    }

    public void insertInSync(){
        insertInTableSync(SHOT_TABLE, 3, 1000, 0);
    }

    public List<ShotEntity> getShotsByActivityParameters(ActivityTimelineParameters parameters) {
        List<String> userIds = parameters.getUserIds();
        List<String> includedTypes = parameters.getIncludedTypes();

        String usersSelection = ShotTable.ID_USER + " IN (" + createListPlaceholders(userIds.size()) + ")";
        String typeSelection = ShotTable.TYPE + " IN ("+ createListPlaceholders(includedTypes.size()) +")";
        String rootTypeSelection = ShotTable.ROOT_TYPE + " <> ?";
        //TODO since & max
        //TODO limit

        int whereArgumentsSize = userIds.size() + includedTypes.size() + 1;
        String[] whereArguments = new String[whereArgumentsSize];
        for (int i = 0; i < userIds.size(); i++) {
            whereArguments[i] = String.valueOf(userIds.get(i));
        }
        int typeArgumentStartIndex = userIds.size();
        for (int i = 0; i < includedTypes.size(); i++) {
            whereArguments[typeArgumentStartIndex + i] = includedTypes.get(i);
        }
        int rootTypeArgumentIndex = typeArgumentStartIndex + includedTypes.size();
        whereArguments[rootTypeArgumentIndex] = parameters.getExcludedRootType();

        String whereClause = usersSelection + " AND " + typeSelection + " AND " + rootTypeSelection;

        Cursor queryResult =
          getReadableDatabase().query(ShotTable.TABLE, ShotTable.PROJECTION, whereClause, whereArguments, null, null,
            ShotTable.CSYS_BIRTH+" DESC");

        List<ShotEntity> resultShots = new ArrayList<>(queryResult.getCount());
        ShotEntity shotEntity;
        if (queryResult.getCount() > 0) {
            queryResult.moveToFirst();
            do {
                shotEntity = shotEntityMapper.fromCursor(queryResult);
                resultShots.add(shotEntity);
            } while (queryResult.moveToNext());
        }
        queryResult.close();
        return resultShots;
    }

    public List<ShotEntity> getShotsByEventParameters(EventTimelineParameters parameters) {
        List<String> userIds = parameters.getUserIds();
        String usersSelection = ShotTable.ID_USER + " IN (" + createListPlaceholders(userIds.size()) + ")";
        String eventSelection = ShotTable.ID_EVENT + " = ?";
        String rootTypeSelection = ShotTable.ROOT_TYPE + " = ?";
        //TODO since & max
        //TODO limit

        String[] whereArguments = new String[userIds.size()+2];
        for (int i = 0; i < userIds.size(); i++) {
            whereArguments[i] = String.valueOf(userIds.get(i));
        }
        whereArguments[userIds.size()] = String.valueOf(parameters.getEventId());
        whereArguments[userIds.size()+1] = String.valueOf(parameters.getShotRootType());
        String whereClause = usersSelection + " AND " + eventSelection + " AND " + rootTypeSelection;

        Cursor queryResult =
          getReadableDatabase().query(ShotTable.TABLE, ShotTable.PROJECTION, whereClause, whereArguments, null, null,
            ShotTable.CSYS_BIRTH+" DESC");

        List<ShotEntity> resultShots = new ArrayList<>(queryResult.getCount());
        ShotEntity shotEntity;
        if (queryResult.getCount() > 0) {
            queryResult.moveToFirst();
            do {
                shotEntity = shotEntityMapper.fromCursor(queryResult);
                resultShots.add(shotEntity);
            } while (queryResult.moveToNext());
        }
        queryResult.close();
        return resultShots;
    }

    public ShotEntity getShotById(String idShot) {
        String whereClause = ShotTable.ID_SHOT + " = ?";
        String[] whereArguments = new String[]{String.valueOf(idShot)};

        Cursor queryResult = getReadableDatabase().query(ShotTable.TABLE,
          ShotTable.PROJECTION,
          whereClause,
          whereArguments,
          null,
          null,
          null);

        ShotEntity shotEntity = null;
        if (queryResult.getCount() > 0) {
            queryResult.moveToFirst();
            shotEntity = shotEntityMapper.fromCursor(queryResult);
        }
        queryResult.close();
        return shotEntity;
    }

    public List<ShotEntity> getRepliesTo(String shotParentId) {
        String whereClause = ShotTable.ID_SHOT_PARENT + " = ?";
        String[] whereArguments = new String[]{shotParentId};

        Cursor queryResult = getReadableDatabase().query(ShotTable.TABLE,
          ShotTable.PROJECTION,
          whereClause,
          whereArguments,
          null,
          null,
          null);

        List<ShotEntity> resultShots = new ArrayList<>(queryResult.getCount());
        ShotEntity shotEntity;
        if (queryResult.getCount() > 0) {
            queryResult.moveToFirst();
            do {
                shotEntity = shotEntityMapper.fromCursor(queryResult);
                resultShots.add(shotEntity);
            } while (queryResult.moveToNext());
        }
        queryResult.close();
        return resultShots;
    }

    public Long getLastModifiedDateForEvent(String eventId) {
        String eventIdClause = ShotTable.ID_EVENT + " = ?";
        String commentTypeOnlyClause = ShotTable.ROOT_TYPE + " = '" + ShotType.COMMENT + "'";

        String whereClause = eventIdClause + " AND " + commentTypeOnlyClause;
        String[] whereArguments = new String[]{String.valueOf(eventId)};
        String order = ShotTable.CSYS_MODIFIED + " desc";

        Cursor queryResult = getReadableDatabase().query(ShotTable.TABLE, ShotTable.PROJECTION, whereClause, whereArguments, null, null, order, "1");

        if (queryResult.getCount() > 0) {
            queryResult.moveToFirst();
            ShotEntity lastShot = shotEntityMapper.fromCursor(queryResult);
            return lastShot.getCsysModified().getTime();
        } else {
            return 0L;
        }
    }

    public Long getLastModifiedDateForActivity() {
        String whereClause = ShotTable.ROOT_TYPE + " <> " + ShotType.COMMENT;
        String order = ShotTable.CSYS_MODIFIED + " desc";

        Cursor queryResult =
          getReadableDatabase().query(ShotTable.TABLE, ShotTable.PROJECTION, whereClause, null, null, null, order, "1");

        if (queryResult.getCount() > 0) {
            queryResult.moveToFirst();
            ShotEntity lastShot = shotEntityMapper.fromCursor(queryResult);
            return lastShot.getCsysModified().getTime();
        } else {
            return 0L;
        }
    }

    public Integer getEventMediaShotsCount(String idEvent, List<String> idUsers) {
        String usersSelection = ShotTable.ID_USER + " IN (" + createListPlaceholders(idUsers.size()) + ")";
        String eventSelection = ShotTable.ID_EVENT + " = ?";
        String imageSelection = ShotTable.IMAGE + " IS NOT NULL ";

        String[] whereArguments = new String[idUsers.size()+1];

        for (int i = 0; i < idUsers.size(); i++) {
            whereArguments[i] = String.valueOf(idUsers.get(i));
        }

        whereArguments[idUsers.size()] = String.valueOf(idEvent);

        String whereClause = usersSelection + " AND " + eventSelection + " AND " + imageSelection;

        Cursor queryResult =
          getReadableDatabase().query(ShotTable.TABLE, ShotTable.PROJECTION, whereClause, whereArguments, null, null,
            ShotTable.CSYS_BIRTH+" DESC");

        List<ShotEntity> resultShots = new ArrayList<>(queryResult.getCount());
        ShotEntity shotEntity;
        if (queryResult.getCount() > 0) {
            queryResult.moveToFirst();
            do {
                shotEntity = shotEntityMapper.fromCursor(queryResult);
                resultShots.add(shotEntity);
            } while (queryResult.moveToNext());
        }
        queryResult.close();

        return resultShots.size();
    }

    public List<ShotEntity> getEventMediaShots(String idEvent, List<String> idUsers) {
        String usersSelection = ShotTable.ID_USER + " IN (" + createListPlaceholders(idUsers.size()) + ")";
        String eventSelection = ShotTable.ID_EVENT + " = ?";
        String imageSelection = ShotTable.IMAGE + " IS NOT NULL ";

        String[] whereArguments = new String[idUsers.size()+1];

        for (int i = 0; i < idUsers.size(); i++) {
            whereArguments[i] = String.valueOf(idUsers.get(i));
        }

        whereArguments[idUsers.size()] = String.valueOf(idEvent);

        String whereClause = usersSelection + " AND " + eventSelection + " AND " + imageSelection;

        Cursor queryResult =
          getReadableDatabase().query(ShotTable.TABLE, ShotTable.PROJECTION, whereClause, whereArguments, null, null,
            ShotTable.CSYS_BIRTH+" DESC");

        List<ShotEntity> resultShots = new ArrayList<>(queryResult.getCount());
        ShotEntity shotEntity;
        if (queryResult.getCount() > 0) {
            queryResult.moveToFirst();
            do {
                shotEntity = shotEntityMapper.fromCursor(queryResult);
                resultShots.add(shotEntity);
            } while (queryResult.moveToNext());
        }
        queryResult.close();

        return resultShots;
    }
}
