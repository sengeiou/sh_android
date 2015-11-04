package com.shootr.mobile.domain.repository;

import java.util.Map;

public interface WatchersRepository {

    Integer getWatchers(String streamId);

    Map<String, Integer> getWatchers();
}
