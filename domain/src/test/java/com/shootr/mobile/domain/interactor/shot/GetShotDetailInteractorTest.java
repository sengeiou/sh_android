package com.shootr.mobile.domain.interactor.shot;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.SpyCallback;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.model.shot.Shot;
import com.shootr.mobile.domain.model.shot.ShotDetail;
import com.shootr.mobile.domain.model.shot.ShotType;
import com.shootr.mobile.domain.model.stream.StreamMode;
import com.shootr.mobile.domain.repository.nice.NicerRepository;
import com.shootr.mobile.domain.repository.shot.ExternalShotRepository;
import com.shootr.mobile.domain.repository.shot.InternalShotRepository;
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
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetShotDetailInteractorTest {

  private static final String ANY_SHOT_ID = "1L";
  private static final Long DATE_OLDER = 1000L;
  private static final Long DATE_MIDDLE = 2000L;
  private static final Long DATE_NEWER = 3000L;
  private static String[] TYPES_SHOT = ShotType.TYPES_SHOWN;
  private static String[] TYPES_STREAM = StreamMode.TYPES_STREAM;

  @Mock InternalShotRepository localShotRepository;
  @Mock ExternalShotRepository remoteShotRepository;
  @Mock NicerRepository nicerRepository;
  @Spy SpyCallback<ShotDetail> spyCallback = new SpyCallback<>();
  @Mock Interactor.ErrorCallback errorCallback;

  private GetShotDetailInteractor interactor;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    interactor =
        new GetShotDetailInteractor(new TestInteractorHandler(), new TestPostExecutionThread(),
            localShotRepository, remoteShotRepository, nicerRepository);
    setupDefaultEmptyShotDetail();
  }

  @Test public void shouldCallbackLocalRepliesInOrderNewerBelow() throws Exception {
    when(localShotRepository.getShotDetail(ANY_SHOT_ID, TYPES_STREAM, TYPES_SHOT)).thenReturn(
        unorderedReplies());

    interactor.loadShotDetail(String.valueOf(ANY_SHOT_ID), true, spyCallback, errorCallback);

    List<Shot> localReplies = spyCallback.firstResult().getReplies();
    Collections.reverse(localReplies);
    assertThat(localReplies).isSortedAccordingTo(new Shot.NewerBelowComparator());
  }

  @Test public void shouldCallbackRemoteRepliesInOrderNewerBelow() throws Exception {
    when(remoteShotRepository.getShotDetail(ANY_SHOT_ID, TYPES_STREAM, TYPES_SHOT)).thenReturn(
        unorderedReplies());

    interactor.loadShotDetail(String.valueOf(ANY_SHOT_ID), true, spyCallback, errorCallback);

    List<Shot> remoteReplies = spyCallback.lastResult().getReplies();
    assertThat(remoteReplies).isSortedAccordingTo(new Shot.NewerBelowComparator());
  }

  @Test public void shouldNotifyErrorWhenRemoteShotRepositoryThrowsException() throws Exception {
    when(remoteShotRepository.getShotDetail(anyString(), anyArray(), anyArray())).thenThrow(
        new ShootrException() {
        });

    interactor.loadShotDetail(String.valueOf(ANY_SHOT_ID), false, spyCallback, errorCallback);

    verify(errorCallback).onError(any(ShootrException.class));
  }

  protected String[] anyArray() {
    return any(String[].class);
  }

  private ShotDetail unorderedReplies() {
    List<Shot> replies = Arrays.asList(shotWithDate(DATE_MIDDLE), shotWithDate(DATE_OLDER),
        shotWithDate(DATE_NEWER));
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
    when(localShotRepository.getShotDetail(ANY_SHOT_ID, TYPES_STREAM, TYPES_SHOT)).thenReturn(
        emptyShotDetail());
    when(remoteShotRepository.getShotDetail(ANY_SHOT_ID, TYPES_STREAM, TYPES_SHOT)).thenReturn(
        emptyShotDetail());
  }
}