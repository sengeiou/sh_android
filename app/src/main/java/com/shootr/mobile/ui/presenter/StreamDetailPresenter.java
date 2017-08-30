package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.data.entity.FollowEntity;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.stream.AddToFavoritesInteractor;
import com.shootr.mobile.domain.interactor.stream.GetFavoriteStatusInteractor;
import com.shootr.mobile.domain.interactor.stream.GetStreamInfoInteractor;
import com.shootr.mobile.domain.interactor.stream.MuteInteractor;
import com.shootr.mobile.domain.interactor.stream.RemoveFromFavoritesInteractor;
import com.shootr.mobile.domain.interactor.stream.RemoveStreamInteractor;
import com.shootr.mobile.domain.interactor.stream.RestoreStreamInteractor;
import com.shootr.mobile.domain.interactor.stream.SelectStreamInteractor;
import com.shootr.mobile.domain.interactor.stream.ShareStreamInteractor;
import com.shootr.mobile.domain.interactor.stream.UnmuteInteractor;
import com.shootr.mobile.domain.interactor.user.FollowInteractor;
import com.shootr.mobile.domain.interactor.user.UnfollowInteractor;
import com.shootr.mobile.domain.model.stream.Stream;
import com.shootr.mobile.domain.model.stream.StreamInfo;
import com.shootr.mobile.domain.model.stream.StreamSearchResult;
import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.ui.model.StreamModel;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.model.mappers.StreamModelMapper;
import com.shootr.mobile.ui.model.mappers.UserModelMapper;
import com.shootr.mobile.ui.views.StreamDetailView;
import com.shootr.mobile.util.ErrorMessageFactory;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;

import static com.shootr.mobile.domain.utils.Preconditions.checkNotNull;

public class StreamDetailPresenter implements Presenter {

  //region Dependencies
  private final GetStreamInfoInteractor streamInfoInteractor;
  private final ShareStreamInteractor shareStreamInteractor;
  private final FollowInteractor followInteractor;
  private final UnfollowInteractor unfollowInteractor;
  private final GetFavoriteStatusInteractor getFavoriteStatusInteractor;
  private final SelectStreamInteractor selectStreamInteractor;
  private final MuteInteractor muteInteractor;
  private final UnmuteInteractor unmuteInteractor;
  private final RemoveStreamInteractor removeStreamInteractor;
  private final RestoreStreamInteractor restoreStreamInteractor;
  private final AddToFavoritesInteractor addToFavoritesInteractor;
  private final RemoveFromFavoritesInteractor removeFromFavoritesInteractor;

  private final StreamModelMapper streamModelMapper;
  private final UserModelMapper userModelMapper;
  private final ErrorMessageFactory errorMessageFactory;

  private StreamDetailView streamDetailView;
  private String idStream;

  private StreamModel streamModel;

  private Integer streamMediaCount;
  private List<UserModel> participantsShown = Collections.emptyList();
  private boolean hasBeenPaused = false;
  private Integer totalWatchers;

  @Inject public StreamDetailPresenter(GetStreamInfoInteractor streamInfoInteractor,
      ShareStreamInteractor shareStreamInteractor, FollowInteractor followInteractor,
      UnfollowInteractor unfollowInteractor,
      GetFavoriteStatusInteractor getFavoriteStatusInteractor,
      SelectStreamInteractor selectStreamInteractor,
      MuteInteractor muteInteractor,
      UnmuteInteractor unmuteInteractor, RemoveStreamInteractor removeStreamInteractor,
      RestoreStreamInteractor restoreStreamInteractor,
      AddToFavoritesInteractor addToFavoritesInteractor,
      RemoveFromFavoritesInteractor removeFromFavoritesInteractor,
      StreamModelMapper streamModelMapper, UserModelMapper userModelMapper,
      ErrorMessageFactory errorMessageFactory) {
    this.streamInfoInteractor = streamInfoInteractor;
    this.shareStreamInteractor = shareStreamInteractor;
    this.followInteractor = followInteractor;
    this.unfollowInteractor = unfollowInteractor;
    this.getFavoriteStatusInteractor = getFavoriteStatusInteractor;
    this.selectStreamInteractor = selectStreamInteractor;
    this.muteInteractor = muteInteractor;
    this.unmuteInteractor = unmuteInteractor;
    this.removeStreamInteractor = removeStreamInteractor;
    this.restoreStreamInteractor = restoreStreamInteractor;
    this.addToFavoritesInteractor = addToFavoritesInteractor;
    this.removeFromFavoritesInteractor = removeFromFavoritesInteractor;
    this.streamModelMapper = streamModelMapper;
    this.userModelMapper = userModelMapper;
    this.errorMessageFactory = errorMessageFactory;
  }
  //endregion

