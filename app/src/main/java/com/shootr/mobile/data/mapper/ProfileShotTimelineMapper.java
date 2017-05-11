package com.shootr.mobile.data.mapper;

import com.shootr.mobile.data.entity.ProfileShotTimelineEntity;
import com.shootr.mobile.domain.model.shot.ProfileShotTimeline;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class ProfileShotTimelineMapper {

  private final ShotEntityMapper shotEntityMapper;

  @Inject public ProfileShotTimelineMapper(ShotEntityMapper shotEntityMapperr) {
    this.shotEntityMapper = shotEntityMapperr;
  }

  public ProfileShotTimeline map(ProfileShotTimelineEntity value) {
    ProfileShotTimeline profileShotTimeline = new ProfileShotTimeline();

    profileShotTimeline.setSinceTimestamp(value.getSinceTimestamp());
    profileShotTimeline.setMaxTimestamp(value.getMaxTimestamp());
    profileShotTimeline.setShots(shotEntityMapper.transform(value.getShotEntities()));

    return profileShotTimeline;
  }

  public List<ProfileShotTimeline> map(List<ProfileShotTimelineEntity> values) {
    List<ProfileShotTimeline> returnValues = new ArrayList<>(values.size());
    for (ProfileShotTimelineEntity value : values) {
      returnValues.add(map(value));
    }
    return returnValues;
  }
}
