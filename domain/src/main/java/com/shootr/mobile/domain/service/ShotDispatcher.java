package com.shootr.mobile.domain.service;

import com.shootr.mobile.domain.QueuedShot;
import com.shootr.mobile.domain.Shot;
import com.shootr.mobile.domain.bus.BusPublisher;
import com.shootr.mobile.domain.bus.ShotFailed;
import com.shootr.mobile.domain.bus.ShotQueued;
import com.shootr.mobile.domain.bus.ShotSent;
import com.shootr.mobile.domain.dagger.TemporaryFilesDir;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShotNotFoundException;
import com.shootr.mobile.domain.exception.StreamReadOnlyException;
import com.shootr.mobile.domain.exception.StreamRemovedException;
import com.shootr.mobile.domain.service.shot.ShootrShotService;
import com.shootr.mobile.domain.utils.Patterns;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.regex.Matcher;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class ShotDispatcher implements ShotSender {

    private final ShotQueueRepository shotQueueRepository;
    private final ShootrShotService shootrShotService;
    private final BusPublisher busPublisher;
    private final ShotQueueListener shotQueueListener;
    private final File queuedImagesDir;

    private boolean isDispatching;

    @Inject public ShotDispatcher(ShotQueueRepository shotQueueRepository, ShootrShotService shootrShotService,
      BusPublisher busPublisher, ShotQueueListener shotQueueListener, @TemporaryFilesDir File externalFilesDir) {
        this.shotQueueRepository = shotQueueRepository;
        this.shootrShotService = shootrShotService;
        this.busPublisher = busPublisher;
        this.shotQueueListener = shotQueueListener;
        this.queuedImagesDir = new File(externalFilesDir, "queuedImages");
        this.queuedImagesDir.mkdirs();
    }

    public void restartQueue() {
        shotQueueListener.resetQueue();
        List<QueuedShot> pendingShotQueue = shotQueueRepository.getPendingShotQueue();
        for (QueuedShot queuedShot : pendingShotQueue) {
            persistShotFailed(queuedShot);
        }
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
        notifyShotQueued(queuedShot);
        startDispatching();
    }

    private QueuedShot putShotIntoPersistenQueue(QueuedShot queuedShot) {
        queuedShot = shotQueueRepository.put(queuedShot);
        queueImageInItsOwnFile(queuedShot);
        return queuedShot;
    }

    private void queueImageInItsOwnFile(QueuedShot shot) {
        File sourceImage = shot.getImageFile();
        if (sourceImage != null) {
            File targetImage = new File(queuedImagesDir, String.valueOf(shot.getIdQueue()));

            if (targetImage.equals(sourceImage)) {
                return;
            }
            copyImage(shot, sourceImage, targetImage);
            shot.setImageFile(targetImage);
            shotQueueRepository.put(shot);
        }
    }

    private void copyImage(QueuedShot shot, File sourceImage, File targetImage) {
        InputStream in = null;
        OutputStream out = null;
        try {
            if (!targetImage.exists()) {
                targetImage.createNewFile();
            }
            in = new FileInputStream(sourceImage);
            out = new FileOutputStream(targetImage);
            byte[] buffer = new byte[1024];
            int lenght;
            while ((lenght = in.read(buffer)) > 0) {
                out.write(buffer, 0, lenght);
            }
        } catch (IOException e) {
            notifyShotSendingFailed(shot, e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                /* no-op */
            }
        }
    }

    private QueuedShot queuedFrom(Shot shot, File shotImage) {
        QueuedShot queuedShot = new QueuedShot(shot);
        if (shotImage != null) {
            queuedShot.setImageFile(shotImage);
        }
        if (shot.getIdQueue() != null) {
            queuedShot.setIdQueue(shot.getIdQueue());
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
        QueuedShot queuedShot = shotQueueRepository.nextQueuedShot();
        if (queuedShot != null) {
            sendShotToServer(queuedShot);
            dispatchNextItems();
        }
    }

    private void sendShotToServer(QueuedShot queuedShot) {
        try {
            notifySendingShot(queuedShot);
            fillImageUrlFromQueuedShot(queuedShot);
            embedVideoFromLinksInComment(queuedShot);
            Shot shotSent = shootrShotService.sendShot(queuedShot.getShot());
            queuedShot.setShot(shotSent);
            notifyShotSent(queuedShot);
            clearShotFromQueue(queuedShot);
        } catch (ServerCommunicationException e) {
            persistShotFailed(queuedShot);
            notifyShotSendingFailed(queuedShot, e);
        } catch (ShotNotFoundException e) {
            clearShotFromQueue(queuedShot);
            notifyShotSendingHasDeletedParent(queuedShot, e);
        } catch (StreamRemovedException e) {
            clearShotFromQueue(queuedShot);
            notifyShotSendingHasRemovedStream(queuedShot, e);
        } catch (StreamReadOnlyException e) {
            clearShotFromQueue(queuedShot);
            notifyShotSendingHasReadOnlyStream(queuedShot, e);
        }
    }

    private void embedVideoFromLinksInComment(QueuedShot queuedShot) {
        boolean alreadyHasVideo = queuedShot.getShot().hasVideoEmbed();
        boolean hasImage = queuedShot.getShot().getImage() != null || queuedShot.getImageFile() != null;
        boolean hasLinks = commentHasLinks(queuedShot.getShot().getComment());
        if (!alreadyHasVideo && !hasImage && hasLinks) {
            Shot shotWithEmbedVideoInfo = shootrShotService.embedVideoFromLinks(queuedShot.getShot());
            queuedShot.setShot(shotWithEmbedVideoInfo);
        }
    }

    private boolean commentHasLinks(String comment) {
        if (comment == null) {
            return false;
        }
        Matcher matcher = Patterns.WEB_URL.matcher(comment);
        return matcher.find();
    }

    private void fillImageUrlFromQueuedShot(QueuedShot queuedShot) {
        if (queuedShot.getShot().getImage() == null && queuedShot.getImageFile() != null) {
            String imageUrl = shootrShotService.uploadShotImage(queuedShot.getImageFile());
            queuedShot.getShot().setImage(imageUrl);
            queuedShot.getImageFile().delete();
            queuedShot.setImageFile(null);
        }
    }

    private void notifyShotQueued(QueuedShot queuedShot) {
        busPublisher.post(new ShotQueued.Event(queuedShot));
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
        shotQueueListener.onShotFailed(queuedShot, e);
        busPublisher.post(new ShotFailed.Event(queuedShot.getShot()));
    }

    private void notifyShotSendingHasDeletedParent(QueuedShot queuedShot, Exception e) {
        shotQueueListener.onShotHasParentDeleted(queuedShot, e);
        busPublisher.post(new ShotFailed.Event(queuedShot.getShot()));
    }

    private void notifyShotSendingHasRemovedStream(QueuedShot queuedShot, Exception e) {
        shotQueueListener.onShotHasStreamRemoved(queuedShot, e);
        busPublisher.post(new ShotFailed.Event(queuedShot.getShot()));
    }

    private void notifyShotSendingHasReadOnlyStream(QueuedShot queuedShot, Exception e) {
        shotQueueListener.onShotIsOnReadOnly(queuedShot, e);
        busPublisher.post(new ShotFailed.Event(queuedShot.getShot()));
    }

    private void persistShotFailed(QueuedShot queuedShot) {
        queuedShot.setFailed(true);
        shotQueueRepository.put(queuedShot);
    }

    private void clearShotFromQueue(QueuedShot queuedShot) {
        shotQueueRepository.remove(queuedShot);
    }
}
