package com.shootr.mobile.data.mapper;

import com.shootr.mobile.data.entity.ActivityEntity;
import com.shootr.mobile.data.entity.Synchronized;
import com.shootr.mobile.domain.model.activity.Activity;
import com.shootr.mobile.domain.model.shot.Shot;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

import static com.shootr.mobile.domain.utils.Preconditions.checkNotNull;

public class ActivityEntityMapper {

    @Inject public ActivityEntityMapper() {
    }

    public Activity transform(ActivityEntity activityEntity) {
        if (activityEntity == null) {
            return null;
        }
        Activity activity = new Activity();
        activity.setIdActivity(activityEntity.getIdActivity());
        activity.setComment(activityEntity.getComment());
        activity.setType(activityEntity.getType());
        activity.setPublishDate(activityEntity.getBirth());

        if (activityEntity.getIdStream() != null) {
            Activity.ActivityStreamInfo eventInfo = new Activity.ActivityStreamInfo();
            eventInfo.setIdStream(activityEntity.getIdStream());
            eventInfo.setStreamTitle(activityEntity.getStreamTitle());
            activity.setStreamInfo(eventInfo);
        }

        Activity.ActivityUserInfo userInfo = new Activity.ActivityUserInfo();
        userInfo.setIdUser(activityEntity.getIdUser());
        userInfo.setUsername(activityEntity.getUsername());
        userInfo.setAvatar(activityEntity.getUserPhoto());
        activity.setUserInfo(userInfo);

        activity.setIdTargetUser(activityEntity.getIdTargetUser());

        if (activityEntity.getIdShot() != null) {
            Shot shot = activityEntity.getShotForMapping();
            checkNotNull(shot, "ActivityEntity has idShot but no ShotForMapping");
            activity.setShot(shot);
        }

        activity.setIdAuthorStream(activityEntity.getIdStreamAuthor());
        activity.setIdPoll(activityEntity.getIdPoll());
        activity.setPollQuestion(activityEntity.getPollQuestion());

        return activity;
    }

    public List<Activity> transform(List<ActivityEntity> activityEntities) {
        List<Activity> activities = new ArrayList<>(activityEntities.size());
        for (ActivityEntity activityEntity : activityEntities) {
            Activity activity = transform(activityEntity);
            if (activity != null) {
                activities.add(activity);
            }
        }
        return activities;
    }

    public ActivityEntity transform(Activity activity) {
        if (activity == null) {
            throw new IllegalArgumentException("Activity can't be null");
        }
        ActivityEntity activityEntity = new ActivityEntity();
        activityEntity.setIdActivity(activity.getIdActivity());
        activityEntity.setComment(activity.getComment());
        activityEntity.setType(activity.getType());

        String idUser = activity.getUserInfo().getIdUser();
        activityEntity.setIdUser(idUser);
        Activity.ActivityStreamInfo eventInfo = activity.getStreamInfo();
        if (eventInfo != null) {
            activityEntity.setIdStream(eventInfo.getIdStream());
            activityEntity.setStreamTitle(eventInfo.getStreamTitle());
        }
        Activity.ActivityUserInfo userInfo = activity.getUserInfo();
        if (userInfo != null) {
            activityEntity.setUsername(userInfo.getUsername());
            activityEntity.setIdUser(userInfo.getIdUser());
            activityEntity.setUserPhoto(userInfo.getAvatar());
        }

        activityEntity.setIdTargetUser(activity.getIdTargetUser());

        activityEntity.setSynchronizedStatus(Synchronized.SYNC_NEW);
        activityEntity.setIdPoll(activity.getIdPoll());
        activityEntity.setPollQuestion(activity.getPollQuestion());

        return activityEntity;
    }
}
