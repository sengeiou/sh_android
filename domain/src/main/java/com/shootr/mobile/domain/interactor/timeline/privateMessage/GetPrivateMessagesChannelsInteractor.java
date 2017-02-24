package com.shootr.mobile.domain.interactor.timeline.privateMessage;

import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.model.privateMessageChannel.PrivateMessageChannel;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.privateMessage.PrivateMessageChannelRepository;
import java.util.List;
import javax.inject.Inject;

public class GetPrivateMessagesChannelsInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final PrivateMessageChannelRepository remotePrivateMessageChannelRepository;
  private final PrivateMessageChannelRepository privateMessageChannelRepository;

  private Callback<List<PrivateMessageChannel>> callback;
  private ErrorCallback errorCallback;
  private boolean localOnly;

  @Inject public GetPrivateMessagesChannelsInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread,
      @Remote PrivateMessageChannelRepository remotePrivateMessageChannelRepository,
      @Local PrivateMessageChannelRepository privateMessageChannelRepository) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.remotePrivateMessageChannelRepository = remotePrivateMessageChannelRepository;
    this.privateMessageChannelRepository = privateMessageChannelRepository;
  }

  public void loadChannels(boolean localOnly, Callback<List<PrivateMessageChannel>> callback,
      ErrorCallback errorCallback) {
    this.callback = callback;
    this.errorCallback = errorCallback;
    interactorHandler.execute(this);
    this.localOnly = localOnly;
  }

  @Override public void execute() throws Exception {
    if (localOnly) {
      loadLocalPrivateChannels();
    } else {
      loadRemoteChannels();
    }
  }

  private void loadRemoteChannels() {
    loadLocalPrivateChannels();
    try {
      loadRemotePrivateChannels();
    } catch (ServerCommunicationException error) {
      notifyError(error);
    }
  }

  private void loadRemotePrivateChannels() {
    remotePrivateMessageChannelRepository.getPrivateMessageChannels();
    notifyLoaded(privateMessageChannelRepository.getPrivateMessageChannels());
  }

  private void loadLocalPrivateChannels() {
    notifyLoaded(privateMessageChannelRepository.getPrivateMessageChannels());
  }

  //region Result
  private void notifyLoaded(final List<PrivateMessageChannel> results) {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        callback.onLoaded(results);
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