  protected void setView(StreamDetailView streamDetailView) {
    this.streamDetailView = streamDetailView;
  }

  public void initialize(final StreamDetailView streamDetailView, final String idStream) {
    setView(streamDetailView);
    this.idStream = idStream;
    this.loadFavoriteStatus();
    this.loadStreamInfo();
  }

  public void loadFavoriteStatus() {
    getFavoriteStatusInteractor.loadFavoriteStatus(idStream, new Interactor.Callback<Boolean>() {
      @Override public void onLoaded(Boolean isFollowing) {
        streamDetailView.setFollowingStream(isFollowing);
      }
    });
  }

  public void loadMutedStatus() {
    streamDetailView.setMuteStatus(streamModel.isMuted());
  }

  public void addStreamAsFavorite() {
    addToFavoritesInteractor.addToFavorites(idStream, new Interactor.CompletedCallback() {
      @Override public void onCompleted() {
        loadFavoriteStatus();
      }
    }, new Interactor.ErrorCallback() {
      @Override public void onError(ShootrException error) {

      }
    });
  }

  public void removeStreamFromFavorites() {
    removeFromFavoritesInteractor.removeFromFavorites(idStream, new Interactor.CompletedCallback() {
      @Override public void onCompleted() {
        loadFavoriteStatus();
      }
    });
  }

  public void editStreamInfo() {
    streamDetailView.navigateToEditStream(idStream);
  }

  public void resultFromEditStreamInfo() {
    loadStreamInfo();
  }

  private void showEditPicturePlaceholderIfEmpty() {
    if (streamModel.getPicture() == null) {
      streamDetailView.showEditPicturePlaceholder();
    }
  }
  //endregion

  //region Stream info
  public void loadStreamInfo() {
    this.showViewLoading();
    this.getStreamInfo();
  }

  public void getContributorsNumber() {
    Long contributorsCount = streamModel.getContributorCount();
    if (contributorsCount > 0) {
      streamDetailView.showContributorsNumber(contributorsCount.intValue(),
          streamModel.amIAuthor());
    } else {
      streamDetailView.hideContributorsNumber(streamModel.amIAuthor());
    }
  }

  public void getStreamInfo() {
    streamInfoInteractor.obtainStreamInfo(idStream, new GetStreamInfoInteractor.Callback() {
      @Override public void onLoaded(StreamInfo streamInfo) {
        onStreamInfoLoaded(streamInfo);
      }
    }, new Interactor.ErrorCallback() {
      @Override public void onError(ShootrException error) {
        String errorMessage = errorMessageFactory.getMessageForError(error);
        streamDetailView.showError(errorMessage);
      }
    });
  }

  public void onStreamInfoLoaded(StreamInfo streamInfo) {
    checkNotNull(streamInfo.getStream(),
        "Received null stream from StreamInfoInteractor. That should never happen >_<");
    this.renderStreamInfo(streamInfo.getStream());
    this.renderWatchersList(streamInfo);
    this.renderFollowingNumber(streamInfo.getNumberOfFollowing());
    this.getContributorsNumber();
    renderStreamFollowers(streamInfo.getStream().getTotalFavorites());
    if (streamModel.amIAuthor()) {
      this.setupRemoveStreamMenuOption();
    }
    this.showViewDetail();
    this.hideViewLoading();
    this.loadMutedStatus();
  }

