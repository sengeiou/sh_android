package com.shootr.android.domain.service;

import com.shootr.android.domain.QueuedShot;
import com.shootr.android.domain.Shot;
import com.shootr.android.domain.bus.BusPublisher;
import com.shootr.android.domain.bus.ShotSent;
import com.shootr.android.domain.exception.RepositoryException;
import com.shootr.android.domain.repository.PhotoService;
import com.shootr.android.domain.repository.ShotRepository;
import com.shootr.android.domain.service.shot.ShootrShotService;
import com.shootr.android.domain.utils.ImageResizer;
import java.io.File;
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
    private static final File IMAGE_FILE_STUB = new File(".");
    private static final File IMAGE_FILE_NULL = null;

    @Mock BusPublisher busPublisher;
    @Mock ShotQueueListener shotQueueListener;
    @Mock ShootrShotService shootrShotService;
    @Spy ShotQueueRepository shotQueueRepository = new StubShotQueueRepository();

    private ShotDispatcher shotDispatcher;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        shotDispatcher = new ShotDispatcher(shotQueueRepository, shootrShotService, busPublisher, shotQueueListener);
    }

    @Test
    public void shouldPutOneShotInRepositoryWhenOneShotSent() throws Exception {
        shotDispatcher.sendShot(shot(), IMAGE_FILE_NULL);

        verify(shootrShotService, times(1)).sendShot(any(Shot.class));
    }

    @Test
    public void shouldSendTwoShotsThroughServiceWhenTwoShotsSentSequentially() throws Exception {
        shotDispatcher.sendShot(shot(), IMAGE_FILE_NULL);
        shotDispatcher.sendShot(shot(), IMAGE_FILE_NULL);

        verify(shootrShotService, times(2)).sendShot(any(Shot.class));
    }

    @Test
    public void shouldRemoveFromQueueWhenShotSent() throws Exception {
        shotDispatcher.sendShot(shot(), IMAGE_FILE_NULL);

        verify(shotQueueRepository, times(1)).remove(any(QueuedShot.class));
    }

    @Test
    public void shouldPutInQueueWithFailedFlagWhenSendingShotFailed() throws Exception {
        when(shootrShotService.sendShot(any(Shot.class))).thenThrow(new RepositoryException(null));

        shotDispatcher.sendShot(shot(), IMAGE_FILE_NULL);

        ArgumentCaptor<QueuedShot> queuedShotCaptor = ArgumentCaptor.forClass(QueuedShot.class);
        verify(shotQueueRepository, atLeastOnce()).put(queuedShotCaptor.capture());
        QueuedShot queuedShot = queuedShotCaptor.getValue();
        assertThat(queuedShot.isFailed()).isTrue();
    }

    @Test
    public void shouldPostANotNullToBusWhenShotSent() throws Exception {
        when(shootrShotService.sendShot(any(Shot.class))).thenReturn(shot());

        shotDispatcher.sendShot(shot(), IMAGE_FILE_NULL);

        ArgumentCaptor<ShotSent.Event> eventArgumentCaptor = ArgumentCaptor.forClass(ShotSent.Event.class);
        verify(busPublisher, times(1)).post(eventArgumentCaptor.capture());
        ShotSent.Event event = eventArgumentCaptor.getValue();

        assertThat(event.getShot()).isNotNull();
    }

    @Test
    public void shouldNotifyListenerOnlyOnceWhenSendingShot() throws Exception {
        shotDispatcher.sendShot(shot(), IMAGE_FILE_NULL);
        verify(shotQueueListener, times(1)).onSendingShot(any(QueuedShot.class));
    }

    @Test
    public void shouldNotifyListenerWhenShotSent() throws Exception {
        shotDispatcher.sendShot(shot(), IMAGE_FILE_NULL);
        verify(shotQueueListener, times(1)).onShotSent(any(QueuedShot.class));
    }

    @Test
    public void shouldNotifyListenerWhenSendingShotFailed() throws Exception {
        when(shootrShotService.sendShot(any(Shot.class))).thenThrow(new RepositoryException(null));

        shotDispatcher.sendShot(shot(), IMAGE_FILE_NULL);

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

    private Shot shot() {
        Shot shot = new Shot();
        shot.setComment(COMMENT_STUB);
        return shot;
    }
}