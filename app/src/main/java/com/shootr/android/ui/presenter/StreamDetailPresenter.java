package com.shootr.android.ui.presenter;

import android.util.Pair;
import com.path.android.jobqueue.JobManager;
import com.shootr.android.data.bus.Main;
import com.shootr.android.data.entity.FollowEntity;
import com.shootr.android.domain.Stream;
import com.shootr.android.domain.StreamInfo;
import com.shootr.android.domain.User;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.shot.ShareShotInteractor;
import com.shootr.android.domain.interactor.stream.ChangeStreamPhotoInteractor;
import com.shootr.android.domain.interactor.stream.GetStreamInfoInteractor;
import com.shootr.android.domain.interactor.stream.ShareStreamInteractor;
import com.shootr.android.service.dataservice.dto.UserDtoFactory;
import com.shootr.android.task.events.CommunicationErrorEvent;
import com.shootr.android.task.events.ConnectionNotAvailableEvent;
import com.shootr.android.task.events.follows.FollowUnFollowResultEvent;
import com.shootr.android.task.jobs.follows.GetFollowUnFollowUserOfflineJob;
import com.shootr.android.task.jobs.follows.GetFollowUnfollowUserOnlineJob;
import com.shootr.android.ui.model.StreamModel;
import com.shootr.android.ui.model.UserModel;
import com.shootr.android.ui.model.mappers.StreamModelMapper;
import com.shootr.android.ui.model.mappers.UserModelMapper;
import com.shootr.android.ui.views.StreamDetailView;
import com.shootr.android.util.ErrorMessageFactory;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import java.io.File;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Provider;
import timber.log.Timber;

import static com.shootr.android.domain.utils.Preconditions.checkNotNull;

public class StreamDetailPresenter implements Presenter, CommunicationPresenter {

    //region Dependencies
    private final Bus bus;
    private final GetStreamInfoInteractor streamInfoInteractor;
    private final ChangeStreamPhotoInteractor changeStreamPhotoInteractor;
    private final ShareStreamInteractor shareStreamInteractor;

    private final StreamModelMapper streamModelMapper;
    private final UserModelMapper userModelMapper;
    private final ErrorMessageFactory errorMessageFactory;

    private final Provider<GetFollowUnfollowUserOnlineJob> followOnlineJobProvider;
    private final Provider<GetFollowUnFollowUserOfflineJob> followOfflineJobProvider;
    private final JobManager jobManager;

    private StreamDetailView streamDetailView;
    private String idStream;

    private UserModel currentUserWatchingModel;
    private StreamModel streamModel;

    private Integer streamMediaCount;
    private List<UserModel> participantsShown;
    private boolean hasBeenPaused = false;

    @Inject
    public StreamDetailPresenter(@Main Bus bus,
      GetStreamInfoInteractor streamInfoInteractor,
      ChangeStreamPhotoInteractor changeStreamPhotoInteractor,
      ShareStreamInteractor shareStreamInteractor,
      StreamModelMapper streamModelMapper,
      UserModelMapper userModelMapper,
      ErrorMessageFactory errorMessageFactory,
      Provider<GetFollowUnfollowUserOnlineJob> followOnlineJobProvider,
      Provider<GetFollowUnFollowUserOfflineJob> followOfflineJobProvider,
      JobManager jobManager) {
        this.bus = bus;
        this.streamInfoInteractor = streamInfoInteractor;
        this.changeStreamPhotoInteractor = changeStreamPhotoInteractor;
        this.shareStreamInteractor = shareStreamInteractor;
        this.streamModelMapper = streamModelMapper;
        this.userModelMapper = userModelMapper;
        this.errorMessageFactory = errorMessageFactory;
        this.followOnlineJobProvider = followOnlineJobProvider;
        this.followOfflineJobProvider = followOfflineJobProvider;
        this.jobManager = jobManager;
    }
    //endregion

    public void initialize(StreamDetailView streamDetailView, String idStream) {
        setView(streamDetailView);
        this.idStream = idStream;
        this.loadStreamInfo();
    }