  private void setupRemoveStreamMenuOption() {
    if (streamModel.isRemoved()) {
      streamDetailView.showRestoreStreamButton();
      streamDetailView.hideRemoveButton();
      streamDetailView.showRemovedFeedback();
    } else {
      streamDetailView.hideRestoreButton();
      streamDetailView.showRemoveStreamButton();
    }
  }

  private void renderStreamFollowers(int streamFollowers) {
    streamDetailView.showStreamFollower(streamFollowers);
  }

  private void showViewDetail() {
    streamDetailView.showDetail();
  }
  //endregion

  public void clickAuthor() {
    streamDetailView.navigateToUser(streamModel.getAuthorId());
  }
  //endregion

  public void photoClick() {
    zoomPhoto();
  }

  private void zoomPhoto() {
    if (streamModel.getPicture() != null) {
      streamDetailView.zoomPhoto(streamModel.getPicture());
    }
  }

  //region renders
  private void renderWatchersList(StreamInfo streamInfo) {
    List<User> watchers = streamInfo.getWatchers();
    participantsShown = userModelMapper.transform(watchers);
    streamDetailView.setWatchers(participantsShown);
    this.totalWatchers = streamModel.getTotalWatchers();
    if (streamInfo.hasMoreParticipants()) {
      streamDetailView.showAllParticipantsButton();
    }
  }

  private void renderStreamInfo(Stream stream) {
    streamModel = streamModelMapper.transform(stream);
    streamDetailView.setStreamTitle(streamModel.getTitle());
    setupStreamPicture();
    streamDetailView.setStreamAuthor(streamModel.getAuthorUsername());
    streamDetailView.setStream(streamModel);
    if (streamModel.getDescription() != null && !streamModel.getDescription().isEmpty()) {
      streamDetailView.setStreamDescription(streamModel.getDescription());
    } else {
      streamDetailView.hideStreamDescription();
    }
    if (streamModel.amIAuthor()) {
      streamDetailView.showEditStreamButton();
      showEditPicturePlaceholderIfEmpty();
    }

    streamMediaCount = streamModel.getMediaCount();
  }

  private void setupStreamPicture() {
    if (streamModel.getPicture() != null) {
      streamDetailView.showPicture();
      streamDetailView.hideNoTextPicture();
      streamDetailView.setStreamPicture(streamModel.getPicture());
      streamDetailView.loadBlurStreamPicture(streamModel.getPicture());
    } else {
      if (!streamModel.amIAuthor()) {
        streamDetailView.hidePicture();
        streamDetailView.showNoTextPicture();
        streamDetailView.setupStreamInitials(streamModel);
      } else {
        streamDetailView.showPicture();
        streamDetailView.hideNoTextPicture();
      }
    }
  }

  private void renderFollowingNumber(Integer numberOfFollowing) {
    streamDetailView.setFollowingNumber(numberOfFollowing, totalWatchers);
  }
  //endregion

  //region View methods
  private void showViewLoading() {
    streamDetailView.showLoading();
  }

  private void hideViewLoading() {
    streamDetailView.hideLoading();
  }
  //endregion

  public void clickMedia() {
    streamDetailView.navigateToMedia(idStream, streamMediaCount);
  }

  public void follow(final String idUser) {
    followInteractor.follow(idUser, new Interactor.CompletedCallback() {
      @Override public void onCompleted() {
        refreshParticipantsFollowings(idUser, FollowEntity.RELATIONSHIP_FOLLOWING);
      }
    }, new Interactor.ErrorCallback() {
      @Override public void onError(ShootrException error) {
        showErrorInView(error);
      }
    });
  }

  private void showErrorInView(ShootrException error) {
    streamDetailView.showError(errorMessageFactory.getMessageForError(error));
  }

