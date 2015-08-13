package com.shootr.android.domain.repository;

import com.shootr.android.domain.exception.NiceAlreadyMarkedException;

public interface NiceShotRepository {

    void mark(String idShot) throws NiceAlreadyMarkedException;

    boolean isMarked(String idShot);

    void unmark(String idShot);
}