    protected void setView(StreamDetailView streamDetailView) {
        this.streamDetailView = streamDetailView;
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
              @Override
              public void onLoaded(Stream stream) {
                  renderStreamInfo(stream);
                  streamDetailView.hideLoadingPictureUpload();
              }
          },
          new Interactor.ErrorCallback() {
              @Override
              public void onError(ShootrException error) {
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
            @Override
            public void onLoaded(StreamInfo streamInfo) {
                onStreamInfoLoaded(streamInfo);
            }
        }, new Interactor.ErrorCallback() {
            @Override
            public void onError(ShootrException error) {
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

    private void refreshWatchers() {
        streamInfoInteractor.obtainStreamInfo(idStream, new GetStreamInfoInteractor.Callback() {
            @Override
            public void onLoaded(StreamInfo streamInfo) {
                if (streamInfo.isDataComplete()) {
                    renderWatchersList(streamInfo);
                }
            }
        }, new Interactor.ErrorCallback() {
            @Override
            public void onError(ShootrException error) {
                String errorMessage = errorMessageFactory.getMessageForError(error);
                streamDetailView.showError(errorMessage);
            }
        });
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
        } else {
            zoomPhoto();
        }
    }

    public void zoomPhoto() {
        streamDetailView.zoomPhoto(streamModel.getPicture());
    }

    //region renders
    private void renderWatchersList(StreamInfo streamInfo) {
        List<User> watchers = streamInfo.getWatchers();
        participantsShown = userModelMapper.transform(watchers);
        streamDetailView.setWatchers(participantsShown);
        if (streamInfo.hasMoreParticipants()) {
            streamDetailView.showAllParticipantsButton();
        }
    }

    private void renderCurrentUserWatching(User currentUserWatch) {
        if (currentUserWatch != null) {
            currentUserWatchingModel = userModelMapper.transform(currentUserWatch);
            streamDetailView.setCurrentUserWatching(currentUserWatchingModel);
        }
    }

    private void renderStreamInfo(Stream stream) {
        streamModel = streamModelMapper.transform(stream);
        streamDetailView.setStreamTitle(streamModel.getTitle());
        streamDetailView.setStreamShortTitle(streamModel.getTag());
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
        if (streamMediaCount > 0) {
            streamDetailView.setMediaCount(streamMediaCount);
        }
    }

    private void renderFollowingNumber(Integer numberOfFollowing) {
        if (numberOfFollowing > 0) {
            streamDetailView.setFollowingNumber(numberOfFollowing);
        }
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

    @Subscribe
    @Override
    public void onCommunicationError(CommunicationErrorEvent event) {
        String communicationErrorMessage = errorMessageFactory.getCommunicationErrorMessage();
        streamDetailView.showError(communicationErrorMessage);
    }

    @Subscribe
    @Override
    public void onConnectionNotAvailable(ConnectionNotAvailableEvent event) {
        String connectionNotAvailableMessage = errorMessageFactory.getConnectionNotAvailableMessage();
        streamDetailView.showError(connectionNotAvailableMessage);
    }

    private void showImageUploadError() {
        streamDetailView.showError(errorMessageFactory.getImageUploadErrorMessage());
    }

    @Override
    public void resume() {
        if (hasBeenPaused) {
            refreshWatchers();
        }
        bus.register(this);
    }

    @Override
    public void pause() {
        hasBeenPaused = true;
        bus.unregister(this);
    }

    public void clickMedia() {
        streamDetailView.navigateToMedia(idStream, streamMediaCount);
    }

    public void follow(String idUser) {
        startfollowUnfollowJob(idUser, true);
    }

    public void unfollow(String idUser) {
        startfollowUnfollowJob(idUser, false);
    }

    protected void startfollowUnfollowJob(String idUser, boolean follow) {
        GetFollowUnFollowUserOfflineJob offlineJob = followOfflineJobProvider.get();
        offlineJob.init(idUser, follow ? UserDtoFactory.FOLLOW_TYPE : UserDtoFactory.UNFOLLOW_TYPE);
        jobManager.addJobInBackground(offlineJob);

        GetFollowUnfollowUserOnlineJob onlineJob = followOnlineJobProvider.get();
        jobManager.addJobInBackground(onlineJob);
    }

    @Subscribe
    public void onFollowUnfollowResultReceived(FollowUnFollowResultEvent event) {
        Pair<String, Boolean> result = event.getResult();
        String idUser = result.first;
        Boolean following = result.second;
        for (UserModel participant : participantsShown) {
            if (participant.getIdUser().equals(idUser)) {
                participant.setRelationship(following? FollowEntity.RELATIONSHIP_FOLLOWING : FollowEntity.RELATIONSHIP_NONE);
                streamDetailView.setWatchers(participantsShown);
                break;
            }
        }
    }

    public void shareStreamViaShootr() {
        shareStreamInteractor.shareStream(streamModel.getIdStream(), new Interactor.CompletedCallback() {
            @Override
            public void onCompleted() {
                streamDetailView.showStreamShared();
            }
        }, new Interactor.ErrorCallback() {
            @Override
            public void onError(ShootrException error) {
                String errorMessage = errorMessageFactory.getMessageForError(error);
                streamDetailView.showError(errorMessage);
            }
        });
    }

    public void shareStreamVia() {
        streamDetailView.shareStreamVia(streamModel);
    }
}
