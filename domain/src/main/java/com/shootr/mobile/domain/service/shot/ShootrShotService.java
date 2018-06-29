package com.shootr.mobile.domain.service.shot;

import com.shootr.mobile.domain.exception.NotAllowedToPublishException;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.UserBlockedToPrivateMessageException;
import com.shootr.mobile.domain.model.privateMessage.PrivateMessage;
import com.shootr.mobile.domain.model.shot.BaseMessage;
import com.shootr.mobile.domain.model.shot.Shot;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.PhotoService;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.follow.FollowRepository;
import com.shootr.mobile.domain.repository.privateMessage.PrivateMessageRepository;
import com.shootr.mobile.domain.repository.shot.ExternalShotRepository;
import com.shootr.mobile.domain.utils.ImageResizer;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;

public class ShootrShotService {

  private final ImageResizer imageResizer;
  private final PhotoService photoService;
  private final ShotGateway shotGateway;
  private final ExternalShotRepository remoteShotRepository;
  private final PrivateMessageRepository privateMessageRepository;
  private final FollowRepository localFollowRepository;

  @Inject public ShootrShotService(ImageResizer imageResizer, PhotoService photoService,
      ShotGateway shotGateway, ExternalShotRepository remoteShotRepository,
      @Remote PrivateMessageRepository privateMessageRepository,
      @Local FollowRepository localFollowRepository) {
    this.imageResizer = imageResizer;
    this.photoService = photoService;
    this.shotGateway = shotGateway;
    this.remoteShotRepository = remoteShotRepository;
    this.privateMessageRepository = privateMessageRepository;
    this.localFollowRepository = localFollowRepository;
  }

  public List<String> uploadShotImage(File imageFile) {
    try {
      File resizedImageFile = getResizedImage(imageFile);
      return uploadPhoto(resizedImageFile);
    } catch (IOException | NullPointerException e) {
      throw new ServerCommunicationException(e);
    }
  }

  public BaseMessage embedVideoFromLinks(BaseMessage originalShot) {
    try {
      return shotGateway.embedVideoInfo(originalShot);
    } catch (IOException e) {
            /* Ignore error and return shot without modifications */
      return originalShot;
    }
  }

  public Shot sendShot(Shot shot) {
    return remoteShotRepository.putShot(shot);
  }

  public void sendShotViaSocket(Shot shot, String idQueue) {
    remoteShotRepository.sendShotViaSocket(shot, idQueue);
  }

  public PrivateMessage sendPrivateMessage(PrivateMessage privateMessage) {
    try {
      if (localFollowRepository.getBlockedIdUsers().contains(privateMessage.getIdTargetUser())) {
        throw new UserBlockedToPrivateMessageException(new Throwable("This user is blocked"));
      }
      return privateMessageRepository.putPrivateMessage(privateMessage);
    } catch (NotAllowedToPublishException e) {
      throw e;
    } catch (UserBlockedToPrivateMessageException err) {
      throw err;
    }
  }

  private List<String> uploadPhoto(File imageFile) throws IOException {
    return photoService.uploadShotImageAndGetUrl(imageFile);
  }

  private File getResizedImage(File newPhotoFile) throws IOException, NullPointerException {
    return imageResizer.getResizedImageFile(newPhotoFile);
  }
}
