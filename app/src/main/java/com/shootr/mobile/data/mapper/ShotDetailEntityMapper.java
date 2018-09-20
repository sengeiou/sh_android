package com.shootr.mobile.data.mapper;

import com.shootr.mobile.data.entity.ShotDetailEntity;
import com.shootr.mobile.data.entity.ShotEntity;
import com.shootr.mobile.domain.model.shot.ShotDetail;
import com.shootr.mobile.domain.model.shot.Replies;
import javax.inject.Inject;

public class ShotDetailEntityMapper {

  private final StreamEntityMapper streamEntityMapper;
  private final ShotEntityMapper shotEntityMapper;
  private final DataEntityMapper dataEntityMapper;

  @Inject public ShotDetailEntityMapper(StreamEntityMapper streamEntityMapper,
      ShotEntityMapper shotEntityMapper, DataEntityMapper dataEntityMapper) {
    this.streamEntityMapper = streamEntityMapper;
    this.shotEntityMapper = shotEntityMapper;
    this.dataEntityMapper = dataEntityMapper;
  }

  public ShotDetail transform(ShotDetailEntity shot) {

    ShotDetail entity = new ShotDetail();
    Replies replies = new Replies();

    entity.setShot(shotEntityMapper.transform((ShotEntity) shot.getItem()));
    entity.setParents(dataEntityMapper.map(shot.getParents()));
    entity.setStream(streamEntityMapper.transform(shot.getStream()));

    replies.setBasic(dataEntityMapper.map(shot.getReplies().getBasic()));
    replies.setPromoted(dataEntityMapper.map(shot.getReplies().getPromoted()));
    replies.setSubscribers(dataEntityMapper.map(shot.getReplies().getSubscribers()));

    entity.setReplies(replies);
    return entity;

  }
}
