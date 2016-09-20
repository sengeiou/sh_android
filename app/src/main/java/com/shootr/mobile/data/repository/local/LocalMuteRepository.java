package com.shootr.mobile.data.repository.local;

import com.shootr.mobile.data.entity.MuteStreamEntity;
import com.shootr.mobile.data.entity.Synchronized;
import com.shootr.mobile.data.repository.datasource.stream.MuteDataSource;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.stream.MuteRepository;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;

public class LocalMuteRepository implements MuteRepository {

    private final MuteDataSource muteDataSource;
    private final MuteRepository remoteMuteRepository;

    @Inject
    public LocalMuteRepository(@Local MuteDataSource muteDataSource, @Remote MuteRepository remoteMuteRepository) {
        this.muteDataSource = muteDataSource;
        this.remoteMuteRepository = remoteMuteRepository;
    }

    @Override public void mute(String idStream) {
        MuteStreamEntity muteStreamEntity = createMute(idStream);
        muteDataSource.mute(muteStreamEntity);
        remoteMuteRepository.dispatchSync();
    }

    @Override public void unmute(String idStream) {
        MuteStreamEntity muteStreamEntity = muteDataSource.getMute(idStream);
        muteStreamEntity.setSynchronizedStatus(Synchronized.SYNC_DELETED);
        muteDataSource.mute(muteStreamEntity);
        remoteMuteRepository.dispatchSync();
    }

    @Override public List<String> getMutedIdStreams() {
        remoteMuteRepository.dispatchSync();
        List<MuteStreamEntity> mutedStreamEntities = muteDataSource.getMutedStreamEntities();
        List<String> mutedIds = new ArrayList<>();
        for (MuteStreamEntity muteStreamEntity : mutedStreamEntities) {
            mutedIds.add(muteStreamEntity.getIdStream());
        }
        return mutedIds;
    }

    @Override public void dispatchSync() {
        throw new IllegalArgumentException("method forbidden");
    }

    private MuteStreamEntity createMute(String idStream) {
        MuteStreamEntity muteStreamEntity = new MuteStreamEntity();
        muteStreamEntity.setIdStream(idStream);
        muteStreamEntity.setBirth(new Date());
        return muteStreamEntity;
    }
}
