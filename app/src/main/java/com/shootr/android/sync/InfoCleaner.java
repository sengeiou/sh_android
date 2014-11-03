package com.shootr.android.sync;

import com.shootr.android.db.manager.MatchManager;
import com.shootr.android.db.manager.WatchManager;
import com.shootr.android.db.objects.MatchEntity;
import com.shootr.android.db.objects.WatchEntity;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class InfoCleaner {

    private WatchManager watchManager;
    private MatchManager matchManager;

    @Inject public InfoCleaner(MatchManager matchManager, WatchManager watchManager) {
        this.matchManager = matchManager;
        this.watchManager = watchManager;
    }

    public void clean() {
        cleanEndedAndAdjournedMatchesWithTheirWatches();

        cleanRejectedWatches();
    }

    private void cleanEndedAndAdjournedMatchesWithTheirWatches() {
        List<MatchEntity> endedAndAdjournedMatches = getEndedAndAdjournedMatches();
        List<WatchEntity> watchesFromEndedAndAdjournedMatches = getWatchesFromMatches(endedAndAdjournedMatches);

        cleanWatches(watchesFromEndedAndAdjournedMatches);
        cleanMatches(endedAndAdjournedMatches);
    }

    private List<MatchEntity> getEndedAndAdjournedMatches() {
        return matchManager.getEndedAndAdjournedMatches();
    }

    private List<WatchEntity> getWatchesFromMatches(List<MatchEntity> matches) {
        List<Long> matchIds = new ArrayList<>();
        for (MatchEntity match : matches) {
            matchIds.add(match.getIdMatch());
        }
        return watchManager.getWatchesFromMatches(matchIds);
    }

    private void cleanWatches(List<WatchEntity> watches) {
        watchManager.deleteWatches(watches);
    }

    private void cleanMatches(List<MatchEntity> matches) {
        matchManager.deleteMatches(matches);
    }

    private void cleanRejectedWatches() {
        List<WatchEntity> rejectedWatches = getRejectedWatches();
        cleanWatches(rejectedWatches);
    }

    public List<WatchEntity> getRejectedWatches() {
        return watchManager.getWatchesRejected();
    }
}
