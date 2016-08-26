package com.shootr.mobile.data.api.entity.mapper;

import com.shootr.mobile.data.entity.ShotEventEntity;
import com.shootr.mobile.domain.model.shot.ShotEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;

public class ShotEventEntityMapper {

    @Inject public ShotEventEntityMapper() {
    }

    public ShotEventEntity transform(ShotEvent shotEvent) {
        if (shotEvent == null) {
            return null;
        }

        ShotEventEntity shotEventEntity = new ShotEventEntity();
        shotEventEntity.setIdShot(shotEvent.getIdShot());
        shotEventEntity.setType(shotEvent.getType());
        shotEventEntity.setTimestamp(new Date().getTime());

        return shotEventEntity;
    }

    public ShotEvent transform(ShotEventEntity shotEventEntity) {
        if (shotEventEntity == null) {
            return null;
        }
        ShotEvent shotEvent = new ShotEvent();
        shotEvent.setIdShot(shotEvent.getIdShot());
        shotEvent.setType(shotEvent.getType());

        return shotEvent;
    }

    public List<ShotEvent> transform(List<ShotEventEntity> shotEventEntities) {
        List<ShotEvent> shotEvents = new ArrayList<>(shotEventEntities.size());
        for (ShotEventEntity shotEventEntity : shotEventEntities) {
            shotEvents.add(transform(shotEventEntity));
        }
        return shotEvents;
    }
}