  public void unfollow(final String idUser) {
    unfollowInteractor.unfollow(idUser, new Interactor.CompletedCallback() {
      @Override public void onCompleted() {
        refreshParticipantsFollowings(idUser, FollowEntity.RELATIONSHIP_NONE);
      }
    });
  }

  private void refreshParticipantsFollowings(String idUser, int relationshipFollowing) {
    for (UserModel participant : participantsShown) {
      if (participant.getIdUser().equals(idUser)) {
        participant.setRelationship(relationshipFollowing);
        streamDetailView.setWatchers(participantsShown);
        break;
      }
    }
  }

  public void shareStreamViaShootr() {
    shareStreamInteractor.shareStream(streamModel.getIdStream(),
        new Interactor.CompletedCallback() {
          @Override public void onCompleted() {
            streamDetailView.showStreamShared();
          }
        }, new Interactor.ErrorCallback() {
          @Override public void onError(ShootrException error) {
            String errorMessage = errorMessageFactory.getMessageForError(error);
            streamDetailView.showError(errorMessage);
          }
        });
  }

  public void shareStreamVia() {
    streamDetailView.shareStreamVia(streamModel);
  }

  public void clickAllParticipants() {
    streamDetailView.goToAllParticipants(idStream);
  }

  public void selectStream() {
    if (streamModel != null) {
      selectStreamInteractor.selectStream(streamModel.getIdStream(),
          new Interactor.Callback<StreamSearchResult>() {
            @Override public void onLoaded(StreamSearchResult streamSearchResult) {
                /* no-op */
            }
          }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
              showErrorInView(error);
            }
          });
    }
  }

  public void onMuteChecked() {
    muteInteractor.mute(idStream, new Interactor.CompletedCallback() {
      @Override public void onCompleted() {
                /* no-op */
      }
    });
  }

  public void onUnmuteChecked() {
    unmuteInteractor.unmute(idStream, new Interactor.CompletedCallback() {
      @Override public void onCompleted() {
                /* no-op */
      }
    });
  }

  public void dataInfoClicked() {
    if (streamModel != null) {
      streamDetailView.goToStreamDataInfo(streamModel);
    }
  }

  public void contributorsClicked() {
    if (streamModel.amIAuthor()) {
      streamDetailView.goToContributorsActivityAsHolder(streamModel.getIdStream());
    } else {
      streamDetailView.goToContributorsActivity(streamModel.getIdStream());
    }
  }

  public void removeStream() {
    streamDetailView.askRemoveStreamConfirmation();
  }

  public void restoreStream() {
    restoreStreamInteractor.restoreStream(streamModel.getIdStream(),
        new Interactor.CompletedCallback() {
          @Override public void onCompleted() {
            streamDetailView.hideRestoreButton();
            streamDetailView.showRemoveStreamButton();
            streamDetailView.showRestoreStreamFeedback();
          }
        }, new Interactor.ErrorCallback() {
          @Override public void onError(ShootrException error) {
            String errorMessage = errorMessageFactory.getMessageForError(error);
            streamDetailView.showError(errorMessage);
          }
        });
  }

  public void confirmRemoveStream() {
    removeStreamInteractor.removeStream(streamModel.getIdStream(),
        new Interactor.CompletedCallback() {
          @Override public void onCompleted() {
            streamDetailView.hideRemoveButton();
            streamDetailView.showRestoreStreamButton();
            streamDetailView.showRemovedFeedback();
          }
        }, new Interactor.ErrorCallback() {
          @Override public void onError(ShootrException error) {
            String errorMessage = errorMessageFactory.getMessageForError(error);
            streamDetailView.showError(errorMessage);
          }
        });
  }

  @Override public void resume() {
    if (hasBeenPaused) {
      selectStream();
      getStreamInfo();
    }
  }

  @Override public void pause() {
    hasBeenPaused = true;
  }

  protected void setStreamModel(StreamModel streamModel) {
    this.streamModel = streamModel;
  }
}
