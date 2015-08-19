package com.shootr.android.data.repository.local;

import com.shootr.android.db.manager.NiceManager;
import com.shootr.android.domain.exception.NiceAlreadyMarkedException;
import com.shootr.android.domain.exception.NiceNotMarkedException;
import com.shootr.android.domain.repository.NiceShotRepository;
import java.lang.ref.WeakReference;
import java.util.Set;
import javax.inject.Inject;

public class LocalNiceShotRepository implements NiceShotRepository {

    private final NiceManager niceManager;
    private WeakReference<Set<String>> markedShots;

    @Inject
    public LocalNiceShotRepository(NiceManager niceManager) {
        this.niceManager = niceManager;
    }

    @Override
    public void mark(String idShot) throws NiceAlreadyMarkedException {
        initMarkedShots();
        getMarkedShots().add(idShot);
        niceManager.mark(idShot);
    }

    @Override
    public boolean isMarked(String idShot) {
        initMarkedShots();
        return getMarkedShots().contains(idShot);
    }

    private void initMarkedShots() {
        if (markedShots == null || getMarkedShots() == null) {
            markedShots = new WeakReference<>(niceManager.getAllMarked());
        }
    }

    @Override
    public void unmark(String idShot) throws NiceNotMarkedException {
        initMarkedShots();
        getMarkedShots().remove(idShot);
        niceManager.unmark(idShot);
    }

    public Set<String> getMarkedShots() {
        return markedShots.get();
    }
}
