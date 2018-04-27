package com.shootr.mobile.data.mapper;

import com.shootr.mobile.data.entity.TimelineEntity;
import com.shootr.mobile.domain.model.StreamTimeline;
import javax.inject.Inject;

public class TimelineEntityMapper {

  private final StreamEntityMapper streamEntityMapper;
  private final DataEntityMapper dataEntityMapper;

  @Inject public TimelineEntityMapper(StreamEntityMapper streamEntityMapper, DataEntityMapper dataEntityMapper) {
    this.streamEntityMapper = streamEntityMapper;
    this.dataEntityMapper = dataEntityMapper;
  }

  public StreamTimeline map(TimelineEntity timelineEntity) {

    StreamTimeline streamTimeline = new StreamTimeline();

    streamTimeline.setParticipantsNumber(timelineEntity.getParticipants().getTotal());
    streamTimeline.setFollowingNumber(timelineEntity.getParticipants().getFollowing());
    streamTimeline.setFixed(dataEntityMapper.map(timelineEntity.getFixed()));
    streamTimeline.setPinned(dataEntityMapper.map(timelineEntity.getPinned()));
    streamTimeline.setItems(dataEntityMapper.map(timelineEntity.getItems()));
    streamTimeline.setStream(streamEntityMapper.transform(timelineEntity.getStream()));
    streamTimeline.setFilter(timelineEntity.getFilter());
    streamTimeline.setNewBadgeContent(timelineEntity.isNewBadgeContent());
    if (timelineEntity.getParams() != null) {
      streamTimeline.setPeriod(timelineEntity.getParams().getPeriod().getDuration());
    }

    return streamTimeline;
  }
}
