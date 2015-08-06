package com.shootr.android.ui.presenter;

import com.shootr.android.domain.StreamSearchResult;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.stream.AddToFavoritesInteractor;
import com.shootr.android.domain.interactor.stream.GetFavoriteStreamsInteractor;
import com.shootr.android.domain.interactor.stream.GetLocalFavoriteStreamsInteractor;
import com.shootr.android.domain.interactor.stream.GetUserListingStreamsInteractor;
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
    private final GetLocalFavoriteStreamsInteractor getLocalFavoriteStreamsInteractor;
    private final StreamResultModelMapper streamResultModelMapper;
    private final ErrorMessageFactory errorMessageFactory;

    private ListingView listingView;
    private String profileIdUser;
    private boolean hasBeenPaused = false;
    private List<StreamSearchResult> streamSearchResults;
    private List<StreamSearchResult> favoriteStreamSearchResults;

    @Inject public ListingListPresenter(GetUserListingStreamsInteractor getUserListingStreamsInteractor,
      AddToFavoritesInteractor addToFavoritesInteractor, RemoveFromFavoritesInteractor removeFromFavoritesInteractor,
      GetFavoriteStreamsInteractor getFavoriteStreamsInteractor,
      GetLocalFavoriteStreamsInteractor getLocalFavoriteStreamsInteractor, StreamResultModelMapper streamResultModelMapper,
      ErrorMessageFactory errorMessageFactory) {
        this.getUserListingStreamsInteractor = getUserListingStreamsInteractor;
        this.addToFavoritesInteractor = addToFavoritesInteractor;
        this.removeFromFavoritesInteractor = removeFromFavoritesInteractor;
        this.getFavoriteStreamsInteractor = getFavoriteStreamsInteractor;
        this.getLocalFavoriteStreamsInteractor = getLocalFavoriteStreamsInteractor;
        this.streamResultModelMapper = streamResultModelMapper;
        this.errorMessageFactory = errorMessageFactory;
    }

    public void setView(ListingView listingView) {
        this.listingView = listingView;
    }

    public void initialize(ListingView listingView, String profileIdUser) {
        this.setView(listingView);
        this.profileIdUser = profileIdUser;
        this.getFavoriteStreams();
        this.loadListingList();
    }

    private void loadListingList() {
        listingView.showLoading();
        getUserListingStreamsInteractor.loadUserListingStreams(new Interactor.Callback<List<StreamSearchResult>>() {
            @Override public void onLoaded(List<StreamSearchResult> streams) {
                listingView.hideLoading();
                streamSearchResults = streams;
                renderStreams();
            }
        }, profileIdUser);
    }

    private void resumeListingList() {
        getUserListingStreamsInteractor.loadUserListingStreams(new Interactor.Callback<List<StreamSearchResult>>() {
            @Override public void onLoaded(List<StreamSearchResult> streams) {
                streamSearchResults = streams;
                renderStreams();
            }
        }, profileIdUser);
    }

    private void renderStreams() {
        if (streamSearchResults != null && favoriteStreamSearchResults != null) {
            List<StreamResultModel> streamModels = streamResultModelMapper.transform(streamSearchResults);
            List<StreamResultModel> favoriteStreamModels = streamResultModelMapper.transform(favoriteStreamSearchResults);

            listingView.showContent();
            listingView.setFavoriteStreams(favoriteStreamModels, streamModels);
        }
    }

    @Override public void resume() {
        if (hasBeenPaused) {
            resumeListingList();
            resumeFavoriteStreams();
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

    public void addToFavorite(StreamResultModel streamResultModel) {
        addToFavoritesInteractor.addToFavorites(streamResultModel.getStreamModel().getIdStream(),
          new Interactor.CompletedCallback() {
              @Override public void onCompleted() {
                  /* no - op*/
              }
          },
          new Interactor.ErrorCallback() {
              @Override public void onError(ShootrException error) {
                  showErrorInView(error);
              }
          });
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

    public void removeFromFavorites(StreamResultModel stream) {
        removeFromFavoritesInteractor.removeFromFavorites(stream.getStreamModel().getIdStream(),
          new Interactor.CompletedCallback() {
              @Override public void onCompleted() {
                  /* no - op*/
              }
          });
    }

    public void getFavoriteStreams() {
        getFavoriteStreamsInteractor.loadFavoriteStreams(new Interactor.Callback<List<StreamSearchResult>>() {
            @Override public void onLoaded(List<StreamSearchResult> streamSearchResults) {
                favoriteStreamSearchResults = streamSearchResults;
                renderStreams();
            }
        });
    }

    public void resumeFavoriteStreams() {
        getLocalFavoriteStreamsInteractor.loadFavoriteStreams(new Interactor.Callback<List<StreamSearchResult>>() {
            @Override public void onLoaded(List<StreamSearchResult> streamSearchResults) {
                favoriteStreamSearchResults = streamSearchResults;
                renderStreams();
            }
        });
    }
}
