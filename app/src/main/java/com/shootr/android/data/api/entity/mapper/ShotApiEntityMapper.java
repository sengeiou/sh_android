package com.shootr.android.data.api.entity.mapper;

import com.shootr.android.data.api.entity.EmbedUserApiEntity;
import com.shootr.android.data.api.entity.ShotApiEntity;
import com.shootr.android.data.entity.ShotEntity;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;

public class ShotApiEntityMapper {

    @Inject
    public ShotApiEntityMapper() {
    }

    public ShotEntity transform(ShotApiEntity shotApiEntity) {
        ShotEntity shotEntity = new ShotEntity();
        shotEntity.setIdShot(shotApiEntity.getIdShot());
        shotEntity.setComment(shotApiEntity.getComment());
        shotEntity.setImage(shotApiEntity.getImage());
        shotEntity.setType(shotApiEntity.getType());

        EmbedUserApiEntity userApiEntity = shotApiEntity.getUser();
        shotEntity.setIdUser(userApiEntity.getIdUser());
        shotEntity.setUsername(userApiEntity.getUserName());
        shotEntity.setUserPhoto(userApiEntity.getPhoto());

        shotEntity.setIdShotParent(shotApiEntity.getIdShotParent());
        shotEntity.setIdUserParent(shotApiEntity.getIdUserParent());
        shotEntity.setUserNameParent(shotApiEntity.getUserNameParent());

        shotEntity.setIdEvent(shotApiEntity.getIdEvent());
        shotEntity.setEventTitle(shotApiEntity.getEventTitle());
        shotEntity.setEventTag(shotApiEntity.getEventTag());

        shotEntity.setVideoUrl(shotApiEntity.getVideoUrl());
        shotEntity.setVideoTitle(shotApiEntity.getVideoTitle());
        shotEntity.setVideoDuration(shotApiEntity.getVideoDuration());

        shotEntity.setCsysBirth(new Date(shotApiEntity.getBirth()));
        shotEntity.setCsysModified(new Date(shotApiEntity.getModified()));
        shotEntity.setCsysRevision(shotApiEntity.getRevision());

        return shotEntity;
    }

    public List<ShotEntity> transform(List<ShotApiEntity> shots) {
        List<ShotEntity> entities = new ArrayList<>(shots.size());
        for (ShotApiEntity apiShot : shots) {
            entities.add(transform(apiShot));
        }
        return entities;
    }
}
