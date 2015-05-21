package com.shootr.android.ui.presenter;

import com.shootr.android.domain.EventInfo;
import com.shootr.android.domain.User;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.event.GetEventMediaInteractor;
import com.shootr.android.domain.interactor.event.VisibleEventInfoInteractor;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.ui.views.EventMediaView;
import java.util.List;
import javax.inject.Inject;

public class EventMediaPresenter implements Presenter {

    private final GetEventMediaInteractor getEventMediaInteractor;
    private final VisibleEventInfoInteractor eventInfoInteractor;
    private EventMediaView eventMediaView;
    private String idEvent;
    private String idUser;

    @Inject public EventMediaPresenter(GetEventMediaInteractor getEventMediaInteractor,
      VisibleEventInfoInteractor eventInfoInteractor) {
        this.getEventMediaInteractor = getEventMediaInteractor;
        this.eventInfoInteractor = eventInfoInteractor;
    }

    protected void setView(EventMediaView eventMediaView) {
        this.eventMediaView = eventMediaView;
    }

    public void initialize(EventMediaView eventMediaView) {
        this.setView(eventMediaView);
    }

    public void retrieveMedia(String idEvent){
        this.idEvent = idEvent;
        getEventInfo();
        renderMedia(idEvent, idUser);
    }

    private void renderMedia(String idEvent, String idUser) {
        getEventMediaInteractor.getEventMedia(idEvent, idUser, new Interactor.Callback() {
            @Override public void onLoaded(Object o) {
                List<ShotModel> shotsWithMedia = (List<ShotModel>) o;
                eventMediaView.setMedia(shotsWithMedia);
            }
        });
    }

    public void getEventInfo() {
        eventInfoInteractor.obtainEventInfo(idEvent, new VisibleEventInfoInteractor.Callback() {
            @Override public void onLoaded(EventInfo eventInfo) {
                onEventInfoLoaded(eventInfo);
            }
        });
    }

    public void onEventInfoLoaded(EventInfo eventInfo) {
        if (eventInfo.getEvent() != null) {
            User currentUserWatching = eventInfo.getCurrentUserWatching();
            idUser = currentUserWatching.getIdUser();
        }
    }

    @Override public void resume() {

    }

    @Override public void pause() {

    }
}
