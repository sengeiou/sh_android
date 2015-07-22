package com.shootr.android.ui.presenter;

import com.shootr.android.domain.StreamSearchResult;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.event.GetUserListingStreamsInteractor;
import com.shootr.android.ui.model.EventResultModel;
import com.shootr.android.ui.model.mappers.EventResultModelMapper;
import com.shootr.android.ui.views.ListingView;
import java.util.List;
import javax.inject.Inject;

public class ListingListPresenter implements Presenter{

    private final GetUserListingStreamsInteractor getUserListingStreamsInteractor;
    private final EventResultModelMapper eventResultModelMapper;

    private ListingView listingView;
    private String profileIdUser;
    private boolean hasBeenPaused = false;

    @Inject public ListingListPresenter(GetUserListingStreamsInteractor getUserListingStreamsInteractor,
      EventResultModelMapper eventResultModelMapper) {
        this.getUserListingStreamsInteractor = getUserListingStreamsInteractor;
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
        getUserListingStreamsInteractor.loadUserListingStreams(new Interactor.Callback<List<StreamSearchResult>>() {
            @Override public void onLoaded(List<StreamSearchResult> events) {
                listingView.hideLoading();
                onListingLoaded(events);
            }
        }, profileIdUser);
    }

    private void onListingLoaded(List<StreamSearchResult> events) {
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
        if (hasBeenPaused) {
            loadListingList();
        }
    }

    @Override public void pause() {
        hasBeenPaused = true;
    }

    public void selectEvent(EventResultModel event) {
        selectEvent(event.getEventModel().getIdEvent(), event.getEventModel().getTitle());
    }

    private void selectEvent(final String idEvent, String eventTitle) {
        listingView.navigateToEventTimeline(idEvent, eventTitle);
    }
}
