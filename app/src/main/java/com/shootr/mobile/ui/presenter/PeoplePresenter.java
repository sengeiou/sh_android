package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.data.bus.Main;
import com.shootr.mobile.domain.User;
import com.shootr.mobile.domain.UserList;
import com.shootr.mobile.domain.interactor.user.GetPeopleInteractor;
import com.shootr.mobile.task.events.CommunicationErrorEvent;
import com.shootr.mobile.task.events.ConnectionNotAvailableEvent;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.model.mappers.UserModelMapper;
import com.shootr.mobile.ui.views.PeopleView;
import com.shootr.mobile.util.ErrorMessageFactory;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import java.util.List;
import javax.inject.Inject;

public class PeoplePresenter implements Presenter, CommunicationPresenter {

    private final Bus bus;
    private final ErrorMessageFactory errorMessageFactory;
    private final GetPeopleInteractor getPeopleInteractor;
    private final UserModelMapper userModelMapper;

    private PeopleView peopleView;
    private Boolean hasBeenPaused = false;

    @Inject public PeoplePresenter(@Main Bus bus, ErrorMessageFactory errorMessageFactory,
      GetPeopleInteractor getPeopleInteractor, UserModelMapper userModelMapper) {
        this.bus = bus;
        this.errorMessageFactory = errorMessageFactory;
        this.getPeopleInteractor = getPeopleInteractor;
        this.userModelMapper = userModelMapper;
    }

    public void initialize() {
        this.loadPeopleList();
    }

    public void setView(PeopleView peopleView){
        this.peopleView = peopleView;
    }

    private void loadPeopleList() {
        this.showViewLoading();
        this.hideViewEmpty();
        this.getPeopleList();
    }

    private void getPeopleList() {
        getPeopleInteractor.obtainPeople();
    }

    @Subscribe
    public void onPeopleListLoaded(UserList userList) {
        this.hideViewLoading();
        List<User> people = userList.getUsers();
        List<UserModel> userModels = userModelMapper.transform(people);
        peopleView.showPeopleList();
        if (people != null && !people.isEmpty()) {
            this.showPeopleListInView(userModels);
            this.hideViewEmpty();
        } else {
            this.showViewEmtpy();
        }
    }

    private void showPeopleListInView(List<UserModel> people) {
        peopleView.renderUserList(people);
    }

    private void showViewLoading() {
        peopleView.showLoading();
    }

    private void hideViewLoading() {
        peopleView.hideLoading();
    }

    private void showViewEmtpy() {
        peopleView.showEmpty();
    }

    private void hideViewEmpty() {
        peopleView.hideEmpty();
    }

    private void showErrorInView(String errorMessage) {
        peopleView.showError(errorMessage);
    }

    @Subscribe @Override
    public void onCommunicationError(CommunicationErrorEvent event) {
        this.hideViewLoading();
        this.showErrorInView(errorMessageFactory.getCommunicationErrorMessage());
    }

    @Subscribe @Override
    public void onConnectionNotAvailable(ConnectionNotAvailableEvent event) {
        this.hideViewLoading();
        this.showErrorInView(errorMessageFactory.getConnectionNotAvailableMessage());
    }

    @Override public void resume() {
        bus.register(this);
        if (hasBeenPaused) {
            getPeopleList();
        }
    }

    @Override public void pause() {
        bus.unregister(this);
        hasBeenPaused = true;
    }
}
