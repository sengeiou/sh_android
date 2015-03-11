package com.shootr.android.data.repository;

import com.shootr.android.data.prefs.LongPreference;
import com.shootr.android.data.prefs.TimelineLastRefresh;
import com.shootr.android.domain.repository.SynchronizationRepository;
import javax.inject.Inject;

public class PreferencesSynchronizationRepository implements SynchronizationRepository {
    //TODO great candidate class for breaking up in smaller ones

    private final LongPreference timelineLastRefreshPreference;

    private long timelineLastRefresh;

    @Inject public PreferencesSynchronizationRepository(
      @TimelineLastRefresh LongPreference timelineLastRefreshPreference) {
        this.timelineLastRefreshPreference = timelineLastRefreshPreference;
    }

    @Override public Long getTimelineLastRefresh() {
        if (timelineLastRefresh == 0) {
            timelineLastRefresh = timelineLastRefreshPreference.get();
        }
        return timelineLastRefresh;
    }

    @Override public void putTimelineLastRefresh(Long date) {
        timelineLastRefresh = date;
        timelineLastRefreshPreference.set(timelineLastRefresh);
    }
}
