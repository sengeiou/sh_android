package com.shootr.android.ui.presenter;

import com.shootr.android.domain.Shot;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.shot.GetRepliesFromShotInteractor;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.ui.model.mappers.ShotModelMapper;
import com.shootr.android.ui.views.ShotDetailView;
import java.util.List;
import javax.inject.Inject;

public class ShotDetailPresenter implements Presenter {

    private final GetRepliesFromShotInteractor getRepliesFromShotInteractor;
    private final ShotModelMapper shotModelMapper;

    private ShotDetailView shotDetailView;
    private ShotModel shotModel;

    @Inject public ShotDetailPresenter(GetRepliesFromShotInteractor getRepliesFromShotInteractor,
      ShotModelMapper shotModelMapper) {
        this.getRepliesFromShotInteractor = getRepliesFromShotInteractor;
        this.shotModelMapper = shotModelMapper;
    }

    public void initialize(ShotDetailView shotDetailView, ShotModel shotModel) {
        this.shotDetailView = shotDetailView;
        this.shotModel = shotModel;
        this.setViewContent(shotModel);
        this.loadReplies();
    }

    private void loadReplies() {
        getRepliesFromShotInteractor.loadReplies(shotModel.getIdShot(), new Interactor.Callback<List<Shot>>() {
            @Override public void onLoaded(List<Shot> replies) {
                List<ShotModel> replyModels = shotModelMapper.transform(replies);
                shotDetailView.renderReplies(replyModels);
            }
        });
    }

    private void setViewContent(ShotModel shotModel) {
        shotDetailView.renderShot(shotModel);
        shotDetailView.setReplyUsername(shotModel.getUsername());
    }

    public void imageClick(ShotModel shot) {
        shotDetailView.openImage(shot.getImage());
    }

    public void avatarClick(Long userId) {
        shotDetailView.openProfile(userId);
    }

    @Override public void resume() {

    }

    @Override public void pause() {

    }
}
