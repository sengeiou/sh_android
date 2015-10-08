package com.shootr.android.domain.interactor.shot;

import com.shootr.android.domain.Shot;
import com.shootr.android.domain.ShotDetail;
import com.shootr.android.domain.executor.TestPostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.SpyCallback;
import com.shootr.android.domain.interactor.TestInteractorHandler;
import com.shootr.android.domain.repository.ShotRepository;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class GetShotDetailInteractorTest {

    private static final String ANY_SHOT_ID = "1L";
    private static final Long DATE_OLDER = 1000L;
    private static final Long DATE_MIDDLE = 2000L;
    private static final Long DATE_NEWER = 3000L;

    @Mock ShotRepository localShotRepository;
    @Mock ShotRepository remoteShotRepository;
    @Spy SpyCallback<ShotDetail> spyCallback = new SpyCallback<>();
    @Mock Interactor.ErrorCallback errorCallback;

    private GetShotDetailInteractor interactor;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        interactor = new GetShotDetailInteractor(new TestInteractorHandler(),
          new TestPostExecutionThread(),
          localShotRepository,
          remoteShotRepository);
        setupDefaultEmptyShotDetail();
    }

    @Test public void shouldCallbackLocalRepliesInOrderNewerBelow() throws Exception {
        when(localShotRepository.getShotDetail(ANY_SHOT_ID)).thenReturn(unorderedReplies());

        interactor.loadShotDetail(String.valueOf(ANY_SHOT_ID), spyCallback, errorCallback);

        List<Shot> localReplies = spyCallback.firstResult().getReplies();
        assertThat(localReplies).isSortedAccordingTo(new Shot.NewerBelowComparator());
    }

    @Test public void shouldCallbackRemoteRepliesInOrderNewerBelow() throws Exception {
        when(remoteShotRepository.getShotDetail(ANY_SHOT_ID)).thenReturn(unorderedReplies());

        interactor.loadShotDetail(String.valueOf(ANY_SHOT_ID), spyCallback, errorCallback);

        List<Shot> remoteReplies = spyCallback.lastResult().getReplies();
        assertThat(remoteReplies).isSortedAccordingTo(new Shot.NewerBelowComparator());
    }

    private ShotDetail unorderedReplies() {
        List<Shot> replies = Arrays.asList(shotWithDate(DATE_MIDDLE), shotWithDate(DATE_OLDER), shotWithDate(DATE_NEWER));
        ShotDetail shotDetail = new ShotDetail();
        shotDetail.setReplies(replies);
        return shotDetail;
    }

    private Shot shotWithDate(Long date) {
        Shot shot = new Shot();
        shot.setPublishDate(new Date(date));
        return shot;
    }

    private ShotDetail emptyShotDetail() {
        ShotDetail shotDetail = new ShotDetail();
        shotDetail.setShot(new Shot());
        shotDetail.setReplies(Collections.<Shot>emptyList());
        shotDetail.setParentShot(null);
        return shotDetail;
    }

    private void setupDefaultEmptyShotDetail() {
        when(localShotRepository.getShotDetail(ANY_SHOT_ID)).thenReturn(emptyShotDetail());
        when(remoteShotRepository.getShotDetail(ANY_SHOT_ID)).thenReturn(emptyShotDetail());
    }
}