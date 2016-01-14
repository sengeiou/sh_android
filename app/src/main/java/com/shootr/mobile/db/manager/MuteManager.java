package com.shootr.mobile.db.manager;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.shootr.mobile.data.entity.MuteStreamEntity;
import com.shootr.mobile.db.DatabaseContract;
import com.shootr.mobile.db.mappers.MuteStreamEntityDBMapper;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class MuteManager extends AbstractManager {

    private static final String MUTE_TABLE = DatabaseContract.MuteTable.TABLE;
    private static final String ID_MUTED = DatabaseContract.MuteTable.ID_MUTED_STREAM;
    private final MuteStreamEntityDBMapper muteStreamEntityDBMapper;

    @Inject public MuteManager(SQLiteOpenHelper dbHelper, MuteStreamEntityDBMapper muteStreamEntityDBMapper) {
        super(dbHelper);
        this.muteStreamEntityDBMapper = muteStreamEntityDBMapper;
    }

    public void mute(MuteStreamEntity muteStreamEntity) {
        if(muteStreamEntity!=null){
            ContentValues contentValues = muteStreamEntityDBMapper.toContentValues(muteStreamEntity);
            getWritableDatabase().insertWithOnConflict(MUTE_TABLE,
              null,
              contentValues,
              SQLiteDatabase.CONFLICT_REPLACE);
        }
    }

    public void unmute(String idStream) {
        String whereClause = ID_MUTED + "=?";
        String[] whereArgs = new String[]{idStream};
        getWritableDatabase().delete(MUTE_TABLE, whereClause, whereArgs);
    }

    public List<MuteStreamEntity> getMutes() {
        List<MuteStreamEntity> muteStreamEntities = new ArrayList<>();
        String args = DatabaseContract.SyncColumns.SYNCHRONIZED+"='N' OR "+DatabaseContract.SyncColumns.SYNCHRONIZED+"= 'S' OR "+DatabaseContract.SyncColumns.SYNCHRONIZED+"='U'";
        Cursor c = getReadableDatabase().query(MUTE_TABLE, DatabaseContract.MuteTable.PROJECTION,args,null,null,null,null);
        if(c.getCount()>0){
            c.moveToFirst();
            do{
                muteStreamEntities.add(muteStreamEntityDBMapper.fromCursor(c));
            }while(c.moveToNext());
        }
        c.close();
        return muteStreamEntities;
    }

    public List<MuteStreamEntity> getMutesNotSynchronized() {
        List<MuteStreamEntity> mutesToUpdate = new ArrayList<>();
        String args = DatabaseContract.SyncColumns.SYNCHRONIZED+"='N' OR "+DatabaseContract.SyncColumns.SYNCHRONIZED+"= 'D' OR "+DatabaseContract.SyncColumns.SYNCHRONIZED+"='U'";
        Cursor c = getReadableDatabase().query(MUTE_TABLE, DatabaseContract.MuteTable.PROJECTION,args,null,null,null,null);
        if(c.getCount()>0){
            c.moveToFirst();
            do{
                mutesToUpdate.add(muteStreamEntityDBMapper.fromCursor(c));
            }while(c.moveToNext());
        }
        c.close();
        return mutesToUpdate;
    }

    public void saveMutesFromServer(List<MuteStreamEntity> muteStreamEntities) {
        SQLiteDatabase database = getWritableDatabase();
        try {
            database.beginTransaction();
            for (MuteStreamEntity muteStreamEntity : muteStreamEntities) {
                ContentValues contentValues = muteStreamEntityDBMapper.toContentValues(muteStreamEntity);
                database.insertWithOnConflict(MUTE_TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
            }
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

    public MuteStreamEntity getMute(String idStream) {
        String where = DatabaseContract.MuteTable.ID_MUTED_STREAM + "= ?";
        String[] whereArguments = new String[] { idStream };

        return readMute(where, whereArguments);
    }

    private MuteStreamEntity readMute(String whereClause, String[] whereArguments) {
        MuteStreamEntity muteStreamEntity = null;
        Cursor c = getReadableDatabase().query(DatabaseContract.MuteTable.TABLE,
          DatabaseContract.MuteTable.PROJECTION,
          whereClause,
          whereArguments,
          null,
          null,
          null,
          null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            muteStreamEntity = muteStreamEntityDBMapper.fromCursor(c);
        }
        c.close();
        return muteStreamEntity;
    }
}
