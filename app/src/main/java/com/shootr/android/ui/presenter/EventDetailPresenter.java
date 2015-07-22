package com.shootr.android.ui.presenter;

import com.shootr.android.data.bus.Main;
import com.shootr.android.domain.Stream;
import com.shootr.android.domain.StreamInfo;
import com.shootr.android.domain.User;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.event.ChangeStreamPhotoInteractor;
import com.shootr.android.domain.interactor.event.GetStreamMediaCountInteractor;
import com.shootr.android.domain.interactor.event.VisibleStreamInfoInteractor;
import com.shootr.android.task.events.CommunicationErrorEvent;
import com.shootr.android.task.events.ConnectionNotAvailableEvent;
import com.shootr.android.ui.model.StreamModel;
import com.shootr.android.ui.model.UserModel;
import com.shootr.android.ui.model.mappers.StreamModelMapper;
import com.shootr.android.ui.model.mappers.UserModelMapper;
import com.shootr.android.ui.views.EventDetailView;
import com.shootr.android.util.ErrorMessageFactory;
import com.shootr.android.util.WatchersTimeFormatter;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import java.io.File;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class EventDetailPresenter implements Presenter, CommunicationPresenter {

    //region Dependencies
    private final Bus bus;
    private final VisibleStreamInfoInteractor eventInfoInteractor;
    private final ChangeStreamPhotoInteractor changeStreamPhotoInteractor;

    private final StreamModelMapper streamModelMapper;
    private final UserModelMapper userModelMapper;
    private final ErrorMessageFactory errorMessageFactory;
    private final WatchersTimeFormatter watchersTimeFormatter;
    private final GetStreamMediaCountInteractor eventMediaCountInteractor;

    private EventDetailView eventDetailView;
    private String idEvent;

    private UserModel currentUserWatchingModel;
    private StreamModel streamModel;

    private Integer eventMediaCount;

    @Inject
    public EventDetailPresenter(@Main Bus bus, VisibleStreamInfoInteractor eventInfoInteractor,
      ChangeStreamPhotoInteractor changeStreamPhotoInteractor, StreamModelMapper streamModelMapper, UserModelMapper userModelMapper, ErrorMessageFactory errorMessageFactory,
      WatchersTimeFormatter watchersTimeFormatter, GetStreamMediaCountInteractor eventMediaCountInteractor) {
        this.bus = bus;
        this.eventInfoInteractor = eventInfoInteractor;
        this.changeStreamPhotoInteractor = changeStreamPhotoInteractor;
        this.streamModelMapper = streamModelMapper;
        this.userModelMapper = userModelMapper;
        this.errorMessageFactory = errorMessageFactory;
        this.watchersTimeFormatter = watchersTimeFormatter;
        this.eventMediaCountInteractor = eventMediaCountInteractor;
    }
    //endregion

    public void initialize(EventDetailView eventDetailView, String idEvent) {
        setView(eventDetailView);
        this.idEvent = idEvent;
        this.loadEventInfo();
    }

    protected void setView(EventDetailView eventDetailView){
        this.eventDetailView = eventDetailView;
    }
    //endregion

    //region Edit stream
    public void editEventClick() {
        eventDetailView.showEditEventPhotoOrInfo();
    }

    public void editEventInfo() {
        eventDetailView.navigateToEditEvent(idEvent);
    }

    public void resultFromEditEventInfo(String idEventEdited) {
        if (idEventEdited.equals(streamModel.getIdStream())) {
            loadEventInfo();
        }
    }

    public void editEventPhoto() {
        eventDetailView.showPhotoPicker();
    }

    public void photoSelected(File photoFile) {
        eventDetailView.showLoadingPictureUpload();
        changeStreamPhotoInteractor.changeStreamPhoto(streamModel.getIdStream(),
          photoFile,
          new ChangeStreamPhotoInteractor.Callback() {
              @Override public void onLoaded(Stream stream) {
                  renderEventInfo(stream);
                  eventDetailView.hideLoadingPictureUpload();
                  eventDetailView.showEditPicture(stream.getPicture());
              }
          },
          new Interactor.ErrorCallback() {
              @Override public void onError(ShootrException error) {
                  eventDetailView.showEditPicture(streamModel.getPicture());
                  eventDetailView.hideLoadingPictureUpload();
                  showImageUploadError();
                  Timber.e(error, "Error changing stream photo");
              }
          });
    }
    //endregion

    //region Event info
    public void refreshInfo() {
        this.getEventInfo();
    }

    public void loadEventInfo() {
        this.showViewLoading();
        this.getEventInfo();
    }

    public void getEventInfo() {
        eventInfoInteractor.obtainStreamInfo(idEvent, new VisibleStreamInfoInteractor.Callback() {
            @Override public void onLoaded(StreamInfo streamInfo) {
                onEventInfoLoaded(streamInfo);
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                String errorMessage = errorMessageFactory.getMessageForError(error);
                eventDetailView.showError(errorMessage);
            }
        });
    }

    public void onEventInfoLoaded(StreamInfo streamInfo) {
        if (streamInfo.getStream() == null) {
            this.showViewEmpty();
        } else {
            this.hideViewEmpty();
            this.renderEventInfo(streamInfo.getStream());
            this.renderWatchersList(streamInfo.getWatchers());
            this.renderCurrentUserWatching(streamInfo.getCurrentUserWatching());
            this.renderWatchersCount(streamInfo.getWatchersCount());
            this.loadMediaCount();
            this.showViewDetail();
        }
        this.hideViewLoading();
    }

    private void loadMediaCount() {
        eventMediaCountInteractor.getStreamMediaCount(idEvent, new Interactor.Callback<Integer>() {
            @Override public void onLoaded(Integer count) {
                eventMediaCount = count;
                eventDetailView.showMediaCount();
                eventDetailView.setMediaCount(count);
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                /* no-op */
            }
        });
    }

    private void showViewDetail() {
        eventDetailView.showContent();
        eventDetailView.showDetail();
    }
    //endregion

    public void clickAuthor() {
        eventDetailView.navigateToUser(streamModel.getAuthorId());
    }
    //endregion

    public void photoClick() {
        if (streamModel.amIAuthor() && streamModel.getPicture() == null) {
            editEventPhoto();
        } else {
            zoomPhoto();
        }
    }

    public void zoomPhoto() {
        eventDetailView.zoomPhoto(streamModel.getPicture());
    }

    //region renders
    private void renderWatchersList(List<User> watchers) {
        List<UserModel> watcherModels = userModelMapper.transform(watchers);
        obtainJoinDatesInText(watcherModels);
        eventDetailView.setWatchers(watcherModels);
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
            eventDetailView.setCurrentUserWatching(currentUserWatchingModel);
        }
    }

    private void renderEventInfo(Stream stream) {
        streamModel = streamModelMapper.transform(stream);
        eventDetailView.setEventTitle(streamModel.getTitle());
        eventDetailView.setEventPicture(streamModel.getPicture());
        eventDetailView.setEventAuthor(streamModel.getAuthorUsername());
        if (streamModel.amIAuthor()) {
            eventDetailView.showEditEventButton();
            eventDetailView.showEditPicture(streamModel.getPicture());
        } else {
            eventDetailView.hideEditEventButton();
            eventDetailView.hideEditPicture();
        }
    }

    private void renderWatchersCount(int watchersCount) {
        eventDetailView.setWatchersCount(watchersCount);
    }
    //endregion

    //region View methods
    private void showViewLoading() {
        eventDetailView.hideContent();
        eventDetailView.showLoading();
    }

    private void hideViewLoading() {
        eventDetailView.hideLoading();
    }

    private void showViewEmpty() {
        eventDetailView.showContent();
        eventDetailView.showEmpty();
    }

    private void hideViewEmpty() {
        eventDetailView.hideEmpty();
    }
    //endregion

    @Subscribe @Override public void onCommunicationError(CommunicationErrorEvent event) {
        String communicationErrorMessage = errorMessageFactory.getCommunicationErrorMessage();
        eventDetailView.showError(communicationErrorMessage);
    }

    @Subscribe @Override public void onConnectionNotAvailable(ConnectionNotAvailableEvent event) {
        String connectionNotAvailableMessage = errorMessageFactory.getConnectionNotAvailableMessage();
        eventDetailView.showError(connectionNotAvailableMessage);
    }

    private void showImageUploadError() {
        eventDetailView.showError(errorMessageFactory.getImageUploadErrorMessage());
    }

    @Override public void resume() {
        bus.register(this);
    }

    @Override public void pause() {
        bus.unregister(this);
    }

    public void clickMedia() {
        eventDetailView.navigateToMedia(idEvent, eventMediaCount);
    }
}
