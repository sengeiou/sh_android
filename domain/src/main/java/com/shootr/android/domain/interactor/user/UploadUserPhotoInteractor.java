package com.shootr.android.domain.interactor.user;

import com.shootr.android.domain.User;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.PhotoService;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.UserRepository;
import com.shootr.android.domain.utils.ImageResizer;
import java.io.File;
import java.io.IOException;
import javax.inject.Inject;

public class UploadUserPhotoInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;

    private final PhotoService photoService;
    private final SessionRepository sessionRepository;
    private final UserRepository localUserRepository;
    private final UserRepository remoteUserRepository;
    private final ImageResizer imageResizer;

    private CompletedCallback completedCallback;
    private ErrorCallback errorCallback;
    private File photo;

    @Inject public UploadUserPhotoInteractor(InteractorHandler interactorHandler, PostExecutionThread postExecutionThread,
      PhotoService photoService, SessionRepository sessionRepository, @Local UserRepository localUserRepository,
      @Remote UserRepository remoteUserRepository, ImageResizer imageResizer) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.photoService = photoService;
        this.sessionRepository = sessionRepository;
        this.localUserRepository = localUserRepository;
        this.remoteUserRepository = remoteUserRepository;
        this.imageResizer = imageResizer;
    }

    public void uploadUserPhoto(File photo, CompletedCallback completedCallback, ErrorCallback errorCallback) {
        this.photo = photo;
        this.completedCallback = completedCallback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        File imageFile = getResizedImage(photo);
        String photoUrl = uploadPhoto(imageFile);
        updateUserWithPhoto(photoUrl);
        notifyLoaded();
    }

    private void updateUserWithPhoto(String photoUrl) {
        User user = localUserRepository.getUserById(sessionRepository.getCurrentUserId());
        user.setPhoto(photoUrl);
        localUserRepository.putUser(user);
        try {
            remoteUserRepository.putUser(user);
        } catch (ServerCommunicationException error) {
            notifyError(error);
        }
    }

    private File getResizedImage(File newPhotoFile) throws IOException {
        return imageResizer.getResizedCroppedImageFile(newPhotoFile);
    }

    private String uploadPhoto(File imageFile) throws IOException {
        return photoService.uploadProfilePhotoAndGetUrl(imageFile);
    }

    private void notifyLoaded() {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                completedCallback.onCompleted();
            }
        });
    }

    private void notifyError(final ShootrException error) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                errorCallback.onError(error);
            }
        });
    }

}
