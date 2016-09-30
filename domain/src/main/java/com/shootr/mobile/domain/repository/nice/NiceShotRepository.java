package com.shootr.mobile.domain.repository.nice;

import com.shootr.mobile.domain.exception.NiceAlreadyMarkedException;
import com.shootr.mobile.domain.exception.NiceNotMarkedException;

public interface NiceShotRepository {

    void mark(String idShot) throws NiceAlreadyMarkedException;

    void unmark(String idShot) throws NiceNotMarkedException;
}
