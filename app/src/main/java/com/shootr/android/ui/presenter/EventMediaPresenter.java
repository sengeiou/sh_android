package com.shootr.android.ui.presenter;

import com.shootr.android.domain.Shot;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.event.GetStreamMediaInteractor;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.ui.model.mappers.ShotModelMapper;
import com.shootr.android.ui.views.StreamMediaView;
import com.shootr.android.util.ErrorMessageFactory;
import java.util.List;
import javax.inject.Inject;

public class EventMediaPresenter implements Presenter {

    private final GetStreamMediaInteractor getStreamMediaInteractor;
    private final ShotModelMapper shotModelMapper;
    private final ErrorMessageFactory errorMessageFactory;
    private StreamMediaView streamMediaView;
    private String idEvent;
    private Integer eventMediaCount;

    @Inject public EventMediaPresenter(GetStreamMediaInteractor getStreamMediaInteractor, ShotModelMapper shotModelMapper,
      ErrorMessageFactory errorMessageFactory) {
        this.getStreamMediaInteractor = getStreamMediaInteractor;
        this.shotModelMapper = shotModelMapper;
        this.errorMessageFactory = errorMessageFactory;
    }

    protected void setView(StreamMediaView streamMediaView) {
        this.streamMediaView = streamMediaView;
    }

    public void initialize(StreamMediaView streamMediaView, String idEvent, Integer eventMediaCount) {
        this.setView(streamMediaView);
        this.idEvent = idEvent;
        this.eventMediaCount = eventMediaCount;
        streamMediaView.showEmpty();
        retrieveMedia();
    }

    public void retrieveMedia(){
        if(eventMediaCount > 0){
            streamMediaView.showLoading();
            streamMediaView.hideEmpty();
        }
        renderMedia(idEvent);
    }

    private void renderMedia(String idEvent) {
        getStreamMediaInteractor.getStreamMedia(idEvent, new Interactor.Callback<List<Shot>>() {
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
