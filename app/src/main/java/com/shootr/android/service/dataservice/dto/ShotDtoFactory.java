package com.shootr.android.service.dataservice.dto;

import com.shootr.android.constant.ServiceConstants;
import com.shootr.android.db.DatabaseContract;
import com.shootr.android.db.DatabaseContract.ShotTable;
import com.shootr.android.db.mappers.ShotMapper;
import com.shootr.android.db.objects.ShotEntity;
import com.shootr.android.service.dataservice.generic.GenericDto;
import com.shootr.android.service.dataservice.generic.MetadataDto;
import com.shootr.android.service.dataservice.generic.OperationDto;
import javax.inject.Inject;

public class ShotDtoFactory {

    private static final String ALIAS_NEW_SHOT = "POST_NEW_SHOT";
    private static final String ALIAS_GET_SHOT = "GET_SHOT";

    private UtilityDtoFactory utilityDtoFactory;
    ShotMapper shotMapper;

    @Inject public ShotDtoFactory(UtilityDtoFactory utilityDtoFactory, ShotMapper shotMapper) {
        this.utilityDtoFactory = utilityDtoFactory;
        this.shotMapper = shotMapper;

    }

    public GenericDto getSingleShotOperationDto(Long idShot) {
        MetadataDto md = new MetadataDto.Builder()
                .operation(ServiceConstants.OPERATION_RETRIEVE)
                .entity(ShotTable.TABLE)
                .putKey(ShotTable.ID_SHOT, idShot)
                .build();

        OperationDto op = new OperationDto.Builder()
                .metadata(md)
                .putData(shotMapper.toDto(null))
                .build();

        return utilityDtoFactory.getGenericDtoFromOperation(ALIAS_GET_SHOT, op);
    }

    public GenericDto getNewShotOperationDto(Long idUser, String comment) {
        if (idUser == null) {
            throw new IllegalArgumentException("idUser must not be null");
        }
        if (idUser <= 0) {
            throw new IllegalArgumentException("idUser must be a positive number");
        }

        if (comment == null) {
            throw new IllegalArgumentException("comment must not be null");
        }

        if (comment.trim().length() == 0) {
            throw new IllegalArgumentException("comment cannot be empty");
        }

        MetadataDto md = new MetadataDto.Builder()
                .operation(ServiceConstants.OPERATION_CREATE)
                .entity(DatabaseContract.ShotTable.TABLE)
                .putKey(DatabaseContract.ShotTable.ID_SHOT, null)
                .build();

        ShotEntity shotTemplate = new ShotEntity();
        shotTemplate.setComment(comment);
        shotTemplate.setIdUser(idUser);

        OperationDto op = new OperationDto.Builder()
                .metadata(md)
                .putData(shotMapper.toDto(shotTemplate))
                .build();

        return utilityDtoFactory.getGenericDtoFromOperation(ALIAS_NEW_SHOT, op);

    }
}
