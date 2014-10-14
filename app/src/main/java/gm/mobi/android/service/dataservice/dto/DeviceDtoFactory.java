package gm.mobi.android.service.dataservice.dto;

import gm.mobi.android.constant.ServiceConstants;
import gm.mobi.android.db.GMContract;
import gm.mobi.android.db.GMContract.DeviceTable;
import gm.mobi.android.db.mappers.DeviceMapper;
import gm.mobi.android.db.objects.Device;
import gm.mobi.android.service.dataservice.generic.FilterBuilder;
import gm.mobi.android.service.dataservice.generic.FilterDto;
import gm.mobi.android.service.dataservice.generic.GenericDto;
import gm.mobi.android.service.dataservice.generic.MetadataDto;
import gm.mobi.android.service.dataservice.generic.OperationDto;
import java.util.logging.Filter;
import javax.inject.Inject;

import static gm.mobi.android.service.dataservice.generic.FilterBuilder.and;

public class DeviceDtoFactory {

    private static final String ALIAS_UPDATE_DEVICE = "UPDATE_DEVICE";

    private DeviceMapper deviceMapper;
    private UtilityDtoFactory utilityDtoFactory;

    @Inject public DeviceDtoFactory(DeviceMapper deviceMapper, UtilityDtoFactory utilityDtoFactory) {
        this.deviceMapper = deviceMapper;
        this.utilityDtoFactory = utilityDtoFactory;
    }

    public GenericDto getUpdateDeviceOperationDto(Device device) {
        FilterDto filter = and(DeviceTable.ID_DEVICE).isEqualTo(null)
          .and(DeviceTable.ID_USER).isEqualTo(device.getIdUser())
          .build();

        MetadataDto md = new MetadataDto.Builder()
          .operation(ServiceConstants.OPERATION_UPDATE_CREATE)
          .entity(DeviceTable.TABLE)
          .putKey(DeviceTable.ID_DEVICE, null)
          .build();

        OperationDto op = new OperationDto.Builder()
          .metadata(md)
          .putData(deviceMapper.toDto(device))
          .build();

        return utilityDtoFactory.getGenericDtoFromOperation(ALIAS_UPDATE_DEVICE, op);
    }

}
