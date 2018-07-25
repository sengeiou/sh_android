package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.shot.ClickShotLinkEventInteractor;
import com.shootr.mobile.domain.interactor.shot.DismissHighlightShotInteractor;
import com.shootr.mobile.domain.interactor.shot.GetHighlightedShotInteractor;
import com.shootr.mobile.domain.interactor.shot.HighlightShotInteractor;
import com.shootr.mobile.domain.interactor.shot.ViewHighlightedShotEventInteractor;
import com.shootr.mobile.domain.interactor.stream.GetLocalStreamInteractor;
import com.shootr.mobile.domain.model.shot.HighlightedShot;
import com.shootr.mobile.domain.model.stream.Stream;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.ui.Poller;
import com.shootr.mobile.ui.model.HighlightedShotModel;
import com.shootr.mobile.ui.model.mappers.HighlightedShotModelMapper;
import com.shootr.mobile.ui.views.HighlightedShotsView;
import javax.inject.Inject;

public class HighlightedShotPresenter implements Presenter {

  private static final long REFRESH_INTERVAL_MILLISECONDS = 10 * 1000;
  private static final long MAX_REFRESH_INTERVAL_MILLISECONDS = 60 * 1000;
  private final GetHighlightedShotInteractor getHighlightedShotsInteractor;
  private final HighlightShotInteractor highlightShotInteractor;
  private final DismissHighlightShotInteractor dismissHighlightShotInteractor;
  private final ViewHighlightedShotEventInteractor viewHighlightedShotEventInteractor;
  private final ClickShotLinkEventInteractor clickShotLinkEventInteractor;
  private final GetLocalStreamInteractor getLocalStreamInteractor;
  private final SessionRepository sessionRepository;
  private final HighlightedShotModelMapper mapper;
  private final Poller poller;

  private HighlightedShotsView view;
  private String idStream;
  private HighlightedShotModel highlightedShotModel;
  private HighlightedShotModel currentHighlightShot;
  private String streamAuthorId;
  private boolean isCurrentUserStreamContributor;

  @Inject
  public HighlightedShotPresenter(GetHighlightedShotInteractor getHighlightedShotsInteractor,
      HighlightShotInteractor highlightShotInteractor,
      DismissHighlightShotInteractor dismissHighlightShotInteractor,
      ViewHighlightedShotEventInteractor viewHighlightedShotEventInteractor,
      ClickShotLinkEventInteractor clickShotLinkEventInteractor,
      GetLocalStreamInteractor getLocalStreamInteractor, SessionRepository sessionRepository,
      HighlightedShotModelMapper mapper, Poller poller) {
    this.getHighlightedShotsInteractor = getHighlightedShotsInteractor;
    this.highlightShotInteractor = highlightShotInteractor;
    this.dismissHighlightShotInteractor = dismissHighlightShotInteractor;
    this.viewHighlightedShotEventInteractor = viewHighlightedShotEventInteractor;
    this.clickShotLinkEventInteractor = clickShotLinkEventInteractor;
    this.getLocalStreamInteractor = getLocalStreamInteractor;
    this.sessionRepository = sessionRepository;
    this.mapper = mapper;
    this.poller = poller;
  }

  public void setView(HighlightedShotsView view) {
    this.view = view;
  }

  public void initialize(final HighlightedShotsView view, String idStream) {
    setView(view);
    this.idStream = idStream;
    getContributorsIds();
    loadHighlightedShots();
    setupPoller();
  }

  private void setupPoller() {
    this.poller.init(REFRESH_INTERVAL_MILLISECONDS, new Runnable() {
      @Override public void run() {
        loadHighlightedShots();
        changeSynchroTimePoller();
      }
    });
  }

  private long handleIntervalSynchro() {
    int actualSynchroInterval = sessionRepository.getSynchroTime();
    long intervalSynchroServerResponse = actualSynchroInterval * 1000;
    if (intervalSynchroServerResponse < REFRESH_INTERVAL_MILLISECONDS) {
      intervalSynchroServerResponse = REFRESH_INTERVAL_MILLISECONDS;
    } else if (intervalSynchroServerResponse > MAX_REFRESH_INTERVAL_MILLISECONDS) {
      intervalSynchroServerResponse = REFRESH_INTERVAL_MILLISECONDS;
    }
    return intervalSynchroServerResponse;
  }

  private void changeSynchroTimePoller() {
    if (poller.isPolling()) {
      long intervalSynchroServerResponse = handleIntervalSynchro();
      if (intervalSynchroServerResponse != poller.getIntervalMilliseconds()) {
        poller.stopPolling();
        poller.setIntervalMilliseconds(intervalSynchroServerResponse);
        poller.startPolling();
      }
    }
  }

