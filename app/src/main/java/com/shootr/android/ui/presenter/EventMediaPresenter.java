package com.shootr.android.ui.presenter;

import com.shootr.android.domain.Shot;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.event.GetEventMediaInteractor;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.ui.model.mappers.ShotModelMapper;
import com.shootr.android.ui.views.EventMediaView;
import java.util.List;
import javax.inject.Inject;

public class EventMediaPresenter implements Presenter {

    private final GetEventMediaInteractor getEventMediaInteractor;
    private final ShotModelMapper shotModelMapper;
    private EventMediaView eventMediaView;
    private String idEvent;
    private Integer eventMediaCount;

    @Inject public EventMediaPresenter(GetEventMediaInteractor getEventMediaInteractor, ShotModelMapper shotModelMapper) {
        this.getEventMediaInteractor = getEventMediaInteractor;
        this.shotModelMapper = shotModelMapper;
    }

    protected void setView(EventMediaView eventMediaView) {
        this.eventMediaView = eventMediaView;
    }

    public void initialize(EventMediaView eventMediaView, String idEvent, Integer eventMediaCount) {
        this.setView(eventMediaView);
        this.idEvent = idEvent;
        this.eventMediaCount = eventMediaCount;
        eventMediaView.showEmpty();
        retrieveMedia();
    }

    public void retrieveMedia(){
        if(eventMediaCount > 0){
            eventMediaView.showLoading();
            eventMediaView.hideEmpty();
        }
        renderMedia(idEvent);
    }

    private void renderMedia(String idEvent) {
        getEventMediaInteractor.getEventMedia(idEvent, new Interactor.Callback() {
            @Override public void onLoaded(Object o) {
                List<Shot> shotsWithMedia = (List<Shot>) o;
                if(shotsWithMedia.size() > 0) {
                    eventMediaView.hideEmpty();
                    List<ShotModel> shotModels = shotModelMapper.transform(shotsWithMedia);
                    eventMediaView.setMedia(shotModels);
                    eventMediaView.hideLoading();
                }
            }
        });
    }

    @Override public void resume() {

    }

    @Override public void pause() {

    }
}
