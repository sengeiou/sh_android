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
    private static final String ALIAS_GET_MEDIA = "GET_MEDIA_SHOTS_FOR_STREAM";

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

    public GenericDto getMediaShotsCountByStream(String idStream, List<String> idUsers) {
        FilterDto streamsFilter = and(
          or(ShotTable.ID_USER).isIn(idUsers)) //
          .and(ShotTable.ID_STREAM).isEqualTo(idStream) //
          .and(ShotTable.IMAGE).isNotEqualTo(null) //
          .and(ShotTable.DELETED).isEqualTo(null) //
          .build();

        MetadataDto md = new MetadataDto.Builder() //
          .operation(ServiceConstants.OPERATION_RETRIEVE)
          .entity(ShotTable.TABLE)
          .filter(streamsFilter)
          .items(0)
          .build();

        OperationDto op = new OperationDto.Builder() //
          .metadata(md) //
          .putData(shotEntityMapper.toDto(null)) //
          .build();

        return utilityDtoFactory.getGenericDtoFromOperation(ALIAS_GET_MEDIA, op);
    }
}
