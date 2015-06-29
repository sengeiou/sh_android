package com.shootr.android.db.manager;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.shootr.android.data.entity.DeviceEntity;
import com.shootr.android.db.DatabaseContract;
import com.shootr.android.db.DatabaseContract.DeviceTable;
import com.shootr.android.db.mappers.DeviceMapper;
import javax.inject.Inject;

public class DeviceManager extends AbstractManager {

    @Inject DeviceMapper deviceMapper;

    public static final String[] PROJECTION = {
      "idDevice", "idUser", "platform", "token", "uniqueDeviceID", "model", "osVer", "birth", "modified", "deleted",
      "revision", "synchronizedStatus"
    };

    @Inject
    public DeviceManager(SQLiteOpenHelper openHelper, DeviceMapper deviceMapper) {
        super(openHelper);
        this.deviceMapper = deviceMapper;
    }

    public DeviceEntity getDeviceByIdUser(String idUser) {
        String whereSelection = DatabaseContract.DeviceTable.ID_USER + " = ?";
        String[] whereArguments = new String[] { String.valueOf(idUser) };

        Cursor queryResult =
          getReadableDatabase().query(DeviceTable.TABLE, PROJECTION, whereSelection, whereArguments, null,
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
        if (contentValues.getAsLong(DatabaseContract.DeviceTable.DELETED) != null) {
            deleteDevice(device);
        } else {
            getWritableDatabase().insertWithOnConflict(DeviceTable.TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        }
        insertInSync();
    }

    private void deleteDevice(DeviceEntity device) {
        //TODO this
    }

    public DeviceEntity getDeviceById(Long idDevice) {
        //TODO rellenar
        return null;
    }

    public DeviceEntity getDeviceByUniqueId(String uniqueId) {
        return null;
    }

    public void insertInSync() {
        insertInTableSync(DeviceTable.TABLE, 4, 0, 0);
    }
}
