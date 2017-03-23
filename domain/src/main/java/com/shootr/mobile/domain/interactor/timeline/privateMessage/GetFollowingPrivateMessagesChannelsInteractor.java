package com.shootr.mobile.domain.interactor.timeline.privateMessage;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.model.privateMessageChannel.PrivateMessageChannel;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.privateMessage.PrivateMessageChannelRepository;
import com.shootr.mobile.domain.repository.user.UserRepository;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class GetFollowingPrivateMessagesChannelsInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final PrivateMessageChannelRepository privateMessageChannelRepository;
  private final UserRepository localUserRepository;

  private String idUser;
  private Callback<List<PrivateMessageChannel>> callback;
  private ErrorCallback errorCallback;

  @Inject public GetFollowingPrivateMessagesChannelsInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread,
      @Local PrivateMessageChannelRepository privateMessageChannelRepository,
      @Local UserRepository localUserRepository) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.privateMessageChannelRepository = privateMessageChannelRepository;
    this.localUserRepository = localUserRepository;
  }

  public void loadChannels(String idUser, Callback<List<PrivateMessageChannel>> callback,
      ErrorCallback errorCallback) {
    this.callback = callback;
    this.errorCallback = errorCallback;
    this.idUser = idUser;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    loadLocalPrivateChannels();
  }

  private void loadLocalPrivateChannels() {
    List<PrivateMessageChannel> channels =
        privateMessageChannelRepository.getPrivateMessageChannels();
    ArrayList<PrivateMessageChannel> followingChannels = filterChannels(channels);
    notifyLoaded(followingChannels);
  }

  private ArrayList<PrivateMessageChannel> filterChannels(List<PrivateMessageChannel> channels) {
    List<String> userList = localUserRepository.getFollowingIds(idUser);
    ArrayList<PrivateMessageChannel> followingChannels = new ArrayList<>();
    for (PrivateMessageChannel channel : channels) {
      if (userList.contains(channel.getIdTargetUser())) {
        followingChannels.add(channel);
      }
    }
    return followingChannels;
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
