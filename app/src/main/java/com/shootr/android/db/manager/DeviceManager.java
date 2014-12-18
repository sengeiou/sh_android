package com.shootr.android.db.manager;

import android.database.sqlite.SQLiteOpenHelper;
import com.shootr.android.db.DatabaseContract.DeviceTable;
import com.shootr.android.db.mappers.DeviceMapper;
import com.shootr.android.domain.DeviceEntity;
import javax.inject.Inject;

public class DeviceManager extends AbstractManager {

    @Inject DeviceMapper deviceMapper;

    @Inject
    public DeviceManager(SQLiteOpenHelper openHelper, DeviceMapper deviceMapper) {
        super(openHelper);
        this.deviceMapper = deviceMapper;
    }

    public void saveDevice(DeviceEntity device) {
        //TODO rellenar
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
