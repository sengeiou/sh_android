package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.User;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.user.AddContributorInteractor;
import com.shootr.mobile.domain.interactor.user.GetContributorsInteractor;
import com.shootr.mobile.domain.interactor.user.RemoveContributorInteractor;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.model.mappers.UserModelMapper;
import com.shootr.mobile.ui.views.ContributorsView;
import com.shootr.mobile.util.ErrorMessageFactory;
import java.util.List;
import javax.inject.Inject;

public class ContributorsPresenter implements Presenter {

    public static final int CONTRIBUTORS_LIMIT = 100;
    private final GetContributorsInteractor getContributorsInteractor;
    private final ErrorMessageFactory errorMessageFactory;
    private final UserModelMapper userModelMapper;
    private final AddContributorInteractor addContributorInteractor;
    private final RemoveContributorInteractor removeContributorInteractor;

    private ContributorsView view;
    private String idStream;
    private Boolean isHolder;

    @Inject public ContributorsPresenter(GetContributorsInteractor getContributorsInteractor,
      AddContributorInteractor addContributorInteractor, RemoveContributorInteractor removeContributorInteractor,
      ErrorMessageFactory errorMessageFactory, UserModelMapper userModelMapper) {
        this.getContributorsInteractor = getContributorsInteractor;
        this.addContributorInteractor = addContributorInteractor;
        this.removeContributorInteractor = removeContributorInteractor;
        this.errorMessageFactory = errorMessageFactory;
        this.userModelMapper = userModelMapper;
    }

    protected void setView(ContributorsView contributorsView) {
        this.view = contributorsView;
    }

    protected void setIdStream(String idStream) {
        this.idStream = idStream;
    }

    private void setIsHolder(Boolean isHolder) {
        this.isHolder = isHolder;
    }

    public void initialize(ContributorsView contributorsView, String idStream, Boolean isHolder) {
        setView(contributorsView);
        setIdStream(idStream);
        setIsHolder(isHolder);
        handleAddContributorsVisibility();
        loadContributors();
    }

    public void handleAddContributorsVisibility(){
        if(!isHolder) {
            view.hideAddContributorsButton();
            view.hideAddContributorsText();
        }
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
        if(countContributors > CONTRIBUTORS_LIMIT){
            view.showContributorsLimitSnackbar();
        }else{
            view.goToSearchContributors();
        }
    }

    public void addContributor(UserModel userModel){
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

    public void removeContributor(UserModel userModel){
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
