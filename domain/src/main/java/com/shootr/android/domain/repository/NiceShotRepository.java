package com.shootr.android.domain.repository;

import com.shootr.android.domain.exception.NiceAlreadyMarkedException;
import com.shootr.android.domain.exception.NiceNotMarkedException;

public interface NiceShotRepository {

    void mark(String idShot) throws NiceAlreadyMarkedException;

    boolean isMarked(String idShot);

    void unmark(String idShot) throws NiceNotMarkedException;
}
