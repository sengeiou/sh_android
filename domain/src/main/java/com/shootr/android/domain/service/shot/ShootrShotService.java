package com.shootr.android.domain.service.shot;

import com.shootr.android.domain.Shot;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.repository.PhotoService;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.ShotRepository;
import com.shootr.android.domain.utils.ImageResizer;
import java.io.File;
import java.io.IOException;
import javax.inject.Inject;

public class ShootrShotService {

    private final ImageResizer imageResizer;
    private final PhotoService photoService;
    private final ShotGateway shotGateway;
    private final ShotRepository remoteShotRepository;

    @Inject public ShootrShotService(ImageResizer imageResizer, PhotoService photoService, ShotGateway shotGateway,
      @Remote ShotRepository remoteShotRepository) {
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
            throw new ServerCommunicationException(e);
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
