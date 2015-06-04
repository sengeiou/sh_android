package com.shootr.android.domain.repository;

import java.util.Map;

public interface WatchersRepository {

    Integer getWatchers(String eventId);

    Map<String, Integer> getWatchers();
}
