package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.exception.ShootrValidationException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.timeline.privateMessage.GetPrivateMessagesChannelsInteractor;
import com.shootr.mobile.domain.model.privateMessageChannel.PrivateMessageChannel;
import com.shootr.mobile.ui.model.PrivateMessageChannelModel;
import com.shootr.mobile.ui.model.mappers.PrivateMessageChannelModelMapper;
import com.shootr.mobile.ui.views.PrivateMessageChannelListView;
import com.shootr.mobile.util.ErrorMessageFactory;
import java.util.List;
import javax.inject.Inject;

public class PrivateMessagesChannelListPresenter implements Presenter {

  private final GetPrivateMessagesChannelsInteractor getPrivateMessagesChannelsInteractor;
  private final PrivateMessageChannelModelMapper mapper;
  private final ErrorMessageFactory errorMessageFactory;

  private PrivateMessageChannelListView view;
  private boolean hasBeenPaused = false;
  private List<PrivateMessageChannelModel> privateMessageChannelModels;

  @Inject public PrivateMessagesChannelListPresenter(
      GetPrivateMessagesChannelsInteractor getPrivateMessagesChannelsInteractor,
      PrivateMessageChannelModelMapper mapper, ErrorMessageFactory errorMessageFactory) {
    this.getPrivateMessagesChannelsInteractor = getPrivateMessagesChannelsInteractor;
    this.mapper = mapper;
    this.errorMessageFactory = errorMessageFactory;
  }

  public void setView(PrivateMessageChannelListView findStreamsView) {
    this.view = findStreamsView;
  }

  public void initialize(final PrivateMessageChannelListView view) {
    this.setView(view);
    loadChannels();
  }

  private void loadChannels() {
    view.showLoading();
    getPrivateMessagesChannelsInteractor.loadChannels(
        new Interactor.Callback<List<PrivateMessageChannel>>() {
          @Override public void onLoaded(List<PrivateMessageChannel> result) {
            onLoadResults(result);
          }
        }, new Interactor.ErrorCallback() {
          @Override public void onError(ShootrException error) {
            view.hideLoading();
            showViewError(error);
          }
        });
  }

  private void onLoadResults(List<PrivateMessageChannel> resultList) {
    if (!resultList.isEmpty()) {
      privateMessageChannelModels = mapper.transform(resultList);
      renderViewChannelList(privateMessageChannelModels);
    } else {
      this.showViewEmpty();
    }
    view.hideLoading();
  }

  private void showViewEmpty() {
    view.showEmpty();
  }

  private void renderViewChannelList(List<PrivateMessageChannelModel> privateMessageChannelModels) {
    view.hideEmpty();
    view.renderChannels(privateMessageChannelModels);
  }

  private void showViewError(ShootrException error) {
    String errorMessage;
    if (error instanceof ShootrValidationException) {
      String errorCode = ((ShootrValidationException) error).getErrorCode();
      errorMessage = errorMessageFactory.getMessageForCode(errorCode);
    } else {
      errorMessage = errorMessageFactory.getMessageForError(error);
    }
    view.showError(errorMessage);
  }

  @Override public void resume() {
    if (hasBeenPaused) {
      loadChannels();
    }
  }

  @Override public void pause() {
    hasBeenPaused = true;
  }
}
