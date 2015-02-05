package com.shootr.android.service.dataservice.dto;

import com.shootr.android.constant.Constants;
import com.shootr.android.data.entity.EventEntity;
import com.shootr.android.db.DatabaseContract;
import com.shootr.android.db.mappers.EventEntityMapper;
import com.shootr.android.db.mappers.TeamMapper;
import com.shootr.android.db.mappers.WatchMapper;
import com.shootr.android.data.entity.WatchEntity;
import com.shootr.android.db.DatabaseContract.WatchTable;
import com.shootr.android.service.dataservice.generic.FilterDto;
import com.shootr.android.service.dataservice.generic.GenericDto;
import com.shootr.android.service.dataservice.generic.MetadataDto;
import com.shootr.android.service.dataservice.generic.OperationDto;
import com.shootr.android.domain.utils.TimeUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

import static com.shootr.android.service.dataservice.generic.FilterBuilder.and;
import static com.shootr.android.service.dataservice.generic.FilterBuilder.or;
import static com.shootr.android.service.dataservice.generic.FilterBuilder.orIsNotDeleted;

public class EventDtoFactory {

    private static final String ALIAS_GET_WATCH_OF_MY_FOLLOWING = "GET_MY_FOLLOWING_WATCHES";
    private static final String ALIAS_GET_EVENTS_FROM_WATCH_FOLLOWING = "GET_EVENTS_FROM_WATCH_FOLLOWING";
    private static final String ALIAS_SET_WATCH_STATUS = "SET_WATCH_STATUS";
    private static final String ALIAS_GET_WATCH_BY_KEYS = "GET_WATCH_BY_KEYS";
    private static final String ALIAS_GET_EVENT_BY_ID_EVENT = "GET_EVENT_BY_ID_EVENT";
    private static final String ALIAS_SEARCH_EVENT = "SEARCH_EVENT";
    private static final String ALIAS_CREATE_EVENT = "CREATE_EVENT";

    private UtilityDtoFactory utilityDtoFactory;
    private EventEntityMapper eventEntityMapper;
    private WatchMapper watchMapper;
    private TimeUtils timeUtils;

    @Inject public EventDtoFactory(UtilityDtoFactory utilityDtoFactory, EventEntityMapper eventEntityMapper,
      WatchMapper watchMapper, TimeUtils timeUtils){
        this.utilityDtoFactory = utilityDtoFactory;
        this.eventEntityMapper = eventEntityMapper;
        this.watchMapper = watchMapper;
        this.timeUtils = timeUtils;
    }

    public GenericDto getWatchFromUsersAndMe(List<Long> userIds, Long idCurrentUser){
        FilterDto watchFollowingFilter =
          or(
            and(DatabaseContract.WatchTable.STATUS).isEqualTo(WatchEntity.STATUS_WATCHING)
            .and(or(DatabaseContract.WatchTable.ID_USER).isIn(userIds))
          )
          .or(WatchTable.ID_USER).isEqualTo(idCurrentUser)
            //TODO filtrar por visible y notificaciones? Si van inclusives
          .build();

        MetadataDto md = new MetadataDto.Builder().operation(Constants.OPERATION_RETRIEVE).entity(
        DatabaseContract.WatchTable.TABLE)
          .includeDeleted(false).filter(watchFollowingFilter).items(1000).build();
        OperationDto op = new OperationDto.Builder().metadata(md).putData(watchMapper.toDto(null)).build();
        return utilityDtoFactory.getGenericDtoFromOperation(ALIAS_GET_WATCH_OF_MY_FOLLOWING, op);
    }

    public GenericDto getWatchFromUsersAndEvent(List<Long> userIds, Long idEvent) {
        if (userIds == null || userIds.isEmpty()) {
            throw new IllegalArgumentException("userIds cannot be null nor empty");
        }
        FilterDto watchFollowingFilter = and(WatchTable.ID_EVENT).isEqualTo(idEvent)
          .and(WatchTable.STATUS)
          .isEqualTo(WatchEntity.STATUS_WATCHING)
          .and(or(WatchTable.ID_USER).isIn(userIds))
          .build();

        MetadataDto md = new MetadataDto.Builder().operation(Constants.OPERATION_RETRIEVE)
          .entity(DatabaseContract.WatchTable.TABLE)
          .includeDeleted(false)
          .filter(watchFollowingFilter)
          .items(1000)
          .build();
        OperationDto op = new OperationDto.Builder().metadata(md).putData(watchMapper.toDto(null)).build();
        return utilityDtoFactory.getGenericDtoFromOperation(ALIAS_GET_WATCH_OF_MY_FOLLOWING, op);
    }

    public GenericDto setWatchStatusGenericDto(WatchEntity watchEntity){
        MetadataDto md = new MetadataDto.Builder()
          .operation(Constants.OPERATION_CREATEUPDATE)
          .entity(WatchTable.TABLE)
          .includeDeleted(true)
          .putKey(WatchTable.ID_USER, watchEntity.getIdUser())
          .putKey(WatchTable.ID_EVENT, watchEntity.getIdEvent())
          .build();

        OperationDto op = new OperationDto.Builder()
          .metadata(md)
          .putData(watchMapper.toDto(watchEntity))
          .build();
        return utilityDtoFactory.getGenericDtoFromOperation(ALIAS_SET_WATCH_STATUS,op);
    }

