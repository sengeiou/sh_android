package com.shootr.android.ui.presenter;

import com.shootr.android.domain.EventSearchResult;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.event.GetUserListingEventsInteractor;
import com.shootr.android.ui.model.EventResultModel;
import com.shootr.android.ui.model.mappers.EventResultModelMapper;
import com.shootr.android.ui.views.ListingView;
import java.util.List;
import javax.inject.Inject;

public class ListingListPresenter implements Presenter{

    private final GetUserListingEventsInteractor getUserListingEventsInteractor;
    private final EventResultModelMapper eventResultModelMapper;

    private ListingView listingView;
    private String profileIdUser;

    @Inject public ListingListPresenter(GetUserListingEventsInteractor getUserListingEventsInteractor,
      EventResultModelMapper eventResultModelMapper) {
        this.getUserListingEventsInteractor = getUserListingEventsInteractor;
        this.eventResultModelMapper = eventResultModelMapper;
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
        getUserListingEventsInteractor.loadUserListingEvents(new Interactor.Callback<List<EventSearchResult>>() {
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
        }else{
            listingView.showLoading();
        }
    }

    private void renderViewEventsList(List<EventResultModel> eventModels) {
        listingView.showContent();
        listingView.renderEvents(eventModels);
    }

    @Override public void resume() {
        /* no-op */
    }

    @Override public void pause() {
        /* no-op */
    }

    public void selectEvent(EventResultModel event) {
        selectEvent(event.getEventModel().getIdEvent(), event.getEventModel().getTitle());
    }

    private void selectEvent(final String idEvent, String eventTitle) {
        listingView.navigateToEventTimeline(idEvent, eventTitle);
    }
}
