package com.shootr.android.db.mappers;

import android.content.ContentValues;
import android.database.Cursor;

import com.shootr.android.db.DatabaseContract;
import com.shootr.android.data.entity.DeviceEntity;
import java.util.HashMap;
import java.util.Map;

public class DeviceMapper extends GenericMapper {

    public DeviceEntity fromCursor(Cursor c) {
        DeviceEntity device = new DeviceEntity();
        device.setIdDevice(c.getLong(c.getColumnIndex(DatabaseContract.CreateDeviceTable.ID_DEVICE)));
        device.setIdUser(c.getLong(c.getColumnIndex(DatabaseContract.CreateDeviceTable.ID_USER)));
        device.setPlatform(c.getInt(c.getColumnIndex(DatabaseContract.CreateDeviceTable.PLATFORM)));
        device.setToken(c.getString(c.getColumnIndex(DatabaseContract.CreateDeviceTable.TOKEN)));
        device.setUniqueDevideID(c.getString(c.getColumnIndex(DatabaseContract.CreateDeviceTable.UNIQUE_DEVICE_ID)));
        device.setModel(c.getString(c.getColumnIndex(DatabaseContract.CreateDeviceTable.MODEL)));
        device.setOsVer(c.getString(c.getColumnIndex(DatabaseContract.CreateDeviceTable.OS_VERSION)));
        setSynchronizedfromCursor(c, device);
        return device;
    }

    public ContentValues toContentValues(DeviceEntity device) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseContract.CreateDeviceTable.ID_DEVICE, device.getIdDevice());
        cv.put(DatabaseContract.CreateDeviceTable.ID_USER, device.getIdUser());
        cv.put(DatabaseContract.CreateDeviceTable.PLATFORM, device.getPlatform());
        cv.put(DatabaseContract.CreateDeviceTable.TOKEN, device.getToken());
        cv.put(DatabaseContract.CreateDeviceTable.UNIQUE_DEVICE_ID, device.getUniqueDevideID());
        cv.put(DatabaseContract.CreateDeviceTable.MODEL, device.getModel());
        cv.put(DatabaseContract.CreateDeviceTable.OS_VERSION, device.getOsVer());
        setSynchronizedtoContentValues(device, cv);
        return cv;
    }

    public DeviceEntity fromDto(Map<String, Object> dto) {
        DeviceEntity device = new DeviceEntity();
        device.setIdDevice(((Number) dto.get(DatabaseContract.CreateDeviceTable.ID_DEVICE)).longValue());
        device.setIdUser(((Number) dto.get(DatabaseContract.CreateDeviceTable.ID_USER)).longValue());
        device.setPlatform(((Number) dto.get(DatabaseContract.CreateDeviceTable.PLATFORM)).intValue());
        device.setToken((String) dto.get(DatabaseContract.CreateDeviceTable.TOKEN));
        device.setUniqueDevideID((String) dto.get(DatabaseContract.CreateDeviceTable.UNIQUE_DEVICE_ID));
        device.setModel((String) dto.get(DatabaseContract.CreateDeviceTable.MODEL));
        device.setOsVer((String) dto.get(DatabaseContract.CreateDeviceTable.OS_VERSION));
        setSynchronizedfromDto(dto, device);
        return device;
    }

    public Map<String, Object> toDto(DeviceEntity device) {
        Map<String,Object> dto = new HashMap<>();
        dto.put(DatabaseContract.CreateDeviceTable.ID_DEVICE, device == null ? null : device.getIdDevice());
        dto.put(DatabaseContract.CreateDeviceTable.ID_USER, device == null ? null : device.getIdUser());
        dto.put(DatabaseContract.CreateDeviceTable.PLATFORM, device == null ? null : device.getPlatform());
        dto.put(DatabaseContract.CreateDeviceTable.TOKEN, device == null ? null : device.getToken());
        dto.put(DatabaseContract.CreateDeviceTable.UNIQUE_DEVICE_ID, device == null ? null : device.getUniqueDevideID());
        dto.put(DatabaseContract.CreateDeviceTable.MODEL, device == null ? null : device.getModel());
        dto.put(DatabaseContract.CreateDeviceTable.OS_VERSION, device == null ? null : device.getOsVer());
        setSynchronizedtoDto(device, dto);
        return dto;
    }
}
