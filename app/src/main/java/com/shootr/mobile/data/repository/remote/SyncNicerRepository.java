package com.shootr.mobile.data.repository.remote;

import com.shootr.mobile.data.entity.NicerEntity;
import com.shootr.mobile.data.mapper.NicerEntityMapper;
import com.shootr.mobile.data.repository.datasource.nicer.NicerDataSource;
import com.shootr.mobile.data.repository.datasource.user.UserDataSource;
import com.shootr.mobile.domain.Nicer;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.NicerRepository;
import com.shootr.mobile.domain.repository.SessionRepository;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class SyncNicerRepository implements NicerRepository {

    private final NicerDataSource nicerDataSource;
    private final NicerEntityMapper nicerEntityMapper;
    private final SessionRepository sessionRepository;
    private final UserDataSource remoteUserDataSource;

    @Inject public SyncNicerRepository(NicerDataSource nicerDataSource, NicerEntityMapper nicerEntityMapper,
      SessionRepository sessionRepository, @Local UserDataSource remoteUserDataSource) {
        this.nicerDataSource = nicerDataSource;
        this.nicerEntityMapper = nicerEntityMapper;
        this.sessionRepository = sessionRepository;
        this.remoteUserDataSource = remoteUserDataSource;
    }

    @Override public List<Nicer> getNicers(String idShot) {
        return nicerEntityMapper.transform(nicerDataSource.getNicers(idShot));
    }

    @Override public List<Nicer> getNicersWithUser(String idShot) {
        return transformNicersEntities(nicerDataSource.getNicersWithUser(idShot));
    }

    @Override public List<Nicer> getNices(String idUser) {
        return transformNicersEntities(nicerDataSource.getNices(idUser));
    }

    private List<Nicer> transformNicersEntities(List<NicerEntity> nicerEntityList) {
        List<Nicer> nicers = new ArrayList<>(nicerEntityList.size());
        for (NicerEntity nicerEntity : nicerEntityList) {
            Nicer nicer = nicerEntityMapper.transform(nicerEntity,
              sessionRepository.getCurrentUserId(),
              isFollower(nicerEntity.getIdUser()),
              isFollowing(nicerEntity.getIdUser()));
            nicers.add(nicer);
        }
        return nicers;
    }

    private boolean isFollower(String userId) {
        return remoteUserDataSource.isFollower(sessionRepository.getCurrentUserId(), userId);
    }

    private boolean isFollowing(String userId) {
        return remoteUserDataSource.isFollowing(sessionRepository.getCurrentUserId(), userId);
    }
}
