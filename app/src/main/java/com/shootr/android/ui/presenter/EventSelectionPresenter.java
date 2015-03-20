package com.shootr.android.ui.presenter;

import com.shootr.android.domain.EventInfo;
import com.shootr.android.domain.Watch;
import com.shootr.android.domain.bus.BusPublisher;
import com.shootr.android.domain.bus.EventChanged;
import com.shootr.android.domain.interactor.event.SelectEventInteractor;
import com.shootr.android.domain.interactor.event.VisibleEventInfoInteractor;
import com.shootr.android.ui.views.EventSelectionView;
import javax.inject.Inject;

public class EventSelectionPresenter implements Presenter{

    private final VisibleEventInfoInteractor visibleEventInfoInteractor; //TODO might use a more lightweight interactor
    private final SelectEventInteractor selectEventInteractor;
    private final BusPublisher busPublisher;

    private EventSelectionView eventSelectionView;

    @Inject public EventSelectionPresenter(VisibleEventInfoInteractor visibleEventInfoInteractor,
      SelectEventInteractor selectEventInteractor, BusPublisher busPublisher) {
        this.visibleEventInfoInteractor = visibleEventInfoInteractor;
        this.selectEventInteractor = selectEventInteractor;
        this.busPublisher = busPublisher;
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

    public void selectEventClick() {
        eventSelectionView.openEventSelectionView();
    }

    public void onEventSelected(Long eventId) {
        selectEventInteractor.selectEvent(eventId, new SelectEventInteractor.Callback() {
            @Override public void onLoaded(Watch watch) {
                onEventChanged(watch.getIdEvent());
            }
        });
    }

    private void onEventChanged(Long idEvent) {
        notifyEventChanged(idEvent);
    }

    private void notifyEventChanged(Long idEvent) {
        busPublisher.post(new EventChanged.Event(idEvent));
    }

    @Override public void resume() {
        
    }

    @Override public void pause() {

    }
}
