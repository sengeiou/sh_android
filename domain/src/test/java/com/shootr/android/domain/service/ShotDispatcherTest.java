package com.shootr.android.domain.service;

import com.shootr.android.domain.QueuedShot;
import com.shootr.android.domain.Shot;
import com.shootr.android.domain.bus.BusPublisher;
import com.shootr.android.domain.bus.ShotSent;
import com.shootr.android.domain.exception.RepositoryException;
import com.shootr.android.domain.repository.ShotRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ShotDispatcherTest {

    private static final String COMMENT_STUB = "comment";
    private static final Long QUEUED_ID = 1L;

    @Mock BusPublisher busPublisher;
    @Mock ShotQueueListener shotQueueListener;
    @Spy ShotRepository shotRepository = new StubShotRepository();
    @Spy ShotQueueRepository shotQueueRepository = new StubShotQueueRepository();

    private ShotDispatcher shotDispatcher;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        shotDispatcher = new ShotDispatcher(shotRepository, shotQueueRepository, busPublisher, shotQueueListener);
    }

    @Test
    public void shouldPutOneShotInRepositoryWhenOneShotSent() throws Exception {
        shotDispatcher.sendShot(shot());

        verify(shotRepository, times(1)).putShot(any(Shot.class));
    }

    @Test
    public void shouldPutTwoShotsInRepositoryWhenTwoShotsSent() throws Exception {
        shotDispatcher.sendShot(shot());
        shotDispatcher.sendShot(shot());

        verify(shotRepository, times(2)).putShot(any(Shot.class));
    }

    @Test
    public void shouldRemoveFromQueueWhenShotSent() throws Exception {
        shotDispatcher.sendShot(shot());

        verify(shotQueueRepository, times(1)).remove(any(QueuedShot.class));

    }

    @Test
    public void shouldPutInQueueWithFailedFlagWhenSendingShotFailed() throws Exception {
        when(shotRepository.putShot(any(Shot.class))).thenThrow(new RepositoryException(null));

        shotDispatcher.sendShot(shot());

        ArgumentCaptor<QueuedShot> queuedShotCaptor = ArgumentCaptor.forClass(QueuedShot.class);
        verify(shotQueueRepository, atLeastOnce()).put(queuedShotCaptor.capture());
        QueuedShot queuedShot = queuedShotCaptor.getValue();
        assertThat(queuedShot.isFailed()).isTrue();
    }

    @Test
    public void shouldPostToBusWhenShotSent() throws Exception {
        shotDispatcher.sendShot(shot());

        ArgumentCaptor<ShotSent.Event> eventArgumentCaptor = ArgumentCaptor.forClass(ShotSent.Event.class);
        verify(busPublisher, times(1)).post(eventArgumentCaptor.capture());
        ShotSent.Event event = eventArgumentCaptor.getValue();

        assertThat(event.getShot()).isNotNull();
    }

    @Test
    public void shouldNotifyListenerOnlyOnceWhenSendingShot() throws Exception {
        shotDispatcher.sendShot(shot());
        verify(shotQueueListener, times(1)).onSendingShot(any(QueuedShot.class));
    }

    @Test
    public void shouldNotifyListenerWhenShotSent() throws Exception {
        shotDispatcher.sendShot(shot());
        verify(shotQueueListener, times(1)).onShotSent(any(QueuedShot.class));
    }

    @Test
    public void shouldNotifyListenerWhenSendingShotFailed() throws Exception {
        when(shotRepository.putShot(any(Shot.class))).thenThrow(new RepositoryException(null));

        shotDispatcher.sendShot(shot());

        verify(shotQueueListener, times(1)).onShotFailed(any(QueuedShot.class));
    }

    class StubShotQueueRepository implements ShotQueueRepository {

        @Override public QueuedShot put(QueuedShot queuedShot) {
            queuedShot.setIdQueue(QUEUED_ID);
            return queuedShot;
        }

        @Override public void remove(QueuedShot queuedShot) {

        }
    }

    class StubShotRepository implements ShotRepository {

        @Override public Shot putShot(Shot shot) {
            return shot;
        }
    }

    private Shot shot() {
        Shot shot = new Shot();
        shot.setComment(COMMENT_STUB);
        return shot;
    }
}