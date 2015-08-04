package com.shootr.android.ui.presenter;

import com.shootr.android.data.bus.Main;
import com.shootr.android.domain.Stream;
import com.shootr.android.domain.StreamInfo;
import com.shootr.android.domain.User;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.stream.ChangeStreamPhotoInteractor;
import com.shootr.android.domain.interactor.stream.GetStreamMediaCountInteractor;
import com.shootr.android.domain.interactor.stream.VisibleStreamInfoInteractor;
import com.shootr.android.task.events.CommunicationErrorStream;
import com.shootr.android.task.events.ConnectionNotAvailableStream;
import com.shootr.android.ui.model.StreamModel;
import com.shootr.android.ui.model.UserModel;
import com.shootr.android.ui.model.mappers.StreamModelMapper;
import com.shootr.android.ui.model.mappers.UserModelMapper;
import com.shootr.android.ui.views.StreamDetailView;
import com.shootr.android.util.ErrorMessageFactory;
import com.shootr.android.util.WatchersTimeFormatter;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import java.io.File;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class StreamDetailPresenter implements Presenter, CommunicationPresenter {

    //region Dependencies
    private final Bus bus;
    private final VisibleStreamInfoInteractor streamInfoInteractor;
    private final ChangeStreamPhotoInteractor changeStreamPhotoInteractor;

    private final StreamModelMapper streamModelMapper;
    private final UserModelMapper userModelMapper;
    private final ErrorMessageFactory errorMessageFactory;
    private final WatchersTimeFormatter watchersTimeFormatter;
    private final GetStreamMediaCountInteractor streamMediaCountInteractor;

    private StreamDetailView streamDetailView;
    private String idStream;

    private UserModel currentUserWatchingModel;
    private StreamModel streamModel;

    private Integer streamMediaCount;

    @Inject
    public StreamDetailPresenter(@Main Bus bus, VisibleStreamInfoInteractor streamInfoInteractor,
      ChangeStreamPhotoInteractor changeStreamPhotoInteractor, StreamModelMapper streamModelMapper,
      UserModelMapper userModelMapper, ErrorMessageFactory errorMessageFactory,
      WatchersTimeFormatter watchersTimeFormatter, GetStreamMediaCountInteractor streamMediaCountInteractor) {
        this.bus = bus;
        this.streamInfoInteractor = streamInfoInteractor;
        this.changeStreamPhotoInteractor = changeStreamPhotoInteractor;
        this.streamModelMapper = streamModelMapper;
        this.userModelMapper = userModelMapper;
        this.errorMessageFactory = errorMessageFactory;
        this.watchersTimeFormatter = watchersTimeFormatter;
        this.streamMediaCountInteractor = streamMediaCountInteractor;
    }
    //endregion

    public void initialize(StreamDetailView streamDetailView, String idStream) {
        setView(streamDetailView);
        this.idStream = idStream;
        this.loadStreamInfo();
    }

    protected void setView(StreamDetailView streamDetailView){
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
              @Override public void onLoaded(Stream stream) {
                  renderStreamInfo(stream);
                  streamDetailView.hideLoadingPictureUpload();
                  streamDetailView.showEditPicture(stream.getPicture());
              }
          },
          new Interactor.ErrorCallback() {
              @Override public void onError(ShootrException error) {
                  streamDetailView.showEditPicture(streamModel.getPicture());
                  streamDetailView.hideLoadingPictureUpload();
                  showImageUploadError();
                  Timber.e(error, "Error changing stream photo");
              }
          });
    }
    //endregion

    //region Stream info
    public void refreshInfo() {
        this.getStreamInfo();
    }

    public void loadStreamInfo() {
        this.showViewLoading();
        this.getStreamInfo();
    }

    public void getStreamInfo() {
        streamInfoInteractor.obtainStreamInfo(idStream, new VisibleStreamInfoInteractor.Callback() {
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
        if (streamInfo.getStream() == null) {
            this.showViewEmpty();
        } else {
            this.hideViewEmpty();
            this.renderStreamInfo(streamInfo.getStream());
            this.renderWatchersList(streamInfo.getWatchers());
            this.renderCurrentUserWatching(streamInfo.getCurrentUserWatching());
            this.renderWatchersCount(streamInfo.getWatchersCount());
            this.loadMediaCount();
            this.showViewDetail();
        }
        this.hideViewLoading();
    }

    private void loadMediaCount() {
        streamMediaCountInteractor.getStreamMediaCount(idStream, new Interactor.Callback<Integer>() {
            @Override public void onLoaded(Integer count) {
                streamMediaCount = count;
                streamDetailView.showMediaCount();
                streamDetailView.setMediaCount(count);
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                /* no-op */
            }
        });
    }

    private void showViewDetail() {
        streamDetailView.showContent();
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
    private void renderWatchersList(List<User> watchers) {
        List<UserModel> watcherModels = userModelMapper.transform(watchers);
        obtainJoinDatesInText(watcherModels);
        streamDetailView.setWatchers(watcherModels);
    }

    private void obtainJoinDatesInText(List<UserModel> watcherModels) {
        for (UserModel watcherModel : watcherModels) {
            watcherModel.setJoinStreamDateText(watchersTimeFormatter.jointDateText(watcherModel.getJoinStreamDate()));
        }
    }

    private void renderCurrentUserWatching(User currentUserWatch) {
        if(currentUserWatch != null){
            currentUserWatchingModel = userModelMapper.transform(currentUserWatch);
            currentUserWatchingModel.setJoinStreamDateText(watchersTimeFormatter.jointDateText(currentUserWatchingModel.getJoinStreamDate()));
            streamDetailView.setCurrentUserWatching(currentUserWatchingModel);
        }
    }

    private void renderStreamInfo(Stream stream) {
        streamModel = streamModelMapper.transform(stream);
        streamDetailView.setStreamTitle(streamModel.getTitle());
        streamDetailView.setStreamPicture(streamModel.getPicture());
        streamDetailView.setStreamAuthor(streamModel.getAuthorUsername());
        if (!streamModel.getDescription().isEmpty()) {
            streamDetailView.setStreamDescription(streamModel.getDescription());
        }
        if (streamModel.amIAuthor()) {
            streamDetailView.showEditStreamButton();
            streamDetailView.showEditPicture(streamModel.getPicture());
        } else {
            streamDetailView.hideEditStreamButton();
            streamDetailView.hideEditPicture();
        }
    }

    private void renderWatchersCount(int watchersCount) {
        streamDetailView.setWatchersCount(watchersCount);
    }
    //endregion

    //region View methods
    private void showViewLoading() {
        streamDetailView.hideContent();
        streamDetailView.showLoading();
    }

    private void hideViewLoading() {
        streamDetailView.hideLoading();
    }

    private void showViewEmpty() {
        streamDetailView.showContent();
        streamDetailView.showEmpty();
    }

    private void hideViewEmpty() {
        streamDetailView.hideEmpty();
    }
    //endregion

    @Subscribe @Override public void onCommunicationError(CommunicationErrorStream stream) {
        String communicationErrorMessage = errorMessageFactory.getCommunicationErrorMessage();
        streamDetailView.showError(communicationErrorMessage);
    }

    @Subscribe @Override public void onConnectionNotAvailable(ConnectionNotAvailableStream stream) {
        String connectionNotAvailableMessage = errorMessageFactory.getConnectionNotAvailableMessage();
        streamDetailView.showError(connectionNotAvailableMessage);
    }

    private void showImageUploadError() {
        streamDetailView.showError(errorMessageFactory.getImageUploadErrorMessage());
    }

    @Override public void resume() {
        bus.register(this);
    }

    @Override public void pause() {
        bus.unregister(this);
    }

    public void clickMedia() {
        streamDetailView.navigateToMedia(idStream, streamMediaCount);
    }
}
