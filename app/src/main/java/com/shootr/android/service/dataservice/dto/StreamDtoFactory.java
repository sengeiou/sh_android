package com.shootr.android.service.dataservice.dto;

import com.shootr.android.constant.Constants;
import com.shootr.android.data.entity.StreamEntity;
import com.shootr.android.db.DatabaseContract;
import com.shootr.android.db.mappers.StreamEntityMapper;
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

public class StreamDtoFactory {

    private static final String ALIAS_GET_STREAMS_FROM_WATCH_FOLLOWING = "GET_STREAMS_FROM_WATCH_FOLLOWING";

    private UtilityDtoFactory utilityDtoFactory;
    private StreamEntityMapper streamEntityMapper;

    @Inject public StreamDtoFactory(UtilityDtoFactory utilityDtoFactory, StreamEntityMapper streamEntityMapper) {
        this.utilityDtoFactory = utilityDtoFactory;
        this.streamEntityMapper = streamEntityMapper;
    }

    public GenericDto getStreamsNotEndedByIds(List<String> streamsIds) {
        FilterDto streamsWatchFollowingFilter = and(orIsNotDeleted(),
          or(DatabaseContract.StreamTable.ID_STREAM).isIn(streamsIds)).build();

        MetadataDto md = new MetadataDto.Builder().operation(Constants.OPERATION_RETRIEVE)
          .entity(DatabaseContract.StreamTable.TABLE)
          .includeDeleted(false)
          .filter(streamsWatchFollowingFilter)
          .items(1000)
          .build();

        OperationDto op = new OperationDto.Builder().metadata(md).putData(streamEntityMapper.toDto(null)).build();

        return utilityDtoFactory.getGenericDtoFromOperation(ALIAS_GET_STREAMS_FROM_WATCH_FOLLOWING, op);
    }
}
