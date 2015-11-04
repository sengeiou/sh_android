package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.Listing;
import com.shootr.mobile.domain.StreamSearchResult;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.stream.AddToFavoritesInteractor;
import com.shootr.mobile.domain.interactor.stream.GetFavoriteStreamsInteractor;
import com.shootr.mobile.domain.interactor.stream.GetUserListingStreamsInteractor;
import com.shootr.mobile.domain.interactor.stream.RemoveFromFavoritesInteractor;
import com.shootr.mobile.domain.interactor.stream.RemoveStreamInteractor;
import com.shootr.mobile.domain.interactor.stream.ShareStreamInteractor;
import com.shootr.mobile.ui.model.StreamResultModel;
import com.shootr.mobile.ui.model.mappers.StreamResultModelMapper;
import com.shootr.mobile.ui.views.ListingView;
import com.shootr.mobile.util.ErrorMessageFactory;
import java.util.List;
import javax.inject.Inject;

public class ListingListPresenter implements Presenter{

    private final GetUserListingStreamsInteractor getUserListingStreamsInteractor;
    private final AddToFavoritesInteractor addToFavoritesInteractor;
    private final RemoveFromFavoritesInteractor removeFromFavoritesInteractor;
    private final GetFavoriteStreamsInteractor getFavoriteStreamsInteractor;
    private final ShareStreamInteractor shareStreamInteractor;
    private final RemoveStreamInteractor removeStreamInteractor;
    private final StreamResultModelMapper streamResultModelMapper;
    private final ErrorMessageFactory errorMessageFactory;

    private ListingView listingView;
    private String profileIdUser;
    private boolean hasBeenPaused = false;
    private List<StreamResultModel> listingStreams;
    private List<StreamResultModel> listingUserFavoritedStreams;
    private List<StreamResultModel> favoriteStreams;
    private boolean isCurrentUser;
    private String idStreamToRemove;

    @Inject public ListingListPresenter(GetUserListingStreamsInteractor getUserListingStreamsInteractor,
      AddToFavoritesInteractor addToFavoritesInteractor, RemoveFromFavoritesInteractor removeFromFavoritesInteractor,
      GetFavoriteStreamsInteractor getFavoriteStreamsInteractor, ShareStreamInteractor shareStreamInteractor,
      RemoveStreamInteractor removeStreamInteractor, StreamResultModelMapper streamResultModelMapper,
      ErrorMessageFactory errorMessageFactory) {
        this.getUserListingStreamsInteractor = getUserListingStreamsInteractor;
        this.addToFavoritesInteractor = addToFavoritesInteractor;
        this.removeFromFavoritesInteractor = removeFromFavoritesInteractor;
        this.getFavoriteStreamsInteractor = getFavoriteStreamsInteractor;
        this.shareStreamInteractor = shareStreamInteractor;
        this.removeStreamInteractor = removeStreamInteractor;
        this.streamResultModelMapper = streamResultModelMapper;
        this.errorMessageFactory = errorMessageFactory;
    }

    protected void setView(ListingView listingView) {
        this.listingView = listingView;
    }

    public void initialize(ListingView listingView, String profileIdUser, Boolean isCurrentUser) {
        this.setView(listingView);
        this.profileIdUser = profileIdUser;
        this.isCurrentUser = isCurrentUser;
        this.loadAddStreamVisibility();
        this.renderListing();
    }

    private void loadAddStreamVisibility() {
        if (isCurrentUser) {
            listingView.showAddStream();
        } else {
            listingView.hideAddStream();
        }
    }

    private void renderListing() {
        this.loadFavoriteStreams();
        this.startLoadingListing();
    }

    private void startLoadingListing() {
        listingView.showLoading();
        loadListing();
    }

    private void loadListing() {
        loadUserListingStreams();
    }

    private void loadUserListingStreams() {
        getUserListingStreamsInteractor.loadUserListingStreams(new Interactor.Callback<Listing>() {
            @Override public void onLoaded(Listing listing) {
                handleStreamsInView(listing);
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                showErrorInView(error);
            }
        }, profileIdUser);
    }

    private void handleStreamsInView(Listing listing) {
        listingView.hideLoading();
        if (listing.getHoldingStreams().isEmpty() && listing.getFavoritedStreams().isEmpty()) {
            listingView.showEmpty();
            listingView.hideContent();
        } else {
            listingStreams = streamResultModelMapper.transform(listing.getHoldingStreams());
            listingUserFavoritedStreams = streamResultModelMapper.transform(listing.getFavoritedStreams());
            renderStreams();
            listingView.hideEmpty();
            listingView.showContent();
            listingView.showSectionTitles();
        }
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
        if (listingStreams != null) {
            listingView.renderHoldingStreams(listingStreams);
        }
        if (listingUserFavoritedStreams != null) {
            listingView.renderFavoritedStreams(listingUserFavoritedStreams);
        }
        if (favoriteStreams != null) {
            listingView.setCurrentUserFavorites(favoriteStreams);
        }
        listingView.updateStreams();
    }

    public void addToFavorite(StreamResultModel streamResultModel) {
        addToFavoritesInteractor.addToFavorites(streamResultModel.getStreamModel().getIdStream(),
          new Interactor.CompletedCallback() {
              @Override public void onCompleted() {
                  if (isCurrentUser) {
                      loadUserListingStreams();
                  }
                  loadFavoriteStreams();
              }
          },
          new Interactor.ErrorCallback() {
              @Override public void onError(ShootrException error) {
                  showErrorInView(error);
              }
          });
    }

    public void removeFromFavorites(StreamResultModel stream) {
        removeFromFavoritesInteractor.removeFromFavorites(stream.getStreamModel().getIdStream(),
          new Interactor.CompletedCallback() {
              @Override public void onCompleted() {
                  if (isCurrentUser) {
                      loadUserListingStreams();
                  }
                  loadFavoriteStreams();
              }
          });
    }

    public void selectStream(StreamResultModel stream) {
        selectStream(stream.getStreamModel().getIdStream(), stream.getStreamModel().getShortTitle(), stream.getStreamModel().getAuthorId());
    }

    private void selectStream(final String idStream, String treamTag, String authorId) {
        listingView.navigateToStreamTimeline(idStream, treamTag, authorId);
    }

    public void streamCreated(String streamId) {
        listingView.navigateToCreatedStreamDetail(streamId);
    }

    private void showErrorInView(ShootrException error) {
        listingView.showError(errorMessageFactory.getMessageForError(error));
    }

    public void shareStream(StreamResultModel stream) {
        shareStreamInteractor.shareStream(stream.getStreamModel().getIdStream(), new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
                listingView.showStreamShared();
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                showErrorInView(error);
            }
        });
    }

    public void openContextualMenu(StreamResultModel stream) {
        if (isCurrentUser && stream.getStreamModel().getAuthorId().equals(profileIdUser) && !stream.getStreamModel().isRemoved()) {
            listingView.showCurrentUserContextMenu(stream);
        } else {
            listingView.showContextMenu(stream);
        }
    }

    public void remove(String idStream) {
        this.idStreamToRemove = idStream;
        listingView.askRemoveStreamConfirmation();
    }

    public void removeStream() {
        if (idStreamToRemove != null) {
            removeStreamInteractor.removeStream(idStreamToRemove, new Interactor.CompletedCallback() {
                @Override public void onCompleted() {
                    loadListing();
                    loadFavoriteStreams();
                }
            }, new Interactor.ErrorCallback() {
                @Override public void onError(ShootrException error) {
                    showErrorInView(error);
                }
            });
        }
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
}