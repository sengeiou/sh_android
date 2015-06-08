package com.shootr.android.service.dataservice.dto;

import com.shootr.android.constant.Constants;
import com.shootr.android.data.entity.ShotEntity;
import com.shootr.android.db.DatabaseContract.ShotTable;
import com.shootr.android.db.mappers.ShotEntityMapper;
import com.shootr.android.domain.ActivityTimelineParameters;
import com.shootr.android.domain.EventTimelineParameters;
import com.shootr.android.service.dataservice.generic.FilterBuilder;
import com.shootr.android.service.dataservice.generic.FilterDto;
import com.shootr.android.service.dataservice.generic.GenericDto;
import com.shootr.android.service.dataservice.generic.MetadataDto;
import com.shootr.android.service.dataservice.generic.OperationDto;
import com.shootr.android.service.dataservice.generic.OperationDto.Builder;

import java.util.List;

import javax.inject.Inject;

import static com.shootr.android.service.dataservice.generic.FilterBuilder.and;
import static com.shootr.android.service.dataservice.generic.FilterBuilder.or;
import static com.shootr.android.service.dataservice.generic.FilterBuilder.orModifiedOrDeletedAfter;

public class TimelineDtoFactory {

    public static final String ALIAS_GET_SHOTS = "GET_SHOTS";
    public static final String ALIAS_GET_NEWER_SHOTS = "GET_NEWER_SHOTS";
    public static final String ALIAS_GET_OLDER_SHOTS = "GET_OLDER_SHOTS";

    private UtilityDtoFactory utilityDtoFactory;
    ShotEntityMapper shotEntityMapper;

    @Inject public TimelineDtoFactory(UtilityDtoFactory utilityDtoFactory, ShotEntityMapper shotEntityMapper) {
        this.utilityDtoFactory = utilityDtoFactory;
        this.shotEntityMapper = shotEntityMapper;
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
                .putData(shotEntityMapper.toDto(null))
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
                .putData(shotEntityMapper.toDto(null))
                .build();

        return utilityDtoFactory.getGenericDtoFromOperation(ALIAS_GET_NEWER_SHOTS, op);
    }

    public GenericDto getOlderShotsOperationDto(List<Long> usersIds, Long referenceDate, Long limit) {
        FilterDto oldShotsFilter = and(or(ShotTable.ID_USER).isIn(usersIds))
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
                .putData(shotEntityMapper.toDto(null))
                .build();

        return utilityDtoFactory.getGenericDtoFromOperation(ALIAS_GET_OLDER_SHOTS, op);
    }

    public GenericDto getActivityTimelineOperationDto(final ActivityTimelineParameters parameters) {
        FilterDto timelineFilter = and( //
          or(ShotTable.ID_USER).isIn(parameters.getUserIds()) //
        ) //
          .and(or(ShotTable.TYPE).isIn(parameters.getIncludedTypes())) //
          .and(ShotTable.CSYS_MODIFIED).greaterThan(parameters.getSinceDate()) //
          .and(ShotTable.CSYS_DELETED).isEqualTo(null) //
          .and(ShotTable.CSYS_MODIFIED).matches(new FilterBuilder.FilterMatcher<FilterBuilder.AndItem>() {
              @Override
              public FilterBuilder.AndItem match(FilterBuilder.ItemField<FilterBuilder.AndItem> itemField) {
                  if (parameters.getMaxDate() != null) {
                      return itemField.lessThan(parameters.getMaxDate());
                  } else {
                      return itemField.isNotEqualTo(null);
                  }
              }
          }).build();

        MetadataDto md = new MetadataDto.Builder() //
          .operation(Constants.OPERATION_RETRIEVE) //
          .entity(ShotTable.TABLE) //
          .items(parameters.getLimit()) //
          .totalItems(parameters.getLimit()) //
          .filter(timelineFilter) //
          .build();

        OperationDto op = new Builder() //
          .metadata(md) //
          .putData(shotEntityMapper.toDto(null)) //
          .build();

        return utilityDtoFactory.getGenericDtoFromOperation(ALIAS_GET_SHOTS, op);
    }

    public GenericDto getEventTimelineOperationDto(final EventTimelineParameters parameters) {
        FilterDto timelineFilter = and( //
          or(ShotTable.ID_USER).isIn(parameters.getUserIds()) //
        ) //
          .and(ShotTable.ID_EVENT).isEqualTo(parameters.getEventId()) //
          .and(ShotTable.CSYS_MODIFIED).greaterThan(parameters.getSinceDate()) //
          .and(ShotTable.CSYS_DELETED).isEqualTo(null) //
          .and(ShotTable.CSYS_MODIFIED).matches(new FilterBuilder.FilterMatcher<FilterBuilder.AndItem>() {
              @Override
              public FilterBuilder.AndItem match(FilterBuilder.ItemField<FilterBuilder.AndItem> itemField) {
                  if (parameters.getMaxDate() != null) {
                      return itemField.lessThan(parameters.getMaxDate());
                  } else {
                      return itemField.isNotEqualTo(null);
                  }
              }
          })
          .build();

        MetadataDto md = new MetadataDto.Builder() //
          .operation(Constants.OPERATION_RETRIEVE) //
          .entity(ShotTable.TABLE) //
          .items(parameters.getLimit()) //
          .totalItems(parameters.getLimit()) //
          .filter(timelineFilter) //
          .build();

        OperationDto op = new Builder() //
          .metadata(md) //
          .putData(shotEntityMapper.toDto(null)) //
          .build();

        return utilityDtoFactory.getGenericDtoFromOperation(ALIAS_GET_SHOTS, op);
    }
}
