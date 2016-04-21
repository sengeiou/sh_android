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
import com.shootr.mobile.ui.model.NicerModel;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.ui.model.mappers.NicerModelMapper;
import com.shootr.mobile.ui.model.mappers.ShotModelMapper;
import com.shootr.mobile.ui.views.ShotDetailView;
import com.shootr.mobile.util.ErrorMessageFactory;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class ShotDetailPresenter implements Presenter, ShotSent.Receiver {

    private final GetShotDetailInteractor getShotDetailInteractor;
    private final MarkNiceShotInteractor markNiceShotInteractor;
    private final UnmarkNiceShotInteractor unmarkNiceShotInteractor;
    private final ShareShotInteractor shareShotInteractor;
    private final ShotModelMapper shotModelMapper;
    private final NicerModelMapper nicerModelMapper;
    private final ErrorMessageFactory errorMessageFactory;
    private final Bus bus;

    private ShotDetailView shotDetailView;
    private ShotModel shotModel;
    private List<ShotModel> repliesModels;
    private boolean justSentReply = false;
    private boolean isNiceBlocked;

    @Inject public ShotDetailPresenter(GetShotDetailInteractor getShotDetailInteractor,
      MarkNiceShotInteractor markNiceShotInteractor, UnmarkNiceShotInteractor unmarkNiceShotInteractor,
      ShareShotInteractor shareShotInteractor, ShotModelMapper shotModelMapper, NicerModelMapper nicerModelMapper,
      @Main Bus bus, ErrorMessageFactory errorMessageFactory) {
        this.getShotDetailInteractor = getShotDetailInteractor;
        this.markNiceShotInteractor = markNiceShotInteractor;
        this.unmarkNiceShotInteractor = unmarkNiceShotInteractor;
        this.shareShotInteractor = shareShotInteractor;
        this.shotModelMapper = shotModelMapper;
        this.nicerModelMapper = nicerModelMapper;
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
        this.loadShotDetail(shotModel);
    }

    public void initialize(final ShotDetailView shotDetailView, String idShot) {
        this.setShotDetailView(shotDetailView);
        loadShotDetailFromIdShot(shotDetailView, idShot);
    }

    public void loadShotDetailFromIdShot(final ShotDetailView shotDetailView, String idShot) {
        getShotDetailInteractor.loadShotDetail(idShot, new Interactor.Callback<ShotDetail>() {
            @Override public void onLoaded(ShotDetail shotDetail) {
                ShotModel shotModel = shotModelMapper.transform(shotDetail.getShot());
                setShotModel(shotModel);
                onShotDetailLoaded(shotDetail);
                initializeNewShotBarDelegate(shotModel, shotDetailView);
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                shotDetailView.showError(errorMessageFactory.getMessageForError(error));
            }
        });
    }

    private void setShotNicers(List<NicerModel> nicers) {
        List<String> usernames = new ArrayList<>();
        if (nicers != null) {
            for (NicerModel nicer : nicers) {
                usernames.add(nicer.getUserName());
            }
        }
        this.shotModel.setNicers(usernames);
    }

    public void initializeNewShotBarDelegate(ShotModel shotModel, ShotDetailView shotDetailView) {
        shotDetailView.setupNewShotBarDelegate(shotModel);
        shotDetailView.initializeNewShotBarPresenter(shotModel.getStreamId());
    }

    private void onRepliesLoaded(List<Shot> replies) {
        int previousReplyCount = repliesModels != null ? repliesModels.size() : 0;
        int newReplyCount = replies.size();
        if (newReplyCount >= previousReplyCount) {
            repliesModels = shotModelMapper.transform(replies);
            renderReplies(previousReplyCount, newReplyCount);
        } else if (repliesModels != null && newReplyCount == 0) {
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

    private void loadShotDetail(ShotModel shotModel) {
        if (shotModel != null) {
            handleShotDetail(shotModel);
        } else {
            shotDetailView.showError(errorMessageFactory.getCommunicationErrorMessage());
        }
    }

    private void handleShotDetail(ShotModel shotModel) {
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
        if (shotDetail.getNicers() != null) {
            setShotNicers(nicerModelMapper.transform(shotDetail.getNicers()));
        }
        shotDetailView.renderShot(shotModel);
        shotDetailView.renderParent(shotModelMapper.transform(shotDetail.getParents()));
        onRepliesLoaded(shotDetail.getReplies());
        shotDetailView.setReplyUsername(shotModel.getUsername());
        setNiceBlocked(false);
    }

    public void setupStreamTitle(Boolean isInStreamTimeline) {
        if (isInStreamTimeline) {
            shotDetailView.disableStreamTitle();
        } else {
            shotDetailView.enableStreamTitle();
        }
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
        if (!isNiceBlocked) {
            setNiceBlocked(true);
            markNiceShotInteractor.markNiceShot(idShot, new Interactor.CompletedCallback() {
                @Override public void onCompleted() {
                    loadShotDetail(shotModel);
                }
            }, new Interactor.ErrorCallback() {
                @Override public void onError(ShootrException error) {
                    setNiceBlocked(false);
                }
            });
        }
    }

    public void unmarkNiceShot(String idShot) {
        if (!isNiceBlocked) {
            setNiceBlocked(true);
            unmarkNiceShotInteractor.unmarkNiceShot(idShot, new Interactor.CompletedCallback() {
                @Override public void onCompleted() {
                    loadShotDetail(shotModel);
                }
            }, new Interactor.ErrorCallback() {
                @Override public void onError(ShootrException error) {
                    setNiceBlocked(false);
                }
            });
        }
    }

    public void streamTitleClick(final ShotModel shotModel) {
        shotDetailView.goToStreamTimeline(shotModel.getStreamId());
    }

    private void startProfileContainerActivity(String username) {
        shotDetailView.startProfileContainerActivity(username);
    }

    private void goToUserProfile(String username) {
        startProfileContainerActivity(username);
    }

    @Subscribe @Override public void onShotSent(ShotSent.Event event) {
        justSentReply = true;
        this.loadShotDetail(shotModel);
    }

    public void shareShotViaShootr() {
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

    public void shareShot() {
        shotDetailView.shareShot(shotModel);
    }

    protected void setNiceBlocked(Boolean blocked) {
        this.isNiceBlocked = blocked;
    }

    @Override public void resume() {
        bus.register(this);
    }

    @Override public void pause() {
        bus.unregister(this);
    }

    public void shotClick(ShotModel shotModel) {
        shotDetailView.openShot(shotModel);
    }

    public void openShotNicers(ShotModel shotModel) {
        shotDetailView.goToNicers(shotModel.getIdShot());
    }
}
