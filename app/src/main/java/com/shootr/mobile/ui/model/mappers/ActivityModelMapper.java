package com.shootr.mobile.ui.model.mappers;

import com.shootr.mobile.domain.model.activity.Activity;
import com.shootr.mobile.infraestructure.Mapper;
import com.shootr.mobile.ui.model.ActivityModel;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class ActivityModelMapper extends Mapper<Activity, ActivityModel> {

    private final ShotModelMapper shotModelMapper;

    @Inject public ActivityModelMapper(ShotModelMapper shotModelMapper) {
        this.shotModelMapper = shotModelMapper;
    }

    @Override public ActivityModel map(Activity activity) {
        ActivityModel activityModel = new ActivityModel();

        activityModel.setPublishDate(activity.getPublishDate());
        activityModel.setComment(activity.getComment());
        activityModel.setType(activity.getType());

        Activity.ActivityUserInfo userInfo = activity.getUserInfo();
        activityModel.setUsername(userInfo.getUsername());
        activityModel.setIdUser(userInfo.getIdUser());
        activityModel.setUserPhoto(userInfo.getAvatar());
        activityModel.setStrategic(userInfo.isStrategic());
        activityModel.setName(activity.getName());

        Activity.ActivityStreamInfo streamInfo = activity.getStreamInfo();
        if (streamInfo != null) {
            activityModel.setIdStream(streamInfo.getIdStream());
            activityModel.setStreamTitle(streamInfo.getStreamTitle());
            activityModel.setStrategic(streamInfo.isStrategic());
            activityModel.setVerified(streamInfo.isVerified());
            activityModel.setFollowing(streamInfo.isFollowing());
            activityModel.setStreamPhoto(activity.getStreamPhoto());
        }

        activityModel.setIdTargetUser(activity.getIdTargetUser());
        activityModel.setTargetUserPhoto(activity.getTargetUserPhoto());
        activityModel.setTargetName(activity.getTargetName());
        activityModel.setTargetUsername(activity.getTargetUsername());

        activityModel.setShot(shotModelMapper.transform(activity.getShot()));

        activityModel.setIdStreamAuthor(activity.getIdAuthorStream());

        activityModel.setIdPoll(activity.getIdPoll());
        activityModel.setPollQuestion(activity.getPollQuestion());
        activityModel.setPollOptionText(activity.getPollOptionText());
        return activityModel;
    }

    public List<ActivityModel> mapActivities(List<Activity> activities) {
        ArrayList<ActivityModel> activityModels = new ArrayList<>();
        for (Activity activity : activities) {
            ActivityModel activityModel = map(activity);
            activityModels.add(activityModel);
        }
        return activityModels;
    }

    @Override public Activity reverseMap(ActivityModel value) {
        return null;
    }
}
