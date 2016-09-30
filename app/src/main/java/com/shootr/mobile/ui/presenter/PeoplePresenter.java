package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.data.bus.Main;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.user.GetMutualsInteractor;
import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.model.mappers.UserModelMapper;
import com.shootr.mobile.ui.views.PeopleView;
import com.shootr.mobile.util.ErrorMessageFactory;
import com.squareup.otto.Bus;
import java.util.List;
import javax.inject.Inject;

public class PeoplePresenter implements Presenter {

    private final Bus bus;
    private final ErrorMessageFactory errorMessageFactory;
    private final GetMutualsInteractor getMutualsInteractor;
    private final UserModelMapper userModelMapper;

    private PeopleView peopleView;
    private Boolean hasBeenPaused = false;

    @Inject public PeoplePresenter(@Main Bus bus, ErrorMessageFactory errorMessageFactory,
      GetMutualsInteractor getMutualsInteractor, UserModelMapper userModelMapper) {
        this.bus = bus;
        this.errorMessageFactory = errorMessageFactory;
        this.getMutualsInteractor = getMutualsInteractor;
        this.userModelMapper = userModelMapper;
    }

    public void initialize() {
        this.loadPeopleList();
    }

    public void setView(PeopleView peopleView) {
        this.peopleView = peopleView;
    }

    private void loadPeopleList() {
        this.showViewLoading();
        this.hideViewEmpty();
        this.getPeopleList();
    }

    private void getPeopleList() {
        getMutualsInteractor.obtainMutuals(new Interactor.Callback<List<User>>() {
            @Override public void onLoaded(List<User> users) {
                onPeopleListLoaded(users);
            }
        });
    }

    private void onPeopleListLoaded(List<User> userList) {
        this.hideViewLoading();
        List<UserModel> userModels = userModelMapper.transform(userList);
        peopleView.showPeopleList();
        if (userList != null && !userList.isEmpty()) {
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
