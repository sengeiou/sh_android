package gm.mobi.android.db.manager;

import gm.mobi.android.db.DatabaseContract.DeviceTable;
import gm.mobi.android.db.mappers.DeviceMapper;
import gm.mobi.android.db.objects.DeviceEntity;
import javax.inject.Inject;

public class DeviceManager extends AbstractManager {

    @Inject DeviceMapper deviceMapper;

    @Inject
    public DeviceManager(DeviceMapper deviceMapper) {
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
