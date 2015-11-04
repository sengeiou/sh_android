package com.shootr.mobile.domain.interactor.shot;

import com.shootr.mobile.domain.ShotDetail;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.SpyCallback;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
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

    @Mock com.shootr.mobile.domain.repository.ShotRepository localShotRepository;
    @Mock com.shootr.mobile.domain.repository.ShotRepository remoteShotRepository;
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

        List<com.shootr.mobile.domain.Shot> localReplies = spyCallback.firstResult().getReplies();
        assertThat(localReplies).isSortedAccordingTo(new com.shootr.mobile.domain.Shot.NewerBelowComparator());
    }

    @Test public void shouldCallbackRemoteRepliesInOrderNewerBelow() throws Exception {
        when(remoteShotRepository.getShotDetail(ANY_SHOT_ID)).thenReturn(unorderedReplies());

        interactor.loadShotDetail(String.valueOf(ANY_SHOT_ID), spyCallback, errorCallback);

        List<com.shootr.mobile.domain.Shot> remoteReplies = spyCallback.lastResult().getReplies();
        assertThat(remoteReplies).isSortedAccordingTo(new com.shootr.mobile.domain.Shot.NewerBelowComparator());
    }

    private ShotDetail unorderedReplies() {
        List<com.shootr.mobile.domain.Shot> replies = Arrays.asList(shotWithDate(DATE_MIDDLE), shotWithDate(DATE_OLDER), shotWithDate(DATE_NEWER));
        ShotDetail shotDetail = new ShotDetail();
        shotDetail.setReplies(replies);
        return shotDetail;
    }

    private com.shootr.mobile.domain.Shot shotWithDate(Long date) {
        com.shootr.mobile.domain.Shot shot = new com.shootr.mobile.domain.Shot();
        shot.setPublishDate(new Date(date));
        return shot;
    }

    private ShotDetail emptyShotDetail() {
        ShotDetail shotDetail = new ShotDetail();
        shotDetail.setShot(new com.shootr.mobile.domain.Shot());
        shotDetail.setReplies(Collections.<com.shootr.mobile.domain.Shot>emptyList());
        shotDetail.setParentShot(null);
        return shotDetail;
    }

    private void setupDefaultEmptyShotDetail() {
        when(localShotRepository.getShotDetail(ANY_SHOT_ID)).thenReturn(emptyShotDetail());
        when(remoteShotRepository.getShotDetail(ANY_SHOT_ID)).thenReturn(emptyShotDetail());
    }
}