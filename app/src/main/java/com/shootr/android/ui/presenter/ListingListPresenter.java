package com.shootr.android.ui.presenter;

import com.shootr.android.domain.EventSearchResult;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.event.GetUserListingEventsInteractor;
import com.shootr.android.domain.interactor.event.SelectEventInteractor;
import com.shootr.android.ui.model.EventModel;
import com.shootr.android.ui.model.EventResultModel;
import com.shootr.android.ui.model.mappers.EventModelMapper;
import com.shootr.android.ui.model.mappers.EventResultModelMapper;
import com.shootr.android.ui.views.ListingView;
import java.util.List;
import javax.inject.Inject;

public class ListingListPresenter implements Presenter{

    private final SelectEventInteractor selectEventInteractor;
    private final GetUserListingEventsInteractor getUserListingEventsInteractor;
    private final EventResultModelMapper eventResultModelMapper;
    private final EventModelMapper eventModelMapper;

    private ListingView listingView;
    private String profileIdUser;

    @Inject public ListingListPresenter(SelectEventInteractor selectEventInteractor,
      GetUserListingEventsInteractor getUserListingEventsInteractor, EventResultModelMapper eventResultModelMapper,
      EventModelMapper eventModelMapper) {
        this.selectEventInteractor = selectEventInteractor;
        this.getUserListingEventsInteractor = getUserListingEventsInteractor;
        this.eventResultModelMapper = eventResultModelMapper;
        this.eventModelMapper = eventModelMapper;
    }

    public void setView(ListingView listingView) {
        this.listingView = listingView;
    }

    public void initialize(ListingView listingView, String profileIdUser) {
        this.setView(listingView);
        this.profileIdUser = profileIdUser;
        this.loadListingList();
    }

    private void loadListingList() {
        listingView.showLoading();
        getUserListingEventsInteractor.getUserListingEvents(new Interactor.Callback<List<EventSearchResult>>() {
            @Override public void onLoaded(List<EventSearchResult> events) {
                listingView.hideLoading();
                onListingLoaded(events);
            }
        }, profileIdUser);
    }

    private void onListingLoaded(List<EventSearchResult> events) {
        if (!events.isEmpty()) {
            List<EventResultModel> eventModels = eventResultModelMapper.transform(events);
            this.renderViewEventsList(eventModels);
            listingView.hideLoading();
        }else{
            listingView.showLoading();
        }
    }

    private void renderViewEventsList(List<EventResultModel> eventModels) {
        listingView.showContent();
        listingView.renderEvents(eventModels);
    }

    @Override public void resume() {

    }

    @Override public void pause() {

    }

    public void selectEvent(EventModel event) {
        selectEvent(event.getIdEvent(), event.getTitle());
    }

    private void selectEvent(final String idEvent, String eventTitle) {
        selectEventInteractor.selectEvent(idEvent, new Interactor.Callback<EventSearchResult>() {
            @Override public void onLoaded(EventSearchResult selectedEvent) {
                onEventSelected(eventModelMapper.transform(selectedEvent.getEvent()));
            }
        });
    }

    private void onEventSelected(EventModel selectedEvent) {
        listingView.navigateToEventTimeline(selectedEvent.getIdEvent(), selectedEvent.getTitle());
    }

}
