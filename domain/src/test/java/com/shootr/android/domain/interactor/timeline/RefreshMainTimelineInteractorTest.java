package com.shootr.android.domain.interactor.timeline;

import com.shootr.android.domain.Shot;
import com.shootr.android.domain.Timeline;
import com.shootr.android.domain.TimelineParameters;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.executor.TestPostExecutionThread;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.interactor.TestInteractorHandler;
import com.shootr.android.domain.repository.EventRepository;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.ShotRepository;
import com.shootr.android.domain.repository.SynchronizationRepository;
import com.shootr.android.domain.repository.UserRepository;
import com.shootr.android.domain.repository.WatchRepository;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RefreshMainTimelineInteractorTest {

    private static final Long DATE_OLDER = 1L;
    private static final Long DATE_MIDDLE = 2L;
    private static final Long DATE_NEWER = 3L;

    private RefreshMainTimelineInteractor interactor;

    @Mock SessionRepository sessionRepository;
    @Mock ShotRepository remoteShotRepository;
    @Mock WatchRepository localWatchRepository;
    @Mock EventRepository localEventRepository;
    @Mock UserRepository localUserRepository;
    @Mock SynchronizationRepository synchronizationRepository;
    @Spy SpyCallback spyCallback = new SpyCallback();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        InteractorHandler interactorHandler = new TestInteractorHandler();
        PostExecutionThread postExecutionThread = new TestPostExecutionThread();

        this.interactor = new RefreshMainTimelineInteractor(interactorHandler, postExecutionThread,
          sessionRepository,
          remoteShotRepository,
          localWatchRepository,
          localEventRepository,
          localUserRepository,
          synchronizationRepository);
    }

    @Test
    public void shouldCallbackShotsInOrderWithPublishDateComparator() throws Exception {
        when(remoteShotRepository.getShotsForTimeline(any(TimelineParameters.class))).thenReturn(unorderedShots());

        interactor.refreshMainTimeline(spyCallback);
        List<Shot> shotsReturned = spyCallback.timelinesReturned.get(0).getShots();

        assertThat(shotsReturned).isSortedAccordingTo(new Shot.PublishDateComparator());
    }

    @Test
    public void shouldUpdateLastRefreshDateWithNewestShotPublishDate() throws Exception {
        when(remoteShotRepository.getShotsForTimeline(any(TimelineParameters.class))).thenReturn(unorderedShots());

        interactor.refreshMainTimeline(spyCallback);

        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
        verify(synchronizationRepository).putTimelineLastRefresh(captor.capture());
        Long updatedRefreshDate = captor.getValue();

        assertThat(updatedRefreshDate).isEqualTo(DATE_NEWER);
    }

    private List<Shot> unorderedShots() {
        return Arrays.asList(shotWithDate(DATE_MIDDLE), shotWithDate(DATE_OLDER), shotWithDate(DATE_NEWER));
    }

    private Shot shotWithDate(Long date) {
        Shot shot = new Shot();
        shot.setPublishDate(new Date(date));
        return shot;
    }

    static class SpyCallback implements RefreshMainTimelineInteractor.Callback {

        public List<Timeline> timelinesReturned = new ArrayList<>();

        @Override public void onLoaded(Timeline timeline) {
            timelinesReturned.add(timeline);
        }
    }

}