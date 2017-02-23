package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.shot.DeleteShotInteractor;
import com.shootr.mobile.domain.interactor.shot.GetLocalHighlightedShotInteractor;
import com.shootr.mobile.domain.interactor.stream.GetLocalStreamInteractor;
import com.shootr.mobile.domain.interactor.user.BanUserInteractor;
import com.shootr.mobile.domain.interactor.user.BlockUserInteractor;
import com.shootr.mobile.domain.interactor.user.GetBlockedIdUsersInteractor;
import com.shootr.mobile.domain.interactor.user.GetFollowingInteractor;
import com.shootr.mobile.domain.interactor.user.UnbanUserInteractor;
import com.shootr.mobile.domain.interactor.user.UnblockUserInteractor;
import com.shootr.mobile.domain.model.shot.HighlightedShot;
import com.shootr.mobile.domain.model.stream.Stream;
import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.model.mappers.UserModelMapper;
import com.shootr.mobile.ui.views.ReportShotView;
import com.shootr.mobile.util.ErrorMessageFactory;
import java.util.List;
import javax.inject.Inject;

public class ReportShotPresenter implements Presenter {

    private static final String EN_LOCALE = "en";
    private final DeleteShotInteractor deleteShotInteractor;
    private final ErrorMessageFactory errorMessageFactory;
    private final SessionRepository sessionRepository;
    private final UserModelMapper userModelMapper;
    private final GetBlockedIdUsersInteractor getBlockedIdUsersInteractor;
    private final BlockUserInteractor blockUserInteractor;
    private final UnblockUserInteractor unblockUserInteractor;
    private final GetFollowingInteractor getFollowingInteractor;
    private final BanUserInteractor banUserInteractor;
    private final UnbanUserInteractor unbanUserInteractor;
    private final GetLocalHighlightedShotInteractor getHighlightedShotInteractor;
    private final GetLocalStreamInteractor getLocalStreamInteractor;

    private ReportShotView reportShotView;
    private String idUserToBlock;
    private String idStream;
    private HighlightedShot currentHighlightedShot;
    private boolean isCurrentUserContributor;

    @Inject
    public ReportShotPresenter(DeleteShotInteractor deleteShotInteractor, ErrorMessageFactory errorMessageFactory,
        SessionRepository sessionRepository, UserModelMapper userModelMapper,
        GetBlockedIdUsersInteractor getBlockedIdUsersInteractor, BlockUserInteractor blockUserInteractor,
        UnblockUserInteractor unblockUserInteractor, GetFollowingInteractor getFollowingInteractor,
        BanUserInteractor banUserInteractor, UnbanUserInteractor unbanUserInteractor,
        GetLocalHighlightedShotInteractor getHighlightedShotInteractor,
        GetLocalStreamInteractor getLocalStreamInteractor) {
        this.deleteShotInteractor = deleteShotInteractor;
        this.errorMessageFactory = errorMessageFactory;
        this.sessionRepository = sessionRepository;
        this.userModelMapper = userModelMapper;
        this.getBlockedIdUsersInteractor = getBlockedIdUsersInteractor;
        this.blockUserInteractor = blockUserInteractor;
        this.unblockUserInteractor = unblockUserInteractor;
        this.getFollowingInteractor = getFollowingInteractor;
        this.banUserInteractor = banUserInteractor;
        this.unbanUserInteractor = unbanUserInteractor;
        this.getHighlightedShotInteractor = getHighlightedShotInteractor;
        this.getLocalStreamInteractor = getLocalStreamInteractor;
    }

    protected void setView(ReportShotView reportShotView) {
        this.reportShotView = reportShotView;
    }

    protected void setIdUserToBlock(String idUser) {
        this.idUserToBlock = idUser;
    }

    public void initialize(ReportShotView reportShotView) {
        setView(reportShotView);
    }

    public void initializeWithIdStream(ReportShotView reportShotView, String streamId) {
        setView(reportShotView);
        this.idStream = streamId;
        getContributorsIds();
    }

    private void getContributorsIds() {
        getLocalStreamInteractor.loadStream(idStream, new GetLocalStreamInteractor.Callback() {
            @Override public void onLoaded(Stream stream) {
                isCurrentUserContributor = stream.isCurrentUserContributor();
            }
        });
    }

    public void report(ShotModel shotModel) {
        UserModel userModel = userModelMapper.transform(sessionRepository.getCurrentUser());
        if (userModel.isEmailConfirmed()) {
            reportShotView.handleReport(sessionRepository.getSessionToken(), shotModel);
        } else {
            reportShotView.showEmailNotConfirmedError();
        }
    }

    public boolean isEnglishLocale(String locale) {
        return locale.equals(EN_LOCALE);
    }

    public void onShotLongPressed(final ShotModel shotModel) {
        if (currentUserIsShotAuthor(shotModel)) {
            showAuthorContextMenu(shotModel);
        } else {
            handleBlockContextMenu(shotModel);
        }
    }

    public void onShotLongPressedWithStreamAuthor(final ShotModel shot, final String streamAuthorIdUser) {
        getHighlightedShotInteractor.loadHighlightedShot(idStream, new Interactor.Callback<HighlightedShot>() {
            @Override public void onLoaded(HighlightedShot highlightedShot) {
                currentHighlightedShot = highlightedShot;
                if (currentUserIsStreamHolder(streamAuthorIdUser) || currentUserIsStreamContributor()) {
                    handlePinContextMenu(shot, streamAuthorIdUser);
                } else {
                    onShotLongPressed(shot);
                }
            }
        });
    }

