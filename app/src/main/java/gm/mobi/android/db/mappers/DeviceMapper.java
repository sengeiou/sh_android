package gm.mobi.android.db.mappers;

import android.content.ContentValues;
import android.database.Cursor;
import gm.mobi.android.db.GMContract;
import gm.mobi.android.db.objects.DeviceEntity;
import java.util.HashMap;
import java.util.Map;

public class DeviceMapper extends GenericMapper {

    public DeviceEntity fromCursor(Cursor c) {
        DeviceEntity device = new DeviceEntity();
        device.setIdDevice(c.getLong(c.getColumnIndex(GMContract.DeviceTable.ID_DEVICE)));
        device.setIdUser(c.getLong(c.getColumnIndex(GMContract.DeviceTable.ID_USER)));
        device.setPlatform(c.getInt(c.getColumnIndex(GMContract.DeviceTable.PLATFORM)));
        device.setToken(c.getString(c.getColumnIndex(GMContract.DeviceTable.TOKEN)));
        device.setUniqueDevideID(c.getString(c.getColumnIndex(GMContract.DeviceTable.UNIQUE_DEVICE_ID)));
        device.setModel(c.getString(c.getColumnIndex(GMContract.DeviceTable.MODEL)));
        device.setOsVer(c.getString(c.getColumnIndex(GMContract.DeviceTable.OS_VERSION)));
        setSynchronizedfromCursor(c, device);
        return device;
    }

    public ContentValues toContentValues(DeviceEntity device) {
        ContentValues cv = new ContentValues();
        cv.put(GMContract.DeviceTable.ID_DEVICE, device.getIdDevice());
        cv.put(GMContract.DeviceTable.ID_USER, device.getIdUser());
        cv.put(GMContract.DeviceTable.PLATFORM, device.getPlatform());
        cv.put(GMContract.DeviceTable.TOKEN, device.getToken());
        cv.put(GMContract.DeviceTable.UNIQUE_DEVICE_ID, device.getUniqueDevideID());
        cv.put(GMContract.DeviceTable.MODEL, device.getModel());
        cv.put(GMContract.DeviceTable.OS_VERSION, device.getOsVer());
        setSynchronizedtoContentValues(device, cv);
        return cv;
    }

    public DeviceEntity fromDto(Map<String, Object> dto) {
        DeviceEntity device = new DeviceEntity();
        device.setIdDevice(((Number) dto.get(GMContract.DeviceTable.ID_DEVICE)).longValue());
        device.setIdUser(((Number) dto.get(GMContract.DeviceTable.ID_USER)).longValue());
        device.setPlatform(((Number) dto.get(GMContract.DeviceTable.PLATFORM)).intValue());
        device.setToken((String) dto.get(GMContract.DeviceTable.TOKEN));
        device.setUniqueDevideID((String) dto.get(GMContract.DeviceTable.UNIQUE_DEVICE_ID));
        device.setModel((String) dto.get(GMContract.DeviceTable.MODEL));
        device.setOsVer((String) dto.get(GMContract.DeviceTable.OS_VERSION));
        setSynchronizedfromDto(dto, device);
        return device;
    }

    public Map<String, Object> toDto(DeviceEntity device) {
        Map<String,Object> dto = new HashMap<>();
        dto.put(GMContract.DeviceTable.ID_DEVICE, device == null ? null : device.getIdDevice());
        dto.put(GMContract.DeviceTable.ID_USER, device == null ? null : device.getIdUser());
        dto.put(GMContract.DeviceTable.PLATFORM, device == null ? null : device.getPlatform());
        dto.put(GMContract.DeviceTable.TOKEN, device == null ? null : device.getToken());
        dto.put(GMContract.DeviceTable.UNIQUE_DEVICE_ID, device == null ? null : device.getUniqueDevideID());
        dto.put(GMContract.DeviceTable.MODEL, device == null ? null : device.getModel());
        dto.put(GMContract.DeviceTable.OS_VERSION, device == null ? null : device.getOsVer());
        setSynchronizedtoDto(device, dto);
        return dto;
    }
}
