package com.shootr.mobile.domain.interactor.timeline.privateMessage;

import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.model.privateMessage.PrivateMessage;
import com.shootr.mobile.domain.model.privateMessageChannel.PrivateMessageChannelTimelineParameters;
import com.shootr.mobile.domain.model.privateMessageChannel.PrivateMessageTimeline;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.privateMessage.PrivateMessageRepository;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;

public class GetPrivateMessageTimelineInteractor implements Interactor {

  //region Dependencies
  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final PrivateMessageRepository remotePrivateMessageRepository;
  private final PrivateMessageRepository localPrivateMessageRepository;
  private String idTargetUser;
  private String idChannel;
  private Callback callback;
  private Boolean goneBackground;
  private Boolean loadFromServer = true;

  @Inject public GetPrivateMessageTimelineInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread,
      @Remote PrivateMessageRepository remotePrivateMessageRepository,
      @Local PrivateMessageRepository localPrivateMessageRepository) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.remotePrivateMessageRepository = remotePrivateMessageRepository;
    this.localPrivateMessageRepository = localPrivateMessageRepository;
  }
  //endregion

  public void loadStreamTimeline(String idChannel, String idTargetUser, Boolean goneBackground,
      Boolean loadFromServer, Callback<PrivateMessageTimeline> callback) {
    this.idTargetUser = idTargetUser;
    this.idChannel = idChannel;
    this.goneBackground = goneBackground;
    this.callback = callback;
    this.loadFromServer = loadFromServer;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    if (idChannel != null) {
      loadLocalPrivateMessages();
    }
    try {
      if (loadFromServer) {
        PrivateMessageTimeline timeline =
            remotePrivateMessageRepository.getPrivateMessageTimeline(idTargetUser);
        notifyLoaded(timeline);
      }
    } catch (ServerCommunicationException error) {
      //TODO aquí hay que retornar algo para crear el timeline en local
    }
  }

  private void loadLocalPrivateMessages() {
    PrivateMessageTimeline timeline =
        localPrivateMessageRepository.getPrivateMessageTimeline(idTargetUser);
    List<PrivateMessage> messages = timeline.getPrivateMessages();
    messages = sortShotsByPublishDate(messages);
    timeline.setPrivateMessages(messages);
    notifyTimelineFromShots(timeline);
  }

  private PrivateMessageChannelTimelineParameters buildParameters() {
    return PrivateMessageChannelTimelineParameters.builder()
        .forChannel(idTargetUser)
        .realTime(goneBackground)
        .build();
  }

  private List<PrivateMessage> sortShotsByPublishDate(List<PrivateMessage> privateMessages) {
    Collections.sort(privateMessages, new PrivateMessage.NewerAboveComparator());
    return privateMessages;
  }

  //region Result
  private void notifyTimelineFromShots(PrivateMessageTimeline timeline) {
    notifyLoaded(timeline);
  }

  private void notifyLoaded(final PrivateMessageTimeline timeline) {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        callback.onLoaded(timeline);
      }
    });
  }
  //endregion
}
