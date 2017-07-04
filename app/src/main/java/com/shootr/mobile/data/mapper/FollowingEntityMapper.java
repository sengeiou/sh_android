package com.shootr.mobile.data.mapper;

import com.shootr.mobile.data.api.entity.FollowingsEntity;
import com.shootr.mobile.data.entity.FollowableEntity;
import com.shootr.mobile.data.entity.StreamEntity;
import com.shootr.mobile.data.entity.UserEntity;
import com.shootr.mobile.domain.model.Followable;
import com.shootr.mobile.domain.model.FollowableType;
import com.shootr.mobile.domain.model.Following;
import java.util.ArrayList;
import javax.inject.Inject;

public class FollowingEntityMapper {

  private final UserEntityMapper userEntityMapper;
  private final StreamEntityMapper streamEntityMapper;

  @Inject public FollowingEntityMapper(UserEntityMapper userEntityMapper, StreamEntityMapper streamEntityMapper) {
    this.userEntityMapper = userEntityMapper;
    this.streamEntityMapper = streamEntityMapper;
  }

  public Following map(FollowingsEntity value) {

    Following following = new Following();

    following.setMaxTimestamp(value.getPagination().getMaxTimestamp());
    following.setSinceTimestamp(value.getPagination().getSinceTimestamp());

    ArrayList<Followable> followables = new ArrayList<>();

    for (FollowableEntity followableEntity : value.getData()) {
      if (followableEntity.getResultType().equals(FollowableType.STREAM)) {
        followables.add(streamEntityMapper.transform((StreamEntity) followableEntity));
      } else if (followableEntity.getResultType().equals(FollowableType.USER)) {
        followables.add(userEntityMapper.transform((UserEntity) followableEntity));
      }
    }

    following.setData(followables);

    return following;
  }
}
