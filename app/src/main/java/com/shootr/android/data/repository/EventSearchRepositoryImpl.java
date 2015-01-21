package com.shootr.android.data.repository;

import com.shootr.android.data.entity.WatchEntity;
import com.shootr.android.db.manager.FollowManager;
import com.shootr.android.db.manager.WatchManager;
import com.shootr.android.domain.Event;
import com.shootr.android.domain.EventSearchResult;
import com.shootr.android.domain.repository.EventSearchRepository;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.ui.model.EventModel;
import com.shootr.android.ui.model.EventResultModel;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

public class EventSearchRepositoryImpl implements EventSearchRepository {

    private final SessionRepository sessionRepository;
    private final FollowManager followManager;
    private final WatchManager watchManager;

    @Inject public EventSearchRepositoryImpl(SessionRepository sessionRepository, FollowManager followManager, WatchManager watchManager) {
        this.sessionRepository = sessionRepository;
        this.followManager = followManager;
        this.watchManager = watchManager;
    }

    @Override public void getDefaultEvents(EventResultListCallback callback) {
        try {
            Map<Long, Integer> eventsWatchesCounts = getWatchersCountByEvents();

            callback.onLoaded(getMockList());

        } catch (SQLException e) {
            callback.onError(e);
        }
    }

    //region Mock data
    private List<EventSearchResult> getMockList() {
        EventSearchResult event1 = mockEvent("Sevilla-Betis", 1421861460000L, "http://www.yotufutbol.com/contenido/estadios/spain/ramon%20sanchez%20pizjuan/estadio%20ramon%20sanchez%20pizjuan5.jpg", 5);
        EventSearchResult event2 = mockEvent("Órbita Laika 6", 1422226800000L, "http://img.rtve.es/imagenes/orbita-laika-tercer-programa/1418917659672.jpg", 2);
        EventSearchResult event3 = mockEvent("Sabadell-Barcelona B", 1422745200000L, null, 1);
        EventSearchResult event4 = mockEvent("Red Hot Chili Peppers - Palacio de Congresos - Sevilla - Línea C4 de cercanías, Renfe", 1422745200000L, "http://img.actualidadmusica.com/wp-content/uploads/2012/06/redhotchilipeppers.jpg", 1);
        EventSearchResult event5 = mockEvent("Eurovisión 2016", 1464735600000L, "http://www.independent.co.uk/incoming/article9296892.ece/alternates/w620/conchita.jpg", 0);

        return Arrays.asList(event1, event2, event3, event4, event5, event1, event2, event3, event4, event5, event1,
          event2, event3, event4, event5, event1, event2, event3, event4, event5);
    }

    private EventSearchResult mockEvent(String title, long date, String picture, int watchers) {
        Event event = new Event();
        event.setTitle(title);
        event.setPicture(picture);
        event.setStartDate(new Date(date));

        EventSearchResult eventResult = new EventSearchResult();
        eventResult.setEvent(event);
        eventResult.setWatchersNumber(watchers);
        return eventResult;
    }
    //endregion

    private Map<Long, Integer> getWatchersCountByEvents() throws SQLException {
        long currentUserId = sessionRepository.getCurrentUserId();

        List<Long> followingAndCurrentUserIds = followManager.getUserFollowingIds(currentUserId);
        followingAndCurrentUserIds.add(currentUserId);

        List<WatchEntity> watches =
          watchManager.getWatchesNotEndedFromUsers(followingAndCurrentUserIds);

        Map<Long, Integer> eventsWatchesCounts = new HashMap<>();
        for (WatchEntity watch : watches) {
            Integer currentCount = eventsWatchesCounts.get(watch.getIdEvent());
            if (currentCount != null) {
                eventsWatchesCounts.put(watch.getIdEvent(), currentCount + 1);
            } else {
                eventsWatchesCounts.put(watch.getIdEvent(), 1);
            }
        }
        return eventsWatchesCounts;
    }
}
