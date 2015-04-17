package com.shootr.android.ui.presenter;

import com.shootr.android.data.bus.Main;
import com.shootr.android.domain.Shot;
import com.shootr.android.domain.bus.ShotSent;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.shot.GetRepliesFromShotInteractor;
import com.shootr.android.domain.interactor.shot.GetReplyParentInteractor;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.ui.model.mappers.ShotModelMapper;
import com.shootr.android.ui.views.ShotDetailView;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import java.util.List;
import javax.inject.Inject;

public class ShotDetailPresenter implements Presenter, ShotSent.Receiver {

    private final GetRepliesFromShotInteractor getRepliesFromShotInteractor;
    private final GetReplyParentInteractor getReplyParentInteractor;
    private final ShotModelMapper shotModelMapper;
    private final Bus bus;

    private ShotDetailView shotDetailView;
    private ShotModel shotModel;
    private List<ShotModel> repliesModels;
    private boolean justSentReply = false;

    @Inject
    public ShotDetailPresenter(GetRepliesFromShotInteractor getRepliesFromShotInteractor,
      GetReplyParentInteractor getReplyParentInteractor, ShotModelMapper shotModelMapper, @Main Bus bus) {
        this.getRepliesFromShotInteractor = getRepliesFromShotInteractor;
        this.getReplyParentInteractor = getReplyParentInteractor;
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
                int previousReplyCount = repliesModels != null ? repliesModels.size() : 0;
                int newReplyCount = replies.size();
                repliesModels = shotModelMapper.transform(replies);
                shotDetailView.renderReplies(repliesModels);
                if (justSentReply && previousReplyCount < newReplyCount) {
                    shotDetailView.scrollToBottom();
                    justSentReply = false;
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
        getReplyParentInteractor.loadReplyParent(shotModel.getParentShotId(), new Interactor.Callback<Shot>() {
            @Override public void onLoaded(Shot shot) {
                if (shot != null) {
                    shotDetailView.renderParent(shotModelMapper.transform(shot));
                } else {
                    //TODO
                }
            }
        });
    }

    public void imageClick(ShotModel shot) {
        shotDetailView.openImage(shot.getImage());
    }

    public void avatarClick(Long userId) {
        shotDetailView.openProfile(userId);
    }

    @Subscribe @Override public void onShotSent(ShotSent.Event event) {
        justSentReply = true;
        this.loadReplies();
    }

    @Override public void resume() {
        bus.register(this);
    }

    @Override public void pause() {
        bus.unregister(this);
    }
}
