package com.shootr.android.data.api.entity.mapper;

import com.shootr.android.data.api.entity.ActivityApiEntity;
import com.shootr.android.data.api.entity.EmbedUserApiEntity;
import com.shootr.android.data.entity.ActivityEntity;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;

public class ActivityApiEntityMapper {

    @Inject public ActivityApiEntityMapper() {
    }

    public ActivityEntity transform(ActivityApiEntity activityApiEntity) {
        ActivityEntity activityEntity = new ActivityEntity();

        activityEntity.setIdActivity(activityApiEntity.getIdActivity());
        activityEntity.setComment(activityApiEntity.getComment());
        activityEntity.setType(activityApiEntity.getType());

        EmbedUserApiEntity userApiEntity = activityApiEntity.getUser();
        activityEntity.setUsername(userApiEntity.getUserName());
        activityEntity.setIdUser(userApiEntity.getIdUser());
        activityEntity.setUserPhoto(userApiEntity.getPhoto());

        activityEntity.setIdEvent(activityApiEntity.getIdEvent());
        activityEntity.setEventTag(activityApiEntity.getEventTag());
        activityEntity.setEventTitle(activityApiEntity.getEventTitle());

        activityEntity.setBirth(new Date(activityApiEntity.getBirth()));
        activityEntity.setModified(new Date(activityApiEntity.getModified()));
        activityEntity.setRevision(activityApiEntity.getRevision());

        return activityEntity;
    }

    public List<ActivityEntity> transform(List<ActivityApiEntity> activities) {
        List<ActivityEntity> entities = new ArrayList<>(activities.size());
        for (ActivityApiEntity activity : activities) {
            entities.add(transform(activity));
        }
        return entities;
    }
}