package com.shootr.mobile.data.api.entity.mapper;

import com.shootr.mobile.data.api.entity.ActivityApiEntity;
import com.shootr.mobile.data.api.entity.EmbedUserApiEntity;
import com.shootr.mobile.data.entity.ActivityEntity;
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
        activityEntity.setStrategic(userApiEntity.isStrategic());
        activityEntity.setName(userApiEntity.getName());


        EmbedUserApiEntity targetUser = activityApiEntity.getTargetUser();
        if (targetUser != null) {
            activityEntity.setIdTargetUser(targetUser.getIdUser());
            activityEntity.setTargetName(targetUser.getName());
            activityEntity.setTargetUsername(targetUser.getUserName());
            activityEntity.setTargetUserPhoto(targetUser.getPhoto());
        }

        activityEntity.setIdStream(activityApiEntity.getIdStream());
        activityEntity.setStreamTitle(activityApiEntity.getStreamTitle());

        activityEntity.setIdShot(activityApiEntity.getIdShot());

        activityEntity.setBirth(new Date(activityApiEntity.getBirth()));
        activityEntity.setModified(new Date(activityApiEntity.getModified()));
        activityEntity.setRevision(activityApiEntity.getRevision());
        activityEntity.setIdPoll(activityApiEntity.getIdPoll());
        activityEntity.setPollQuestion(activityApiEntity.getPollQuestion());

        if (activityApiEntity.getStream() != null) {
            activityEntity.setIdStreamAuthor(activityApiEntity.getStream().getIdUser());
            activityEntity.setVerified(activityApiEntity.getStream().getVerifiedUser() == 1L);
            activityEntity.setStreamPhoto(activityApiEntity.getStream().getPhoto());
        }

        if (activityApiEntity.getPollVote() != null) {
            activityEntity.setPollOptionText(activityApiEntity.getPollVote().getPollOptionText());
        }

        return activityEntity;
    }

    public List<ActivityEntity> transform(List<ActivityApiEntity> activities) {
        List<ActivityEntity> entities = new ArrayList<>(activities.size());
        for (ActivityApiEntity activity : activities) {
            if (activity.getUser() != null) {
                entities.add(transform(activity));
            }
        }
        return entities;
    }
}
