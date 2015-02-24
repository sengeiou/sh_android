package com.shootr.android.domain.service;

import com.shootr.android.domain.QueuedShot;
import com.shootr.android.domain.Shot;
import com.shootr.android.domain.bus.BusPublisher;
import com.shootr.android.domain.bus.ShotSent;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.ShotRepository;
import java.util.LinkedList;
import java.util.Queue;
import javax.inject.Inject;

public class ShotDispatcher {

    private final ShotRepository remoteShotRepository;
    private final ShotQueueRepository shotQueueRepository;
    private final BusPublisher busPublisher;
    private final ShotQueueListener shotQueueListener;

    private boolean isDispatching;
    private Queue<QueuedShot> shotDispatchingQueue;

    @Inject public ShotDispatcher(@Remote ShotRepository remoteShotRepository, ShotQueueRepository shotQueueRepository,
      BusPublisher busPublisher, ShotQueueListener shotQueueListener) {
        this.remoteShotRepository = remoteShotRepository;
        this.shotQueueRepository = shotQueueRepository;
        this.busPublisher = busPublisher;
        this.shotQueueListener = shotQueueListener;
        shotDispatchingQueue = new LinkedList<>();
        init();
    }

    public void init() {

    }

    public void sendShot(Shot shot) {
        if (shot == null) {
            throw new IllegalArgumentException("Can't send a null shot. You crazy person.");
        }
        addToQueueAndDispatch(shot);
    }

    private void addToQueueAndDispatch(Shot shot) {
        QueuedShot queuedShot = putShotIntoPersistenQueue(shot);
        shotDispatchingQueue.add(queuedShot);
        dispatchNextItem();
    }

    private QueuedShot putShotIntoPersistenQueue(Shot shot) {
        QueuedShot queuedShot = new QueuedShot(shot);
        return shotQueueRepository.put(queuedShot);
    }

    private void dispatchNextItem() {
        if (!isDispatching) {
            isDispatching = true;
            sendShotToServer(shotDispatchingQueue.poll());
            if (shotDispatchingQueue.peek() == null) {
                isDispatching = false;
            }
        }
    }

    private void sendShotToServer(QueuedShot queuedShot) {
        try {
            notifySendingShot(queuedShot);
            Shot shotSent = remoteShotRepository.putShot(queuedShot.getShot());
            queuedShot.setShot(shotSent);
            notifyShotSent(queuedShot);
            clearShotFromQueue(queuedShot);
        } catch (Exception e) {
            notifyShotSendingFailed(queuedShot);
        }
        dispatchNextItem();
    }

    private void notifySendingShot(QueuedShot queuedShot) {
        shotQueueListener.onSendingShot(queuedShot);
    }

    private void notifyShotSent(QueuedShot queuedShot) {
        busPublisher.post(new ShotSent.Event(queuedShot.getShot()));
        shotQueueListener.onShotSent(queuedShot);
    }

    private void notifyShotSendingFailed(QueuedShot queuedShot) {
        queuedShot.setFailed(true);
        shotQueueRepository.put(queuedShot);
        shotQueueListener.onShotFailed(queuedShot);
    }

    private void clearShotFromQueue(QueuedShot queuedShot) {
        shotQueueRepository.remove(queuedShot);
    }
}
