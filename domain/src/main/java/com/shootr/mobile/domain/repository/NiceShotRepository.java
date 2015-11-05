package com.shootr.mobile.domain.repository;

import com.shootr.mobile.domain.exception.NiceAlreadyMarkedException;
import com.shootr.mobile.domain.exception.NiceNotMarkedException;

public interface NiceShotRepository {

    void mark(String idShot) throws NiceAlreadyMarkedException;

    boolean isMarked(String idShot);

    void unmark(String idShot) throws NiceNotMarkedException;
}
