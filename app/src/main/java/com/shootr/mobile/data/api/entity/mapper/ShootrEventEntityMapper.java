package com.shootr.mobile.data.api.entity.mapper;

import com.shootr.mobile.data.entity.ShootrEventEntity;
import com.shootr.mobile.domain.model.shot.ShootrEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;

public class ShotEventEntityMapper {

    @Inject public ShotEventEntityMapper() {
    }

    public ShootrEventEntity transform(ShootrEvent shootrEvent) {
        if (shootrEvent == null) {
            return null;
        }

        ShootrEventEntity shootrEventEntity = new ShootrEventEntity();
        shootrEventEntity.setIdShot(shootrEvent.getIdShot());
        shootrEventEntity.setType(shootrEvent.getType());
        shootrEventEntity.setTimestamp(new Date().getTime());

        return shootrEventEntity;
    }

    public ShootrEvent transform(ShootrEventEntity shootrEventEntity) {
        if (shootrEventEntity == null) {
            return null;
        }
        ShootrEvent shootrEvent = new ShootrEvent();
        shootrEvent.setIdShot(shootrEvent.getIdShot());
        shootrEvent.setType(shootrEvent.getType());

        return shootrEvent;
    }

    public List<ShootrEvent> transform(List<ShootrEventEntity> shotEventEntities) {
        List<ShootrEvent> shootrEvents = new ArrayList<>(shotEventEntities.size());
        for (ShootrEventEntity shootrEventEntity : shotEventEntities) {
            shootrEvents.add(transform(shootrEventEntity));
        }
        return shootrEvents;
    }
}
