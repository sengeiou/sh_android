package com.shootr.mobile.domain.interactor.stream;

import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.utils.ImageResizer;
import java.io.File;
import java.io.IOException;
import javax.inject.Inject;

public class ChangeStreamPhotoInteractor implements Interactor {

    private final com.shootr.mobile.domain.interactor.InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final ImageResizer imageResizer;
    private final com.shootr.mobile.domain.repository.PhotoService photoService; //TODO does this need to be a repository? It's a bit special, I think
    private final com.shootr.mobile.domain.repository.StreamRepository localStreamRepository;
    private final com.shootr.mobile.domain.repository.StreamRepository remoteStreamRepository;

    private String idStream;
    private File photoFile;
    private Callback callback;
    private ErrorCallback errorCallback;

    @Inject public ChangeStreamPhotoInteractor(com.shootr.mobile.domain.interactor.InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, ImageResizer imageResizer, com.shootr.mobile.domain.repository.PhotoService photoService,
      @Local com.shootr.mobile.domain.repository.StreamRepository localStreamRepository, @com.shootr.mobile.domain.repository.Remote
    com.shootr.mobile.domain.repository.StreamRepository remoteStreamRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.imageResizer = imageResizer;
        this.photoService = photoService;
        this.localStreamRepository = localStreamRepository;
        this.remoteStreamRepository = remoteStreamRepository;
    }

    public void changeStreamPhoto(String idStream, File photoFile, Callback callback, ErrorCallback errorCallback) {
        this.idStream = idStream;
        this.photoFile = photoFile;
        this.callback = callback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        try {
            File resizedImageFile = imageResizer.getResizedImageFile(photoFile);
            String imageUrl = photoService.uploadStreamImageAndGetUrl(resizedImageFile, idStream);
            com.shootr.mobile.domain.Stream stream = localStreamRepository.getStreamById(idStream);
            stream.setPicture(imageUrl);
            com.shootr.mobile.domain.Stream remoteStream = remoteStreamRepository.putStream(stream);
            notifyLoaded(remoteStream);
        } catch (IOException e) {
            notifyError(new com.shootr.mobile.domain.exception.ServerCommunicationException(e));
        } catch (com.shootr.mobile.domain.exception.ServerCommunicationException e) {
            notifyError(e);
        }
    }

    private void notifyLoaded(final com.shootr.mobile.domain.Stream remoteStream) {
        postExecutionThread.post(new Runnable() {
            public void run() {
                callback.onLoaded(remoteStream);
            }
        });
    }

    private void notifyError(final com.shootr.mobile.domain.exception.ShootrException e) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                errorCallback.onError(e);
            }
        });
    }

    public interface Callback {

        void onLoaded(com.shootr.mobile.domain.Stream stream);

    }
}