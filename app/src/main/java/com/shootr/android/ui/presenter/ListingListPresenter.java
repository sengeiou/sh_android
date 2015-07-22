package com.shootr.android.ui.presenter;

import com.shootr.android.domain.StreamSearchResult;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.event.GetUserListingStreamsInteractor;
import com.shootr.android.ui.model.StreamResultModel;
import com.shootr.android.ui.model.mappers.StreamResultModelMapper;
import com.shootr.android.ui.views.ListingView;
import java.util.List;
import javax.inject.Inject;

public class ListingListPresenter implements Presenter{

    private final GetUserListingStreamsInteractor getUserListingStreamsInteractor;
    private final StreamResultModelMapper streamResultModelMapper;

    private ListingView listingView;
    private String profileIdUser;
    private boolean hasBeenPaused = false;

    @Inject public ListingListPresenter(GetUserListingStreamsInteractor getUserListingStreamsInteractor,
      StreamResultModelMapper streamResultModelMapper) {
        this.getUserListingStreamsInteractor = getUserListingStreamsInteractor;
        this.streamResultModelMapper = streamResultModelMapper;
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
            List<StreamResultModel> eventModels = streamResultModelMapper.transform(events);
            this.renderViewEventsList(eventModels);
        }else{
            listingView.showLoading();
        }
    }

    private void renderViewEventsList(List<StreamResultModel> eventModels) {
        listingView.showContent();
        listingView.renderStreams(eventModels);
    }

    @Override public void resume() {
        if (hasBeenPaused) {
            loadListingList();
        }
    }

    @Override public void pause() {
        hasBeenPaused = true;
    }

    public void selectEvent(StreamResultModel event) {
        selectEvent(event.getStreamModel().getIdStream(), event.getStreamModel().getTitle());
    }

    private void selectEvent(final String idEvent, String eventTitle) {
        listingView.navigateToStreamTimeline(idEvent, eventTitle);
    }
}
