package com.shootr.android.task.jobs.info;

import android.app.Application;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.shootr.android.data.entity.EventEntity;
import com.shootr.android.data.mapper.UserEntityMapper;
import com.shootr.android.domain.User;
import com.shootr.android.ui.model.EventModel;
import com.shootr.android.ui.model.mappers.EventEntityModelMapper;
import com.shootr.android.ui.model.mappers.UserEntityWatchingModelMapper;
import com.squareup.otto.Bus;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.db.manager.FollowManager;
import com.shootr.android.db.manager.EventManager;
import com.shootr.android.db.manager.UserManager;
import com.shootr.android.db.manager.WatchManager;
import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.data.entity.WatchEntity;
import com.shootr.android.service.ShootrService;
import com.shootr.android.task.events.info.WatchingInfoResult;
import com.shootr.android.task.jobs.ShootrBaseJob;
import com.shootr.android.ui.model.UserWatchingModel;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import timber.log.Timber;

public class GetWatchingInfoJob extends ShootrBaseJob<WatchingInfoResult> {

    private static final int PRIORITY = 5;
    private ShootrService service;
    private SessionRepository sessionRepository;
    private EventEntityModelMapper eventEntityModelMapper;
    private UserEntityWatchingModelMapper userWatchingModelMapper;
    private UserManager userManager;
    private WatchManager watchManager;
    private EventManager eventManager;
    private FollowManager followManager;
    private InfoListBuilderFactory infoListBuilderFactory;
    private boolean postOnlineInfoOnly;
    private UserEntityMapper userEntityMapper;

    @Inject public GetWatchingInfoJob(Application application, Bus bus, NetworkUtil networkUtil, ShootrService service,
      SessionRepository sessionRepository, EventEntityModelMapper eventEntityModelMapper, UserEntityWatchingModelMapper userWatchingModelMapper,
      UserManager userManager, FollowManager followManager, WatchManager watchManager, EventManager eventManager,
      InfoListBuilderFactory infoListBuilderFactory, UserEntityMapper userEntityMapper) {
        super(new Params(PRIORITY).groupBy("info"), application, bus, networkUtil);
        this.service = service;
        this.sessionRepository = sessionRepository;
        this.eventEntityModelMapper = eventEntityModelMapper;
        this.userWatchingModelMapper = userWatchingModelMapper;
        this.userManager = userManager;
        this.watchManager = watchManager;
        this.eventManager = eventManager;
        this.followManager = followManager;
        this.infoListBuilderFactory = infoListBuilderFactory;
        this.userEntityMapper = userEntityMapper;
    }

    public void init(boolean postOnlineInfoOnly) {
        this.postOnlineInfoOnly = postOnlineInfoOnly;
    }

    @Override protected void run() throws SQLException, IOException {
        if(!postOnlineInfoOnly) {
            Map<EventModel, Collection<UserWatchingModel>> infoListOffline = obtainInfoList(false);
            if (infoListOffline != null) {
                Timber.d("Sending watching list offline");
                postSuccessfulEvent(new WatchingInfoResult(infoListOffline));
            }
        }
        if (hasInternetConnection()) {
            Timber.d("Sending watching list online");
            Map<EventModel, Collection<UserWatchingModel>> infoListOnline = obtainInfoList(true);
            if (infoListOnline != null) {
                postSuccessfulEvent(new WatchingInfoResult(infoListOnline));
            }
        }
    }

    private Map<EventModel, Collection<UserWatchingModel>> obtainInfoList(boolean useOnlineData)
      throws IOException, SQLException {
        InfoListBuilder infoListBuilder = infoListBuilderFactory.getInfoListBuilder(sessionRepository,
          eventEntityModelMapper,userWatchingModelMapper,
          userEntityMapper);
        List<WatchEntity> watches = getWatches(useOnlineData);
        if (watches != null && !watches.isEmpty()) {
            infoListBuilder.setWatches(watches);
            infoListBuilder.provideEvents(getEvents(infoListBuilder.getEventIds(), useOnlineData));
            infoListBuilder.provideUsers(getUsersFromDatabase(infoListBuilder.getUserIds()));
        }

        EventEntity nextEventFromMyTeam = getNextEventWhereMyFavoriteTeamPlays(useOnlineData);
        if (nextEventFromMyTeam != null) {
            infoListBuilder.putMyTeamEvent(nextEventFromMyTeam);
        }

        return infoListBuilder.build();
    }

    private EventEntity getNextEventWhereMyFavoriteTeamPlays(boolean useOnlineData) throws IOException {
        EventEntity nextEvent = eventManager.getNextEventFromTeam(getFavoriteTeamId());
        if (useOnlineData) {
            EventEntity nextEventFromServer = service.getNextEventWhereMyFavoriteTeamPlays(getFavoriteTeamId());
            if (nextEventFromServer != null) {
                nextEvent = nextEventFromServer;
                eventManager.saveEvent(nextEventFromServer);
            }
        }
        return nextEvent;
    }

    private List<EventEntity> getEvents(List<Long> eventIds, boolean useOnlineData) throws IOException {
        if (eventIds.isEmpty()) {
            return new ArrayList<>(0);
        }
        List<EventEntity> events = eventManager.getEventsByIds(eventIds);
        if (useOnlineData) {
            List<EventEntity> eventsFromServer = service.getEventsByIds(eventIds);
            if (eventsFromServer != null) {
                events = eventsFromServer;
                saveEventsInDatabase(eventsFromServer);
            }
        }
        return events;
    }

    private void saveEventsInDatabase(List<EventEntity> eventsToSave) {
        eventManager.deleteAllEvents();
        eventManager.saveEvents(eventsToSave);
    }

    private List<UserEntity> getUsersFromDatabase(List<Long> usersIds) {
        return userManager.getUsersByIds(usersIds);
    }

    private List<WatchEntity> getWatches(boolean useOnlineData) throws SQLException, IOException {
        List<WatchEntity> watches = getWatchesFromDatabase();
            if (useOnlineData) {
            List<WatchEntity> newWatchesFromServer = service.getWatchesFromUsersAndMe(getIdsFromMyFollowingAndMe(),
              sessionRepository.getCurrentUserId());
            if (newWatchesFromServer != null) {
                watches = newWatchesFromServer;
                replaceWatchesInDatabase(newWatchesFromServer);
            }
        }
        return watches;
    }

    public List<WatchEntity> getWatchesFromDatabase() throws SQLException {
        return watchManager.getWatchesNotEndedFromUsers(getIdsFromMyFollowingAndMe());
    }

    private void replaceWatchesInDatabase(List<WatchEntity> newWatchesFromServer) {
        watchManager.deleteAllWatches();
        watchManager.saveWatches(newWatchesFromServer);
    }

    public List<Long> getIdsFromMyFollowingAndMe() throws SQLException {
        return followManager.getUserFollowingIdsWithOwnUser(sessionRepository.getCurrentUserId());
    }

    @Override protected boolean isNetworkRequired() {
        return false;
    }

    public Long getFavoriteTeamId() {
        User currentUser = sessionRepository.getCurrentUser();
        return currentUser.getFavoriteTeamId();
    }
}
