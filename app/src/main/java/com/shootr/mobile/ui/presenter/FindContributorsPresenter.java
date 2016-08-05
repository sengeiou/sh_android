package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.user.contributor.ManageContributorsInteractor;
import com.shootr.mobile.domain.interactor.user.contributor.FindContributorsInteractor;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.model.mappers.UserModelMapper;
import com.shootr.mobile.ui.views.FindContributorsView;
import com.shootr.mobile.util.ErrorMessageFactory;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class FindContributorsPresenter implements Presenter {

    private final FindContributorsInteractor findContributorsInteractor;
    private final ManageContributorsInteractor manageContributorsInteractor;
    private final UserModelMapper userModelMapper;
    private final ErrorMessageFactory errorMessageFactory;

    private Boolean hasBeenPaused = false;
    private FindContributorsView findContributorsView;
    private List<UserModel> contributors;
    private String query;
    private String idStream;

    @Inject public FindContributorsPresenter(FindContributorsInteractor findContributorsInteractor,
      ManageContributorsInteractor manageContributorsInteractor, UserModelMapper userModelMapper,
      ErrorMessageFactory errorMessageFactory) {
        this.findContributorsInteractor = findContributorsInteractor;
        this.manageContributorsInteractor = manageContributorsInteractor;
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
        Integer currentPage = 0;
        findContributorsInteractor.findContributors(idStream,
          this.query,
          currentPage,
          new Interactor.Callback<List<User>>() {
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
          },
          new Interactor.ErrorCallback() {
              @Override public void onError(ShootrException error) {
                  findContributorsView.hideLoading();
                  findContributorsView.showError(errorMessageFactory.getMessageForError(error));
              }
          });
    }

    public void addContributor(final UserModel userModel) {
        manageContributorsInteractor.manageContributor(idStream,
          userModel.getIdUser(),
          true,
          new Interactor.CompletedCallback() {
              @Override public void onCompleted() {
                  findContributorsView.finishActivity();
              }
          },
          new Interactor.ErrorCallback() {
              @Override public void onError(ShootrException error) {
                  findContributorsView.showError(errorMessageFactory.getMessageForError(error));
              }
          });
    }

    public void removeContributor(final UserModel userModel) {
        manageContributorsInteractor.manageContributor(idStream,
          userModel.getIdUser(),
          false,
          new Interactor.CompletedCallback() {
              @Override public void onCompleted() {
                  findContributorsView.finishActivity();
              }
          },
          new Interactor.ErrorCallback() {
              @Override public void onError(ShootrException error) {
                  findContributorsView.showError(errorMessageFactory.getMessageForError(error));
              }
          });
    }

    public void onContributorClick(UserModel userModel) {
        findContributorsView.showAddConfirmation(userModel);
    }

    @Override public void resume() {
        if (hasBeenPaused) {
            searchContributors(query);
        }
    }

    @Override public void pause() {
        hasBeenPaused = true;
    }
}
