package com.shootr.android.data.mapper;

import com.shootr.android.data.entity.ShotEntity;
import com.shootr.android.data.entity.Synchronized;
import com.shootr.android.domain.Shot;
import com.shootr.android.domain.User;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ShotEntityMapper {

    @Inject public ShotEntityMapper() {
    }

    //TODO don't like having to provide the user. But the service doesn't provide it embedded with the shot.
    public Shot transform(ShotEntity shotEntity, User user) {
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
        if (!user.getIdUser().equals(shotEntity.getIdUser())) {
            throw new IllegalArgumentException(
              String.format("User id %d and shot's user id %d doesn't match", user.getIdUser(),
                shotEntity.getIdUser()));
        }
        Shot.ShotUserInfo userInfo = new Shot.ShotUserInfo();
        userInfo.setIdUser(user.getIdUser());
        userInfo.setUsername(user.getUsername());
        userInfo.setAvatar(user.getPhoto());
        shot.setUserInfo(userInfo);
        return shot;
    }

    public ShotEntity transform(Shot shot) {
        if (shot == null) {
            throw new IllegalArgumentException("Shot can't be null");
        }
        ShotEntity shotEntity = new ShotEntity();
        shotEntity.setIdShot(shot.getIdShot());
        shotEntity.setComment(shot.getComment());
        shotEntity.setImage(shot.getImage());
        shotEntity.setType(ShotEntity.TYPE_COMMENT);
        shotEntity.setIdUser(shot.getUserInfo().getIdUser());
        shotEntity.setIdEvent(shot.getEventInfo().getIdEvent());
        shotEntity.setEventTitle(shot.getEventInfo().getEventTitle());
        shotEntity.setEventTag(shot.getEventInfo().getEventTag());

        shotEntity.setCsysSynchronized(Synchronized.SYNC_NEW);
        return shotEntity;
    }
}
