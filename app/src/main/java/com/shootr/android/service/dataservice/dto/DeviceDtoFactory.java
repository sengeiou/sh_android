package com.shootr.android.service.dataservice.dto;

import com.shootr.android.constant.ServiceConstants;
import com.shootr.android.db.DatabaseContract.CreateDeviceTable;
import com.shootr.android.db.mappers.DeviceMapper;
import com.shootr.android.data.entity.DeviceEntity;
import com.shootr.android.service.dataservice.generic.GenericDto;
import com.shootr.android.service.dataservice.generic.MetadataDto;
import com.shootr.android.service.dataservice.generic.OperationDto;
import javax.inject.Inject;

import static com.shootr.android.service.dataservice.generic.FilterBuilder.and;

public class DeviceDtoFactory {

    private static final String ALIAS_UPDATE_DEVICE = "UPDATE_DEVICE";

    private DeviceMapper deviceMapper;
    private UtilityDtoFactory utilityDtoFactory;

    @Inject public DeviceDtoFactory(DeviceMapper deviceMapper, UtilityDtoFactory utilityDtoFactory) {
        this.deviceMapper = deviceMapper;
        this.utilityDtoFactory = utilityDtoFactory;
    }

    public GenericDto getUpdateDeviceOperationDto(DeviceEntity device) {
        MetadataDto md = new MetadataDto.Builder()
          .operation(ServiceConstants.OPERATION_UPDATE_CREATE)
          .entity(CreateDeviceTable.TABLE)
          .putKey(CreateDeviceTable.UNIQUE_DEVICE_ID, device.getUniqueDevideID())
          .build();

        OperationDto op = new OperationDto.Builder()
          .metadata(md)
          .putData(deviceMapper.toDto(device))
          .build();

        return utilityDtoFactory.getGenericDtoFromOperation(ALIAS_UPDATE_DEVICE, op);
    }

}
