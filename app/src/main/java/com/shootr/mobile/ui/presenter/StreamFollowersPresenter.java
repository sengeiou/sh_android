package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.GetStreamFollowersListInteractor;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.stream.AddToFavoritesInteractor;
import com.shootr.mobile.domain.interactor.stream.RemoveFromFavoritesInteractor;
import com.shootr.mobile.domain.interactor.stream.SelectStreamInteractor;
import com.shootr.mobile.domain.interactor.user.FollowInteractor;
import com.shootr.mobile.domain.interactor.user.UnfollowInteractor;
import com.shootr.mobile.domain.model.Follows;
import com.shootr.mobile.domain.model.stream.StreamSearchResult;
import com.shootr.mobile.ui.model.StreamModel;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.model.mappers.FollowModelMapper;
import com.shootr.mobile.ui.views.FollowView;
import com.shootr.mobile.util.ErrorMessageFactory;
import java.util.Date;
import javax.inject.Inject;

public class StreamFollowersPresenter implements Presenter {

  private final AddToFavoritesInteractor addToFavoritesInteractor;
  private final RemoveFromFavoritesInteractor removeFromFavoritesInteractor;
  private final FollowInteractor followInteractor;
  private final UnfollowInteractor unfollowInteractor;
  private final SelectStreamInteractor selectStreamInteractor;
  private final GetStreamFollowersListInteractor getStreamFollowersListInteractor;
  private final FollowModelMapper followingModelMapper;
  private final ErrorMessageFactory errorMessageFactory;

  private FollowView followView;
  private String idStream;
  private long maxTimestamp;
  private boolean isLoadingItems;
  private boolean mightHaveMoreItems;

  @Inject public StreamFollowersPresenter(AddToFavoritesInteractor addToFavoritesInteractor,
      RemoveFromFavoritesInteractor removeFromFavoritesInteractor,
      FollowInteractor followInteractor, UnfollowInteractor unfollowInteractor,
      SelectStreamInteractor selectStreamInteractor,
      GetStreamFollowersListInteractor getStreamFollowersListInteractor, FollowModelMapper followingModelMapper,
      ErrorMessageFactory errorMessageFactory) {
    this.addToFavoritesInteractor = addToFavoritesInteractor;
    this.removeFromFavoritesInteractor = removeFromFavoritesInteractor;
    this.followInteractor = followInteractor;
    this.unfollowInteractor = unfollowInteractor;
    this.selectStreamInteractor = selectStreamInteractor;
    this.getStreamFollowersListInteractor = getStreamFollowersListInteractor;
    this.followingModelMapper = followingModelMapper;
    this.errorMessageFactory = errorMessageFactory;
  }

  public void initialize(FollowView followView, String idStream) {
    this.followView = followView;
    this.idStream = idStream;
    maxTimestamp = new Date().getTime();
    loadFollows(true);
  }

  private void loadFollows(boolean firstLoad) {
    isLoadingItems = true;
    loadFollowers(firstLoad);
  }

  private void showError(ShootrException error) {
    followView.showError(error.getMessage());
    isLoadingItems = false;
  }

  private void loadFollowers(final boolean firstLoad) {
    getStreamFollowersListInteractor.getFollowerList(idStream, maxTimestamp, new Interactor.Callback<Follows>() {
      @Override public void onLoaded(Follows follows) {
        renderItems(follows, firstLoad);
      }
    }, new Interactor.ErrorCallback() {
      @Override public void onError(ShootrException error) {
        showError(error);
      }
    });
  }

  private void renderItems(Follows follows, boolean firstLoad) {
    isLoadingItems = false;
    maxTimestamp = follows.getMaxTimestamp();
    if (firstLoad) {
      if (!follows.getData().isEmpty()) {
        followView.renderItems(followingModelMapper.transform(follows));
      } else {
        followView.showNoFollowing();
      }
    } else {
      followView.renderMoreItems(followingModelMapper.transform(follows));
    }
    mightHaveMoreItems = maxTimestamp != 0L;
  }

  public void addToFavorites(final StreamModel streamModel) {
    addToFavoritesInteractor.addToFavorites(streamModel.getIdStream(),
        new Interactor.CompletedCallback() {
          @Override public void onCompleted() {
            /* no-op */
          }
        }, new Interactor.ErrorCallback() {
          @Override public void onError(ShootrException error) {
            followView.showError(errorMessageFactory.getMessageForError(error));
          }
        });
  }

  public void removeFromFavorites(final StreamModel streamModel) {
    removeFromFavoritesInteractor.removeFromFavorites(streamModel.getIdStream(),
        new Interactor.CompletedCallback() {
          @Override public void onCompleted() {
            /* no-op */
          }
        });
  }

  public void followUser(final UserModel userModel) {
    followInteractor.follow(userModel.getIdUser(), new Interactor.CompletedCallback() {
      @Override public void onCompleted() {
        followView.showFollow(userModel);
      }
    }, new Interactor.ErrorCallback() {
      @Override public void onError(ShootrException error) {
        followView.showError(errorMessageFactory.getMessageForError(error));
      }
    });
  }

  public void unfollowUser(final UserModel userModel) {
    unfollowInteractor.unfollow(userModel.getIdUser(), new Interactor.CompletedCallback() {
      @Override public void onCompleted() {
        followView.showUnfollow(userModel);
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

  public void showingLastShot() {
    if (!isLoadingItems && mightHaveMoreItems) {
      loadFollows(false);
    }
  }
}
