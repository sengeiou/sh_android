package gm.mobi.android.task.events.timeline;

import java.util.List;

import gm.mobi.android.ui.adapters.TimelineAdapter;

public class OldShotsReceivedEvent {

    private List<TimelineAdapter.MockShot> shots;

    public OldShotsReceivedEvent(List<TimelineAdapter.MockShot> shots) {

        this.shots = shots;
    }

    public List<TimelineAdapter.MockShot> getShots() {
        return shots;
    }
}
