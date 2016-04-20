package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.User;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.user.ContributorInteractor;
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
    private final ContributorInteractor contributorInteractor;
    private final UserModelMapper userModelMapper;
    private final ErrorMessageFactory errorMessageFactory;

    private FindContributorsView findContributorsView;
    private List<UserModel> contributors;
    private String query;
    private String idStream;
    private Integer currentPage;

    @Inject public FindContributorsPresenter(FindContributorsInteractor findContributorsInteractor,
      ContributorInteractor contributorInteractor, UserModelMapper userModelMapper,
      ErrorMessageFactory errorMessageFactory) {
        this.findContributorsInteractor = findContributorsInteractor;
        this.contributorInteractor = contributorInteractor;
        this.userModelMapper = userModelMapper;
        this.errorMessageFactory = errorMessageFactory;
    }

    protected void setView(FindContributorsView findContributorsView) {
        this.findContributorsView = findContributorsView;
    }

    public void initialize(FindContributorsView findContributorsView, String idStream) {
        this.setView(findContributorsView);
        this.contributors = new ArrayList<>();
        this.idStream = idStream;
    }

    public void searchContributors(String query) {
        this.query = query;
        findContributorsView.hideEmpty();
        findContributorsView.hideContent();
        findContributorsView.hideKeyboard();
        findContributorsView.showLoading();
        findContributorsView.setCurrentQuery(query);
        currentPage = 0;
        findContributorsInteractor.findContributors(idStream, this.query, currentPage, new Interactor.Callback<List<User>>() {
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

    public void addContributor(UserModel userModel) {
        contributorInteractor.addRemoveContributor(idStream, userModel.getIdUser(), true, new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
                //TODO: refresh list
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                //TODO: show error
            }
        });
    }

    public void removeContributor(UserModel userModel) {
        contributorInteractor.addRemoveContributor(idStream,
          userModel.getIdUser(),
          false,
          new Interactor.CompletedCallback() {
              @Override public void onCompleted() {
                  //TODO: refresh list
              }
          },
          new Interactor.ErrorCallback() {
              @Override public void onError(ShootrException error) {
                  //TODO: show error
              }
          });
    }

    @Override public void resume() {
        //TODO refresh list
    }

    @Override public void pause() {
        //TODO refresh pause
    }
}
