package com.shootr.android.domain.repository;

public interface NiceShotRepository {

    void mark(String idShot);

    boolean isMarked(String idShot);

    void unmark(String idShot);
}
