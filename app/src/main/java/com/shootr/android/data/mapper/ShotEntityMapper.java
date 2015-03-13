package com.shootr.android.data.mapper;

import android.support.v4.util.LongSparseArray;
import com.shootr.android.data.entity.ShotEntity;
import com.shootr.android.data.entity.Synchronized;
import com.shootr.android.domain.Shot;
import com.shootr.android.domain.User;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ShotEntityMapper {

    @Inject public ShotEntityMapper() {
    }

    //TODO don't like having to provide the user. But the service doesn't provide it embedded with the shot.
    public Shot transform(ShotEntity shotEntity, User user) {
        if (shotEntity == null || user == null) {
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
        if (!user.getIdUser().equals(shotEntity.getIdUser())) {
            throw new IllegalArgumentException(String.format("User id %d and shot's user id %d doesn't match",
              user.getIdUser(),
              shotEntity.getIdUser()));
        }
        Shot.ShotUserInfo userInfo = new Shot.ShotUserInfo();
        userInfo.setIdUser(user.getIdUser());
        userInfo.setUsername(user.getUsername());
        userInfo.setAvatar(user.getPhoto());
        shot.setUserInfo(userInfo);
        return shot;
    }

    public List<Shot> transform(List<ShotEntity> shotEntities, List<User> users) {
        LongSparseArray<User> usersSparseArray = new LongSparseArray<>(users.size());
        for (User user : users) {
            if (user != null) {
                usersSparseArray.put(user.getIdUser(), user);
            }
        }

        List<Shot> shots = new ArrayList<>(shotEntities.size());
        for (ShotEntity shotEntity : shotEntities) {
            Shot shot = transform(shotEntity, usersSparseArray.get(shotEntity.getIdUser()));
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
        shotEntity.setIdShot(shot.getIdShot());
        shotEntity.setComment(shot.getComment());
        shotEntity.setImage(shot.getImage());
        shotEntity.setType(ShotEntity.TYPE_COMMENT);
        shotEntity.setIdUser(shot.getUserInfo().getIdUser());
        Shot.ShotEventInfo eventInfo = shot.getEventInfo();
        if (eventInfo != null) {
            shotEntity.setIdEvent(eventInfo.getIdEvent());
            shotEntity.setEventTitle(eventInfo.getEventTitle());
            shotEntity.setEventTag(eventInfo.getEventTag());
        }

        shotEntity.setCsysSynchronized(Synchronized.SYNC_NEW);
        return shotEntity;
    }
}
