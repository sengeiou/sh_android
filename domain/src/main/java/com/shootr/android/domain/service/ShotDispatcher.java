package com.shootr.android.domain.service;

import com.shootr.android.domain.QueuedShot;
import com.shootr.android.domain.Shot;
import com.shootr.android.domain.bus.BusPublisher;
import com.shootr.android.domain.bus.ShotSent;
import com.shootr.android.domain.service.shot.ShootrShotService;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ShotDispatcher implements ShotSender {

    private final ShotQueueRepository shotQueueRepository;
    private final ShootrShotService shootrShotService;
    private final BusPublisher busPublisher;
    private final ShotQueueListener shotQueueListener;

    private boolean isDispatching;
    private final Queue<QueuedShot> shotDispatchingQueue;

    @Inject public ShotDispatcher(ShotQueueRepository shotQueueRepository, ShootrShotService shootrShotService,
      BusPublisher busPublisher, ShotQueueListener shotQueueListener) {
        this.shotQueueRepository = shotQueueRepository;
        this.shootrShotService = shootrShotService;
        this.busPublisher = busPublisher;
        this.shotQueueListener = shotQueueListener;
        shotDispatchingQueue = new LinkedBlockingQueue<>();
        init();
    }

    public void init() {

    }

    public void restartQueue() {
        List<QueuedShot> pendingShotQueue = shotQueueRepository.getPendingShotQueue();
        for (QueuedShot queuedShot : pendingShotQueue) {
            shotDispatchingQueue.add(queuedShot);
            notifyShotQueued(queuedShot);
        }
        startDispatching();
    }

    @Override public void sendShot(Shot shot, File shotImage) {
        if (shot == null) {
            throw new IllegalArgumentException("Can't send a null shot. You crazy person.");
        }
        QueuedShot queuedShot = queuedFrom(shot, shotImage);
        addToQueueAndDispatch(queuedShot);
    }

    private void addToQueueAndDispatch(QueuedShot queuedShot) {
        queuedShot = putShotIntoPersistenQueue(queuedShot);
        shotDispatchingQueue.add(queuedShot);
        notifyShotQueued(queuedShot);
        startDispatching();
    }

    private QueuedShot putShotIntoPersistenQueue(QueuedShot shot) {
        return shotQueueRepository.put(shot);
    }

    private QueuedShot queuedFrom(Shot shot, File shotImage) {
        QueuedShot queuedShot = new QueuedShot(shot);
        if (shotImage != null) {
            queuedShot.setImageFile(shotImage);
        }
        return queuedShot;
    }

    private void startDispatching() {
        if (!isDispatching) {
            isDispatching = true;
            dispatchNextItems();
            isDispatching = false;
        }
    }

    private void dispatchNextItems() {
        sendShotToServer(shotDispatchingQueue.poll());
        if (shotDispatchingQueue.peek() != null) {
            dispatchNextItems();
        }
    }

    private void sendShotToServer(QueuedShot queuedShot) {
        try {
            notifySendingShot(queuedShot);
            fillImageUrlFromQueuedShot(queuedShot);
            Shot shotSent = shootrShotService.sendShot(queuedShot.getShot());
            queuedShot.setShot(shotSent);
            notifyShotSent(queuedShot);
            clearShotFromQueue(queuedShot);
        } catch (Exception e) {
            notifyShotSendingFailed(queuedShot, e);
        }
    }

    private void fillImageUrlFromQueuedShot(QueuedShot queuedShot) {
        if (queuedShot.getShot().getImage() == null && queuedShot.getImageFile() != null) {
            String imageUrl = shootrShotService.uploadShotImage(queuedShot.getImageFile());
            queuedShot.getShot().setImage(imageUrl);
        }
    }

    private void notifyShotQueued(QueuedShot queuedShot) {
        shotQueueListener.onQueueShot(queuedShot);
    }

    private void notifySendingShot(QueuedShot queuedShot) {
        shotQueueListener.onSendingShot(queuedShot);
    }

    private void notifyShotSent(QueuedShot queuedShot) {
        busPublisher.post(new ShotSent.Event(queuedShot.getShot()));
        shotQueueListener.onShotSent(queuedShot);
    }

    private void notifyShotSendingFailed(QueuedShot queuedShot, Exception e) {
        queuedShot.setFailed(true);
        shotQueueListener.onShotFailed(queuedShot, e);
        shotQueueRepository.put(queuedShot);
    }

    private void clearShotFromQueue(QueuedShot queuedShot) {
        shotQueueRepository.remove(queuedShot);
    }
}
