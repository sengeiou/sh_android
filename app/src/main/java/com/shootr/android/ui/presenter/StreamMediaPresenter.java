package com.shootr.android.ui.presenter;

import com.shootr.android.domain.Shot;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.stream.GetStreamMediaInteractor;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.ui.model.mappers.ShotModelMapper;
import com.shootr.android.ui.views.StreamMediaView;
import com.shootr.android.util.ErrorMessageFactory;
import java.util.List;
import javax.inject.Inject;

public class StreamMediaPresenter implements Presenter {

    private final GetStreamMediaInteractor getStreamMediaInteractor;
    private final ShotModelMapper shotModelMapper;
    private final ErrorMessageFactory errorMessageFactory;
    private StreamMediaView streamMediaView;
    private String idStream;
    private Integer streamMediaCount;

    @Inject public StreamMediaPresenter(GetStreamMediaInteractor getStreamMediaInteractor,
      ShotModelMapper shotModelMapper, ErrorMessageFactory errorMessageFactory) {
        this.getStreamMediaInteractor = getStreamMediaInteractor;
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

    public void retrieveMedia(){
        if(streamMediaCount > 0){
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

    @Override public void resume() {
        /* no-op */
    }

    @Override public void pause() {
        /* no-op */
    }
}
