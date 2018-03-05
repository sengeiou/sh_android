package com.shootr.mobile.domain.service;

import com.shootr.mobile.domain.bus.BusPublisher;
import com.shootr.mobile.domain.bus.MessageFailed;
import com.shootr.mobile.domain.bus.ShotFailed;
import com.shootr.mobile.domain.bus.ShotQueued;
import com.shootr.mobile.domain.bus.ShotSent;
import com.shootr.mobile.domain.dagger.TemporaryFilesDir;
import com.shootr.mobile.domain.exception.NotAllowedToPublishException;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShotNotFoundException;
import com.shootr.mobile.domain.exception.StreamReadOnlyException;
import com.shootr.mobile.domain.exception.StreamRemovedException;
import com.shootr.mobile.domain.exception.UserBlockedToPrivateMessageException;
import com.shootr.mobile.domain.model.privateMessage.PrivateMessage;
import com.shootr.mobile.domain.model.shot.BaseMessage;
import com.shootr.mobile.domain.model.shot.QueuedShot;
import com.shootr.mobile.domain.model.shot.Sendable;
import com.shootr.mobile.domain.model.shot.Shot;
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

public class ShotDispatcher implements MessageSender {

  private static final int IMAGE_URL = 0;
  private static final int IMAGE_WIDTH = 1;
  private static final int IMAGE_HEIGHT = 2;
  private static final int ID_MEDIA = 3;
  private final QueueRepository queueRepository;
  private final ShootrShotService shootrShotService;
  private final BusPublisher busPublisher;
  private final ShotQueueListener shotQueueListener;
  private final File queuedImagesDir;

  private boolean isDispatching;

  @Inject
  public ShotDispatcher(QueueRepository queueRepository, ShootrShotService shootrShotService,
      BusPublisher busPublisher, ShotQueueListener shotQueueListener,
      @TemporaryFilesDir File externalFilesDir) {
    this.queueRepository = queueRepository;
    this.shootrShotService = shootrShotService;
    this.busPublisher = busPublisher;
    this.shotQueueListener = shotQueueListener;
    this.queuedImagesDir = new File(externalFilesDir, "queuedImages");
    this.queuedImagesDir.mkdirs();
  }

  public void restartQueue() {
    shotQueueListener.resetQueue();
    List<QueuedShot> pendingShotQueue = queueRepository.getPendingQueue();
    for (QueuedShot queuedShot : pendingShotQueue) {
      persistShotFailed(queuedShot);
    }
  }

  @Override public void sendMessage(Sendable shot, File shotImage) {
    if (shot == null) {
      throw new IllegalArgumentException("Can't closeSocket a null shot. You crazy person.");
    }
    QueuedShot queuedShot = queuedFrom((BaseMessage) shot, shotImage);
    addToQueueAndDispatch(queuedShot);
  }

  private void addToQueueAndDispatch(QueuedShot queuedShot) {
    queuedShot = putShotIntoPersistenQueue(queuedShot);
    notifyShotQueued(queuedShot);
    if (queuedShot.getBaseMessage() instanceof Shot) {
      startDispatching(QueueRepository.SHOT_TYPE);
    } else {
      startDispatching(QueueRepository.MESSAGE_TYPE);
    }
  }

