package com.shootr.mobile.ui.model.mappers;

import com.shootr.mobile.domain.model.activity.Activity;
import com.shootr.mobile.domain.model.activity.ActivityType;
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
        activityModel.setPollOptionText(activity.getPollOptionText());
        return activityModel;
    }

    public List<ActivityModel> mapWithFollowingsAndFavorites(List<Activity> activities,
        ArrayList<String> followingsIds, ArrayList<String> favoritesIds) {
        ArrayList<ActivityModel> activityModels = new ArrayList<>();
        for (Activity activity : activities) {
            ActivityModel activityModel = map(activity);
            if (activityModel.getType().equals(ActivityType.START_FOLLOW)) {
                activityModel.setAmIFollowing(followingsIds.contains(activityModel.getIdUser()));
            } else if (activityModel.getType().equals(ActivityType.STARTED_SHOOTING)) {
                activityModel.setFavorite(favoritesIds.contains(activityModel.getIdStream()));
            }
            activityModels.add(activityModel);
        }
        return activityModels;
    }

    @Override public Activity reverseMap(ActivityModel value) {
        return null;
    }
}
