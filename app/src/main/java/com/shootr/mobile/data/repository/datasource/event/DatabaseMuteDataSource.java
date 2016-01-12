package com.shootr.mobile.data.repository.datasource.event;

import com.shootr.mobile.data.entity.MuteStreamEntity;
import com.shootr.mobile.db.manager.MuteManager;
import java.util.List;
import javax.inject.Inject;

public class DatabaseMuteDataSource implements MuteDataSource {

    private final MuteManager muteManager;

    @Inject public DatabaseMuteDataSource(MuteManager muteManager) {
        this.muteManager = muteManager;
    }

    @Override public void mute(MuteStreamEntity muteStreamEntity) {
        muteManager.mute(muteStreamEntity);
    }

    @Override public void unmute(String idStream) {
        muteManager.unmute(idStream);
    }

    @Override public List<MuteStreamEntity> getMutedStreamEntities() {
        return muteManager.getMutes();
    }

    @Override public List<MuteStreamEntity> getEntitiesNotSynchronized() {
        return muteManager.getMutesNotSynchronized();
    }

    @Override public void putMuteds(List<MuteStreamEntity> muteStreamEntities) {
        muteManager.saveMutesFromServer(muteStreamEntities);
    }
}
