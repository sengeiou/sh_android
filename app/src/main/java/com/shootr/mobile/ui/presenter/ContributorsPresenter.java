package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.user.FollowInteractor;
import com.shootr.mobile.domain.interactor.user.UnfollowInteractor;
import com.shootr.mobile.domain.interactor.user.contributor.GetContributorsInteractor;
import com.shootr.mobile.domain.interactor.user.contributor.ManageContributorsInteractor;
import com.shootr.mobile.domain.model.user.Contributor;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.model.mappers.UserModelMapper;
import com.shootr.mobile.ui.views.ContributorsView;
import com.shootr.mobile.util.ErrorMessageFactory;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class ContributorsPresenter implements Presenter {

    public static final int CONTRIBUTORS_LIMIT = 100;
    private final GetContributorsInteractor getContributorsInteractor;
    private final ErrorMessageFactory errorMessageFactory;
    private final UserModelMapper userModelMapper;
    private final ManageContributorsInteractor manageContributorsInteractor;
    private final FollowInteractor followInteractor;
    private final UnfollowInteractor unfollowInteractor;

    private Boolean hasBeenPaused = false;
    private ContributorsView view;
    private String idStream;
    private Boolean isHolder;

    @Inject public ContributorsPresenter(GetContributorsInteractor getContributorsInteractor,
        ManageContributorsInteractor manageContributorsInteractor, ErrorMessageFactory errorMessageFactory,
        UserModelMapper userModelMapper, FollowInteractor followInteractor,
        UnfollowInteractor unfollowInteractor) {
        this.getContributorsInteractor = getContributorsInteractor;
        this.manageContributorsInteractor = manageContributorsInteractor;
        this.errorMessageFactory = errorMessageFactory;
        this.userModelMapper = userModelMapper;
        this.followInteractor = followInteractor;
        this.unfollowInteractor = unfollowInteractor;
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

    public void handleAddContributorsVisibility() {
        if (!isHolder) {
            view.hideAddContributorsButton();
            view.hideAddContributorsText();
        }
    }

    private void loadContributors() {
        view.hideEmpty();
        view.showLoading();
        getContributorsInteractor.obtainContributors(idStream, true, new Interactor.Callback<List<Contributor>>() {
            @Override public void onLoaded(List<Contributor> contributors) {
                view.hideLoading();
                view.showAllContributors();
                List<UserModel> contributorModels = new ArrayList<>(contributors.size());
                for (Contributor contributor : contributors) {
                    contributorModels.add(userModelMapper.transform(contributor.getUser()));
                }
                renderContributors(contributorModels);
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                view.showError(errorMessageFactory.getMessageForError(error));
                view.hideLoading();
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
        if (countContributors >= CONTRIBUTORS_LIMIT) {
            view.showContributorsLimitSnackbar();
        } else {
            view.goToSearchContributors();
        }
    }

    public void addContributor(final UserModel userModel) {
        manageContributorsInteractor.manageContributor(idStream,
          userModel.getIdUser(),
          true,
          new Interactor.CompletedCallback() {
              @Override public void onCompleted() {
                  view.removeContributorFromList(userModel);
              }
          },
          new Interactor.ErrorCallback() {
              @Override public void onError(ShootrException error) {
                  view.showError(errorMessageFactory.getMessageForError(error));
              }
          });
    }

    public void onLongContributorClick(UserModel userModel) {
        if (isHolder) {
            view.showContextMenu(userModel);
        }
    }

    public void removeContributor(final UserModel userModel) {
        manageContributorsInteractor.manageContributor(idStream,
          userModel.getIdUser(),
          false,
          new Interactor.CompletedCallback() {
              @Override public void onCompleted() {
                  view.removeContributorFromList(userModel);
              }
          },
          new Interactor.ErrorCallback() {
              @Override public void onError(ShootrException error) {
                  view.showError(errorMessageFactory.getMessageForError(error));
              }
          });
    }

    public void followContributor(final UserModel userModel) {
        followInteractor.follow(userModel.getIdUser(), new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
                view.renderFollow(userModel);
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                view.showError(errorMessageFactory.getMessageForError(error));
            }
        });
    }

    public void unfollowContributor(final UserModel userModel) {
        unfollowInteractor.unfollow(userModel.getIdUser(), new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
                view.renderUnfollow(userModel);
            }
        });
    }

    @Override public void resume() {
        if (hasBeenPaused) {
            loadContributors();
        }
    }

    @Override public void pause() {
        hasBeenPaused = true;
    }
}
