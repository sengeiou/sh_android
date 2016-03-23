package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.User;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.user.GetContributorsInteractor;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.model.mappers.UserModelMapper;
import com.shootr.mobile.ui.views.ContributorsView;
import com.shootr.mobile.util.ErrorMessageFactory;
import java.util.List;
import javax.inject.Inject;

public class ContributorsPresenter implements Presenter {

    private final GetContributorsInteractor getContributorsInteractor;
    private final ErrorMessageFactory errorMessageFactory;
    private final UserModelMapper userModelMapper;

    private ContributorsView view;
    private String idStream;

    @Inject public ContributorsPresenter(GetContributorsInteractor getContributorsInteractor,
      ErrorMessageFactory errorMessageFactory, UserModelMapper userModelMapper) {
        this.getContributorsInteractor = getContributorsInteractor;
        this.errorMessageFactory = errorMessageFactory;
        this.userModelMapper = userModelMapper;
    }

    protected void setView(ContributorsView contributorsView) {
        this.view = contributorsView;
    }

    protected void setIdStream(String idStream) {
        this.idStream = idStream;
    }

    public void initialize(ContributorsView contributorsView, String idStream) {
        setView(contributorsView);
        setIdStream(idStream);
        loadContributors();
    }

    private void loadContributors() {
        view.hideEmpty();
        view.showLoading();
        getContributorsInteractor.obtainContributors(idStream, new Interactor.Callback<List<User>>() {
            @Override public void onLoaded(List<User> contributors) {
                view.hideLoading();
                view.showAllContributors();
                List<UserModel> contributorModels = userModelMapper.transform(contributors);
                renderContributors(contributorModels);
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                view.showError(errorMessageFactory.getMessageForError(error));
            }
        });
    }

    private void renderContributors(List<UserModel> contributors) {
        if (!contributors.isEmpty()) {
            view.renderAllContributors(contributors);
        } else {
            view.showEmpty();
        }
    }

    public void onAddContributorClick(Integer countContributors) {
        if(countContributors - 1 >100){
            view.showContributorsLimitSnackbar();
        }else{
            view.goToSearchContributors();
        }
    }

    @Override public void resume() {

    }

    @Override public void pause() {

    }
}