    public GenericDto getEventsNotEndedByIds(List<Long> eventsIds){
        FilterDto eventsWatchFollowingFilter = and(orIsNotDeleted(),
          or(DatabaseContract.EventTable.ID_EVENT).isIn(eventsIds),
          or(DatabaseContract.EventTable.END_DATE).greaterThan(timeUtils.getCurrentDate()),
          or(DatabaseContract.EventTable.BEGIN_DATE).isNotEqualTo(null))
          .build();

        MetadataDto md = new MetadataDto.Builder().operation(Constants.OPERATION_RETRIEVE)
          .entity(DatabaseContract.EventTable.TABLE)
          .includeDeleted(false)
          .filter(eventsWatchFollowingFilter)
          .items(1000)
          .build();

        OperationDto op = new OperationDto.Builder().metadata(md).putData(eventEntityMapper.toDto(null)).build();

        return utilityDtoFactory.getGenericDtoFromOperation(ALIAS_GET_EVENTS_FROM_WATCH_FOLLOWING, op);
    }

    public GenericDto saveEvent(EventEntity eventEntity) {
        MetadataDto md = new MetadataDto.Builder().operation(Constants.OPERATION_UPDATE_CREATE)
          .putKey(DatabaseContract.EventTable.ID_EVENT, null)
          .entity(DatabaseContract.EventTable.TABLE)
          .build();

        OperationDto op = new OperationDto.Builder().metadata(md).putData(eventEntityMapper.toDto(eventEntity)).build();

        return utilityDtoFactory.getGenericDtoFromOperation(ALIAS_CREATE_EVENT, op);
    }

    public GenericDto getWatchByKeys(Long idUser, Long idEvent) {
        MetadataDto md = new MetadataDto.Builder()
          .operation(Constants.OPERATION_RETRIEVE)
          .entity(WatchTable.TABLE)
          .putKey(WatchTable.ID_USER, idUser)
          .putKey(WatchTable.ID_EVENT, idEvent)
          .items(1).build();
        OperationDto op = new OperationDto.Builder().metadata(md).putData(watchMapper.toDto(null)).build();
        return utilityDtoFactory.getGenericDtoFromOperation(ALIAS_GET_WATCH_BY_KEYS,op);
    }

    public GenericDto getEventById(Long idEvent) {
        MetadataDto md = new MetadataDto.Builder()
          .operation(Constants.OPERATION_RETRIEVE)
          .entity(DatabaseContract.EventTable.TABLE)
          .putKey(WatchTable.ID_EVENT, idEvent)
          .items(1).build();
        OperationDto op = new OperationDto.Builder().metadata(md).putData(eventEntityMapper.toDto(null)).build();
        return utilityDtoFactory.getGenericDtoFromOperation(ALIAS_GET_EVENT_BY_ID_EVENT,op);

    }

    public GenericDto getWatchVisible(Long currentUserId) {
        FilterDto watchFollowingFilter = and(WatchTable.ID_USER).isEqualTo(currentUserId)
          .and(WatchTable.VISIBLE)
          .isEqualTo(WatchEntity.VISIBLE)
          .and(WatchTable.STATUS)
          .isNotEqualTo(null)
          .and(WatchTable.NOTIFICATION)
          .isNotEqualTo(null)
          .build();

        MetadataDto md = new MetadataDto.Builder().operation(Constants.OPERATION_RETRIEVE).entity(
          DatabaseContract.WatchTable.TABLE)
          .includeDeleted(false).filter(watchFollowingFilter).items(1000).build();
        OperationDto op = new OperationDto.Builder().metadata(md).putData(watchMapper.toDto(null)).build();
        return utilityDtoFactory.getGenericDtoFromOperation(ALIAS_GET_WATCH_OF_MY_FOLLOWING, op);
    }

    public GenericDto getSearchEventDto(String query, Map<Long, Integer> eventsWatchesCounts) {
        MetadataDto md = new MetadataDto.Builder()
          .items(50)
          .operation(Constants.OPERATION_RETRIEVE)
          .entity("SearchEvent")
          .putKey("pattern", query)
          .build();

        OperationDto.Builder operationBuilder = new OperationDto.Builder();
        for (Long idEvent : eventsWatchesCounts.keySet()) {
            Integer watchers = eventsWatchesCounts.get(idEvent);
            Map<String, Object> dataItem = new HashMap<>(2);
            dataItem.put("idEvent", idEvent);
            dataItem.put("watchers", watchers);
            operationBuilder.putData(dataItem);
        }
        OperationDto op = operationBuilder.metadata(md).build();
        return utilityDtoFactory.getGenericDtoFromOperation(ALIAS_SEARCH_EVENT, op);
    }
}
