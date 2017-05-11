package com.shootr.mobile.data.api.entity.mapper;

import com.shootr.mobile.data.api.entity.ProfileShotTimelineApiEntity;
import com.shootr.mobile.data.entity.ProfileShotTimelineEntity;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class ProfileShotTimelineApiEntityMapper {

  private final ShotApiEntityMapper shotApiEntityMapper;

  @Inject public ProfileShotTimelineApiEntityMapper(ShotApiEntityMapper shotApiEntityMapper) {
    this.shotApiEntityMapper = shotApiEntityMapper;
  }

  public ProfileShotTimelineEntity map(ProfileShotTimelineApiEntity value) {
    ProfileShotTimelineEntity profileShotTimelineEntity = new ProfileShotTimelineEntity();

    profileShotTimelineEntity.setMaxTimestamp(value.getPagination().getMaxTimestamp());
    profileShotTimelineEntity.setSinceTimestamp(value.getPagination().getSinceTimestamp());
    profileShotTimelineEntity.setShotEntities(shotApiEntityMapper.transform(value.getShots()));

    return profileShotTimelineEntity;
  }

  public List<ProfileShotTimelineEntity> map(List<ProfileShotTimelineApiEntity> values) {
    List<ProfileShotTimelineEntity> returnValues = new ArrayList<>(values.size());
    for (ProfileShotTimelineApiEntity value : values) {
      returnValues.add(map(value));
    }
    return returnValues;
  }
}
