package com.shootr.android.data.mapper;

import com.shootr.android.data.entity.ShotEntity;
import com.shootr.android.data.entity.ShotQueueEntity;
import com.shootr.android.domain.QueuedShot;
import com.shootr.android.domain.Shot;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class ShotQueueEntityMapper {

    private final UserAvatarUrlBuilder avatarBuilder;

    @Inject public ShotQueueEntityMapper(UserAvatarUrlBuilder avatarBuilder) {
        this.avatarBuilder = avatarBuilder;
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
        entity.setIdShot(idShot != null ? idShot : null);
        entity.setComment(shot.getComment());
        entity.setImage(shot.getImage());
        entity.setCsysBirth(shot.getPublishDate());
        String parentShotId = shot.getParentShotId();
        entity.setIdShotParent(parentShotId != null ? parentShotId : null);
        String parentShotUserId = shot.getParentShotUserId();
        entity.setIdUserParent(parentShotUserId!= null ? parentShotUserId : null);
        String parentShotUsername = shot.getParentShotUsername();
        entity.setUserNameParent(parentShotUsername != null ? parentShotUsername : null);

        Shot.ShotEventInfo eventInfo = shot.getEventInfo();
        if (eventInfo != null) {
            entity.setIdEvent(eventInfo.getIdEvent());
            entity.setEventTitle(eventInfo.getEventTitle());
            entity.setEventTag(eventInfo.getEventTag());
        }

        entity.setIdUser(shot.getUserInfo().getIdUser());
        entity.setUsername(shot.getUserInfo().getUsername());

        entity.setType(ShotEntity.TYPE_COMMENT);
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
        shot.setPublishDate(entity.getCsysBirth());

        shot.setIdQueue(entity.getIdQueue());

        Shot.ShotUserInfo userInfo = new Shot.ShotUserInfo();
        userInfo.setIdUser(entity.getIdUser());
        userInfo.setUsername(entity.getUsername());
        userInfo.setAvatar(avatarBuilder.thumbnail(entity.getIdUser()));
        shot.setUserInfo(userInfo);

        Shot.ShotEventInfo eventInfo = new Shot.ShotEventInfo();
        eventInfo.setIdEvent(entity.getIdEvent());
        eventInfo.setEventTitle(entity.getEventTitle());
        eventInfo.setEventTag(entity.getEventTag());
        if (eventInfo.getIdEvent() != null) {
            shot.setEventInfo(eventInfo);
        }

        String idShotParent = entity.getIdShotParent();
        shot.setParentShotId(idShotParent);
        String idUserParent = entity.getIdUserParent();
        shot.setParentShotUserId(idUserParent);
        String userNameParent = entity.getUserNameParent();
        shot.setParentShotUsername(userNameParent);

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