  private QueuedShot putShotIntoPersistenQueue(QueuedShot queuedShot) {
    queuedShot = queueRepository.put(queuedShot);
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
      queueRepository.put(shot);
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

  private QueuedShot queuedFrom(BaseMessage shot, File shotImage) {
    QueuedShot queuedShot = new QueuedShot(shot);
    if (shotImage != null) {
      queuedShot.setImageFile(shotImage);
    }
    if (shot.getIdQueue() != null) {
      queuedShot.setIdQueue(shot.getIdQueue());
    }
    return queuedShot;
  }

  private void startDispatching(String queuedType) {
    if (!isDispatching) {
      isDispatching = true;
      dispatchNextItems(queuedType);
      isDispatching = false;
    }
  }

  private void dispatchNextItems(String queuedType) {
    QueuedShot queuedShot = queueRepository.nextQueued(queuedType);
    if (queuedShot != null) {
      sendShotToServer(queuedShot);
      dispatchNextItems(queuedType);
    }
  }

  private void sendShotToServer(QueuedShot queuedShot) {
    try {
      notifySendingShot(queuedShot);
      fillImageUrlFromQueuedShot(queuedShot);
      BaseMessage shotSent;
      if (queuedShot.getBaseMessage() instanceof Shot) {
        shotSent = shootrShotService.sendShot((Shot) queuedShot.getBaseMessage());
      } else {
        shotSent =
            shootrShotService.sendPrivateMessage((PrivateMessage) queuedShot.getBaseMessage());
      }
      queuedShot.setBaseMessage(shotSent);
      notifyShotSent(queuedShot);
      clearShotFromQueue(queuedShot);
    } catch (NotAllowedToPublishException e) {
      persistShotFailed(queuedShot);
      notifyPrivateMessageNotAllowed(queuedShot, e);
    } catch (UserBlockedToPrivateMessageException e) {
      clearShotFromQueue(queuedShot);
      notifyShotSendingHasBlockedUser(queuedShot, e);
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
    boolean alreadyHasVideo = queuedShot.getBaseMessage().hasVideoEmbed();
    boolean hasImage =
        queuedShot.getBaseMessage().getImage() != null || queuedShot.getImageFile() != null;
    boolean hasLinks = commentHasLinks(queuedShot.getBaseMessage().getComment());
    if (!alreadyHasVideo && !hasImage && hasLinks) {
      BaseMessage shotWithEmbedVideoInfo =
          shootrShotService.embedVideoFromLinks(queuedShot.getBaseMessage());
      queuedShot.setBaseMessage(shotWithEmbedVideoInfo);
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
    if (queuedShot.getBaseMessage().getImage() == null && queuedShot.getImageFile() != null) {
      List<String> shotImage = shootrShotService.uploadShotImage(queuedShot.getImageFile());
      queuedShot.getBaseMessage().setImage(shotImage.get(IMAGE_URL));
      try {
        queuedShot.getBaseMessage().setImageWidth(Long.valueOf(shotImage.get(IMAGE_WIDTH)));
        queuedShot.getBaseMessage().setImageHeight(Long.valueOf(shotImage.get(IMAGE_HEIGHT)));
      } catch (NumberFormatException e) {
        /* no-op */
      }
      queuedShot.getBaseMessage().setImageIdMedia(shotImage.get(ID_MEDIA));
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
    busPublisher.post(new ShotSent.Event(queuedShot.getBaseMessage()));
    shotQueueListener.onShotSent(queuedShot);
  }

  private void notifyShotSendingFailed(QueuedShot queuedShot, Exception e) {
    shotQueueListener.onShotFailed(queuedShot, e);
    busPublisher.post(new ShotFailed.Event(queuedShot.getBaseMessage()));
  }

  private void notifyShotSendingHasBlockedUser(QueuedShot queuedShot, Exception e) {
    shotQueueListener.onPrivateMessageBlockedUser(queuedShot, e);
    busPublisher.post(new MessageFailed.Event(queuedShot.getBaseMessage()));
  }

  private void notifyPrivateMessageNotAllowed(QueuedShot queuedShot, Exception e) {
    shotQueueListener.onPrivateMessageNotAllowed(queuedShot, e);
    busPublisher.post(new ShotFailed.Event(queuedShot.getBaseMessage()));
  }

  private void notifyShotSendingHasDeletedParent(QueuedShot queuedShot, Exception e) {
    shotQueueListener.onShotHasParentDeleted(queuedShot, e);
    busPublisher.post(new ShotFailed.Event(queuedShot.getBaseMessage()));
  }

  private void notifyShotSendingHasRemovedStream(QueuedShot queuedShot, Exception e) {
    shotQueueListener.onShotHasStreamRemoved(queuedShot, e);
    busPublisher.post(new ShotFailed.Event(queuedShot.getBaseMessage()));
  }

  private void notifyShotSendingHasReadOnlyStream(QueuedShot queuedShot, Exception e) {
    shotQueueListener.onShotIsOnReadOnly(queuedShot, e);
    busPublisher.post(new ShotFailed.Event(queuedShot.getBaseMessage()));
  }

  private void persistShotFailed(QueuedShot queuedShot) {
    queuedShot.setFailed(true);
    queueRepository.put(queuedShot);
  }

  private void clearShotFromQueue(QueuedShot queuedShot) {
    queueRepository.remove(queuedShot);
  }
}
