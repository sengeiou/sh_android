package com.shootr.android.db.mappers;

import android.content.ContentValues;
import android.database.Cursor;
import com.shootr.android.data.entity.DeviceEntity;
import com.shootr.android.db.DatabaseContract;
import java.util.HashMap;
import java.util.Map;

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

    public DeviceEntity fromDto(Map<String, Object> dto) {
        DeviceEntity device = new DeviceEntity();
        device.setIdDevice((String)((dto.get(DatabaseContract.DeviceTable.ID_DEVICE))));
        device.setPlatform(((Number) dto.get(DatabaseContract.DeviceTable.PLATFORM)).intValue());
        device.setToken((String) dto.get(DatabaseContract.DeviceTable.TOKEN));
        device.setUniqueDevideID((String) dto.get(DatabaseContract.DeviceTable.UNIQUE_DEVICE_ID));
        device.setModel((String) dto.get(DatabaseContract.DeviceTable.MODEL));
        device.setOsVer((String) dto.get(DatabaseContract.DeviceTable.OS_VERSION));
        device.setAppVer((String) dto.get(DatabaseContract.DeviceTable.APP_VERSION));
        device.setLocale((String) dto.get(DatabaseContract.DeviceTable.LOCALE));
        return device;
    }

    public Map<String, Object> toDto(DeviceEntity device) {
        Map<String, Object> dto = new HashMap<>();
        dto.put(DatabaseContract.DeviceTable.ID_DEVICE, device == null ? null : device.getIdDevice());
        dto.put(DatabaseContract.DeviceTable.PLATFORM, device == null ? null : device.getPlatform());
        dto.put(DatabaseContract.DeviceTable.TOKEN, device == null ? null : device.getToken());
        dto.put(DatabaseContract.DeviceTable.UNIQUE_DEVICE_ID, device == null ? null : device.getUniqueDevideID());
        dto.put(DatabaseContract.DeviceTable.MODEL, device == null ? null : device.getModel());
        dto.put(DatabaseContract.DeviceTable.OS_VERSION, device == null ? null : device.getOsVer());
        dto.put(DatabaseContract.DeviceTable.LOCALE, device == null ? null : device.getLocale());
        dto.put(DatabaseContract.DeviceTable.APP_VERSION, device == null ? null : device.getAppVer());
        return dto;
    }
}
