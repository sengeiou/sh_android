package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.User;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.user.AddContributorInteractor;
import com.shootr.mobile.domain.interactor.user.FindContributorsInteractor;
import com.shootr.mobile.domain.interactor.user.RemoveContributorInteractor;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.model.mappers.UserModelMapper;
import com.shootr.mobile.ui.views.FindContributorsView;
import com.shootr.mobile.util.ErrorMessageFactory;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class FindContributorsPresenter implements Presenter {

    private final FindContributorsInteractor findContributorsInteractor;
    private final AddContributorInteractor addContributorInteractor;
    private final RemoveContributorInteractor removeContributorInteractor;
    private final UserModelMapper userModelMapper;
    private final ErrorMessageFactory errorMessageFactory;

    private FindContributorsView findContributorsView;
    private List<UserModel> contributors;
    private String query;
    private String idStream;
    private Integer currentPage;

    @Inject public FindContributorsPresenter(FindContributorsInteractor findContributorsInteractor,
      AddContributorInteractor addContributorInteractor, RemoveContributorInteractor removeContributorInteractor,
      UserModelMapper userModelMapper, ErrorMessageFactory errorMessageFactory) {
        this.findContributorsInteractor = findContributorsInteractor;
        this.addContributorInteractor = addContributorInteractor;
        this.removeContributorInteractor = removeContributorInteractor;
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
        addContributorInteractor.addContributor(idStream, userModel.getIdUser(), new Interactor.CompletedCallback() {
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
        removeContributorInteractor.removeContributor(idStream,
          userModel.getIdUser(),
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

    }

    @Override public void pause() {

    }
}
