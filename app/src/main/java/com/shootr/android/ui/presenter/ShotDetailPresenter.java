package com.shootr.android.ui.presenter;

import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.ui.views.ShotDetailView;
import javax.inject.Inject;

public class ShotDetailPresenter implements Presenter {

    private ShotDetailView shotDetailView;
    private ShotModel shotModel;

    @Inject public ShotDetailPresenter() {
    }

    public void initialize(ShotDetailView shotDetailView, ShotModel shotModel) {
        this.shotDetailView = shotDetailView;
        this.shotModel = shotModel;
        this.setViewContent(shotModel);
    }

    private void setViewContent(ShotModel shotModel) {
        shotDetailView.renderShot(shotModel);
        shotDetailView.setReplyUsername(shotModel.getUsername());
    }

    public void imageClick() {
        shotDetailView.openImage(shotModel.getImage());
    }

    public void avatarClick() {
        shotDetailView.openProfile(shotModel.getIdUser());
    }

    @Override public void resume() {

    }

    @Override public void pause() {

    }
}
