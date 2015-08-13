package com.shootr.android.data.repository.remote;

import com.shootr.android.domain.exception.NiceAlreadyMarkedException;
import com.shootr.android.domain.exception.NiceNotMarkedException;
import com.shootr.android.domain.repository.NiceShotRepository;
import com.shootr.android.domain.service.shot.ShootrShotService;
import javax.inject.Inject;

public class RemoteNiceShotRepository implements NiceShotRepository {

    private final ShootrShotService shootrShotService;

    @Inject public RemoteNiceShotRepository(ShootrShotService shootrShotService) {
        this.shootrShotService = shootrShotService;
    }

    @Override
    public void mark(String idShot) throws NiceAlreadyMarkedException {
        shootrShotService.markNiceShot(idShot);
    }

    @Override
    public boolean isMarked(String idShot) {
        throw new IllegalStateException("Server doesn't allow checking nice status");
    }

    @Override
    public void unmark(String idShot) throws NiceNotMarkedException {
        shootrShotService.unmarkNiceShot(idShot);
    }
}
