package gm.mobi.android.service.dataservice.dto;

import java.util.List;

import javax.inject.Inject;

import gm.mobi.android.constant.Constants;
import gm.mobi.android.db.GMContract.ShotTable;
import gm.mobi.android.db.mappers.ShotMapper;
import gm.mobi.android.service.dataservice.generic.FilterDto;
import gm.mobi.android.service.dataservice.generic.GenericDto;
import gm.mobi.android.service.dataservice.generic.MetadataDto;
import gm.mobi.android.service.dataservice.generic.OperationDto;
import gm.mobi.android.service.dataservice.generic.OperationDto.Builder;

import static gm.mobi.android.service.dataservice.generic.FilterBuilder.and;
import static gm.mobi.android.service.dataservice.generic.FilterBuilder.orModifiedOrDeletedAfter;
import static gm.mobi.android.service.dataservice.generic.FilterBuilder.or;

public class TimelineDtoFactory {

    public static final String ALIAS_GET_SHOTS = "GET_SHOTS";
    public static final String ALIAS_GET_NEWER_SHOTS = "GET_NEWER_SHOTS";
    public static final String ALIAS_GET_OLDER_SHOTS = "GET_OLDER_SHOTS";

    private UtilityDtoFactory utilityDtoFactory;

    @Inject public TimelineDtoFactory(UtilityDtoFactory utilityDtoFactory) {
        this.utilityDtoFactory = utilityDtoFactory;
    }

    public GenericDto getAllShotsOperationDto(List<Long> usersIds, Long limit) {
        FilterDto shotsFilter = and(
                or(ShotTable.ID_USER).isIn(usersIds)
        )
                .and(ShotTable.CSYS_DELETED).isEqualTo(null)
                .and(ShotTable.CSYS_MODIFIED).greaterThan(0L)
                .build();

        MetadataDto md = new MetadataDto.Builder()
                .operation(Constants.OPERATION_RETRIEVE)
                .entity(ShotTable.TABLE)
                .includeDeleted(false)
                .items(limit)
                .filter(shotsFilter)
                .build();

        OperationDto op = new OperationDto.Builder()
                .metadata(md)
                .putData(ShotMapper.toDto(null))
                .build();

        return utilityDtoFactory.getGenericDtoFromOperation(ALIAS_GET_SHOTS, op);

    }

    public GenericDto getNewerShotsOperationDto(List<Long> usersIds, Long referenceDate, Long limit) {
        // Build filter
        FilterDto newShotsFilter = and(
                or(ShotTable.ID_USER).isIn(usersIds),
                orModifiedOrDeletedAfter(referenceDate)
        ).build();

        // Build metadata for the operation
        MetadataDto md = new MetadataDto.Builder()
                .operation(Constants.OPERATION_RETRIEVE)
                .entity(ShotTable.TABLE)
                .includeDeleted(true)
                .filter(newShotsFilter)
                .items(limit)
                .build();

        // Build the operation
        OperationDto op = new OperationDto.Builder()
                .metadata(md)
                .putData(ShotMapper.toDto(null))
                .build();

        return utilityDtoFactory.getGenericDtoFromOperation(ALIAS_GET_NEWER_SHOTS, op);
    }

    public GenericDto getOlderShotsOperationDto(List<Long> usersIds, Long referenceDate, Long limit) {
        FilterDto oldShotsFilter = and(
                or(ShotTable.ID_USER).isIn(usersIds)
        )
                .and(ShotTable.CSYS_MODIFIED).lessThan(referenceDate) //TODO antiguos por fecha de modificación o de creación?
                .and(ShotTable.CSYS_DELETED).isEqualTo(null)
                .build();

        MetadataDto md = new MetadataDto.Builder()
                .operation(Constants.OPERATION_RETRIEVE)
                .entity(ShotTable.TABLE)
                .items(limit)
                .filter(oldShotsFilter)
                .build();

        OperationDto op = new Builder()
                .metadata(md)
                .putData(ShotMapper.toDto(null))
                .build();

        return utilityDtoFactory.getGenericDtoFromOperation(ALIAS_GET_OLDER_SHOTS, op);
    }

}
