package com.shootr.android.domain.interactor.shot;

import com.shootr.android.domain.Shot;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.executor.TestPostExecutionThread;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.interactor.SpyCallback;
import com.shootr.android.domain.interactor.TestInteractorHandler;
import com.shootr.android.domain.repository.ShotRepository;
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
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetRepliesFromShotInteractorTest {

    private static final String ANY_SHOT_ID = "1L";
    private static final Long DATE_OLDER = 1000L;
    private static final Long DATE_MIDDLE = 2000L;
    private static final Long DATE_NEWER = 3000L;

    @Mock ShotRepository localShotRepository;
    @Mock ShotRepository remoteShotRepository;
    @Spy SpyCallback<List<Shot>> spyCallback = new SpyCallback<>();

    private GetRepliesFromShotInteractor interactor;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        InteractorHandler interactorHandler = new TestInteractorHandler();
        PostExecutionThread postExecutionThread = new TestPostExecutionThread();
        interactor = new GetRepliesFromShotInteractor(interactorHandler,
          postExecutionThread,
          localShotRepository,
          remoteShotRepository);
    }

    @Test public void shouldCallbackLocalRepliesInOrderNewerBelow() throws Exception {
        when(localShotRepository.getReplies(ANY_SHOT_ID)).thenReturn(unorderedShots());

        interactor.loadReplies(String.valueOf(ANY_SHOT_ID), spyCallback);

        assertThat(spyCallback.lastResult).isSortedAccordingTo(new Shot.NewerBelowComparator());
    }

    @Test public void shouldCallbackRemoteRepliesInOrderNewerBelow() throws Exception {
        when(remoteShotRepository.getReplies(ANY_SHOT_ID)).thenReturn(unorderedShots());

        interactor.loadReplies(String.valueOf(ANY_SHOT_ID), spyCallback);

        assertThat(spyCallback.lastResult).isSortedAccordingTo(new Shot.NewerBelowComparator());
    }

    @Test public void shouldCallbackNothingWhenRepliesEmptyInLocalAndRemote() throws Exception {
        when(localShotRepository.getReplies(ANY_SHOT_ID)).thenReturn(new ArrayList<Shot>());
        when(remoteShotRepository.getReplies(ANY_SHOT_ID)).thenReturn(new ArrayList<Shot>());

        interactor.loadReplies(String.valueOf(ANY_SHOT_ID), spyCallback);

        verify(spyCallback, never()).onLoaded(anyListOf(Shot.class));
    }

    private List<Shot> unorderedShots() {
        return Arrays.asList(shotWithDate(DATE_MIDDLE), shotWithDate(DATE_OLDER), shotWithDate(DATE_NEWER));
    }

    private Shot shotWithDate(Long date) {
        Shot shot = new Shot();
        shot.setPublishDate(new Date(date));
        return shot;
    }

}