package com.shootr.mobile.data.mapper;

import com.shootr.mobile.data.entity.ShotQueueEntity;
import com.shootr.mobile.domain.model.shot.QueuedShot;
import com.shootr.mobile.domain.model.shot.Shot;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class ShotQueueEntityMapper {

    @Inject public ShotQueueEntityMapper() {
    }

    public ShotQueueEntity transform(QueuedShot queuedShot) {
        if (queuedShot == null) {
            return null;
        }
        ShotQueueEntity entity = new ShotQueueEntity();
        entity.setIdQueue(queuedShot.getIdQueue());
        entity.setFailed(queuedShot.isFailed() ? 1 : 0);
        File imageFile = queuedShot.getImageFile();
        if (imageFile != null) {
            entity.setImageFile(imageFile.getAbsolutePath());
        }

        Shot shot = queuedShot.getShot();
        String idShot = shot.getIdShot();
        entity.setIdShot(idShot);
        entity.setComment(shot.getComment());
        entity.setImage(shot.getImage());
        entity.setBirth(shot.getPublishDate());
        entity.setIdShotParent(shot.getParentShotId());
        entity.setIdUserParent(shot.getParentShotUserId());
        entity.setUserNameParent(shot.getParentShotUsername());

        Shot.ShotStreamInfo eventInfo = shot.getStreamInfo();
        if (eventInfo != null) {
            entity.setIdStream(eventInfo.getIdStream());
            entity.setStreamTitle(eventInfo.getStreamTitle());
        }

        entity.setIdUser(shot.getUserInfo().getIdUser());
        entity.setUsername(shot.getUserInfo().getUsername());

        entity.setVideoUrl(shot.getVideoUrl());
        entity.setVideoTitle(shot.getVideoTitle());
        entity.setVideoDuration(shot.getVideoDuration());

        entity.setType(shot.getType());
        return entity;
    }

    public QueuedShot transform(ShotQueueEntity entity) {
        if (entity == null) {
            return null;
        }
        QueuedShot queuedShot = new QueuedShot();
        queuedShot.setIdQueue(entity.getIdQueue());
        queuedShot.setFailed(entity.getFailed() == 1);
        String imageFile = entity.getImageFile();
        if (imageFile != null) {
            queuedShot.setImageFile(new File(imageFile));
        }

        Shot shot = new Shot();
        shot.setIdShot(entity.getIdShot());
        shot.setComment(entity.getComment());
        shot.setImage(entity.getImage());
        shot.setPublishDate(entity.getBirth());

        shot.setIdQueue(entity.getIdQueue());

        Shot.ShotUserInfo userInfo = new Shot.ShotUserInfo();
        userInfo.setIdUser(entity.getIdUser());
        userInfo.setUsername(entity.getUsername());
        userInfo.setAvatar(entity.getUserPhoto());
        shot.setUserInfo(userInfo);

        Shot.ShotStreamInfo eventInfo = new Shot.ShotStreamInfo();
        eventInfo.setIdStream(entity.getIdStream());
        eventInfo.setStreamTitle(entity.getStreamTitle());
        if (eventInfo.getIdStream() != null) {
            shot.setStreamInfo(eventInfo);
        }

        shot.setParentShotId(entity.getIdShotParent());
        shot.setParentShotUserId(entity.getIdUserParent());
        shot.setParentShotUsername(entity.getUserNameParent());

        shot.setVideoUrl(entity.getVideoUrl());
        shot.setVideoTitle(entity.getVideoTitle());
        shot.setVideoDuration(entity.getVideoDuration());

        shot.setType(entity.getType());

        queuedShot.setShot(shot);
        return queuedShot;
    }

    public List<QueuedShot> transform(List<ShotQueueEntity> shotQueueEntities) {
        List<QueuedShot> results = new ArrayList<>(shotQueueEntities.size());
        for (ShotQueueEntity shotQueueEntity : shotQueueEntities) {
            results.add(transform(shotQueueEntity));
        }
        return results;
    }
}
