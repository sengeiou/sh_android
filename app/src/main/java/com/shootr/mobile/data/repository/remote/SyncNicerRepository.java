package com.shootr.mobile.data.repository.remote;

import com.shootr.mobile.data.mapper.NicerEntityMapper;
import com.shootr.mobile.data.repository.datasource.nicer.NicerDataSource;
import com.shootr.mobile.domain.Nicer;
import com.shootr.mobile.domain.repository.NicerRepository;
import java.util.List;
import javax.inject.Inject;

public class SyncNicerRepository implements NicerRepository {

    private final NicerDataSource nicerDataSource;
    private final NicerEntityMapper nicerEntityMapper;

    @Inject public SyncNicerRepository(NicerDataSource nicerDataSource, NicerEntityMapper nicerEntityMapper) {
        this.nicerDataSource = nicerDataSource;
        this.nicerEntityMapper = nicerEntityMapper;
    }

    @Override public List<Nicer> getNicers(String idShot) {
        return nicerEntityMapper.transform(nicerDataSource.getNicers(idShot));
    }

    @Override public List<Nicer> getNicersWithUser(String idShot) {
        return nicerEntityMapper.transform(nicerDataSource.getNicersWithUser(idShot));
    }
}
