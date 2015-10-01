package com.shootr.android.db.manager;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.shootr.android.data.entity.DeviceEntity;
import com.shootr.android.db.DatabaseContract.DeviceTable;
import com.shootr.android.db.mappers.DeviceEntityDBMapper;
import javax.inject.Inject;

public class DeviceManager extends AbstractManager {

    @Inject DeviceEntityDBMapper deviceMapper;

    @Inject
    public DeviceManager(SQLiteOpenHelper openHelper, DeviceEntityDBMapper deviceMapper) {
        super(openHelper);
        this.deviceMapper = deviceMapper;
    }

    public DeviceEntity getDevice() {
        Cursor queryResult =
          getReadableDatabase().query(DeviceTable.TABLE, DeviceTable.PROJECTION, null, null, null,
            null, null);

        DeviceEntity deviceEntity = null;
        if (queryResult.getCount() > 0) {
            queryResult.moveToFirst();
            deviceEntity = deviceMapper.fromCursor(queryResult);
        }
        queryResult.close();
        return deviceEntity;
    }

    public void saveDevice(DeviceEntity device) {
        ContentValues contentValues = deviceMapper.toContentValues(device);
        getWritableDatabase().insertWithOnConflict(DeviceTable.TABLE,
          null,
          contentValues,
          SQLiteDatabase.CONFLICT_REPLACE);
    }
}
