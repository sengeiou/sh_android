package com.shootr.mobile.domain.service;

import com.shootr.mobile.domain.QueuedShot;
import com.shootr.mobile.domain.bus.BusPublisher;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import static com.shootr.mobile.domain.asserts.QueuedShotAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ShotDispatcherTest {

    private static final Long QUEUED_ID = 1L;
    private static final Long QUEUED_ID_NEW = 2L;
    private static final String COMMENT_STUB = "comment";
    private static final File IMAGE_FILE_STUB = new File(".");
    private static final File IMAGE_FILE_NULL = null;
    private static final File EXTERNAL_FILES_STUB = new File("ext");

    @Mock BusPublisher busPublisher;
    @Mock ShotQueueListener shotQueueListener;
    @Mock com.shootr.mobile.domain.service.shot.ShootrShotService shootrShotService;
    @Spy ShotQueueRepository shotQueueRepository = new StubShotQueueRepository();

    private ShotDispatcher shotDispatcher;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        shotDispatcher = new ShotDispatcher(shotQueueRepository, shootrShotService, busPublisher, shotQueueListener, EXTERNAL_FILES_STUB);
    }

    @Test
    public void shouldPutOneShotInRepositoryWhenOneShotSent() throws Exception {
        shotDispatcher.sendShot(shot(), IMAGE_FILE_NULL);

        verify(shootrShotService, times(1)).sendShot(any(com.shootr.mobile.domain.Shot.class));
    }

    @Test
    public void shouldSendTwoShotsThroughServiceWhenTwoShotsSentSequentially() throws Exception {
        shotDispatcher.sendShot(shot(), IMAGE_FILE_NULL);
        shotDispatcher.sendShot(shot(), IMAGE_FILE_NULL);

        verify(shootrShotService, times(2)).sendShot(any(com.shootr.mobile.domain.Shot.class));
    }

    @Test
    public void shouldRemoveFromQueueWhenShotSent() throws Exception {
        shotDispatcher.sendShot(shot(), IMAGE_FILE_NULL);

        verify(shotQueueRepository, times(1)).remove(any(QueuedShot.class));
    }

    @Test
    public void shouldPutInQueueWithFailedFlagWhenSendingShotFailed() throws Exception {
        when(shootrShotService.sendShot(any(com.shootr.mobile.domain.Shot.class))).thenThrow(serverCommunicationException());

        shotDispatcher.sendShot(shot(), IMAGE_FILE_NULL);

        ArgumentCaptor<QueuedShot> queuedShotCaptor = ArgumentCaptor.forClass(QueuedShot.class);
        verify(shotQueueRepository, atLeastOnce()).put(queuedShotCaptor.capture());
        QueuedShot queuedShot = queuedShotCaptor.getValue();
        assertThat(queuedShot.isFailed()).isTrue();
    }

    @Test
    public void shouldPostANotNullToBusWhenShotSent() throws Exception {
        when(shootrShotService.sendShot(any(com.shootr.mobile.domain.Shot.class))).thenReturn(shot());

        shotDispatcher.sendShot(shot(), IMAGE_FILE_NULL);

        ArgumentCaptor<com.shootr.mobile.domain.bus.ShotSent.Event> streamArgumentCaptor = ArgumentCaptor.forClass(com.shootr.mobile.domain.bus.ShotSent.Event.class);
        verify(busPublisher, atLeastOnce()).post(streamArgumentCaptor.capture());
        com.shootr.mobile.domain.bus.ShotSent.Event event = streamArgumentCaptor.getValue();

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
        when(shootrShotService.sendShot(any(com.shootr.mobile.domain.Shot.class))).thenThrow(serverCommunicationException());

        shotDispatcher.sendShot(shot(), IMAGE_FILE_NULL);

        verify(shotQueueListener, times(1)).onShotFailed(any(QueuedShot.class), any(Exception.class));
    }

    @Test
    public void shouldNotCreateNewQueuedShotIfShotHasIdQueue() throws Exception {
        shotDispatcher.sendShot(shotInQueue(), IMAGE_FILE_NULL);

        ArgumentCaptor<QueuedShot> captor = ArgumentCaptor.forClass(QueuedShot.class);
        verify(shotQueueRepository, atLeastOnce()).put(captor.capture());
        QueuedShot persistedQueuedShot = captor.getAllValues().get(0);
        assertThat(persistedQueuedShot).hasIdQueue(QUEUED_ID);
    }

    private com.shootr.mobile.domain.exception.ServerCommunicationException serverCommunicationException() {
        return new com.shootr.mobile.domain.exception.ServerCommunicationException(new Throwable());
    }

    private com.shootr.mobile.domain.Shot shot() {
        com.shootr.mobile.domain.Shot shot = new com.shootr.mobile.domain.Shot();
        shot.setComment(COMMENT_STUB);
        return shot;
    }

    private com.shootr.mobile.domain.Shot shotInQueue() {
        com.shootr.mobile.domain.Shot shot = shot();
        shot.setIdQueue(QUEUED_ID);
        return shot;
    }

    private QueuedShot queuedShot() {
        QueuedShot queuedShot = new QueuedShot(shot());
        queuedShot.setIdQueue(QUEUED_ID);
        return queuedShot;
    }

    private QueuedShot newQueuedShot() {
        QueuedShot queuedShot = new QueuedShot(shot());
        queuedShot.setIdQueue(QUEUED_ID_NEW);
        return queuedShot;
    }

    private QueuedShot copy(QueuedShot queuedShot) {
        QueuedShot copy = new QueuedShot();
        copy.setShot(queuedShot.getShot());
        copy.setIdQueue(queuedShot.getIdQueue());
        copy.setImageFile(queuedShot.getImageFile());
        return copy;
    }

    class StubShotQueueRepository implements ShotQueueRepository {

        List<QueuedShot> queuedShots = new ArrayList<>();

        @Override public QueuedShot put(QueuedShot queuedShot) {
            if (queuedShot.isFailed()) {
                queuedShots.remove(0);
                return queuedShot;
            } else {
                QueuedShot copy = copy(queuedShot);
                copy.setIdQueue(QUEUED_ID_NEW);
                queuedShots.add(copy);
                return copy;
            }
        }

        @Override public void remove(QueuedShot queuedShot) {
            queuedShots.remove(0);
        }

        @Override public List<QueuedShot> getPendingShotQueue() {
            return Arrays.asList(new QueuedShot(shot()));
        }

        @Override public QueuedShot nextQueuedShot() {
            if (queuedShots.isEmpty()) {
                return null;
            } else {
                return queuedShots.get(0);
            }
        }

        @Override public List<QueuedShot> getFailedShotQueue() {
            return null;
        }

        @Override public QueuedShot getShotQueue(Long queuedShotId) {
            return new QueuedShot(shot());
        }

    }
}