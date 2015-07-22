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
            @Override public void onLoaded(List<StreamSearchResult> streams) {
                listingView.hideLoading();
                onListingLoaded(streams);
            }
        }, profileIdUser);
    }

    private void onListingLoaded(List<StreamSearchResult> streams) {
        if (!streams.isEmpty()) {
            List<StreamResultModel> streamModels = streamResultModelMapper.transform(streams);
            this.renderViewStreamList(streamModels);
        }else{
            listingView.showLoading();
        }
    }

    private void renderViewStreamList(List<StreamResultModel> streamModels) {
        listingView.showContent();
        listingView.renderStreams(streamModels);
    }

    @Override public void resume() {
        if (hasBeenPaused) {
            loadListingList();
        }
    }

    @Override public void pause() {
        hasBeenPaused = true;
    }

    public void selectStream(StreamResultModel stream) {
        selectStream(stream.getStreamModel().getIdStream(), stream.getStreamModel().getTitle());
    }

    private void selectStream(final String idStream, String streamTitle) {
        listingView.navigateToStreamTimeline(idStream, streamTitle);
    }
}
