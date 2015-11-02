package com.shootr.android.ui.presenter;

import com.shootr.android.domain.User;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.shot.DeleteShotInteractor;
import com.shootr.android.domain.interactor.user.BlockUserInteractor;
import com.shootr.android.domain.interactor.user.GetBlockedIdUsersInteractor;
import com.shootr.android.domain.interactor.user.GetFollowingInteractor;
import com.shootr.android.domain.interactor.user.UnblockUserInteractor;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.ui.model.UserModel;
import com.shootr.android.ui.model.mappers.UserModelMapper;
import com.shootr.android.ui.views.ReportShotView;
import com.shootr.android.util.ErrorMessageFactory;
import java.util.List;
import javax.inject.Inject;

public class ReportShotPresenter implements Presenter {

    private final DeleteShotInteractor deleteShotInteractor;
    private final ErrorMessageFactory errorMessageFactory;
    private final SessionRepository sessionRepository;
    private final UserModelMapper userModelMapper;
    private final GetBlockedIdUsersInteractor getBlockedIdUsersInteractor;
    private final BlockUserInteractor blockUserInteractor;
    private final UnblockUserInteractor unblockUserInteractor;
    private final GetFollowingInteractor getFollowingInteractor;

    private ReportShotView reportShotView;
    private String idUserToBlock;

    @Inject public ReportShotPresenter(DeleteShotInteractor deleteShotInteractor,
      ErrorMessageFactory errorMessageFactory, SessionRepository sessionRepository, UserModelMapper userModelMapper,
      GetBlockedIdUsersInteractor getBlockedIdUsersInteractor, BlockUserInteractor blockUserInteractor,
      UnblockUserInteractor unblockUserInteractor, GetFollowingInteractor getFollowingInteractor) {
        this.deleteShotInteractor = deleteShotInteractor;
        this.errorMessageFactory = errorMessageFactory;
        this.sessionRepository = sessionRepository;
        this.userModelMapper = userModelMapper;
        this.getBlockedIdUsersInteractor = getBlockedIdUsersInteractor;
        this.blockUserInteractor = blockUserInteractor;
        this.unblockUserInteractor = unblockUserInteractor;
        this.getFollowingInteractor = getFollowingInteractor;
    }

    protected void setView(ReportShotView reportShotView) {
        this.reportShotView = reportShotView;
    }

    protected void setIdUserToBlock(ShotModel shotModel) {
        this.idUserToBlock = shotModel.getIdUser();
    }

    public void initialize(ReportShotView reportShotView) {
        setView(reportShotView);
    }

    public void report(ShotModel shotModel) {
        UserModel userModel = userModelMapper.transform(sessionRepository.getCurrentUser());
        if (userModel.isEmailConfirmed()) {
            reportShotView.goToReport(sessionRepository.getSessionToken(), shotModel);
        } else {
            reportShotView.showEmailNotConfirmedError();
        }
    }

    public void onShotLongPressed(final ShotModel shotModel) {
        if (sessionRepository.getCurrentUserId().equals(shotModel.getIdUser())) {
            reportShotView.showHolderContextMenu(shotModel);
        } else {
            getBlockedIdUsersInteractor.loadBlockedIdUsers(new Interactor.Callback<List<String>>() {
                @Override public void onLoaded(List<String> blockedIds) {
                    if (blockedIds.contains(shotModel.getIdUser())) {
                        reportShotView.showContextMenuWithUnblock(shotModel);
                    } else {
                        reportShotView.showContextMenu(shotModel);
                    }
                }
            }, new Interactor.ErrorCallback() {
                @Override public void onError(ShootrException error) {
                    showErrorInView(error);
                }
            });
        }

    }

    public void deleteShot(final ShotModel shotModel) {
        deleteShotInteractor.deleteShot(shotModel.getIdShot(), new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
                reportShotView.notifyDeletedShot(shotModel);
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                String errorMessage = errorMessageFactory.getMessageForError(error);
                reportShotView.showError(errorMessage);
            }
        });
    }

    public void blockUserClicked(final ShotModel shotModel) {
        setIdUserToBlock(shotModel);
        reportShotView.showBlockUserConfirmation();
    }

    public void blockUser(String idUser) {
        blockUserInteractor.block(idUser, new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
                reportShotView.showUserBlocked();
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                showErrorInView(error);
            }
        });
    }

    public void unblockUser(ShotModel shotModel) {
        unblockUserInteractor.unblock(shotModel.getIdUser(), new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
                reportShotView.showUserUnblocked();
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                showErrorInView(error);
            }
        });
    }

    public void confirmBlock() {
        getFollowingInteractor.obtainPeople(new Interactor.Callback<List<User>>() {
            @Override public void onLoaded(List<User> users) {
                handleUserBlocking(users, idUserToBlock);
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                showErrorInView(error);
            }
        });
    }

    private void handleUserBlocking(List<User> users, String idUserToBlock) {
        Boolean following = false;
        for (User user : users) {
            if (user.getIdUser().equals(idUserToBlock)) {
                following = true;
                break;
            }
        }
        if (!following) {
            blockUser(idUserToBlock);
        } else {
            reportShotView.showBlockUserAlert();
        }
    }

    private void showErrorInView(ShootrException error) {
        reportShotView.showError(errorMessageFactory.getMessageForError(error));
    }

    @Override public void resume() {
        /* no-op */
    }

    @Override public void pause() {
        /* no-op */
    }
}
