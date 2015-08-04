package com.shootr.android.ui.presenter;

import com.shootr.android.domain.Shot;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.shot.GetAllShotsByUserInteractor;
import com.shootr.android.domain.interactor.shot.GetOlderAllShotsByUserInteractor;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.ui.model.mappers.ShotModelMapper;
import com.shootr.android.ui.views.AllShotsView;
import com.shootr.android.util.ErrorMessageFactory;
import java.util.List;
import javax.inject.Inject;

public class AllShotsPresenter implements Presenter {

    private final GetAllShotsByUserInteractor getAllShotsByUserInteractor;
    private final GetOlderAllShotsByUserInteractor getOlderAllShotsByUserInteractor;
    private final ErrorMessageFactory errorMessageFactory;
    private final ShotModelMapper shotModelMapper;

    private AllShotsView allShotsView;
    private String userId;
    private boolean isLoadingOlderShots;
    private boolean mightHaveMoreShots = true;

    @Inject public AllShotsPresenter(GetAllShotsByUserInteractor getAllShotsByUserInteractor,
      GetOlderAllShotsByUserInteractor getOlderAllShotsByUserInteractor, ErrorMessageFactory errorMessageFactory,
      ShotModelMapper shotModelMapper) {
        this.getAllShotsByUserInteractor = getAllShotsByUserInteractor;
        this.getOlderAllShotsByUserInteractor = getOlderAllShotsByUserInteractor;
        this.errorMessageFactory = errorMessageFactory;
        this.shotModelMapper = shotModelMapper;
    }

    public void initialize(AllShotsView allShotsView, String userId) {
        setView(allShotsView);
        setUserId(userId);
        loadAllShots(allShotsView, userId);
    }

    protected void setUserId(String userId) {
        this.userId = userId;
    }

    protected void setView(AllShotsView allShotsView) {
        this.allShotsView = allShotsView;
    }

    private void loadAllShots(final AllShotsView allShotsView, String userId) {
        allShotsView.showLoading();
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

    @Override public void resume() {
        /* no-op */
    }

    @Override public void pause() {
        /* no-op */
    }
}
