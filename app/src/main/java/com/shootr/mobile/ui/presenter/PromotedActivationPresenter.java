package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.stream.UpdateStreamInteractor;
import com.shootr.mobile.domain.model.stream.Stream;
import com.shootr.mobile.ui.model.StreamModel;
import com.shootr.mobile.ui.model.mappers.StreamModelMapper;
import com.shootr.mobile.ui.views.PromotedActivationView;
import com.shootr.mobile.util.ErrorMessageFactory;
import javax.inject.Inject;

public class PromotedActivationPresenter implements Presenter {

  private final UpdateStreamInteractor updateStreamInteractor;
  private final StreamModelMapper streamModelMapper;
  private final ErrorMessageFactory errorMessageFactory;

  private PromotedActivationView view;
  private Boolean hasBeenPaused = false;
  private StreamModel stream;

  @Inject public PromotedActivationPresenter(UpdateStreamInteractor updateStreamInteractor,
      StreamModelMapper streamModelMapper, ErrorMessageFactory errorMessageFactory) {
    this.updateStreamInteractor = updateStreamInteractor;
    this.streamModelMapper = streamModelMapper;
    this.errorMessageFactory = errorMessageFactory;
  }

  protected void setView(PromotedActivationView view) {
    this.view = view;
  }

  public void initialize(PromotedActivationView view, StreamModel stream) {
    this.setView(view);
    this.stream = stream;
    setupSwitch(stream);
  }

  private void setupSwitch(StreamModel stream) {
    if (stream.canTogglePromoted()) {
      this.view.enableSwitch();
    } else {
      this.view.disableSwitch();
    }

    if (stream.isPromotedShotsEnabled()) {
      view.setOnText();
    } else {
      view.setOffText();
    }
  }

  @Override public void resume() {
      /* no-op */
  }

  @Override public void pause() {
    this.hasBeenPaused = true;
  }

  public void onActivatePromoted() {
    view.disableSwitch();
    updateStreamInteractor.updatePromotedActivation(stream.getIdStream(), true, new UpdateStreamInteractor.Callback() {
      @Override public void onLoaded(Stream stream) {
        setupSwitch(streamModelMapper.transform(stream));
      }
    }, new Interactor.ErrorCallback() {
      @Override public void onError(ShootrException error) {

      }
    });
  }

  public void onDesactivatePromoted() {
    view.disableSwitch();
    updateStreamInteractor.updatePromotedActivation(stream.getIdStream(), false, new UpdateStreamInteractor.Callback() {
      @Override public void onLoaded(Stream stream) {
        setupSwitch(streamModelMapper.transform(stream));
      }
    }, new Interactor.ErrorCallback() {
      @Override public void onError(ShootrException error) {

      }
    });
  }

}
