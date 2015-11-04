package com.shootr.mobile.domain.interactor.user;

import java.io.File;
import java.io.IOException;
import javax.inject.Inject;

public class UploadUserPhotoInteractor implements com.shootr.mobile.domain.interactor.Interactor {

    private final com.shootr.mobile.domain.interactor.InteractorHandler interactorHandler;
    private final com.shootr.mobile.domain.executor.PostExecutionThread postExecutionThread;

    private final com.shootr.mobile.domain.repository.PhotoService photoService;
    private final com.shootr.mobile.domain.repository.SessionRepository sessionRepository;
    private final com.shootr.mobile.domain.repository.UserRepository localUserRepository;
    private final com.shootr.mobile.domain.repository.UserRepository remoteUserRepository;
    private final com.shootr.mobile.domain.utils.ImageResizer imageResizer;

    private CompletedCallback completedCallback;
    private ErrorCallback errorCallback;
    private File photo;

    @Inject
    public UploadUserPhotoInteractor(com.shootr.mobile.domain.interactor.InteractorHandler interactorHandler, com.shootr.mobile.domain.executor.PostExecutionThread postExecutionThread,
      com.shootr.mobile.domain.repository.PhotoService photoService, com.shootr.mobile.domain.repository.SessionRepository sessionRepository, @com.shootr.mobile.domain.repository.Local
    com.shootr.mobile.domain.repository.UserRepository localUserRepository,
      @com.shootr.mobile.domain.repository.Remote
      com.shootr.mobile.domain.repository.UserRepository remoteUserRepository, com.shootr.mobile.domain.utils.ImageResizer imageResizer) {
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
        try {
            File imageFile = getResizedImage(photo);
            String photoUrl = uploadPhoto(imageFile);
            updateUserWithPhoto(photoUrl);
        } catch (com.shootr.mobile.domain.exception.ServerCommunicationException error) {
            notifyError(error);
        } catch (IOException error) {
            notifyError(new com.shootr.mobile.domain.exception.ImageResizingException(error));
        }
    }

    private void updateUserWithPhoto(String photoUrl) {
        com.shootr.mobile.domain.User user = getUserWithPhoto(photoUrl);
        remoteUserRepository.putUser(user);
        localUserRepository.putUser(user);
        notifyLoaded();
    }

    private com.shootr.mobile.domain.User getUserWithPhoto(String photoUrl) {
        com.shootr.mobile.domain.User user = localUserRepository.getUserById(sessionRepository.getCurrentUserId());
        user.setPhoto(photoUrl);
        return user;
    }

    private File getResizedImage(File newPhotoFile) throws IOException {
        return imageResizer.getResizedCroppedImageFile(newPhotoFile);
    }

    private String uploadPhoto(File imageFile) {
        return photoService.uploadProfilePhotoAndGetUrl(imageFile);
    }

    private void notifyLoaded() {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                completedCallback.onCompleted();
            }
        });
    }

    private void notifyError(final com.shootr.mobile.domain.exception.ShootrException error) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                errorCallback.onError(error);
            }
        });
    }
}
