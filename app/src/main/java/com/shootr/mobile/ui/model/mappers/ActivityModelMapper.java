package com.shootr.mobile.ui.model.mappers;

import com.shootr.mobile.domain.model.activity.Activity;
import com.shootr.mobile.infraestructure.Mapper;
import com.shootr.mobile.ui.model.ActivityModel;
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

        Activity.ActivityStreamInfo streamInfo = activity.getStreamInfo();
        if (streamInfo != null) {
            activityModel.setIdStream(streamInfo.getIdStream());
            activityModel.setStreamTitle(streamInfo.getStreamTitle());
        }

        activityModel.setIdTargetUser(activity.getIdTargetUser());

        activityModel.setShot(shotModelMapper.transform(activity.getShot()));

        activityModel.setIdStreamAuthor(activity.getIdAuthorStream());

        activityModel.setIdPoll(activity.getIdPoll());
        activityModel.setPollQuestion(activity.getPollQuestion());
        return activityModel;
    }

    @Override public Activity reverseMap(ActivityModel value) {
        return null;
    }
}
