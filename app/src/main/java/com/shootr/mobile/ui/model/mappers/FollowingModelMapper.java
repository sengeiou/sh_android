package com.shootr.mobile.ui.model.mappers;

import com.shootr.mobile.domain.model.Followable;
import com.shootr.mobile.domain.model.FollowableType;
import com.shootr.mobile.domain.model.Following;
import com.shootr.mobile.domain.model.shot.ProfileShotTimeline;
import com.shootr.mobile.domain.model.stream.Stream;
import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.ui.model.FollowingModel;
import com.shootr.mobile.ui.model.ProfileShotTimelineModel;
import com.shootr.mobile.ui.model.SearchableModel;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class FollowingModelMapper {

  private UserModelMapper userModelMapper;
  private StreamModelMapper streamModelMapper;

  @Inject public FollowingModelMapper(UserModelMapper userModelMapper, StreamModelMapper streamModelMapper) {
    this.userModelMapper = userModelMapper;
    this.streamModelMapper = streamModelMapper;
  }

  public FollowingModel transform(Following value) {
    if (value == null) {
      return null;
    }

    FollowingModel followingModel = new FollowingModel();

    followingModel.setMaxTimestamp(value.getMaxTimestamp());
    followingModel.setSinceTimestamp(value.getSinceTimestamp());

    ArrayList<SearchableModel> searchableModels = new ArrayList<>();

    for (Followable followable : value.getData()) {
      if (followable.getFollowableType().equals(FollowableType.STREAM)) {
        searchableModels.add(streamModelMapper.transform((Stream) followable));
      } else if (followable.getFollowableType().equals(FollowableType.USER)) {
        searchableModels.add(userModelMapper.transform((User) followable));
      }
    }

    followingModel.setData(searchableModels);

    return followingModel;
  }
}
