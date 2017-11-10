package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.user.FollowInteractor;
import com.shootr.mobile.domain.interactor.user.GetNicersInteractor;
import com.shootr.mobile.domain.interactor.user.UnfollowInteractor;
import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.model.mappers.UserModelMapper;
import com.shootr.mobile.ui.views.NicersView;
import com.shootr.mobile.util.ErrorMessageFactory;
import java.util.List;
import javax.inject.Inject;

public class NicersPresenter implements Presenter {

    private final GetNicersInteractor getNicersInteractor;
    private final FollowInteractor followInteractor;
    private final UnfollowInteractor unfollowInteractor;
    private final ErrorMessageFactory errorMessageFactory;
    private final UserModelMapper userModelMapper;

    private NicersView nicersView;
    private List<UserModel> nicers;
    private String idShot;
    private Boolean hasBeenPaused = false;

    @Inject public NicersPresenter(GetNicersInteractor getNicersInteractor, FollowInteractor followInteractor,
      UnfollowInteractor unfollowInteractor, ErrorMessageFactory errorMessageFactory, UserModelMapper userModelMapper) {
        this.getNicersInteractor = getNicersInteractor;
        this.followInteractor = followInteractor;
        this.unfollowInteractor = unfollowInteractor;
        this.errorMessageFactory = errorMessageFactory;
        this.userModelMapper = userModelMapper;
    }

    protected void setView(NicersView nicersView) {
        this.nicersView = nicersView;
    }

    public void initialize(NicersView nicersView, String idShot) {
        this.setView(nicersView);
        this.idShot = idShot;
        this.loadNicers();
    }

    private void loadNicers() {
        nicersView.hideEmpty();
        nicersView.showLoading();
        getNicersInteractor.obtainNicersWithUser(idShot, new Interactor.Callback<List<User>>() {
            @Override public void onLoaded(List<User> users) {
                nicersView.hideLoading();
                nicersView.showNicersList();
                renderNicers(users);
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                nicersView.showError(errorMessageFactory.getMessageForError(error));
            }
        });
    }

    private void renderNicers(List<User> users) {
        List<UserModel> userModels = userModelMapper.transform(users);
        nicers = userModels;
        if (!nicers.isEmpty()) {
            nicersView.renderNicers(userModels);
        } else {
            nicersView.showEmpty();
        }
    }

    public void followUser(final UserModel userModel) {
        followInteractor.follow(userModel.getIdUser(), new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
                refreshNicersFollowings(userModel.getIdUser(), true);
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                showErrorInView(error);
            }
        });
    }

    private void showErrorInView(ShootrException error) {
        nicersView.showError(errorMessageFactory.getMessageForError(error));
    }

    public void unfollowUser(final UserModel userModel) {
        unfollowInteractor.unfollow(userModel.getIdUser(), new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
                refreshNicersFollowings(userModel.getIdUser(), false);
            }
        });
    }

    private void refreshNicersFollowings(String idUser, boolean following) {
        for (UserModel userModel : nicers) {
            if (userModel.getIdUser().equals(idUser)) {
                userModel.setFollowing(following);
                nicersView.renderNicers(nicers);
                break;
            }
        }
    }

    @Override public void resume() {
        if (hasBeenPaused) {
            loadNicers();
        }
    }

    @Override public void pause() {
        this.hasBeenPaused = true;
    }
}
