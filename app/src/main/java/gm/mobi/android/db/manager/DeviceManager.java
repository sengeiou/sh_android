package gm.mobi.android.db.manager;

import gm.mobi.android.db.GMContract.DeviceTable;
import gm.mobi.android.db.mappers.DeviceMapper;
import gm.mobi.android.db.objects.Device;
import javax.inject.Inject;

public class DeviceManager extends AbstractManager {

    @Inject DeviceMapper deviceMapper;

    @Inject
    public DeviceManager(DeviceMapper deviceMapper) {
        this.deviceMapper = deviceMapper;
    }

    public void saveDevice(Device device) {
        //TODO rellenar
    }

    public Device getDeviceById(Long idDevice) {
        //TODO rellenar
        return null;
    }

    public Device getDeviceByUniqueId(String uniqueId) {
        return null;
    }

    public void insertInSync() {
        insertInTableSync(DeviceTable.TABLE, 4, 0, 0);
    }
}
