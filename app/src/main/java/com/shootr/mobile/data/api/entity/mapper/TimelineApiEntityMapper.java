package com.shootr.mobile.data.api.entity.mapper;

import com.shootr.mobile.data.api.entity.TimelineApiEntity;
import com.shootr.mobile.data.entity.TimelineEntity;
import javax.inject.Inject;

public class TimelineApiEntityMapper {

  private final DataApiEntityMapper dataApiEntityMapper;

  @Inject public TimelineApiEntityMapper(DataApiEntityMapper shotApiEntityMapper) {
    this.dataApiEntityMapper = shotApiEntityMapper;
  }

  public TimelineEntity map(TimelineApiEntity apiEntity) {

    TimelineEntity entity = new TimelineEntity();

    entity.setStream(apiEntity.getStream());
    entity.setParticipants(apiEntity.getParticipants());
    entity.setFixed(dataApiEntityMapper.map(apiEntity.getFixed()));
    entity.setPinned(dataApiEntityMapper.map(apiEntity.getPinned()));
    entity.setItems(dataApiEntityMapper.map(apiEntity.getItems()));
    entity.setFilter(apiEntity.getFilter());
    entity.setNewBadgeContent(apiEntity.isNewBadgeContent());


    return entity;
  }
}
