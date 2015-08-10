package com.shootr.android.ui.presenter;

import com.shootr.android.data.bus.Main;
import com.shootr.android.domain.Shot;
import com.shootr.android.domain.ShotDetail;
import com.shootr.android.domain.bus.ShotSent;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.shot.GetShotDetailInteractor;
import com.shootr.android.domain.interactor.shot.MarkNiceShotInteractor;
import com.shootr.android.domain.interactor.shot.UnmarkNiceShotInteractor;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.ui.model.mappers.ShotModelMapper;
import com.shootr.android.ui.views.ShotDetailView;
import com.shootr.android.util.ErrorMessageFactory;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import java.util.List;
import javax.inject.Inject;

public class ShotDetailPresenter implements Presenter, ShotSent.Receiver {

    private final GetShotDetailInteractor getShotDetailInteractor;
    private final MarkNiceShotInteractor markNiceShotInteractor;
    private final UnmarkNiceShotInteractor unmarkNiceShotInteractor;
    private final ShotModelMapper shotModelMapper;
    private final ErrorMessageFactory errorMessageFactory;
    private final Bus bus;

    private ShotDetailView shotDetailView;
    private ShotModel shotModel;
    private List<ShotModel> repliesModels;
    private boolean justSentReply = false;

    @Inject
    public ShotDetailPresenter(GetShotDetailInteractor getShotDetailInteractor,
      MarkNiceShotInteractor markNiceShotInteractor, UnmarkNiceShotInteractor unmarkNiceShotInteractor, ShotModelMapper shotModelMapper,
      @Main Bus bus,
      ErrorMessageFactory errorMessageFactory) {
        this.getShotDetailInteractor = getShotDetailInteractor;
        this.markNiceShotInteractor = markNiceShotInteractor;
        this.unmarkNiceShotInteractor = unmarkNiceShotInteractor;
        this.shotModelMapper = shotModelMapper;
        this.bus = bus;
        this.errorMessageFactory = errorMessageFactory;
    }

    public void initialize(ShotDetailView shotDetailView, ShotModel shotModel) {
        this.shotDetailView = shotDetailView;
        this.shotModel = shotModel;
        this.loadShotDetail();
    }

    private void onRepliesLoaded(List<Shot> replies) {
        int previousReplyCount = repliesModels != null ? repliesModels.size() : 0;
        int newReplyCount = replies.size();
        repliesModels = shotModelMapper.transform(replies);
        shotDetailView.renderReplies(repliesModels);
        if (justSentReply && previousReplyCount < newReplyCount) {
            shotDetailView.scrollToBottom();
            justSentReply = false;
        }
    }

    private void loadShotDetail() {
        shotDetailView.renderShot(shotModel);
        getShotDetailInteractor.loadShotDetail(shotModel.getIdShot(), new Interactor.Callback<ShotDetail>() {
            @Override public void onLoaded(ShotDetail shotDetail) {
                shotModel = shotModelMapper.transform(shotDetail.getShot());
                shotDetailView.renderShot(shotModel);
                shotDetailView.renderParent(shotModelMapper.transform(shotDetail.getParentShot()));
                onRepliesLoaded(shotDetail.getReplies());
                shotDetailView.setReplyUsername(shotModel.getUsername());
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                shotDetailView.showError(errorMessageFactory.getMessageForError(error));
            }
        });
    }

    public void imageClick(ShotModel shot) {
        shotDetailView.openImage(shot.getImage());
    }

    public void avatarClick(String userId) {
        shotDetailView.openProfile(userId);
    }

    public void usernameClick(String username) {
        goToUserProfile(username);

    }

    public void markNiceShot(String idShot) {
        markNiceShotInteractor.markNiceShot(idShot, new Interactor.CompletedCallback() {
            @Override
            public void onCompleted() {
                loadShotDetail();
            }
        });
    }

    public void unmarkNiceShot(String idShot) {
        unmarkNiceShotInteractor.unmarkNiceShot(idShot, new Interactor.CompletedCallback() {
            @Override
            public void onCompleted() {
                loadShotDetail();
            }
        });
    }

    private void startProfileContainerActivity(String username) {
        shotDetailView.startProfileContainerActivity(username);
    }

    private void goToUserProfile(String username) {
        startProfileContainerActivity(username);
    }

    @Subscribe @Override public void onShotSent(ShotSent.Stream stream) {
        justSentReply = true;
        this.loadShotDetail();
    }

    @Override public void resume() {
        bus.register(this);
    }

    @Override public void pause() {
        bus.unregister(this);
    }
}
