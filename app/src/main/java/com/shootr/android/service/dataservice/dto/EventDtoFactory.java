package com.shootr.android.service.dataservice.dto;

import com.shootr.android.constant.Constants;
import com.shootr.android.data.entity.EventEntity;
import com.shootr.android.db.DatabaseContract;
import com.shootr.android.db.mappers.EventEntityMapper;
import com.shootr.android.service.dataservice.generic.FilterDto;
import com.shootr.android.service.dataservice.generic.GenericDto;
import com.shootr.android.service.dataservice.generic.MetadataDto;
import com.shootr.android.service.dataservice.generic.OperationDto;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

import static com.shootr.android.service.dataservice.generic.FilterBuilder.and;
import static com.shootr.android.service.dataservice.generic.FilterBuilder.or;
import static com.shootr.android.service.dataservice.generic.FilterBuilder.orIsNotDeleted;

public class EventDtoFactory {

    private static final String ALIAS_GET_EVENTS_FROM_WATCH_FOLLOWING = "GET_EVENTS_FROM_WATCH_FOLLOWING";
    private static final String ALIAS_GET_EVENT_BY_ID_EVENT = "GET_EVENT_BY_ID_EVENT";
    private static final String ALIAS_SEARCH_EVENT = "SEARCH_EVENT";
    private static final String ALIAS_CREATE_EVENT = "CREATE_EVENT";
    public static final String ALIAS_LISTING_EVENTS = "GET_ALL_EVENTS_CREATED_BYUSER";

    private UtilityDtoFactory utilityDtoFactory;
    private EventEntityMapper eventEntityMapper;

    @Inject public EventDtoFactory(UtilityDtoFactory utilityDtoFactory, EventEntityMapper eventEntityMapper) {
        this.utilityDtoFactory = utilityDtoFactory;
        this.eventEntityMapper = eventEntityMapper;
    }

    public GenericDto getEventsNotEndedByIds(List<String> eventsIds) {
        FilterDto eventsWatchFollowingFilter = and(orIsNotDeleted(),
          or(DatabaseContract.EventTable.ID_EVENT).isIn(eventsIds)).build();

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
          .putKey(DatabaseContract.EventTable.ID_EVENT, eventEntity.getIdEvent())
          .entity(DatabaseContract.EventTable.TABLE)
          .build();

        OperationDto op = new OperationDto.Builder().metadata(md).putData(eventEntityMapper.toDto(eventEntity)).build();

        return utilityDtoFactory.getGenericDtoFromOperation(ALIAS_CREATE_EVENT, op);
    }

    public GenericDto getEventById(String idEvent) {
        MetadataDto md = new MetadataDto.Builder().operation(Constants.OPERATION_RETRIEVE)
          .entity(DatabaseContract.EventTable.TABLE)
          .putKey(DatabaseContract.EventTable.ID_EVENT, idEvent)
          .items(1)
          .build();
        OperationDto op = new OperationDto.Builder().metadata(md).putData(eventEntityMapper.toDto(null)).build();
        return utilityDtoFactory.getGenericDtoFromOperation(ALIAS_GET_EVENT_BY_ID_EVENT, op);
    }

    public GenericDto getSearchEventDto(String query, Map<String, Integer> eventsWatchesCounts,
                                        String locale) {
        MetadataDto md = new MetadataDto.Builder().items(50)
          .operation(Constants.OPERATION_RETRIEVE)
          .entity("SearchEventMongo")
                .putKey("pattern", query)
                .putKey("locale", locale)
          .build();

        OperationDto.Builder operationBuilder = new OperationDto.Builder();
        for (String idEvent : eventsWatchesCounts.keySet()) {
            Integer watchers = eventsWatchesCounts.get(idEvent);
            Map<String, Object> dataItem = new HashMap<>(2);
            dataItem.put("idEvent", idEvent);
            dataItem.put("watchers", watchers);
            operationBuilder.putData(dataItem);
        }
        OperationDto op = operationBuilder.metadata(md).build();
        return utilityDtoFactory.getGenericDtoFromOperation(ALIAS_SEARCH_EVENT, op);
    }

    public GenericDto getListingCount(String idUser) {
        FilterDto eventsListingFilter = and(
          or(DatabaseContract.EventTable.ID_USER).isEqualTo(idUser))
          .build();
        MetadataDto md = new MetadataDto.Builder().operation(Constants.OPERATION_RETRIEVE)
          .entity(DatabaseContract.EventTable.TABLE)
          .filter(eventsListingFilter)
          .items(0)
          .build();
        OperationDto op = new OperationDto.Builder().metadata(md).putData(eventEntityMapper.toDto(null)).build();
        return utilityDtoFactory.getGenericDtoFromOperation(ALIAS_LISTING_EVENTS, op);
    }
}
