package com.shootr.android.domain.interactor.event;

import com.shootr.android.domain.Event;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.StreamRepository;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.PhotoService;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.utils.ImageResizer;
import java.io.File;
import java.io.IOException;
import javax.inject.Inject;

public class ChangeEventPhotoInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final ImageResizer imageResizer;
    private final PhotoService photoService; //TODO does this need to be a repository? It's a bit special, I think
    private final StreamRepository localStreamRepository;
    private final StreamRepository remoteStreamRepository;

    private String idEvent;
    private File photoFile;
    private Callback callback;
    private ErrorCallback errorCallback;

    @Inject public ChangeEventPhotoInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, ImageResizer imageResizer, PhotoService photoService,
      @Local StreamRepository localStreamRepository, @Remote StreamRepository remoteStreamRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.imageResizer = imageResizer;
        this.photoService = photoService;
        this.localStreamRepository = localStreamRepository;
        this.remoteStreamRepository = remoteStreamRepository;
    }

    public void changeEventPhoto(String idEvent, File photoFile, Callback callback, ErrorCallback errorCallback) {
        this.idEvent = idEvent;
        this.photoFile = photoFile;
        this.callback = callback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        try {
            File resizedImageFile = imageResizer.getResizedImageFile(photoFile);
            String imageUrl = photoService.uploadEventImageAndGetUrl(resizedImageFile, idEvent);
            Event event = localStreamRepository.getStreamById(idEvent);
            event.setPicture(imageUrl);
            Event remoteEvent = remoteStreamRepository.putStream(event);
            notifyLoaded(remoteEvent);
        } catch (IOException e) {
            notifyError(new ServerCommunicationException(e));
        } catch (ServerCommunicationException e) {
            notifyError(e);
        }
    }

    private void notifyLoaded(final Event remoteEvent) {
        postExecutionThread.post(new Runnable() {
            public void run() {
                callback.onLoaded(remoteEvent);
            }
        });
    }

    private void notifyError(final ShootrException e) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                errorCallback.onError(e);
            }
        });
    }

    public interface Callback {

        public void onLoaded(Event event);

    }
}
