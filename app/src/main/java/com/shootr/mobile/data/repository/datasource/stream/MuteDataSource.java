package com.shootr.mobile.data.repository.datasource.stream;

import com.shootr.mobile.data.entity.MuteStreamEntity;
import java.util.List;

public interface MuteDataSource {

    void mute(MuteStreamEntity muteStreamEntity);

    void unmute(String idStream);

    List<MuteStreamEntity> getMutedStreamEntities();

    List<MuteStreamEntity> getEntitiesNotSynchronized();

    void putMuteds(List<MuteStreamEntity> muteStreamEntities);

    MuteStreamEntity getMute(String idStream);
}