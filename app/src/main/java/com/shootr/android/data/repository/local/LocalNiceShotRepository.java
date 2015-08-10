package com.shootr.android.data.repository.local;

import com.shootr.android.domain.repository.NiceShotRepository;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class LocalNiceShotRepository implements NiceShotRepository {

    private List<String> markedShots;

    @Inject public LocalNiceShotRepository() {
        markedShots = new ArrayList<>();
    }

    @Override public void mark(String idShot) {
        markedShots.add(idShot);
    }

    @Override
    public boolean isMarked(String idShot) {
        return markedShots.contains(idShot);
    }

    @Override
    public void unmark(String idShot) {
        markedShots.remove(idShot);
    }
}