    public void handlePinContextMenu(ShotModel shot, String streamAuthorId) {
        if (currentUserIsShotAuthor(shot) && currentUserIsStreamHolder(streamAuthorId)) {
            showAuthorContextMenuWithHighlight(shot);
        } else if (currentUserIsShotAuthor(shot)) {
            showAuthorContextMenu(shot);
        } else if (currentUserIsStreamContributor()) {
            showContributorContextMenu(shot);
        } else {
            if (shotIsHighlighted(shot)) {
                reportShotView.showHolderContextMenuWithDismissHighlight(shot);
            } else {
                reportShotView.showHolderContextMenu(shot);
            }
        }
    }

     private void showContributorContextMenu(ShotModel shot) {
         if (currentUserIsShotAuthor(shot)) {
             handleContributorPinShotVisibility(shot);
         } else {
             if (shotIsHighlighted(shot)) {
                 reportShotView.showContributorContextMenuWithDismissHighlight(shot);
             } else {
                 reportShotView.showContributorContextMenu(shot);
             }
         }
     }

    private void handleContributorPinShotVisibility(ShotModel shot) {
        if (isShotVisible(shot)) {
            if (shotIsHighlighted(shot)) {
                reportShotView.showContributorContextMenuWithPinAndDismissHighlight(shot);
            } else {
                reportShotView.showContributorContextMenuWithPinAndHighlight(shot);
            }
        } else {
            if (shotIsHighlighted(shot)) {
                reportShotView.showContributorContextMenuWithoutPinAndDismissHighlight(shot);
            } else {
                reportShotView.showContributorContextMenuWithoutPinAndHighlight(shot);
            }
        }
    }

    private void showAuthorContextMenuWithHighlight(ShotModel shot) {
        if (isShotVisible(shot)) {
            if (shotIsHighlighted(shot)) {
                reportShotView.showAuthorContextMenuWithPinAndDismissHighlight(shot);
            } else {
                reportShotView.showAuthorContextMenuWithPinAndHighlight(shot);
            }
        } else {
            if (shotIsHighlighted(shot)) {
                reportShotView.showAuthorContextMenuWithoutPinAndDismissHighlight(shot);
            } else {
                reportShotView.showAuthorContextMenuWithoutPinAndHighlight(shot);
            }
        }
    }

    public void showAuthorContextMenu(ShotModel shot) {
        if (isShotVisible(shot)) {
            reportShotView.showAuthorContextMenuWithPin(shot);
        } else {
            reportShotView.showAuthorContextMenuWithoutPin(shot);
        }
    }

    public boolean isShotVisible(ShotModel shot) {
        return shot.getHide() != null && shot.getHide() != 0L;
    }

    private boolean currentUserIsStreamContributor() {
        return isCurrentUserContributor;
    }

    public boolean currentUserIsShotAuthor(ShotModel shotModel) {
        return sessionRepository.getCurrentUserId().equals(shotModel.getIdUser());
    }

    public boolean currentUserIsStreamHolder(String streamAuthorIdUser) {
        return sessionRepository.getCurrentUserId().equals(streamAuthorIdUser);
    }

    private boolean shotIsHighlighted(ShotModel shot) {
        return currentHighlightedShot != null && currentHighlightedShot.getShot().getIdShot().equals(shot.getIdShot());
    }

    public void handleBlockContextMenu(final ShotModel shotModel) {
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

    public void blockUserClicked(ShotModel shotModel) {
        setIdUserToBlock(shotModel.getIdUser());
        checkIfUserCanBeBlocked();
    }

    public void blockUserClicked(UserModel userModel) {
        setIdUserToBlock(userModel.getIdUser());
        checkIfUserCanBeBlocked();
    }

    public void checkIfUserCanBeBlocked() {
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
                reportShotView.showErrorLong(errorMessageFactory.getMessageForError(error));
            }
        });
    }

    public void unblockUserClicked(UserModel userModel) {
        unblockUserInteractor.unblock(userModel.getIdUser(), new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
                reportShotView.showUserUnblocked();
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                reportShotView.showErrorLong(errorMessageFactory.getMessageForError(error));
            }
        });
    }

    public void confirmBlock() {
        blockUser(idUserToBlock);
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
            reportShotView.showBlockUserConfirmation();
        } else {
            reportShotView.showBlockFollowingUserAlert();
        }
    }

    private void showErrorInView(ShootrException error) {
        reportShotView.showError(errorMessageFactory.getMessageForError(error));
    }

    public void confirmBan(UserModel userModel) {
        banUserInteractor.ban(userModel.getIdUser(), new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
                reportShotView.showUserBanned();
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                showErrorInView(error);
            }
        });
    }

    public void confirmUnBan(UserModel userModel) {
        unbanUserInteractor.unban(userModel.getIdUser(), new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
                reportShotView.showUserUnbanned();
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                reportShotView.showErrorLong(errorMessageFactory.getMessageForError(error));
            }
        });
    }

    public void reportClicked(String language, String sessionToken, ShotModel shotModel) {
        if (isEnglishLocale(language)) {
            reportShotView.goToReport(sessionToken, shotModel);
        } else {
            reportShotView.showAlertLanguageSupportDialog(sessionToken, shotModel);
        }
    }

    @Override public void resume() {
        /* no-op */
    }

    @Override public void pause() {
        /* no-op */
    }
}
