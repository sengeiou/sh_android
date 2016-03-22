package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.User;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.user.FindContributorsInteractor;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.model.mappers.UserModelMapper;
import com.shootr.mobile.ui.views.FindContributorsView;
import com.shootr.mobile.util.ErrorMessageFactory;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class FindContributorsPresenter implements Presenter {

    private final FindContributorsInteractor findContributorsInteractor;
    private final UserModelMapper userModelMapper;
    private final ErrorMessageFactory errorMessageFactory;

    private FindContributorsView findContributorsView;
    private List<UserModel> contributors;
    private String query;

    @Inject public FindContributorsPresenter(FindContributorsInteractor findContributorsInteractor, UserModelMapper userModelMapper,
      ErrorMessageFactory errorMessageFactory) {
        this.findContributorsInteractor = findContributorsInteractor;
        this.userModelMapper = userModelMapper;
        this.errorMessageFactory = errorMessageFactory;
    }

    protected void setView(FindContributorsView findContributorsView) {
        this.findContributorsView = findContributorsView;
    }

    public void initialize(FindContributorsView findContributorsView) {
        this.setView(findContributorsView);
        this.contributors = new ArrayList<>();
    }

    public void searchContributors(String query) {
        this.query = query;
        findContributorsView.hideEmpty();
        findContributorsView.hideContent();
        findContributorsView.hideKeyboard();
        findContributorsView.showLoading();
        findContributorsView.setCurrentQuery(query);
        findContributorsInteractor.findContributors(query, new Interactor.Callback<List<User>>() {
            @Override public void onLoaded(List<User> users) {
                findContributorsView.hideLoading();
                contributors = userModelMapper.transform(users);
                if (!contributors.isEmpty()) {
                    findContributorsView.showContent();
                    findContributorsView.renderContributors(contributors);
                } else {
                    findContributorsView.showEmpty();
                }
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                findContributorsView.hideLoading();
                findContributorsView.showError(errorMessageFactory.getMessageForError(error));
            }
        });
    }

    @Override public void resume() {

    }

    @Override public void pause() {

    }
}
