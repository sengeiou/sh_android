package com.shootr.android.ui.model.mappers;

import com.shootr.android.domain.Activity;
import com.shootr.android.ui.model.ActivityModel;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class ActivityModelMapper {

    @Inject public ActivityModelMapper() {
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

        Activity.ActivityEventInfo eventInfo = activity.getEventInfo();
        if (eventInfo != null) {
            activityModel.setIdEvent(eventInfo.getIdEvent());
            activityModel.setEventTag(eventInfo.getEventTag());
            activityModel.setEventTitle(eventInfo.getEventTitle());
        }

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
