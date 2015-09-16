package com.shootr.android.domain.repository;

import com.shootr.android.domain.exception.NiceAlreadyMarkedException;
import com.shootr.android.domain.exception.NiceNotMarkedException;
import com.shootr.android.domain.exception.ShotRemovedException;

public interface NiceShotRepository {

    void mark(String idShot) throws NiceAlreadyMarkedException, ShotRemovedException;

    boolean isMarked(String idShot);

    void unmark(String idShot) throws NiceNotMarkedException, ShotRemovedException;
}
