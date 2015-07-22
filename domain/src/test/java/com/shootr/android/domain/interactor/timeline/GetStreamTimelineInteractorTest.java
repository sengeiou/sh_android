package com.shootr.android.domain.interactor.timeline;

import com.shootr.android.domain.Stream;
import com.shootr.android.domain.StreamTimelineParameters;
import com.shootr.android.domain.Shot;
import com.shootr.android.domain.Timeline;
import com.shootr.android.domain.User;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.executor.TestPostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.interactor.TestInteractorHandler;
import com.shootr.android.domain.repository.StreamRepository;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.ShotRepository;
import com.shootr.android.domain.repository.TimelineSynchronizationRepository;
import com.shootr.android.domain.repository.UserRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetStreamTimelineInteractorTest {

    private static final String ID_SHOT_WITHOUT_EVENT = "shot_without_event";
    private static final String WATCHING_EVENT_ID = "watching_event";
    private static final String ID_SHOT_WITH_EVENT = "shot_with_event";
    private static final String EVENT_AUTHOR_ID = "event_author";
    private static final String ID_SHOT_FROM_AUTHOR = "shot_from_author";
    private static final String ID_CURRENT_USER = "current_user";

    private static final Long DATE_OLDER = 1000L;
    private static final Long DATE_MIDDLE = 2000L;
    private static final Long DATE_NEWER = 3000L;

    @Mock ShotRepository localShotRepository;
    @Mock UserRepository localUserRepository;
    @Spy SpyCallback spyCallback = new SpyCallback();
    @Mock StreamRepository streamRepository;
    @Mock SessionRepository sessionRepository;
    @Mock TimelineSynchronizationRepository timelineSynchronizationRepository;
    @Mock Interactor.ErrorCallback errorCallback;

    private GetStreamTimelineInteractor interactor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        InteractorHandler interactorHandler = new TestInteractorHandler();
        PostExecutionThread postExecutionThread = new TestPostExecutionThread();

        when(localUserRepository.getPeople()).thenReturn(people());
        when(sessionRepository.getCurrentUserId()).thenReturn(ID_CURRENT_USER);

        interactor = new GetStreamTimelineInteractor(interactorHandler,
          postExecutionThread,
          sessionRepository,
          localShotRepository, streamRepository,
          localUserRepository
        );
    }

    @Test
    public void shouldNotRetrieveTimelineWhenNoEventWatching() throws Exception {
        when(localUserRepository.getUserById(ID_CURRENT_USER)).thenReturn(currentUserNotWatching());

        interactor.loadStreamTimeline(spyCallback, errorCallback);

        verify(localShotRepository, never()).getShotsForStreamTimeline(any(StreamTimelineParameters.class));
    }

    @Test
    public void shouldCallbackShotsInOrderWithPublishDateComparator() throws Exception {
        setupWatchingEvent();
        when(localShotRepository.getShotsForStreamTimeline(any(StreamTimelineParameters.class))).thenReturn(unorderedShots());

        interactor.loadStreamTimeline(spyCallback, errorCallback);
        List<Shot> localShotsReturned = spyCallback.timelinesReturned.get(0).getShots();

        assertThat(localShotsReturned).isSortedAccordingTo(new Shot.NewerAboveComparator());
    }

    private User currentUserNotWatching() {
        User user = new User();
        user.setIdUser(ID_CURRENT_USER);
        user.setIdWatchingStream(null);
        return user;
    }

    private User currentUserWatching() {
        User user = new User();
        user.setIdUser(ID_CURRENT_USER);
        user.setIdWatchingStream(WATCHING_EVENT_ID);
        return user;
    }

    private List<Shot> unorderedShots() {
        return Arrays.asList(shotWithDate(DATE_MIDDLE), shotWithDate(DATE_OLDER), shotWithDate(DATE_NEWER));
    }

    private Shot shotWithDate(Long date) {
        Shot shot = new Shot();
        shot.setPublishDate(new Date(date));
        return shot;
    }

    private void setupWatchingEvent() {
        when(localUserRepository.getUserById(ID_CURRENT_USER)).thenReturn(currentUserWatching());
        when(streamRepository.getStreamById(eq(WATCHING_EVENT_ID))).thenReturn(watchingEvent());
    }

    //region Stubs

    private List<Shot> shotsFromAuthor() {
        return Arrays.asList(shotFromAuthor());
    }

    private List<User> people() {
        return Arrays.asList(new User());
    }

    private Shot shotFromAuthor() {
        Shot shot = new Shot();
        shot.setIdShot(ID_SHOT_FROM_AUTHOR);
        return shot;
    }

    private Shot shotWithEvent() {
        Shot shot = new Shot();
        shot.setIdShot(ID_SHOT_WITH_EVENT);
        shot.setEventInfo(watchingEventInfo());
        return shot;
    }

    private Shot.ShotEventInfo watchingEventInfo() {
        Shot.ShotEventInfo eventInfo = new Shot.ShotEventInfo();
        eventInfo.setIdEvent(WATCHING_EVENT_ID);
        return eventInfo;
    }

    private Stream watchingEvent() {
        Stream stream = new Stream();
        stream.setId(WATCHING_EVENT_ID);
        stream.setAuthorId(EVENT_AUTHOR_ID);
        return stream;
    }

    //endregion

    //region Spies
    private StreamTimelineParameters captureTimelineParametersFromRepositoryCall(ShotRepository shotRepository) {
        ArgumentCaptor<StreamTimelineParameters> captor = ArgumentCaptor.forClass(StreamTimelineParameters.class);
        verify(shotRepository).getShotsForStreamTimeline(captor.capture());
        return captor.getValue();
    }

    static class SpyCallback implements Interactor.Callback<Timeline> {

        public List<Timeline> timelinesReturned = new ArrayList<>();

        @Override public void onLoaded(Timeline timeline) {
            timelinesReturned.add(timeline);
        }
    }
    //endregion
}