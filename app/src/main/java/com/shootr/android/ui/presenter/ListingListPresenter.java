package com.shootr.android.ui.presenter;

import com.shootr.android.domain.StreamSearchResult;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.stream.AddToFavoritesInteractor;
import com.shootr.android.domain.interactor.stream.GetFavoriteStreamsInteractor;
import com.shootr.android.domain.interactor.stream.GetUserListingStreamsInteractor;
import com.shootr.android.domain.interactor.stream.RecommendStreamInteractor;
import com.shootr.android.domain.interactor.stream.RemoveFromFavoritesInteractor;
import com.shootr.android.domain.service.StreamIsAlreadyInFavoritesException;
import com.shootr.android.ui.model.StreamResultModel;
import com.shootr.android.ui.model.mappers.StreamResultModelMapper;
import com.shootr.android.ui.views.ListingView;
import com.shootr.android.util.ErrorMessageFactory;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class ListingListPresenter implements Presenter{

    private final GetUserListingStreamsInteractor getUserListingStreamsInteractor;
    private final AddToFavoritesInteractor addToFavoritesInteractor;
    private final RemoveFromFavoritesInteractor removeFromFavoritesInteractor;
    private final GetFavoriteStreamsInteractor getFavoriteStreamsInteractor;
    private final RecommendStreamInteractor recommendStreamInteractor;
    private final StreamResultModelMapper streamResultModelMapper;
    private final ErrorMessageFactory errorMessageFactory;

    private ListingView listingView;
    private String profileIdUser;
    private boolean hasBeenPaused = false;
    private List<StreamResultModel> listingStreams;
    private List<StreamResultModel> favoriteStreams;

    @Inject public ListingListPresenter(GetUserListingStreamsInteractor getUserListingStreamsInteractor,
      AddToFavoritesInteractor addToFavoritesInteractor, RemoveFromFavoritesInteractor removeFromFavoritesInteractor,
      GetFavoriteStreamsInteractor getFavoriteStreamsInteractor, RecommendStreamInteractor recommendStreamInteractor,
      StreamResultModelMapper streamResultModelMapper, ErrorMessageFactory errorMessageFactory) {
        this.getUserListingStreamsInteractor = getUserListingStreamsInteractor;
        this.addToFavoritesInteractor = addToFavoritesInteractor;
        this.removeFromFavoritesInteractor = removeFromFavoritesInteractor;
        this.getFavoriteStreamsInteractor = getFavoriteStreamsInteractor;
        this.recommendStreamInteractor = recommendStreamInteractor;
        this.streamResultModelMapper = streamResultModelMapper;
        this.errorMessageFactory = errorMessageFactory;
    }

    protected void setView(ListingView listingView) {
        this.listingView = listingView;
    }

    public void initialize(ListingView listingView, String profileIdUser) {
        this.setView(listingView);
        this.profileIdUser = profileIdUser;
        this.loadFavoriteStreams();
        this.startLoadingListing();
    }

    private void startLoadingListing() {
        listingView.showLoading();
        loadListing();
    }

    private void loadListing() {
        getUserListingStreamsInteractor.loadUserListingStreams(new Interactor.Callback<List<StreamSearchResult>>() {
            @Override public void onLoaded(List<StreamSearchResult> streams) {
                listingView.hideLoading();
                if (!streams.isEmpty()) {
                    listingStreams = streamResultModelMapper.transform(streams);
                    renderStreams();
                    listingView.hideEmpty();
                    listingView.showContent();
                } else {
                    listingView.showEmpty();
                    listingView.hideContent();
                }
            }
        }, profileIdUser);
    }

    public void loadFavoriteStreams() {
        getFavoriteStreamsInteractor.loadFavoriteStreamsFromLocalOnly(new Interactor.Callback<List<StreamSearchResult>>() {
            @Override public void onLoaded(List<StreamSearchResult> favorites) {
                favoriteStreams = streamResultModelMapper.transform(favorites);
                renderStreams();
            }
        });
    }

    private void renderStreams() {
        if (listingStreams != null && favoriteStreams != null) {
            listingView.renderStreams(listingStreams);
            listingView.setFavoriteStreams(favoriteStreams);
        }
    }

    public void addToFavorite(StreamResultModel streamResultModel) {
        addToFavoritesInteractor.addToFavorites(streamResultModel.getStreamModel().getIdStream(),
          new Interactor.CompletedCallback() {
              @Override
              public void onCompleted() {
                  loadFavoriteStreams();
              }
          },
          new Interactor.ErrorCallback() {
              @Override
              public void onError(ShootrException error) {
                  showErrorInView(error);
              }
          });
    }

    public void removeFromFavorites(StreamResultModel stream) {
        removeFromFavoritesInteractor.removeFromFavorites(stream.getStreamModel().getIdStream(),
          new Interactor.CompletedCallback() {
              @Override public void onCompleted() {
                  loadFavoriteStreams();
              }
          });
    }

    public void selectStream(StreamResultModel stream) {
        selectStream(stream.getStreamModel().getIdStream(), stream.getStreamModel().getTitle());
    }

    private void selectStream(final String idStream, String streamTitle) {
        listingView.navigateToStreamTimeline(idStream, streamTitle);
    }

    public void streamCreated(String streamId) {
        listingView.navigateToCreatedStreamDetail(streamId);
    }

    private void showErrorInView(ShootrException error) {
        String errorMessage;
        if(error instanceof StreamIsAlreadyInFavoritesException){
            errorMessage = errorMessageFactory.getStreamIsAlreadyInFavoritesError();
        }else if (error instanceof ServerCommunicationException) {
            errorMessage = errorMessageFactory.getCommunicationErrorMessage();
        }else{
            Timber.e(error, "Unhandled error logging in");
            errorMessage = errorMessageFactory.getUnknownErrorMessage();
        }
        listingView.showError(errorMessage);
    }

    @Override public void resume() {
        if (hasBeenPaused) {
            loadListing();
            loadFavoriteStreams();
        }
    }

    @Override public void pause() {
        hasBeenPaused = true;
    }

    public void recommendStream(StreamResultModel stream) {
        recommendStreamInteractor.recommendStream(stream.getStreamModel().getIdStream(),
          new Interactor.CompletedCallback() {
              @Override public void onCompleted() {
                  listingView.showStreamRecommended();
              }
          },
          new Interactor.ErrorCallback() {
              @Override public void onError(ShootrException error) {
                  showErrorInView(error);
              }
          });
    }
}
