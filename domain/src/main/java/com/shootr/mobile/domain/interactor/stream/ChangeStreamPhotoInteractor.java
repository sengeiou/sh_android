package com.shootr.mobile.domain.interactor.stream;

import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.PhotoService;
import com.shootr.mobile.domain.repository.stream.ExternalStreamRepository;
import com.shootr.mobile.domain.repository.stream.StreamRepository;
import com.shootr.mobile.domain.utils.ImageResizer;
import java.io.File;
import java.io.IOException;
import javax.inject.Inject;

public class ChangeStreamPhotoInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final ImageResizer imageResizer;
  private final PhotoService photoService;
      //TODO does this need to be a repository? It's a bit special, I think
  private final StreamRepository localStreamRepository;
  private final ExternalStreamRepository remoteStreamRepository;

  private String idStream;
  private File photoFile;
  private Callback callback;
  private ErrorCallback errorCallback;

  @Inject public ChangeStreamPhotoInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, ImageResizer imageResizer, PhotoService photoService,
      @Local StreamRepository localStreamRepository,
      ExternalStreamRepository remoteStreamRepository) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.imageResizer = imageResizer;
    this.photoService = photoService;
    this.localStreamRepository = localStreamRepository;
    this.remoteStreamRepository = remoteStreamRepository;
  }

  public void changeStreamPhoto(String idStream, File photoFile, Callback callback,
      ErrorCallback errorCallback) {
    this.idStream = idStream;
    this.photoFile = photoFile;
    this.callback = callback;
    this.errorCallback = errorCallback;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    try {
      File resizedImageFile = imageResizer.getResizedImageFile(photoFile);
      String idMedia = photoService.uploadStreamImageAndGetIdMedia(resizedImageFile, idStream);
      notifyLoaded(idMedia);
    } catch (IOException e) {
      notifyError(new ServerCommunicationException(e));
    } catch (ServerCommunicationException e) {
      notifyError(e);
    } catch (OutOfMemoryError error) {
      notifyError(new ShootrException() {
      });
    }
  }

  private void notifyLoaded(final String idMedia) {
    postExecutionThread.post(new Runnable() {
      public void run() {
        callback.onLoaded(idMedia);
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

    void onLoaded(String idMedia);
  }
}
