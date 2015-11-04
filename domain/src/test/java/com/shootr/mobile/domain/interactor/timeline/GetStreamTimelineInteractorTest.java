package com.shootr.mobile.domain.interactor.timeline;

import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.repository.UserRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

public class GetStreamTimelineInteractorTest {

    private static final String ID_SHOT_WITHOUT_STREAM = "shot_without_stream";
    private static final String WATCHING_STREAM_ID = "watching_stream";
    private static final String ID_SHOT_WITH_STREAM = "shot_with_stream";
    private static final String STREAM_AUTHOR_ID = "stream_author";
    private static final String ID_SHOT_FROM_AUTHOR = "shot_from_author";
    private static final String ID_CURRENT_USER = "current_user";
    private static final String STREAM_ID = "stream";

    private static final Long DATE_OLDER = 1000L;
    private static final Long DATE_MIDDLE = 2000L;
    private static final Long DATE_NEWER = 3000L;

    @Mock com.shootr.mobile.domain.repository.ShotRepository localShotRepository;
    @Mock UserRepository localUserRepository;
    @Spy SpyCallback spyCallback = new SpyCallback();
    @Mock com.shootr.mobile.domain.repository.StreamRepository streamRepository;
    @Mock com.shootr.mobile.domain.repository.SessionRepository sessionRepository;
    @Mock com.shootr.mobile.domain.repository.TimelineSynchronizationRepository timelineSynchronizationRepository;

    private GetStreamTimelineInteractor interactor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        com.shootr.mobile.domain.interactor.InteractorHandler interactorHandler = new TestInteractorHandler();
        com.shootr.mobile.domain.executor.PostExecutionThread postExecutionThread = new TestPostExecutionThread();

        when(localUserRepository.getPeople()).thenReturn(people());
        when(sessionRepository.getCurrentUserId()).thenReturn(ID_CURRENT_USER);

        interactor = new GetStreamTimelineInteractor(interactorHandler,
          postExecutionThread, localShotRepository);
    }

    @Test
    public void shouldCallbackShotsInOrderWithPublishDateComparator() throws Exception {
        setupWatchingStream();
        when(localShotRepository.getShotsForStreamTimeline(any(com.shootr.mobile.domain.StreamTimelineParameters.class))).thenReturn(unorderedShots());

        interactor.loadStreamTimeline(STREAM_ID, spyCallback);
        List<com.shootr.mobile.domain.Shot> localShotsReturned = spyCallback.timelinesReturned.get(0).getShots();

        assertThat(localShotsReturned).isSortedAccordingTo(new com.shootr.mobile.domain.Shot.NewerAboveComparator());
    }

    private com.shootr.mobile.domain.User currentUserWatching() {
        com.shootr.mobile.domain.User user = new com.shootr.mobile.domain.User();
        user.setIdUser(ID_CURRENT_USER);
        user.setIdWatchingStream(WATCHING_STREAM_ID);
        return user;
    }

    private List<com.shootr.mobile.domain.Shot> unorderedShots() {
        return Arrays.asList(shotWithDate(DATE_MIDDLE), shotWithDate(DATE_OLDER), shotWithDate(DATE_NEWER));
    }

    private com.shootr.mobile.domain.Shot shotWithDate(Long date) {
        com.shootr.mobile.domain.Shot shot = new com.shootr.mobile.domain.Shot();
        shot.setPublishDate(new Date(date));
        return shot;
    }

    private void setupWatchingStream() {
        when(localUserRepository.getUserById(ID_CURRENT_USER)).thenReturn(currentUserWatching());
        when(streamRepository.getStreamById(eq(WATCHING_STREAM_ID))).thenReturn(watchingStream());
    }

    //region Stubs

    private List<com.shootr.mobile.domain.User> people() {
        return Arrays.asList(new com.shootr.mobile.domain.User());
    }

    private com.shootr.mobile.domain.Stream watchingStream() {
        com.shootr.mobile.domain.Stream stream = new com.shootr.mobile.domain.Stream();
        stream.setId(WATCHING_STREAM_ID);
        stream.setAuthorId(STREAM_AUTHOR_ID);
        return stream;
    }

    //endregion

    //region Spies
    static class SpyCallback implements Interactor.Callback<com.shootr.mobile.domain.Timeline> {

        public List<com.shootr.mobile.domain.Timeline> timelinesReturned = new ArrayList<>();

        @Override public void onLoaded(com.shootr.mobile.domain.Timeline timeline) {
            timelinesReturned.add(timeline);
        }
    }
    //endregion
}