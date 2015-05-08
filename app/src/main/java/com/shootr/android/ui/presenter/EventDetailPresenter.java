package com.shootr.android.ui.presenter;

import com.shootr.android.data.bus.Main;
import com.shootr.android.domain.Event;
import com.shootr.android.domain.EventInfo;
import com.shootr.android.domain.User;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.event.ChangeEventPhotoInteractor;
import com.shootr.android.domain.interactor.event.VisibleEventInfoInteractor;
import com.shootr.android.domain.interactor.user.GetCheckinStatusInteractor;
import com.shootr.android.domain.interactor.user.PerformCheckinInteractor;
import com.shootr.android.task.events.CommunicationErrorEvent;
import com.shootr.android.task.events.ConnectionNotAvailableEvent;
import com.shootr.android.ui.model.EventModel;
import com.shootr.android.ui.model.UserModel;
import com.shootr.android.ui.model.mappers.EventModelMapper;
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
    private final VisibleEventInfoInteractor eventInfoInteractor;
    private final ChangeEventPhotoInteractor changeEventPhotoInteractor;
    private final GetCheckinStatusInteractor getCheckinStatusInteractor;
    private final PerformCheckinInteractor performCheckinInteractor;

    private final EventModelMapper eventModelMapper;
    private final UserModelMapper userModelMapper;
    private final ErrorMessageFactory errorMessageFactory;
    private final WatchersTimeFormatter watchersTimeFormatter;

    private EventDetailView eventDetailView;
    private String idEvent;

    private UserModel currentUserWatchingModel;
    private EventModel eventModel;

    private Boolean isCurrentUserWatchingThisEvent;
    private Boolean hasUserCheckdIn;

    @Inject
    public EventDetailPresenter(@Main Bus bus, VisibleEventInfoInteractor eventInfoInteractor,
      ChangeEventPhotoInteractor changeEventPhotoInteractor, GetCheckinStatusInteractor getCheckinStatusInteractor,
      PerformCheckinInteractor performCheckinInteractor, EventModelMapper eventModelMapper, UserModelMapper userModelMapper, ErrorMessageFactory errorMessageFactory,
                                WatchersTimeFormatter watchersTimeFormatter) {
        this.bus = bus;
        this.eventInfoInteractor = eventInfoInteractor;
        this.changeEventPhotoInteractor = changeEventPhotoInteractor;
        this.getCheckinStatusInteractor = getCheckinStatusInteractor;
        this.performCheckinInteractor = performCheckinInteractor;
        this.eventModelMapper = eventModelMapper;
        this.userModelMapper = userModelMapper;
        this.errorMessageFactory = errorMessageFactory;
        this.watchersTimeFormatter = watchersTimeFormatter;
    }
    //endregion

    public void initialize(EventDetailView eventDetailView, String idEvent) {
        setView(eventDetailView);
        this.idEvent = idEvent;
        this.loadEventInfo();
        this.loadCheckinStatus();
    }

    protected void setView(EventDetailView eventDetailView){
        this.eventDetailView = eventDetailView;
    }

    public void loadCheckinStatus() {
        getCheckinStatusInteractor.loadCheckinStatus(new Interactor.Callback<Boolean>() {
            @Override
            public void onLoaded(Boolean currentCheckIn) {
                hasUserCheckdIn = currentCheckIn;
                if (!currentCheckIn) {
                    updateCheckinVisibility();
                }
            }
        });
    }

    private void updateCheckinVisibility(){
        if(isCurrentUserWatchingThisEvent != null && hasUserCheckdIn != null){
            if(!hasUserCheckdIn && isCurrentUserWatchingThisEvent){
                eventDetailView.showCheckin();
            }
        }

    }
    //endregion

    //region Edit event
    public void editEventClick() {
        eventDetailView.showEditEventPhotoOrInfo();

    }

    public void editEventInfo() {
        String idEvent = eventModel.getIdEvent();
        eventDetailView.navigateToEditEvent(idEvent);
    }

    public void resultFromEditEventInfo(String idEventEdited) {
        if (idEventEdited.equals(eventModel.getIdEvent())) {
            loadEventInfo();
        }
    }

    public void editEventPhoto() {
        eventDetailView.showPhotoPicker();
    }

    public void photoSelected(File photoFile) {
        eventDetailView.showLoadingPictureUpload();
        changeEventPhotoInteractor.changeEventPhoto(eventModel.getIdEvent(), photoFile,
                new ChangeEventPhotoInteractor.Callback() {
                    @Override
                    public void onLoaded(Event event) {
                        renderEventInfo(event);
                        eventDetailView.hideLoadingPictureUpload();
                        eventDetailView.showEditPicture(event.getPicture());
                    }
                }, new Interactor.ErrorCallback() {
                    @Override
                    public void onError(ShootrException error) {
                        eventDetailView.showEditPicture(eventModel.getPicture());
                        eventDetailView.hideLoadingPictureUpload();
                        showImageUploadError();
                        Timber.e(error, "Error changing event photo");
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
        eventInfoInteractor.obtainEventInfo(idEvent, new VisibleEventInfoInteractor.Callback() {
            @Override
            public void onLoaded(EventInfo eventInfo) {
                onEventInfoLoaded(eventInfo);
            }
        });
    }

    public void onEventInfoLoaded(EventInfo eventInfo) {
        if (eventInfo.getEvent() == null) {
            this.showViewEmpty();
        } else {
            this.hideViewEmpty();
            this.renderEventInfo(eventInfo.getEvent());
            this.renderWatchersList(eventInfo.getWatchers());
            this.renderCurrentUserWatching(eventInfo.getCurrentUserWatching());
            isCurrentUserWatchingEvent(eventInfo.getCurrentUserWatching());
            this.renderWatchersCount(eventInfo.getWatchersCount());
            this.showViewDetail();
            updateCheckinVisibility();
        }
        this.hideViewLoading();
    }

    private void showViewDetail() {
        eventDetailView.showContent();
        eventDetailView.showDetail();
    }
    //endregion

    public void clickAuthor() {
        eventDetailView.navigateToUser(eventModel.getAuthorId());
    }

    //region Check in
    public void clickCheckin() {
        eventDetailView.showCheckinConfirmation();
    }

    public void confirmCheckin() {
        performCheckin();
    }

    public void retryCheckin() {
        performCheckin();
    }

    private void performCheckin() {
        eventDetailView.showCheckinLoading();
        performCheckinInteractor.performCheckin(new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
                eventDetailView.hideCheckin();
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                Timber.e(error, "Error while doing check-in");
                String errorMessage = errorMessageFactory.getMessageForError(error);
                eventDetailView.showCheckinErrorRetry(errorMessage);
                eventDetailView.hideCheckinLoading();
            }
        });
    }
    //endregion

    public void photoClick() {
        if (eventModel.amIAuthor() && eventModel.getPicture() == null) {
            editEventPhoto();
        } else {
            zoomPhoto();
        }
    }

    public void zoomPhoto() {
        eventDetailView.zoomPhoto(eventModel.getPicture());
    }

    //region renders
    private void renderWatchersList(List<User> watchers) {
        List<UserModel> watcherModels = userModelMapper.transform(watchers);
        obtainJoinDatesInText(watcherModels);
        eventDetailView.setWatchers(watcherModels);
    }

    private void obtainJoinDatesInText(List<UserModel> watcherModels) {
        for (UserModel watcherModel : watcherModels) {
            watcherModel.setJoinEventDateText(
                    watchersTimeFormatter.jointDateText(watcherModel.getJoinEventDate()));
        }
    }

    private void renderCurrentUserWatching(User currentUserWatch) {
        if (isCurrentUserWatchingEvent(currentUserWatch)) {
            currentUserWatchingModel = userModelMapper.transform(currentUserWatch);
            currentUserWatchingModel.setJoinEventDateText(
                    watchersTimeFormatter.jointDateText(
                            currentUserWatchingModel.getJoinEventDate()));
            eventDetailView.setCurrentUserWatching(currentUserWatchingModel);
        }
    }

    private boolean isCurrentUserWatchingEvent(User currentUserWatch) {
        if(currentUserWatch != null){
            isCurrentUserWatchingThisEvent = true;
            return isCurrentUserWatchingThisEvent;
        }
        return false;
    }

    private void renderEventInfo(Event event) {
        eventModel = eventModelMapper.transform(event);
        eventDetailView.setEventTitle(eventModel.getTitle());
        eventDetailView.setEventDate(eventModel.getDatetime());
        eventDetailView.setEventPicture(eventModel.getPicture());
        eventDetailView.setEventAuthor(eventModel.getAuthorUsername());
        if (eventModel.amIAuthor()) {
            eventDetailView.showEditEventButton();
            eventDetailView.showEditPicture(eventModel.getPicture());
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
}
