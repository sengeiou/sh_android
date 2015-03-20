package com.shootr.android.ui.presenter;

import com.shootr.android.domain.EventInfo;
import com.shootr.android.domain.interactor.event.VisibleEventInfoInteractor;
import com.shootr.android.ui.views.EventSelectionView;
import javax.inject.Inject;

public class EventSelectionPresenter implements Presenter{

    private final VisibleEventInfoInteractor visibleEventInfoInteractor; //TODO might use a more lightweight interactor

    private EventSelectionView eventSelectionView;

    @Inject public EventSelectionPresenter(VisibleEventInfoInteractor visibleEventInfoInteractor) {
        this.visibleEventInfoInteractor = visibleEventInfoInteractor;
    }

    protected void setView(EventSelectionView eventSelectionView) {
        this.eventSelectionView = eventSelectionView;
    }

    public void initialize(EventSelectionView eventSelectionView) {
        this.setView(eventSelectionView);
        this.loadCurrentEventTitle();
    }

    protected void loadCurrentEventTitle() {
        visibleEventInfoInteractor.obtainVisibleEventInfo(new VisibleEventInfoInteractor.Callback() {
            @Override public void onLoaded(EventInfo eventInfo) {
                if (eventInfo.getEvent() != null) {
                    String visibleEventTag = eventInfo.getEvent().getTag();
                    eventSelectionView.showCurrentEventTitle(visibleEventTag);
                } else {
                    eventSelectionView.showHallTitle();
                }
            }
        });
    }

    @Override public void resume() {
        
    }

    @Override public void pause() {

    }
}
