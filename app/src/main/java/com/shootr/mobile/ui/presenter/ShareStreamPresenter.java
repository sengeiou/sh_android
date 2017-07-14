package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.interactor.stream.GetLocalStreamInteractor;
import com.shootr.mobile.domain.model.stream.Stream;
import com.shootr.mobile.ui.model.StreamModel;
import com.shootr.mobile.ui.model.mappers.StreamModelMapper;
import com.shootr.mobile.ui.views.ShareStreamView;
import javax.inject.Inject;

public class ShareStreamPresenter implements Presenter {

  private final GetLocalStreamInteractor getLocalStreamInteractor;
  private final StreamModelMapper streamModelMapper;

  private ShareStreamView shareStreamView;
  private StreamModel streamModel;

  @Inject public ShareStreamPresenter(GetLocalStreamInteractor getLocalStreamInteractor,
      StreamModelMapper streamModelMapper) {
    this.getLocalStreamInteractor = getLocalStreamInteractor;
    this.streamModelMapper = streamModelMapper;
  }

  public void initialize(ShareStreamView shareStreamView, String idStream) {
    this.shareStreamView = shareStreamView;
    loadStream(idStream);
  }

  private void loadStream(String idStream) {
    getLocalStreamInteractor.loadStream(idStream, new GetLocalStreamInteractor.Callback() {
      @Override public void onLoaded(Stream stream) {
        streamModel = streamModelMapper.transform(stream);
        shareStreamView.renderStreamInfo(streamModel);
      }
    });
  }

  @Override public void resume() {
    /* no-op */
  }

  @Override public void pause() {
    /* no-op */
  }

  public void onShareClick() {
    if (streamModel != null) {
      shareStreamView.shareStreamVia(streamModel);
    }
  }
}
