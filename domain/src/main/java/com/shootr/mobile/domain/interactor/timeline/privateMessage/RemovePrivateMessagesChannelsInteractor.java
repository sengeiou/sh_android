package com.shootr.mobile.domain.interactor.timeline.privateMessage;

import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.privateMessage.PrivateMessageChannelRepository;
import javax.inject.Inject;

public class RemovePrivateMessagesChannelsInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final PrivateMessageChannelRepository remotePrivateMessageChannelRepository;
  private final PrivateMessageChannelRepository privateMessageChannelRepository;

  private Callback<String> callback;
  private String idPrivateMessageChannel;

  @Inject public RemovePrivateMessagesChannelsInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread,
      @Remote PrivateMessageChannelRepository remotePrivateMessageChannelRepository,
      @Local PrivateMessageChannelRepository privateMessageChannelRepository) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.remotePrivateMessageChannelRepository = remotePrivateMessageChannelRepository;
    this.privateMessageChannelRepository = privateMessageChannelRepository;
  }

  public void removePrivateMessageChannel(String idPrivateMessageChannel,
      Callback<String> callback) {
    this.idPrivateMessageChannel = idPrivateMessageChannel;
    this.callback = callback;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    try {
      remotePrivateMessageChannelRepository.putRemovedPrivateMessageChannel(
          idPrivateMessageChannel);
      privateMessageChannelRepository.putRemovedPrivateMessageChannel(idPrivateMessageChannel);
    } catch (ServerCommunicationException error) {
      privateMessageChannelRepository.markPrivateMessageChannelDeleted(idPrivateMessageChannel);
    }
    notifyLoaded(idPrivateMessageChannel);
  }

  //region Result
  private void notifyLoaded(final String idPrivateMessageChannel) {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        callback.onLoaded(idPrivateMessageChannel);
      }
    });
  }
}
