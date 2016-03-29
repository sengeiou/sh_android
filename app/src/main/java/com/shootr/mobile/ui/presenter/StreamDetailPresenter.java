package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.data.entity.FollowEntity;
import com.shootr.mobile.domain.Stream;
import com.shootr.mobile.domain.StreamInfo;
import com.shootr.mobile.domain.StreamSearchResult;
import com.shootr.mobile.domain.User;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.stream.ChangeStreamPhotoInteractor;
import com.shootr.mobile.domain.interactor.stream.GetMutedStreamsInteractor;
import com.shootr.mobile.domain.interactor.stream.GetStreamInfoInteractor;
import com.shootr.mobile.domain.interactor.stream.MuteInteractor;
import com.shootr.mobile.domain.interactor.stream.SelectStreamInteractor;
import com.shootr.mobile.domain.interactor.stream.ShareStreamInteractor;
import com.shootr.mobile.domain.interactor.stream.UnmuteInteractor;
import com.shootr.mobile.domain.interactor.user.FollowInteractor;
import com.shootr.mobile.domain.interactor.user.UnfollowInteractor;
import com.shootr.mobile.ui.model.StreamModel;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.model.mappers.StreamModelMapper;
import com.shootr.mobile.ui.model.mappers.UserModelMapper;
import com.shootr.mobile.ui.views.StreamDetailView;
import com.shootr.mobile.util.ErrorMessageFactory;
import java.io.File;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

import static com.shootr.mobile.domain.utils.Preconditions.checkNotNull;

public class StreamDetailPresenter implements Presenter {

    //region Dependencies
    private final GetStreamInfoInteractor streamInfoInteractor;
    private final ChangeStreamPhotoInteractor changeStreamPhotoInteractor;
    private final ShareStreamInteractor shareStreamInteractor;
    private final FollowInteractor followInteractor;
    private final UnfollowInteractor unfollowInteractor;
    private final SelectStreamInteractor selectStreamInteractor;
    private final GetMutedStreamsInteractor getMutedStreamsInteractor;
    private final MuteInteractor muteInteractor;
    private final UnmuteInteractor unmuteInteractor;

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
      ChangeStreamPhotoInteractor changeStreamPhotoInteractor, ShareStreamInteractor shareStreamInteractor,
      FollowInteractor followInteractor, UnfollowInteractor unfollowInteractor,
      SelectStreamInteractor selectStreamInteractor, GetMutedStreamsInteractor getMutedStreamsInteractor,
      MuteInteractor muteInteractor, UnmuteInteractor unmuteInteractor, StreamModelMapper streamModelMapper,
      UserModelMapper userModelMapper, ErrorMessageFactory errorMessageFactory) {
        this.streamInfoInteractor = streamInfoInteractor;
        this.changeStreamPhotoInteractor = changeStreamPhotoInteractor;
        this.shareStreamInteractor = shareStreamInteractor;
        this.followInteractor = followInteractor;
        this.unfollowInteractor = unfollowInteractor;
        this.selectStreamInteractor = selectStreamInteractor;
        this.getMutedStreamsInteractor = getMutedStreamsInteractor;
        this.muteInteractor = muteInteractor;
        this.unmuteInteractor = unmuteInteractor;
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
        this.loadMutedStatus();
        this.loadStreamInfo();
    }

    public void loadMutedStatus() {
        getMutedStreamsInteractor.loadMutedStreamIds(new Interactor.Callback<List<String>>() {
            @Override public void onLoaded(List<String> ids) {
                streamDetailView.setMuteStatus(ids.contains(idStream));
            }
        });
    }
    //endregion

    //region Edit stream
    public void editStreamClick() {
        streamDetailView.showEditStreamPhotoOrInfo();
    }

    public void editStreamInfo() {
        streamDetailView.navigateToEditStream(idStream);
    }

    public void resultFromEditStreamInfo() {
        loadStreamInfo();
    }

    public void editStreamPhoto() {
        streamDetailView.showPhotoPicker();
    }

    public void photoSelected(File photoFile) {
        streamDetailView.showLoadingPictureUpload();
        changeStreamPhotoInteractor.changeStreamPhoto(streamModel.getIdStream(),
          photoFile,
          new ChangeStreamPhotoInteractor.Callback() {
              @Override public void onLoaded(Stream stream) {
                  renderStreamInfo(stream);
                  streamDetailView.hideLoadingPictureUpload();
              }
          },
          new Interactor.ErrorCallback() {
              @Override public void onError(ShootrException error) {
                  showEditPicturePlaceholderIfEmpty();
                  streamDetailView.hideLoadingPictureUpload();
                  showImageUploadError();
                  Timber.e(error, "Error changing stream photo");
              }
          });
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
        this.renderCurrentUserWatching(streamInfo.getCurrentUserWatching());
        this.renderFollowingNumber(streamInfo.getNumberOfFollowing());
        this.showViewDetail();
        this.hideViewLoading();
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
        if (streamModel.amIAuthor() && streamModel.getPicture() == null) {
            editStreamPhoto();
        } else if (streamModel.getPicture() != null) {
            zoomPhoto();
        }
    }

    public void zoomPhoto() {
        streamDetailView.zoomPhoto(streamModel.getPicture());
    }

    //region renders
    private void renderWatchersList(StreamInfo streamInfo) {
        if (participantsShown.isEmpty() || streamInfo.isDataComplete()) {
            List<User> watchers = streamInfo.getWatchers();
            participantsShown = userModelMapper.transform(watchers);
            streamDetailView.setWatchers(participantsShown);
            if (streamInfo.hasMoreParticipants()) {
                this.totalWatchers = streamModel.getTotalWatchers();
                streamDetailView.showAllParticipantsButton();
            } else {
                this.totalWatchers = watchers.size();
            }
        }
    }

    private void renderCurrentUserWatching(User currentUserWatch) {
        if (currentUserWatch != null) {
            UserModel currentUserWatchingModel = userModelMapper.transform(currentUserWatch);
            streamDetailView.setCurrentUserWatching(currentUserWatchingModel);
        }
    }

    private void renderStreamInfo(Stream stream) {
        streamModel = streamModelMapper.transform(stream);
        streamDetailView.setStreamTitle(streamModel.getTitle());
        streamDetailView.setStreamShortTitle(streamModel.getShortTitle());
        streamDetailView.setStreamPicture(streamModel.getPicture());
        streamDetailView.setStreamAuthor(streamModel.getAuthorUsername());
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

    private void showImageUploadError() {
        streamDetailView.showError(errorMessageFactory.getImageUploadErrorMessage());
    }

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
        shareStreamInteractor.shareStream(streamModel.getIdStream(), new Interactor.CompletedCallback() {
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
              },
              new Interactor.ErrorCallback() {
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
        streamDetailView.goToStreamDataInfo(streamModel);
    }

    public void contributorsClicked() {
        if(streamModel.amIAuthor()) {
            streamDetailView.goToContributorsActivityAsHolder(streamModel.getIdStream());
        }else {
            streamDetailView.goToContributorsActivity(streamModel.getIdStream());
        }
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
}
