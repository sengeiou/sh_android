package com.shootr.android.ui.presenter;

import com.shootr.android.domain.Shot;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.shot.GetAllShotsByUserInteractor;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.ui.model.mappers.ShotModelMapper;
import com.shootr.android.ui.views.AllShotsView;
import com.shootr.android.util.ErrorMessageFactory;
import java.util.List;
import javax.inject.Inject;

public class AllShotsPresenter implements Presenter {

    private final GetAllShotsByUserInteractor getAllShotsByUserInteractor;
    private final ErrorMessageFactory errorMessageFactory;
    private final ShotModelMapper shotModelMapper;

    private AllShotsView allShotsView;
    private String userId;

    @Inject public AllShotsPresenter(GetAllShotsByUserInteractor getAllShotsByUserInteractor,
      ErrorMessageFactory errorMessageFactory, ShotModelMapper shotModelMapper) {
        this.getAllShotsByUserInteractor = getAllShotsByUserInteractor;
        this.errorMessageFactory = errorMessageFactory;
        this.shotModelMapper = shotModelMapper;
    }

    public void initialize(final AllShotsView allShotsView, String userId) {
        setView(allShotsView);
        this.userId = userId;
        allShotsView.showLoading();
        loadAllShots(allShotsView, userId);
    }

    private void loadAllShots(final AllShotsView allShotsView, String userId) {
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
                allShotsView.showError(errorMessageFactory.getMessageForError(error));
            }
        });
    }

    private void setView(AllShotsView allShotsView) {
        this.allShotsView = allShotsView;
    }

    @Override public void resume() {
        /* no-op */
    }

    @Override public void pause() {
        /* no-op */
    }
}
