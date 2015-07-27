package com.shootr.android.data.mapper;

import com.shootr.android.data.entity.ActivityEntity;
import com.shootr.android.data.entity.Synchronized;
import com.shootr.android.domain.Activity;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
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
            eventInfo.setStreamTag(activityEntity.getStreamTag());
            activity.setStreamInfo(eventInfo);
        }

        Activity.ActivityUserInfo userInfo = new Activity.ActivityUserInfo();
        userInfo.setIdUser(activityEntity.getIdUser());
        userInfo.setUsername(activityEntity.getUsername());
        userInfo.setAvatar(activityEntity.getUserPhoto());
        activity.setUserInfo(userInfo);

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
            activityEntity.setStreamTag(eventInfo.getStreamTag());
        }
        Activity.ActivityUserInfo userInfo = activity.getUserInfo();
        if (userInfo != null) {
            activityEntity.setUsername(userInfo.getUsername());
            activityEntity.setIdUser(userInfo.getIdUser());
            activityEntity.setUserPhoto(userInfo.getAvatar());
        }

        activityEntity.setSynchronizedStatus(Synchronized.SYNC_NEW);
        return activityEntity;
    }
}
