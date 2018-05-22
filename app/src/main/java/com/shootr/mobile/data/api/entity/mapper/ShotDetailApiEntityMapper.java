package com.shootr.mobile.data.api.entity.mapper;

import com.shootr.mobile.data.api.entity.ShotApiEntity;
import com.shootr.mobile.data.api.entity.ShotDetailApiEntity;
import com.shootr.mobile.data.entity.NewShotDetailEntity;
import com.shootr.mobile.data.entity.RepliesEntity;
import javax.inject.Inject;

public class ShotDetailApiEntityMapper {

  private final ShotApiEntityMapper shotApiEntityMapper;
  private final DataApiEntityMapper dataApiEntityMapper;

  @Inject public ShotDetailApiEntityMapper(ShotApiEntityMapper shotApiEntityMapper,
      DataApiEntityMapper dataApiEntityMapper) {
    this.shotApiEntityMapper = shotApiEntityMapper;
    this.dataApiEntityMapper = dataApiEntityMapper;
  }

  public NewShotDetailEntity transform(ShotDetailApiEntity shot) {

    NewShotDetailEntity entity = new NewShotDetailEntity();
    RepliesEntity  repliesEntity = new RepliesEntity();

    entity.setItem(shotApiEntityMapper.transform((ShotApiEntity) shot.getItem()));
    entity.setParents(dataApiEntityMapper.map(shot.getParents()));
    entity.setStream(shot.getStream());

    repliesEntity.setBasic(dataApiEntityMapper.map(shot.getReplies().getOther()));
    repliesEntity.setPromoted(dataApiEntityMapper.map(shot.getReplies().getPromoted()));
    repliesEntity.setSubscribers(dataApiEntityMapper.map(shot.getReplies().getSubscribers()));

    entity.setReplies(repliesEntity);


    return entity;
  }
}
