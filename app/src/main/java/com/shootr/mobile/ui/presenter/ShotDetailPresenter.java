package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.data.bus.Main;
import com.shootr.mobile.domain.Shot;
import com.shootr.mobile.domain.ShotDetail;
import com.shootr.mobile.domain.bus.ShotSent;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.shot.GetShotDetailInteractor;
import com.shootr.mobile.domain.interactor.shot.MarkNiceShotInteractor;
import com.shootr.mobile.domain.interactor.shot.ShareShotInteractor;
import com.shootr.mobile.domain.interactor.shot.UnmarkNiceShotInteractor;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.ui.model.mappers.ShotModelMapper;
import com.shootr.mobile.ui.views.ShotDetailView;
import com.shootr.mobile.util.ErrorMessageFactory;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import java.util.List;
import javax.inject.Inject;

public class ShotDetailPresenter implements Presenter, ShotSent.Receiver {

    private final GetShotDetailInteractor getShotDetailInteractor;
    private final MarkNiceShotInteractor markNiceShotInteractor;
    private final UnmarkNiceShotInteractor unmarkNiceShotInteractor;
    private final ShareShotInteractor shareShotInteractor;
    private final ShotModelMapper shotModelMapper;
    private final ErrorMessageFactory errorMessageFactory;
    private final Bus bus;

    private ShotDetailView shotDetailView;
    private ShotModel shotModel;
    private List<ShotModel> repliesModels;
    private boolean justSentReply = false;
    private boolean isNiceBlocked;

    @Inject
    public ShotDetailPresenter(GetShotDetailInteractor getShotDetailInteractor,
      MarkNiceShotInteractor markNiceShotInteractor, UnmarkNiceShotInteractor unmarkNiceShotInteractor,
      ShareShotInteractor shareShotInteractor, ShotModelMapper shotModelMapper, @Main Bus bus,
      ErrorMessageFactory errorMessageFactory) {
        this.getShotDetailInteractor = getShotDetailInteractor;
        this.markNiceShotInteractor = markNiceShotInteractor;
        this.unmarkNiceShotInteractor = unmarkNiceShotInteractor;
        this.shareShotInteractor = shareShotInteractor;
        this.shotModelMapper = shotModelMapper;
        this.bus = bus;
        this.errorMessageFactory = errorMessageFactory;
    }

    protected void setShotModel(ShotModel shotModel) {
        this.shotModel = shotModel;
    }

    protected void setShotDetailView(ShotDetailView shotDetailView) {
        this.shotDetailView = shotDetailView;
    }

    public void initialize(ShotDetailView shotDetailView, ShotModel shotModel) {
        this.setShotDetailView(shotDetailView);
        this.setShotModel(shotModel);
        this.loadShotDetail();
    }

    private void onRepliesLoaded(List<Shot> replies) {
        int previousReplyCount = repliesModels != null ? repliesModels.size() : 0;
        int newReplyCount = replies.size();
        if (newReplyCount >= previousReplyCount) {
            repliesModels = shotModelMapper.transform(replies);
            renderReplies(previousReplyCount, newReplyCount);
        } else if (repliesModels!= null && newReplyCount == 0){
            renderReplies(previousReplyCount, newReplyCount);
        }
    }

    private void renderReplies(int previousReplyCount, int newReplyCount) {
        shotDetailView.renderReplies(repliesModels);
        if (justSentReply && previousReplyCount < newReplyCount) {
            shotDetailView.scrollToBottom();
            justSentReply = false;
        }
    }

    private void loadShotDetail() {
        getShotDetailInteractor.loadShotDetail(shotModel.getIdShot(), new Interactor.Callback<ShotDetail>() {
            @Override public void onLoaded(ShotDetail shotDetail) {
                onShotDetailLoaded(shotDetail);
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                shotDetailView.showError(errorMessageFactory.getMessageForError(error));
            }
        });
    }

    private void onShotDetailLoaded(ShotDetail shotDetail) {
        setShotModel(shotModelMapper.transform(shotDetail.getShot()));
        shotDetailView.renderShot(shotModel);
        shotDetailView.renderParent(shotModelMapper.transform(shotDetail.getParentShot()));
        onRepliesLoaded(shotDetail.getReplies());
        shotDetailView.setReplyUsername(shotModel.getUsername());
        setNiceBlocked(false);
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
        if(!isNiceBlocked) {
            setNiceBlocked(true);
            markNiceShotInteractor.markNiceShot(idShot, new Interactor.CompletedCallback() {
                @Override public void onCompleted() {
                    loadShotDetail();
                }
            }, new Interactor.ErrorCallback() {
                @Override public void onError(ShootrException error) {
                    setNiceBlocked(false);
                }
            });
        }
    }

    public void unmarkNiceShot(String idShot) {
        if(!isNiceBlocked) {
            setNiceBlocked(true);
            unmarkNiceShotInteractor.unmarkNiceShot(idShot, new Interactor.CompletedCallback() {
                @Override public void onCompleted() {
                    loadShotDetail();
                }
            }, new Interactor.ErrorCallback() {
                @Override public void onError(ShootrException error) {
                    setNiceBlocked(false);
                }
            });
        }
    }

    private void startProfileContainerActivity(String username) {
        shotDetailView.startProfileContainerActivity(username);
    }

    private void goToUserProfile(String username) {
        startProfileContainerActivity(username);
    }

    @Subscribe @Override public void onShotSent(ShotSent.Event event) {
        justSentReply = true;
        this.loadShotDetail();
    }

    public void shareShot(ShotModel shotModel) {
        shareShotInteractor.shareShot(shotModel.getIdShot(), new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
                shotDetailView.showShotShared();
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                shotDetailView.showError(errorMessageFactory.getMessageForError(error));
            }
        });
    }

    protected void setNiceBlocked(Boolean blocked){
        this.isNiceBlocked= blocked;
    }

    @Override public void resume() {
        bus.register(this);
    }

    @Override public void pause() {
        bus.unregister(this);
    }
}
