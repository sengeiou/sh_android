package com.shootr.android.ui.presenter;

import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.ui.views.ShotDetailView;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
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
        shotDetailView.renderReplies(mockReplies());
    }

    private List<ShotModel> mockReplies() {
        return Arrays.asList(
          mockReply(),
          mockReply(),
          mockReply(),
          mockReply()
        );
    }

    private ShotModel mockReply() {
        ShotModel shotModel = new ShotModel();
        shotModel.setUsername("usuario");
        shotModel.setComment("Hola, esto es un reply");
        shotModel.setPhoto("http://i.ytimg.com/vi/mSFTRoBY99s/hqdefault.jpg");
        shotModel.setCsysBirth(new Date());
        return shotModel;
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
