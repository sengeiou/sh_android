package com.shootr.mobile.domain.service.shot;

import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.model.shot.Shot;
import com.shootr.mobile.domain.repository.PhotoService;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.ShotRepository;
import com.shootr.mobile.domain.utils.ImageResizer;
import java.io.File;
import java.io.IOException;
import java.util.List;
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

    public List<String> uploadShotImage(File imageFile) {
        try {
            File resizedImageFile = getResizedImage(imageFile);
            return uploadPhoto(resizedImageFile);
        } catch (IOException | NullPointerException e) {
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

    private List<String> uploadPhoto(File imageFile) throws IOException {
        return photoService.uploadShotImageAndGetUrl(imageFile);
    }

    private File getResizedImage(File newPhotoFile) throws IOException, NullPointerException {
        return imageResizer.getResizedImageFile(newPhotoFile);
    }
}
