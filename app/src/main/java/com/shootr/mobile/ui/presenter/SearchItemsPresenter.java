package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.stream.AddToFavoritesInteractor;
import com.shootr.mobile.domain.interactor.stream.RemoveFromFavoritesInteractor;
import com.shootr.mobile.domain.interactor.stream.SelectStreamInteractor;
import com.shootr.mobile.domain.interactor.stream.ShareStreamInteractor;
import com.shootr.mobile.domain.interactor.user.FollowInteractor;
import com.shootr.mobile.domain.interactor.user.UnfollowInteractor;
import com.shootr.mobile.domain.model.stream.StreamSearchResult;
import com.shootr.mobile.ui.model.StreamModel;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.views.SearchStreamView;
import com.shootr.mobile.ui.views.SearchUserView;
import com.shootr.mobile.util.ErrorMessageFactory;
import javax.inject.Inject;

public class SearchItemsPresenter implements Presenter {

  private final AddToFavoritesInteractor addToFavoritesInteractor;
  private final RemoveFromFavoritesInteractor removeFromFavoritesInteractor;
  private final ShareStreamInteractor shareStreamInteractor;
  private final FollowInteractor followInteractor;
  private final UnfollowInteractor unfollowInteractor;
  private final SelectStreamInteractor selectStreamInteractor;
  private final ErrorMessageFactory errorMessageFactory;

  private SearchStreamView searchStreamView;
  private SearchUserView searchUserView;

  @Inject public SearchItemsPresenter(AddToFavoritesInteractor addToFavoritesInteractor,
      RemoveFromFavoritesInteractor removeFromFavoritesInteractor,
      ShareStreamInteractor shareStreamInteractor, FollowInteractor followInteractor,
      UnfollowInteractor unfollowInteractor, SelectStreamInteractor selectStreamInteractor,
      ErrorMessageFactory errorMessageFactory) {
    this.addToFavoritesInteractor = addToFavoritesInteractor;
    this.removeFromFavoritesInteractor = removeFromFavoritesInteractor;
    this.shareStreamInteractor = shareStreamInteractor;
    this.followInteractor = followInteractor;
    this.unfollowInteractor = unfollowInteractor;
    this.selectStreamInteractor = selectStreamInteractor;
    this.errorMessageFactory = errorMessageFactory;
  }

  public void initialize(SearchUserView searchUserView, SearchStreamView searchStreamView) {
    this.searchStreamView = searchStreamView;
    this.searchUserView = searchUserView;
  }

  public void addToFavorites(final StreamModel streamModel) {
    addToFavoritesInteractor.addToFavorites(streamModel.getIdStream(),
        new Interactor.CompletedCallback() {
          @Override public void onCompleted() {
            searchStreamView.showAddedToFavorites(streamModel);
          }
        }, new Interactor.ErrorCallback() {
          @Override public void onError(ShootrException error) {
            searchStreamView.showError(errorMessageFactory.getMessageForError(error));
          }
        });
  }

  public void removeFromFavorites(final StreamModel streamModel) {
    removeFromFavoritesInteractor.removeFromFavorites(streamModel.getIdStream(),
        new Interactor.CompletedCallback() {
          @Override public void onCompleted() {
            searchStreamView.showRemovedFromFavorites(streamModel);
          }
        });
  }

  public void shareStream(StreamModel streamModel) {
    shareStreamInteractor.shareStream(streamModel.getIdStream(),
        new Interactor.CompletedCallback() {
          @Override public void onCompleted() {
            searchStreamView.showStreamShared();
          }
        }, new Interactor.ErrorCallback() {
          @Override public void onError(ShootrException error) {
            searchStreamView.showError(errorMessageFactory.getMessageForError(error));
          }
        });
  }

  public void followUser(final UserModel userModel) {
    followInteractor.follow(userModel.getIdUser(), new Interactor.CompletedCallback() {
      @Override public void onCompleted() {
        searchUserView.showFollow(userModel);
      }
    }, new Interactor.ErrorCallback() {
      @Override public void onError(ShootrException error) {
        searchUserView.showError(errorMessageFactory.getMessageForError(error));
      }
    });
  }

  public void unfollowUser(final UserModel userModel) {
    unfollowInteractor.unfollow(userModel.getIdUser(), new Interactor.CompletedCallback() {
      @Override public void onCompleted() {
        searchUserView.showUnfollow(userModel);
      }
    });
  }

  @Override public void resume() {
    /* no-op */
  }

  @Override public void pause() {
    /* no-op */
  }

  public void selectStream(StreamModel stream) {
    selectStreamInteractor.selectStream(stream.getIdStream(),
        new Interactor.Callback<StreamSearchResult>() {
          @Override public void onLoaded(StreamSearchResult streamSearchResult) {
              /* no-op */
          }
        }, new Interactor.ErrorCallback() {
          @Override public void onError(ShootrException error) {
              /* no-op */
          }
        });
  }

  public void openContextualMenu(StreamModel streamModel) {
    if (streamModel.isFavorite()) {
      searchStreamView.openContextualMenuWithUnmarkFavorite(streamModel);
    } else {
      searchStreamView.openContextualMenuWithAddFavorite(streamModel);
    }
  }
}