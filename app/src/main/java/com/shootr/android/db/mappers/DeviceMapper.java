package com.shootr.android.db.mappers;

import android.content.ContentValues;
import android.database.Cursor;
import com.shootr.android.data.entity.DeviceEntity;
import com.shootr.android.db.DatabaseContract;

public class DeviceMapper extends GenericMapper {

    public DeviceEntity fromCursor(Cursor c) {
        DeviceEntity device = new DeviceEntity();
        device.setIdDevice(c.getString(c.getColumnIndex(DatabaseContract.DeviceTable.ID_DEVICE)));
        device.setPlatform(c.getInt(c.getColumnIndex(DatabaseContract.DeviceTable.PLATFORM)));
        device.setToken(c.getString(c.getColumnIndex(DatabaseContract.DeviceTable.TOKEN)));
        device.setUniqueDevideID(c.getString(c.getColumnIndex(DatabaseContract.DeviceTable.UNIQUE_DEVICE_ID)));
        device.setModel(c.getString(c.getColumnIndex(DatabaseContract.DeviceTable.MODEL)));
        device.setOsVer(c.getString(c.getColumnIndex(DatabaseContract.DeviceTable.OS_VERSION)));
        device.setAppVer(c.getString(c.getColumnIndex(DatabaseContract.DeviceTable.APP_VERSION)));
        device.setLocale(c.getString(c.getColumnIndex(DatabaseContract.DeviceTable.LOCALE)));
        return device;
    }

    public ContentValues toContentValues(DeviceEntity device) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseContract.DeviceTable.ID_DEVICE, device.getIdDevice());
        cv.put(DatabaseContract.DeviceTable.PLATFORM, device.getPlatform());
        cv.put(DatabaseContract.DeviceTable.TOKEN, device.getToken());
        cv.put(DatabaseContract.DeviceTable.UNIQUE_DEVICE_ID, device.getUniqueDevideID());
        cv.put(DatabaseContract.DeviceTable.MODEL, device.getModel());
        cv.put(DatabaseContract.DeviceTable.OS_VERSION, device.getOsVer());
        cv.put(DatabaseContract.DeviceTable.APP_VERSION, device.getAppVer());
        cv.put(DatabaseContract.DeviceTable.LOCALE, device.getLocale());
        return cv;
    }
}
