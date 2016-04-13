package com.shootr.mobile.domain.repository;

import com.shootr.mobile.domain.exception.NiceAlreadyMarkedException;
import com.shootr.mobile.domain.exception.NiceNotMarkedException;
import java.util.List;

public interface NiceShotRepository {

    void mark(String idShot) throws NiceAlreadyMarkedException;

    boolean isMarked(String idShot);

    void unmark(String idShot) throws NiceNotMarkedException;

    void markAll(List<String> nicedIdShots);
}
