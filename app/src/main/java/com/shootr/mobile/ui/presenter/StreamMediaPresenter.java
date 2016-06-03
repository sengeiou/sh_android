package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.Shot;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.shot.GetOlderStreamMediaInteractor;
import com.shootr.mobile.domain.interactor.shot.GetStreamMediaInteractor;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.ui.model.mappers.ShotModelMapper;
import com.shootr.mobile.ui.views.StreamMediaView;
import com.shootr.mobile.util.ErrorMessageFactory;
import java.util.List;
import javax.inject.Inject;

public class StreamMediaPresenter implements Presenter {

    private final GetStreamMediaInteractor getStreamMediaInteractor;
    private final GetOlderStreamMediaInteractor getOlderStreamMediaInteractor;
    private final ShotModelMapper shotModelMapper;
    private final ErrorMessageFactory errorMessageFactory;
    private StreamMediaView streamMediaView;
    private String idStream;
    private Integer streamMediaCount;
    private boolean isLoadingOlderMedia;
    private boolean mightHaveMoreMedia = true;

    @Inject public StreamMediaPresenter(GetStreamMediaInteractor getStreamMediaInteractor,
      GetOlderStreamMediaInteractor getOlderStreamMediaInteractor, ShotModelMapper shotModelMapper,
      ErrorMessageFactory errorMessageFactory) {
        this.getStreamMediaInteractor = getStreamMediaInteractor;
        this.getOlderStreamMediaInteractor = getOlderStreamMediaInteractor;
        this.shotModelMapper = shotModelMapper;
        this.errorMessageFactory = errorMessageFactory;
    }

    protected void setView(StreamMediaView streamMediaView) {
        this.streamMediaView = streamMediaView;
    }

    public void initialize(StreamMediaView streamMediaView, String idStream, Integer streamMediaCount) {
        this.setView(streamMediaView);
        this.idStream = idStream;
        this.streamMediaCount = streamMediaCount;
        streamMediaView.showEmpty();
        retrieveMedia();
    }

    public void retrieveMedia() {
        if (streamMediaCount > 0) {
            streamMediaView.showLoading();
            streamMediaView.hideEmpty();
        }
        renderMedia(idStream);
    }

    private void renderMedia(String idStream) {
        getStreamMediaInteractor.getStreamMedia(idStream, new Interactor.Callback<List<Shot>>() {
            @Override public void onLoaded(List<Shot> shotsWithMedia) {
                if (shotsWithMedia != null && !shotsWithMedia.isEmpty()) {
                    streamMediaView.hideEmpty();
                    List<ShotModel> shotModels = shotModelMapper.transform(shotsWithMedia);
                    streamMediaView.setMedia(shotModels);
                    streamMediaView.hideLoading();
                }
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                String errorMessage = errorMessageFactory.getMessageForError(error);
                streamMediaView.showError(errorMessage);
            }
        });
    }

    public void showingLastMedia(ShotModel lastMedia) {
        if (!isLoadingOlderMedia && mightHaveMoreMedia) {
            this.loadOlderMedia(lastMedia.getBirth().getTime());
        }
    }

    private void loadOlderMedia(long lastMediaInScreenDate) {
        isLoadingOlderMedia = true;
        streamMediaView.showLoadingOldMedia();
        getOlderStreamMediaInteractor.getOlderStreamMedia(idStream,
          lastMediaInScreenDate,
          new Interactor.Callback<List<Shot>>() {
              @Override public void onLoaded(List<Shot> shotsWithMedia) {
                  isLoadingOlderMedia = false;
                  streamMediaView.hideLoadingOldMedia();
                  if (shotsWithMedia != null && !shotsWithMedia.isEmpty()) {
                      List<ShotModel> shotModels = shotModelMapper.transform(shotsWithMedia);
                      streamMediaView.addOldMedia(shotModels);
                  } else {
                      streamMediaView.showNoMoreMedia();
                      mightHaveMoreMedia = false;
                  }
              }
          },
          new Interactor.ErrorCallback() {
              @Override public void onError(ShootrException error) {
                  streamMediaView.hideLoadingOldMedia();
                  String errorMessage = errorMessageFactory.getMessageForError(error);
                  streamMediaView.showError(errorMessage);
              }
          });
    }

    @Override public void resume() {
        /* no-op */
    }

    @Override public void pause() {
        /* no-op */
    }
}
