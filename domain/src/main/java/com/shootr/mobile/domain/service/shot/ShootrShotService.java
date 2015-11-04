package com.shootr.mobile.domain.service.shot;

import com.shootr.mobile.domain.Shot;
import com.shootr.mobile.domain.utils.ImageResizer;
import java.io.File;
import java.io.IOException;
import javax.inject.Inject;

public class ShootrShotService {

    private final ImageResizer imageResizer;
    private final com.shootr.mobile.domain.repository.PhotoService photoService;
    private final ShotGateway shotGateway;
    private final com.shootr.mobile.domain.repository.ShotRepository remoteShotRepository;

    @Inject public ShootrShotService(ImageResizer imageResizer, com.shootr.mobile.domain.repository.PhotoService photoService, ShotGateway shotGateway,
      @com.shootr.mobile.domain.repository.Remote
      com.shootr.mobile.domain.repository.ShotRepository remoteShotRepository) {
        this.imageResizer = imageResizer;
        this.photoService = photoService;
        this.shotGateway = shotGateway;
        this.remoteShotRepository = remoteShotRepository;
    }

    public String uploadShotImage(File imageFile) {
        try {
            File resizedImageFile = getResizedImage(imageFile);
            return uploadPhoto(resizedImageFile);
        } catch (IOException e) {
            throw new com.shootr.mobile.domain.exception.ServerCommunicationException(e);
        }
    }

    public Shot embedVideoFromLinks(Shot originalShot) {
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

    private String uploadPhoto(File imageFile) throws IOException {
        return photoService.uploadShotImageAndGetUrl(imageFile);
    }

    private File getResizedImage(File newPhotoFile) throws IOException {
        return imageResizer.getResizedImageFile(newPhotoFile);
    }
}
