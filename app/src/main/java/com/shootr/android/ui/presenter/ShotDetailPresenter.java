package com.shootr.android.ui.presenter;

import com.shootr.android.data.bus.Main;
import com.shootr.android.domain.Shot;
import com.shootr.android.domain.bus.ShotSent;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.shot.GetRepliesFromShotInteractor;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.ui.model.mappers.ShotModelMapper;
import com.shootr.android.ui.views.ShotDetailView;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;

public class ShotDetailPresenter implements Presenter, ShotSent.Receiver {

    private final GetRepliesFromShotInteractor getRepliesFromShotInteractor;
    private final ShotModelMapper shotModelMapper;
    private final Bus bus;

    private ShotDetailView shotDetailView;
    private ShotModel shotModel;
    private List<ShotModel> repliesModels;

    @Inject
    public ShotDetailPresenter(GetRepliesFromShotInteractor getRepliesFromShotInteractor,
      ShotModelMapper shotModelMapper, @Main Bus bus) {
        this.getRepliesFromShotInteractor = getRepliesFromShotInteractor;
        this.shotModelMapper = shotModelMapper;
        this.bus = bus;
    }

    public void initialize(ShotDetailView shotDetailView, ShotModel shotModel) {
        this.shotDetailView = shotDetailView;
        this.shotModel = shotModel;
        this.loadShotDetail();
        this.loadReplies();
    }

    private void loadReplies() {
        getRepliesFromShotInteractor.loadReplies(shotModel.getIdShot(), new Interactor.Callback<List<Shot>>() {

            @Override public void onLoaded(List<Shot> replies) {
                int previousReplyCount = repliesModels != null ? repliesModels.size() : Integer.MAX_VALUE;
                int newReplyCount = replies.size();
                repliesModels = shotModelMapper.transform(replies);
                shotDetailView.renderReplies(repliesModels);
                if (previousReplyCount < newReplyCount) {
                    shotDetailView.scrollToBottom();
                }
            }
        });
    }

    private void loadShotDetail() {
        shotDetailView.renderShot(shotModel);
        shotDetailView.setReplyUsername(shotModel.getUsername());
        if (shotModel.isReply()) {
            this.loadParentShot();
        }
    }

    private void loadParentShot() {
        //TODO interactor
        shotDetailView.renderParent(mockParent());
    }

    private ShotModel mockParent() {
        ShotModel shotModel = new ShotModel();
        shotModel.setUsername("padre");
        shotModel.setComment("Hola soy shot padre");
        shotModel.setIdShot(0L);
        shotModel.setCsysBirth(new Date());
        shotModel.setPhoto("http://2.bp.blogspot.com/-qNmjJuVhLqQ/UNig55G5ZeI/AAAAAAAAGMY/MfhFCTOL044/s1600/obama+tweets+1.jpg");
        return shotModel;
    }

    public void imageClick(ShotModel shot) {
        shotDetailView.openImage(shot.getImage());
    }

    public void avatarClick(Long userId) {
        shotDetailView.openProfile(userId);
    }

    @Subscribe @Override public void onShotSent(ShotSent.Event event) {
        this.loadReplies();
    }

    @Override public void resume() {
        bus.register(this);
    }

    @Override public void pause() {
        bus.unregister(this);
    }
}
