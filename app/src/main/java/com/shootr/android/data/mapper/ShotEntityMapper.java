package com.shootr.android.data.mapper;

import com.shootr.android.data.entity.ShotEntity;
import com.shootr.android.data.entity.Synchronized;
import com.shootr.android.domain.Shot;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ShotEntityMapper {

    private  final UserAvatarUrlBuilder avatarBuilder;

    @Inject public ShotEntityMapper(UserAvatarUrlBuilder avatarBuilder) {
        this.avatarBuilder = avatarBuilder;
    }

    public Shot transform(ShotEntity shotEntity) {
        if (shotEntity == null) {
            return null;
        }
        Shot shot = new Shot();
        shot.setIdShot(shotEntity.getIdShot());
        shot.setComment(shotEntity.getComment());
        shot.setImage(shotEntity.getImage());
        shot.setPublishDate(shotEntity.getCsysBirth());
        if (shotEntity.getIdEvent() != null) {
            Shot.ShotEventInfo eventInfo = new Shot.ShotEventInfo();
            eventInfo.setIdEvent(shotEntity.getIdEvent());
            eventInfo.setEventTitle(shotEntity.getEventTitle());
            eventInfo.setEventTag(shotEntity.getEventTag());
            shot.setEventInfo(eventInfo);
        }

        Shot.ShotUserInfo userInfo = new Shot.ShotUserInfo();
        userInfo.setIdUser(shotEntity.getIdUser());
        userInfo.setUsername(shotEntity.getUsername());
        userInfo.setAvatar(avatarBuilder.thumbnail(shotEntity.getIdUser()));
        shot.setUserInfo(userInfo);

        String idShotParent = shotEntity.getIdShotParent();
        shot.setParentShotId(idShotParent != null ? idShotParent : null);
        String idUserParent = shotEntity.getIdUserParent();
        shot.setParentShotUserId(idUserParent != null? idUserParent : null);
        String userNameParent = shotEntity.getUserNameParent();
        shot.setParentShotUsername(userNameParent);

        return shot;
    }

    public List<Shot> transform(List<ShotEntity> shotEntities) {
        List<Shot> shots = new ArrayList<>(shotEntities.size());
        for (ShotEntity shotEntity : shotEntities) {
            Shot shot = transform(shotEntity);
            if (shot != null) {
                shots.add(shot);
            }
        }
        return shots;
    }

    public ShotEntity transform(Shot shot) {
        if (shot == null) {
            throw new IllegalArgumentException("Shot can't be null");
        }
        ShotEntity shotEntity = new ShotEntity();
        String idShot = shot.getIdShot();
        shotEntity.setIdShot(idShot != null && idShot != "null"? idShot : null);
        shotEntity.setComment(shot.getComment());
        shotEntity.setImage(shot.getImage());
        shotEntity.setType(ShotEntity.TYPE_COMMENT);
        String idUser = shot.getUserInfo().getIdUser();
        shotEntity.setIdUser(idUser != null? idUser : null);
        Shot.ShotEventInfo eventInfo = shot.getEventInfo();
        if (eventInfo != null) {
            shotEntity.setIdEvent(eventInfo.getIdEvent());
            shotEntity.setEventTitle(eventInfo.getEventTitle());
            shotEntity.setEventTag(eventInfo.getEventTag());
        }

        String parentShotId = shot.getParentShotId();
        shotEntity.setIdShotParent(parentShotId != null && parentShotId != "null" ? parentShotId : null );
        String parentShotUserId = shot.getParentShotUserId();
        shotEntity.setIdUserParent(parentShotUserId != null && parentShotUserId != "null" ? parentShotUserId : null);
        String parentShotUsername = shot.getParentShotUsername();
        shotEntity.setUserNameParent(parentShotUsername != "" ? parentShotUsername : null);

        shotEntity.setCsysSynchronized(Synchronized.SYNC_NEW);
        return shotEntity;
    }
}