  private void startPollingShots() {
    poller.startPolling();
  }

  private void stopPollingShots() {
    poller.stopPolling();
  }

  public void refreshHighlight() {
    if (idStream != null) {
      loadHighlightedShots();
    }
  }

  private void loadHighlightedShots() {
    getHighlightedShotsInteractor.loadHighlightedShots(idStream,
        new Interactor.Callback<HighlightedShot>() {
          @Override public void onLoaded(HighlightedShot highlightedShot) {
            highlightedShotModel = mapper.map(highlightedShot);
            if (highlightedShot == null) {
              view.hideHighlightedShots();
            } else {
              handleHighlightShot();
            }
          }
        });
  }

  private void handleHighlightShot() {
    if (!highlightedShotModel.getVisible()) {
      view.hideHighlightedShots();
    } else if (isCurrentHighlightShot(highlightedShotModel)) {
      view.updateHighlightShotInfo(highlightedShotModel);
    } else if (isNewHighlightShot(highlightedShotModel)) {
      changeCurrentHighlightShot(highlightedShotModel);
      storeViewCount();
    } else {
      showNewHighlightShot(highlightedShotModel);
      storeViewCount();
    }
  }

  private void showNewHighlightShot(HighlightedShotModel newHighlightedShotModel) {
    currentHighlightShot = newHighlightedShotModel;
    view.showHighlightedShot(highlightedShotModel);
  }

  private void changeCurrentHighlightShot(
      HighlightedShotModel newHighlightedShotModel) {
    currentHighlightShot = newHighlightedShotModel;
    view.refreshHighlightedShots(highlightedShotModel);
  }

  private void storeViewCount() {
    viewHighlightedShotEventInteractor.countViewEvent(
        currentHighlightShot.getShotModel().getIdShot(), new Interactor.CompletedCallback() {
          @Override public void onCompleted() {
        /* no-op */
          }
        });
  }

  public void storeClickCount() {
    if (currentHighlightShot != null) {
      clickShotLinkEventInteractor.countClickLinkEvent(
          currentHighlightShot.getShotModel().getIdShot(), new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
        /* no-op */
            }
          });
    }
  }

  private boolean isCurrentHighlightShot(HighlightedShotModel newHighlightedShotModel) {
    return currentHighlightShot != null
        && currentHighlightShot.getIdHighlightedShot().equals(newHighlightedShotModel.getIdHighlightedShot());
  }

  private boolean isNewHighlightShot(HighlightedShotModel newHighlightedShotModel) {
    return currentHighlightShot != null
        && !currentHighlightShot.getIdHighlightedShot().equals(newHighlightedShotModel.getIdHighlightedShot());
  }

  public void highlightShot(String idShot) {
    highlightShotInteractor.highlight(idShot, idStream, new Interactor.CompletedCallback() {
      @Override public void onCompleted() {
        refreshHighlight();
      }
    });
  }

  public void onDismissHighlightShot(String idHighlightShot, String streamAuthorIdUser) {
    if (currentUserIsStreamContributor()
        || currentUserIsStreamHolder(streamAuthorIdUser)) {
      view.showDismissDialog(idHighlightShot);
    } else {
      dismissHighlightShot(idHighlightShot, false);
    }
  }

  public void onMenuDismissHighlightShot() {
    view.showDismissDialog(currentHighlightShot.getIdHighlightedShot());
  }

  public void removeHighlightShot(String idHighlightShot) {
    dismissHighlightShot(idHighlightShot, true);
  }

  private void dismissHighlightShot(String idHighlightShot, Boolean isAdminOrContributor) {
    dismissHighlightShotInteractor.dismiss(idHighlightShot, isAdminOrContributor, new Interactor.CompletedCallback() {
      @Override public void onCompleted() {
        loadHighlightedShots();
      }
    });
  }

  private void getContributorsIds() {
    getLocalStreamInteractor.loadStream(idStream, new GetLocalStreamInteractor.Callback() {
      @Override public void onLoaded(Stream stream) {
        isCurrentUserStreamContributor = stream.isCurrentUserContributor();
        view.setHighlightShotBackground(currentUserIsAdmin(streamAuthorId));
      }
    });
  }

  public boolean currentUserIsStreamHolder(String streamAuthorIdUser) {
    return sessionRepository.getCurrentUserId().equals(streamAuthorIdUser);
  }

  private boolean currentUserIsStreamContributor() {
    return isCurrentUserStreamContributor;
  }

  @Override public void resume() {
    startPollingShots();
  }

  @Override public void pause() {
    stopPollingShots();
  }

  public Boolean currentUserIsAdmin(String userId) {
    this.streamAuthorId = userId;
    return currentUserIsStreamHolder(userId) ||
        currentUserIsStreamContributor();
  }
}
