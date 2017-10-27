package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.exception.ImageResizingException;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.PhotoService;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.user.UserRepository;
import com.shootr.mobile.domain.utils.ImageResizer;
import java.io.File;
import java.io.IOException;
import javax.inject.Inject;

public class UploadUserPhotoInteractor implements com.shootr.mobile.domain.interactor.Interactor {

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

    @Inject
    public UploadUserPhotoInteractor(InteractorHandler interactorHandler, PostExecutionThread postExecutionThread,
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
        try {
            File imageFile = getResizedImage(photo);
            String photoUrl = uploadPhoto(imageFile);
            updateUserWithPhoto(photoUrl);
        } catch (ServerCommunicationException error) {
            notifyError(error);
        } catch (IOException error) {
            notifyError(new ImageResizingException(error));
        } catch (NullPointerException error) {
            notifyError(new ImageResizingException(error));
        }
    }

    private void updateUserWithPhoto(String photoUrl) {
        User user = getUserWithPhoto(photoUrl);
        remoteUserRepository.putUser(user);
        localUserRepository.putUser(user);
        notifyLoaded();
    }

    private User getUserWithPhoto(String photoUrl) {
        User user = localUserRepository.getUserById(sessionRepository.getCurrentUserId());
        user.setPhoto(photoUrl);
        return user;
    }

    private File getResizedImage(File newPhotoFile) throws IOException {
        imageResizer.setFromProfile(true);
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

    private void notifyError(final ShootrException error) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                errorCallback.onError(error);
            }
        });
    }
}
