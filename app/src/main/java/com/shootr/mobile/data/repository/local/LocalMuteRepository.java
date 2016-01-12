package com.shootr.mobile.data.repository.local;

import com.shootr.mobile.data.entity.MuteStreamEntity;
import com.shootr.mobile.data.repository.datasource.event.MuteDataSource;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.MuteRepository;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class LocalMuteRepository implements MuteRepository {

    private final MuteDataSource muteDataSource;

    @Inject public LocalMuteRepository(@Local MuteDataSource muteDataSource) {
        this.muteDataSource = muteDataSource;
    }

    @Override public void mute(String idStream) {
        MuteStreamEntity muteStreamEntity = createMute(idStream);
        muteDataSource.mute(muteStreamEntity);
    }

    @Override public void unmute(String idStream) {
        muteDataSource.unmute(idStream);
    }

    @Override public List<String> getMutedIdStreams() {
        List<MuteStreamEntity> mutedStreamEntities = muteDataSource.getMutedStreamEntities();
        List<String> mutedIds = new ArrayList<>();
        for (MuteStreamEntity muteStreamEntity : mutedStreamEntities) {
            mutedIds.add(muteStreamEntity.getIdStream());
        }
        return mutedIds;
    }

    private MuteStreamEntity createMute(String idStream) {
        MuteStreamEntity muteStreamEntity = new MuteStreamEntity();
        muteStreamEntity.setIdStream(idStream);
        return muteStreamEntity;
    }
}
