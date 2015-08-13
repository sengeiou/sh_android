package com.shootr.android.data.repository.local;

import com.shootr.android.db.manager.NiceManager;
import com.shootr.android.domain.exception.NiceAlreadyMarkedException;
import com.shootr.android.domain.repository.NiceShotRepository;
import javax.inject.Inject;

public class LocalNiceShotRepository implements NiceShotRepository {

    private final NiceManager niceManager;

    @Inject public LocalNiceShotRepository(NiceManager niceManager) {
        this.niceManager = niceManager;
    }

    @Override public void mark(String idShot) throws NiceAlreadyMarkedException {
        niceManager.mark(idShot);
    }

    @Override
    public boolean isMarked(String idShot) {
        return niceManager.isMarked(idShot);
    }

    @Override
    public void unmark(String idShot) {
        niceManager.unmark(idShot);
    }
}
