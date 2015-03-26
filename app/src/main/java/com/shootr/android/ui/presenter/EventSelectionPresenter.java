package com.shootr.android.ui.presenter;

import com.shootr.android.domain.EventInfo;
import com.shootr.android.domain.bus.BusPublisher;
import com.shootr.android.domain.bus.EventChanged;
import com.shootr.android.domain.interactor.event.ExitEventInteractor;
import com.shootr.android.domain.interactor.event.SelectEventInteractor;
import com.shootr.android.domain.interactor.event.VisibleEventInfoInteractor;
import com.shootr.android.ui.views.EventSelectionView;
import javax.inject.Inject;

public class EventSelectionPresenter implements Presenter {

    private final VisibleEventInfoInteractor visibleEventInfoInteractor; //TODO might use a more lightweight interactor
    private final SelectEventInteractor selectEventInteractor;
    private final ExitEventInteractor exitEventInteractor;
    private final BusPublisher busPublisher;

    private EventSelectionView eventSelectionView;

    @Inject public EventSelectionPresenter(VisibleEventInfoInteractor visibleEventInfoInteractor,
      SelectEventInteractor selectEventInteractor, ExitEventInteractor exitEventInteractor, BusPublisher busPublisher) {
        this.visibleEventInfoInteractor = visibleEventInfoInteractor;
        this.selectEventInteractor = selectEventInteractor;
        this.exitEventInteractor = exitEventInteractor;
        this.busPublisher = busPublisher;
    }

    public void setView(EventSelectionView eventSelectionView) {
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
                    showViewEventTimeline(visibleEventTag);
                } else {
                    showViewHall();
                }
            }
        });
    }

    private void showViewEventTimeline(String visibleEventTag) {
        eventSelectionView.showCurrentEventTitle(visibleEventTag);
        eventSelectionView.showExitButton();
    }

    private void showViewHall() {
        eventSelectionView.showHallTitle();
        eventSelectionView.hideExitButton();
    }

    public void selectEventClick() {
        eventSelectionView.openEventSelectionView();
    }

    public void exitEventClick() {
        exitEventInteractor.exitEvent(new ExitEventInteractor.Callback() {
            @Override public void onLoaded() {
                onEventChanged(null);
            }
        });
    }

    public void onEventSelected(Long eventId) {
        selectEventInteractor.selectEvent(eventId, new SelectEventInteractor.Callback() {
            @Override public void onLoaded(Long selectedEventId) {
                onEventChanged(selectedEventId);
            }
        });
    }

    private void onEventChanged(Long idEvent) {
        if (idEvent == null) {
            showViewHall();
        } else {
            loadCurrentEventTitle();
        }
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
