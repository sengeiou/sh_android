package com.shootr.android.ui.model.mappers;

import com.shootr.android.domain.Activity;
import com.shootr.android.ui.model.ActivityModel;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class ActivityModelMapper {

    private final ShotModelMapper shotModelMapper;

    @Inject public ActivityModelMapper(ShotModelMapper shotModelMapper) {
        this.shotModelMapper = shotModelMapper;
    }

    public ActivityModel transform(Activity activity) {
        ActivityModel activityModel = new ActivityModel();

        activityModel.setPublishDate(activity.getPublishDate());
        activityModel.setComment(activity.getComment());
        activityModel.setType(activity.getType());

        Activity.ActivityUserInfo userInfo = activity.getUserInfo();
        activityModel.setUsername(userInfo.getUsername());
        activityModel.setIdUser(userInfo.getIdUser());
        activityModel.setUserPhoto(userInfo.getAvatar());

        Activity.ActivityStreamInfo streamInfo = activity.getStreamInfo();
        if (streamInfo != null) {
            activityModel.setIdStream(streamInfo.getIdStream());
            activityModel.setStreamTag(streamInfo.getStreamTag());
            activityModel.setStreamTitle(streamInfo.getStreamTitle());
        }

        activityModel.setShot(shotModelMapper.transform(activity.getShot()));

        return activityModel;
    }

    public List<ActivityModel> transform(List<Activity> activities) {
        List<ActivityModel> activityModels = new ArrayList<>();
        for (Activity activity : activities) {
            activityModels.add(transform(activity));
        }
        return activityModels;
    }
}
