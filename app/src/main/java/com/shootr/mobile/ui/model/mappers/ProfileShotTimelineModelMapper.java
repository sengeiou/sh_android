package com.shootr.mobile.ui.model.mappers;

import com.shootr.mobile.domain.model.shot.ProfileShotTimeline;
import com.shootr.mobile.ui.model.ProfileShotTimelineModel;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class ProfileShotTimelineModelMapper {

  private ShotModelMapper shotModelMapper;

  @Inject public ProfileShotTimelineModelMapper(ShotModelMapper shotModelMapper) {
    this.shotModelMapper = shotModelMapper;
  }

  public ProfileShotTimelineModel transform(ProfileShotTimeline value) {
    if (value == null) {
      return null;
    }

    ProfileShotTimelineModel profileShotTimelineModel = new ProfileShotTimelineModel();

    profileShotTimelineModel.setMaxTimestamp(value.getMaxTimestamp());
    profileShotTimelineModel.setSinceTimestamp(value.getSinceTimestamp());
    profileShotTimelineModel.setShots(shotModelMapper.transform(value.getShots()));

    return profileShotTimelineModel;
  }

  public List<ProfileShotTimelineModel> transform(List<ProfileShotTimeline> values) {
    ArrayList<ProfileShotTimelineModel> models = new ArrayList<>();
    for (ProfileShotTimeline profileShotTimeline : values) {
      models.add(transform(profileShotTimeline));
    }
    return models;
  }
}
