package com.shootr.android.domain.repository;

import java.util.Map;

public interface WatchersRepository {

    Integer getWatchers(String streamId);

    Map<String, Integer> getWatchers();
}
