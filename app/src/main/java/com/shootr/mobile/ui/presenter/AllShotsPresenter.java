package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.Shot;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.shot.GetAllShotsByUserInteractor;
import com.shootr.mobile.domain.interactor.shot.GetOlderAllShotsByUserInteractor;
import com.shootr.mobile.domain.interactor.shot.HideShotInteractor;
import com.shootr.mobile.domain.interactor.shot.MarkNiceShotInteractor;
import com.shootr.mobile.domain.interactor.shot.ShareShotInteractor;
import com.shootr.mobile.domain.interactor.shot.UnmarkNiceShotInteractor;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.ui.model.mappers.ShotModelMapper;
import com.shootr.mobile.ui.views.AllShotsView;
import com.shootr.mobile.util.ErrorMessageFactory;
import java.util.List;
import javax.inject.Inject;

public class AllShotsPresenter implements Presenter {

    private final GetAllShotsByUserInteractor getAllShotsByUserInteractor;
    private final GetOlderAllShotsByUserInteractor getOlderAllShotsByUserInteractor;
    private final MarkNiceShotInteractor markNiceShotInteractor;
    private final UnmarkNiceShotInteractor unmarkNiceShotInteractor;
    private final ShareShotInteractor shareShotInteractor;
    private final ErrorMessageFactory errorMessageFactory;
    private final ShotModelMapper shotModelMapper;
    private final HideShotInteractor hideShotInteractor;

    private AllShotsView allShotsView;
    private String userId;
    private boolean isLoadingOlderShots;
    private boolean mightHaveMoreShots = true;
    private boolean hasBeenPaused = false;
    private Boolean isCurrentUser=false;

    @Inject public AllShotsPresenter(GetAllShotsByUserInteractor getAllShotsByUserInteractor,
      GetOlderAllShotsByUserInteractor getOlderAllShotsByUserInteractor,MarkNiceShotInteractor markNiceShotInteractor, UnmarkNiceShotInteractor unmarkNiceShotInteractor, HideShotInteractor hideShotInteractor, ShareShotInteractor shareShotInteractor, ErrorMessageFactory errorMessageFactory,
      ShotModelMapper shotModelMapper) {
        this.getAllShotsByUserInteractor = getAllShotsByUserInteractor;
        this.getOlderAllShotsByUserInteractor = getOlderAllShotsByUserInteractor;
        this.markNiceShotInteractor = markNiceShotInteractor;
        this.unmarkNiceShotInteractor = unmarkNiceShotInteractor;
        this.hideShotInteractor= hideShotInteractor;
        this.shareShotInteractor = shareShotInteractor;
        this.errorMessageFactory = errorMessageFactory;
        this.shotModelMapper = shotModelMapper;
    }

    public void initialize(AllShotsView allShotsView, String userId, Boolean isCurrentUser) {
        this.setView(allShotsView);
        this.setUserId(userId);
        this.startLoadingAllShots();
        this.isCurrentUser=isCurrentUser;
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
        getAllShotsByUserInteractor.loadAllShots(userId, new Interactor.Callback<List<Shot>>() {
            @Override public void onLoaded(List<Shot> shots) {
                List<ShotModel> shotModels = shotModelMapper.transform(shots);
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

    public void showingLastShot(ShotModel lastShot) {
        if (!isLoadingOlderShots && mightHaveMoreShots) {
            this.loadOlderShots(lastShot.getBirth().getTime());
        }
    }

    protected void loadOlderShots(long lastShotInScreenDate) {
        isLoadingOlderShots = true;
        allShotsView.showLoadingOldShots();
        getOlderAllShotsByUserInteractor.loadAllShots(userId,
          lastShotInScreenDate,
          new Interactor.Callback<List<Shot>>() {
              @Override public void onLoaded(List<Shot> shots) {
                  isLoadingOlderShots = false;
                  List<ShotModel> shotModels = shotModelMapper.transform(shots);
                  if (!shotModels.isEmpty()) {
                      allShotsView.addOldShots(shotModels);
                  } else {
                      mightHaveMoreShots = false;
                  }
                  allShotsView.hideLoadingOldShots();
              }
          },
          new Interactor.ErrorCallback() {
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

    public void hideShot(String idShot){
        hideShotInteractor.hideShot(idShot, new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
                loadAllShots();
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

    public void shareShot(ShotModel shotModel) {
        shareShotInteractor.shareShot(shotModel.getIdShot(), new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
                allShotsView.showShotShared();
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
