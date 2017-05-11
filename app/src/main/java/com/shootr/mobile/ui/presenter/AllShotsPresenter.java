package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.shot.GetProfileShotTimelineInteractor;
import com.shootr.mobile.domain.interactor.shot.MarkNiceShotInteractor;
import com.shootr.mobile.domain.interactor.shot.ReshootInteractor;
import com.shootr.mobile.domain.interactor.shot.UndoReshootInteractor;
import com.shootr.mobile.domain.interactor.shot.UnmarkNiceShotInteractor;
import com.shootr.mobile.domain.model.shot.ProfileShotTimeline;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.ui.model.mappers.ShotModelMapper;
import com.shootr.mobile.ui.views.AllShotsView;
import com.shootr.mobile.util.ErrorMessageFactory;
import java.util.List;
import javax.inject.Inject;

public class AllShotsPresenter implements Presenter {

    private final GetProfileShotTimelineInteractor getProfileShotTimelineInteractor;
    private final MarkNiceShotInteractor markNiceShotInteractor;
    private final UnmarkNiceShotInteractor unmarkNiceShotInteractor;
    private final ReshootInteractor reshootInteractor;
    private final UndoReshootInteractor undoReshootInteractor;
    private final ErrorMessageFactory errorMessageFactory;
    private final ShotModelMapper shotModelMapper;

    private AllShotsView allShotsView;
    private String userId;
    private boolean isLoadingOlderShots;
    private boolean mightHaveMoreShots = true;
    private boolean hasBeenPaused = false;
    private Boolean isCurrentUser = false;
    private long maxTimestamp;

    @Inject
    public AllShotsPresenter(GetProfileShotTimelineInteractor getProfileShotTimelineInteractor,
        MarkNiceShotInteractor markNiceShotInteractor,
        UnmarkNiceShotInteractor unmarkNiceShotInteractor,
        UndoReshootInteractor undoReshootInteractor,
        ReshootInteractor reshootInteractor, ErrorMessageFactory errorMessageFactory,
        ShotModelMapper shotModelMapper) {
        this.getProfileShotTimelineInteractor = getProfileShotTimelineInteractor;
        this.markNiceShotInteractor = markNiceShotInteractor;
        this.unmarkNiceShotInteractor = unmarkNiceShotInteractor;
        this.undoReshootInteractor = undoReshootInteractor;
        this.reshootInteractor = reshootInteractor;
        this.errorMessageFactory = errorMessageFactory;
        this.shotModelMapper = shotModelMapper;
    }

    public void initialize(AllShotsView allShotsView, String userId, Boolean isCurrentUser) {
        this.setView(allShotsView);
        this.setUserId(userId);
        this.startLoadingAllShots();
        this.isCurrentUser = isCurrentUser;
    }

    protected void setUserId(String userId) {
        this.userId = userId;
    }

    protected void setView(AllShotsView allShotsView) {
        this.allShotsView = allShotsView;
    }

    private void startLoadingAllShots() {
        allShotsView.showLoading();
        loadAllShots();
    }

    private void loadAllShots() {
        getProfileShotTimelineInteractor.loadProfileShotTimeline(userId, null,
            new Interactor.Callback<ProfileShotTimeline>() {
                @Override public void onLoaded(ProfileShotTimeline profileShotTimeline) {
                    maxTimestamp = profileShotTimeline.getMaxTimestamp();
                    List<ShotModel> shotModels =
                        shotModelMapper.transform(profileShotTimeline.getShots());
                    allShotsView.hideLoading();
                    allShotsView.setShots(shotModels);
                    if (!shotModels.isEmpty()) {
                        allShotsView.hideEmpty();
                        allShotsView.showShots();
                    } else {
                        allShotsView.showEmpty();
                        allShotsView.hideShots();
                    }
                }
            }, new Interactor.ErrorCallback() {
                @Override public void onError(ShootrException error) {
                    allShotsView.hideLoading();
                    allShotsView.showError(errorMessageFactory.getMessageForError(error));
                }
            });
    }

    public void showingLastShot() {
        if (!isLoadingOlderShots && mightHaveMoreShots) {
            this.loadOlderShots();
        }
    }

    protected void loadOlderShots() {
        isLoadingOlderShots = true;
        allShotsView.showLoadingOldShots();

        getProfileShotTimelineInteractor.loadProfileShotTimeline(userId, maxTimestamp,
            new Interactor.Callback<ProfileShotTimeline>() {
                @Override public void onLoaded(ProfileShotTimeline profileShotTimeline) {
                    isLoadingOlderShots = false;
                    maxTimestamp = profileShotTimeline.getMaxTimestamp();
                    List<ShotModel> shotModels =
                        shotModelMapper.transform(profileShotTimeline.getShots());
                    if (!shotModels.isEmpty()) {
                        allShotsView.addOldShots(shotModels);
                    }
                    mightHaveMoreShots = maxTimestamp != 0L;
                    allShotsView.hideLoadingOldShots();
                }
            }, new Interactor.ErrorCallback() {
                @Override public void onError(ShootrException error) {
                    allShotsView.hideLoadingOldShots();
                    allShotsView.showError(errorMessageFactory.getCommunicationErrorMessage());
                }
            });
    }

    public void markNiceShot(String idShot) {
        markNiceShotInteractor.markNiceShot(idShot, new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
                loadAllShots();
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                /* no - op */
            }
        });
    }

    public void unmarkNiceShot(String idShot) {
        unmarkNiceShotInteractor.unmarkNiceShot(idShot, new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
                loadAllShots();
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                /* no - op */
            }
        });
    }

    @Override public void resume() {
        if (hasBeenPaused) {
            loadAllShots();
        }
    }

    @Override public void pause() {
        hasBeenPaused = true;
    }

    public void reshoot(final ShotModel shotModel) {
        reshootInteractor.reshoot(shotModel.getIdShot(), new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
                allShotsView.notifyReshot(shotModel.getIdShot(), true);
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                allShotsView.showError(errorMessageFactory.getMessageForError(error));
            }
        });
    }

    public void undoReshoot(final ShotModel shotModel) {
        undoReshootInteractor.undoReshoot(shotModel.getIdShot(), new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
                allShotsView.notifyReshot(shotModel.getIdShot(), false);
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                allShotsView.showError(errorMessageFactory.getMessageForError(error));
            }
        });
    }

    public Boolean getIsCurrentUser() {
        return isCurrentUser;
    }
}
