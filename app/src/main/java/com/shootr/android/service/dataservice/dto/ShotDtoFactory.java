package com.shootr.android.service.dataservice.dto;

import com.shootr.android.constant.ServiceConstants;
import com.shootr.android.db.DatabaseContract;
import com.shootr.android.db.DatabaseContract.ShotTable;
import com.shootr.android.db.mappers.ShotEntityMapper;
import com.shootr.android.data.entity.ShotEntity;
import com.shootr.android.service.dataservice.generic.FilterDto;
import com.shootr.android.service.dataservice.generic.GenericDto;
import com.shootr.android.service.dataservice.generic.MetadataDto;
import com.shootr.android.service.dataservice.generic.OperationDto;
import javax.inject.Inject;

import static com.shootr.android.service.dataservice.generic.FilterBuilder.and;
import static com.shootr.android.service.dataservice.generic.FilterBuilder.or;

public class ShotDtoFactory {

    private static final String ALIAS_NEW_SHOT = "POST_NEW_SHOT";
    private static final String ALIAS_GET_SHOT = "GET_SHOT";
    private static final String ALIAS_GET_LATEST_SHOTS = "GET_LATEST_SHOTS";
    private static final String ALIAS_GET_REPLIES = "GET_REPLIES_OF_SHOT";
    private static final int REPLIES_MAX_ITEMS = 50;

    private UtilityDtoFactory utilityDtoFactory;
    ShotEntityMapper shotEntityMapper;

    @Inject public ShotDtoFactory(UtilityDtoFactory utilityDtoFactory, ShotEntityMapper shotEntityMapper) {
        this.utilityDtoFactory = utilityDtoFactory;
        this.shotEntityMapper = shotEntityMapper;

    }

    public GenericDto getSingleShotOperationDto(Long idShot) {
        MetadataDto md = new MetadataDto.Builder()
                .operation(ServiceConstants.OPERATION_RETRIEVE)
                .entity(ShotTable.TABLE)
                .putKey(ShotTable.ID_SHOT, idShot)
                .build();

        OperationDto op = new OperationDto.Builder()
                .metadata(md)
                .putData(shotEntityMapper.toDto(null))
                .build();

        return utilityDtoFactory.getGenericDtoFromOperation(ALIAS_GET_SHOT, op);
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

    public GenericDto getLatestShotsFromIdUser(Long idUser, Long latestShotNumber) {
        if (idUser == null) {
            throw new IllegalArgumentException("idUser must not be null");
        }

        FilterDto shotsFilter = and(or(ShotTable.ID_USER).isEqualTo(idUser))
          .and(ShotTable.CSYS_DELETED).isEqualTo(null)
          .and(ShotTable.CSYS_MODIFIED).greaterThan(0L).build();

        MetadataDto md = new MetadataDto.Builder().operation(ServiceConstants.OPERATION_RETRIEVE)
          .entity(ShotTable.TABLE)
          .filter(shotsFilter).items(latestShotNumber).build();

        OperationDto op = new OperationDto.Builder().metadata(md).putData(shotEntityMapper.toDto(null)).build();

        return utilityDtoFactory.getGenericDtoFromOperation(ALIAS_GET_LATEST_SHOTS, op);
    }

    public GenericDto getRepliesOperationDto(Long shotId) {
        FilterDto repliesFilter = and(ShotTable.ID_SHOT_PARENT).isEqualTo(shotId) //
          .and(ShotTable.CSYS_MODIFIED).lessOrEqualThan(futureModifiedTimeToSkipServerCache()) // //TODO se podria optimizar usando el modified del timeline, pero de momento no me fio, hay que pensarlo bien
          .build();

        MetadataDto md = new MetadataDto.Builder() //
          .operation(ServiceConstants.OPERATION_RETRIEVE)
          .entity(ShotTable.TABLE)
          .filter(repliesFilter)
          .items(REPLIES_MAX_ITEMS)
          .build();

        OperationDto op = new OperationDto.Builder() //
          .metadata(md) //
          .putData(shotEntityMapper.toDto(null)) //
          .build();

        return utilityDtoFactory.getGenericDtoFromOperation(ALIAS_GET_REPLIES, op);
    }

    /**
     * This method returns a different modified time each time it's called, so that we skip server's cache for the
     * replies query.
     *
     * This is a temporarily (REALLY, I MEAN IT) solution to a problem we are having where requesting replies after
     * publishing a new one doesn't return it back.
     *
     * returns timestamp for 1 day into the future
     */
    private long futureModifiedTimeToSkipServerCache() {
        return System.currentTimeMillis() + (1000L * 60L * 60L * 60L * 24L);
    }
}
