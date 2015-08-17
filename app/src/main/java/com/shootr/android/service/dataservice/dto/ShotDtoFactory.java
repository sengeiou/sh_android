package com.shootr.android.service.dataservice.dto;

import com.shootr.android.constant.ServiceConstants;
import com.shootr.android.data.entity.ShotEntity;
import com.shootr.android.db.DatabaseContract;
import com.shootr.android.db.DatabaseContract.ShotTable;
import com.shootr.android.db.mappers.ShotEntityMapper;
import com.shootr.android.service.dataservice.generic.FilterDto;
import com.shootr.android.service.dataservice.generic.GenericDto;
import com.shootr.android.service.dataservice.generic.MetadataDto;
import com.shootr.android.service.dataservice.generic.OperationDto;
import java.util.List;
import javax.inject.Inject;

import static com.shootr.android.service.dataservice.generic.FilterBuilder.and;
import static com.shootr.android.service.dataservice.generic.FilterBuilder.or;

public class ShotDtoFactory {

    private static final String ALIAS_NEW_SHOT = "POST_NEW_SHOT";

    private UtilityDtoFactory utilityDtoFactory;
    ShotEntityMapper shotEntityMapper;

    @Inject public ShotDtoFactory(UtilityDtoFactory utilityDtoFactory, ShotEntityMapper shotEntityMapper) {
        this.utilityDtoFactory = utilityDtoFactory;
        this.shotEntityMapper = shotEntityMapper;
    }

    public GenericDto getNewShotOperationDto(ShotEntity shotTemplate) {
        MetadataDto md = new MetadataDto.Builder()
                .operation(ServiceConstants.OPERATION_CREATE)
                .entity(DatabaseContract.ShotTable.TABLE)
                .putKey(DatabaseContract.ShotTable.ID_SHOT, null)
                .build();

        OperationDto op = new OperationDto.Builder()
                .metadata(md)
                .putData(shotEntityMapper.toDto(shotTemplate))
                .build();

        return utilityDtoFactory.getGenericDtoFromOperation(ALIAS_NEW_SHOT, op);

    }
}
