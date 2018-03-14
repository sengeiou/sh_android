package com.shootr.mobile.ui.presenter.streamtimeline;

import android.util.Log;
import com.shootr.mobile.data.bus.Main;
import com.shootr.mobile.domain.bus.EventReceived;
import com.shootr.mobile.domain.bus.ShotSent;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.CreateTopicInteractor;
import com.shootr.mobile.domain.interactor.DeleteHighlightedItemInteractor;
import com.shootr.mobile.domain.interactor.DeleteTopicInteractor;
import com.shootr.mobile.domain.interactor.GetCachedTimelineInteractor;
import com.shootr.mobile.domain.interactor.GetStreamTimelineInteractor;
import com.shootr.mobile.domain.interactor.GetTimelineInteractor;
import com.shootr.mobile.domain.interactor.GetTimelineRepositionInteractor;
import com.shootr.mobile.domain.interactor.HighlightItemInteractor;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.PutLastStreamVisitInteractor;
import com.shootr.mobile.domain.interactor.SubscribeTimelineInteractor;
import com.shootr.mobile.domain.interactor.shot.CallCtaCheckInInteractor;
import com.shootr.mobile.domain.interactor.shot.ClickShotLinkEventInteractor;
import com.shootr.mobile.domain.interactor.shot.DismissHighlightShotInteractor;
import com.shootr.mobile.domain.interactor.shot.HighlightShotInteractor;
import com.shootr.mobile.domain.interactor.shot.MarkNiceShotInteractor;
import com.shootr.mobile.domain.interactor.shot.ReshootInteractor;
import com.shootr.mobile.domain.interactor.shot.UndoReshootInteractor;
import com.shootr.mobile.domain.interactor.shot.UnmarkNiceShotInteractor;
import com.shootr.mobile.domain.interactor.shot.ViewHighlightedShotEventInteractor;
import com.shootr.mobile.domain.interactor.stream.SelectStreamInteractor;
import com.shootr.mobile.domain.interactor.stream.UpdateStreamInteractor;
import com.shootr.mobile.domain.interactor.timeline.PutTimelineRepositionInteractor;
import com.shootr.mobile.domain.model.BadgeContent;
import com.shootr.mobile.domain.model.FixedItemSocketMessage;
import com.shootr.mobile.domain.model.NewBadgeContentSocketMessage;
import com.shootr.mobile.domain.model.NewItemSocketMessage;
import com.shootr.mobile.domain.model.ParticipantsSocketMessage;
import com.shootr.mobile.domain.model.PinnedItemSocketMessage;
import com.shootr.mobile.domain.model.PrintableItem;
import com.shootr.mobile.domain.model.PrintableType;
import com.shootr.mobile.domain.model.SocketMessage;
import com.shootr.mobile.domain.model.StreamTimeline;
import com.shootr.mobile.domain.model.TimelineSocketMessage;
import com.shootr.mobile.domain.model.TimelineType;
import com.shootr.mobile.domain.model.UpdateItemSocketMessage;
import com.shootr.mobile.domain.model.shot.Shot;
import com.shootr.mobile.domain.model.shot.ShotType;
import com.shootr.mobile.domain.model.stream.StreamSearchResult;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.ui.Poller;
import com.shootr.mobile.ui.model.PollModel;
import com.shootr.mobile.ui.model.PrintableModel;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.ui.model.StreamModel;
import com.shootr.mobile.ui.model.TopicModel;
import com.shootr.mobile.ui.model.mappers.PrintableModelMapper;
import com.shootr.mobile.ui.model.mappers.ShotModelMapper;
import com.shootr.mobile.ui.model.mappers.StreamModelMapper;
import com.shootr.mobile.ui.presenter.Presenter;
import com.shootr.mobile.ui.views.streamtimeline.FixedItemView;
import com.shootr.mobile.ui.views.streamtimeline.StreamTimelineView;
import com.shootr.mobile.util.ErrorMessageFactory;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;

public class StreamTimelinePresenter
    implements Presenter, ShotSent.Receiver, EventReceived.Receiver {

  private static final int TIMELINE_LOADING = 1;
  private static final int TIMELINE_PAGINATING = 2;
  private static final int TIMELINE_RESUMING = 3;
  private static final int TIMELINE_LOADED = 4;
  private static final String SCHEMA = "shootr://";
  private static final long REFRESH_INTERVAL_MILLISECONDS = 10 * 1000;
  private static final long MAX_REFRESH_INTERVAL_MILLISECONDS = 60 * 1000;
  private static final int MAX_LENGTH = 40;
  private static final int SINCE_TIMESTAMP = 0;
  private static final int FOLLOWERS = 0;
  private static final int CONNECTED = 1;

  private final GetStreamTimelineInteractor getStreamTimelineInteractor;
  private final SelectStreamInteractor selectStreamInteractor;
  private final UpdateStreamInteractor updateStreamInteractor;
  private final CallCtaCheckInInteractor callCtaCheckInInteractor;
  private final MarkNiceShotInteractor markNiceShotInteractor;
  private final UnmarkNiceShotInteractor unmarkNiceShotInteractor;
  private final ReshootInteractor reshootInteractor;
  private final UndoReshootInteractor undoReshootInteractor;
  private final HighlightShotInteractor highlightShotInteractor;
  private final SubscribeTimelineInteractor subscribeTimelineInteractor;
  private final DismissHighlightShotInteractor dismissHighlightShotInteractor;
  private final ViewHighlightedShotEventInteractor viewHighlightedShotEventInteractor;
  private final ClickShotLinkEventInteractor clickShotLinkEventInteractor;
  private final HighlightItemInteractor highlightItemInteractor;
  private final DeleteHighlightedItemInteractor deleteHighlightedItemInteractor;
  private final CreateTopicInteractor createTopicInteractor;
  private final DeleteTopicInteractor deleteTopicInteractor;
  private final GetTimelineInteractor getTimelineInteractor;
  private final GetCachedTimelineInteractor getCachedTimelineInteractor;
  private final PutLastStreamVisitInteractor putLastStreamVisitInteractor;
  private final PutTimelineRepositionInteractor putTimelineRepositionInteractor;
  private final GetTimelineRepositionInteractor getTimelineRepositionInteractor;
  private final SessionRepository sessionRepository;
  private final PrintableModelMapper printableModelMapper;
  private final ShotModelMapper shotModelMapper;
  private final StreamModelMapper streamModelMapper;
  private final ErrorMessageFactory errorMessageFactory;
  private final Bus bus;
  private final Poller poller;

  private StreamTimelineView view;
  private FixedItemView fixedItemView;
  private String idStream;
  private StreamModel streamModel;
  private boolean canFixItem;
  private ArrayList<String> fixedItemsIds = new ArrayList<>();
  private long maxTimestamp;
  private long sinceTimestamp;
  private boolean mightHaveMoreItems;
  private boolean isFirstLoad;
  private boolean hasBeenPaused;
  private int newShotsNumber;
  private boolean isFirstShotPosition;
  private String currentTopicText;
  private String currentTimelineType = TimelineType.MAIN;
  private int loadType;

  @Inject public StreamTimelinePresenter(GetStreamTimelineInteractor getStreamTimelineInteractor,
      SelectStreamInteractor selectStreamInteractor, UpdateStreamInteractor updateStreamInteractor,
      CallCtaCheckInInteractor callCtaCheckInInteractor,
      MarkNiceShotInteractor markNiceShotInteractor,
      UnmarkNiceShotInteractor unmarkNiceShotInteractor, ReshootInteractor reshootInteractor,
      UndoReshootInteractor undoReshootInteractor, HighlightShotInteractor highlightShotInteractor,
      SubscribeTimelineInteractor subscribeTimelineInteractor,
      DismissHighlightShotInteractor dismissHighlightShotInteractor,
      ViewHighlightedShotEventInteractor viewHighlightedShotEventInteractor,
      ClickShotLinkEventInteractor clickShotLinkEventInteractor,
      HighlightItemInteractor highlightItemInteractor,
      DeleteHighlightedItemInteractor deleteHighlightedItemInteractor,
      CreateTopicInteractor createTopicInteractor, DeleteTopicInteractor deleteTopicInteractor,
      GetTimelineInteractor getTimelineInteractor,
      GetCachedTimelineInteractor getCachedTimelineInteractor,
      PutLastStreamVisitInteractor putLastStreamVisitInteractor,
      PutTimelineRepositionInteractor putTimelineRepositionInteractor,
      GetTimelineRepositionInteractor getTimelineRepositionInteractor,
      SessionRepository sessionRepository, PrintableModelMapper printableModelMapper,
      ShotModelMapper shotModelMapper, StreamModelMapper streamModelMapper,
      ErrorMessageFactory errorMessageFactory, @Main Bus bus, Poller poller) {
    this.getStreamTimelineInteractor = getStreamTimelineInteractor;
    this.selectStreamInteractor = selectStreamInteractor;
    this.updateStreamInteractor = updateStreamInteractor;
    this.callCtaCheckInInteractor = callCtaCheckInInteractor;
    this.markNiceShotInteractor = markNiceShotInteractor;
    this.unmarkNiceShotInteractor = unmarkNiceShotInteractor;
    this.reshootInteractor = reshootInteractor;
    this.undoReshootInteractor = undoReshootInteractor;
    this.highlightShotInteractor = highlightShotInteractor;
    this.subscribeTimelineInteractor = subscribeTimelineInteractor;
    this.dismissHighlightShotInteractor = dismissHighlightShotInteractor;
    this.viewHighlightedShotEventInteractor = viewHighlightedShotEventInteractor;
    this.clickShotLinkEventInteractor = clickShotLinkEventInteractor;
    this.highlightItemInteractor = highlightItemInteractor;
    this.deleteHighlightedItemInteractor = deleteHighlightedItemInteractor;
    this.createTopicInteractor = createTopicInteractor;
    this.deleteTopicInteractor = deleteTopicInteractor;
    this.getTimelineInteractor = getTimelineInteractor;
    this.getCachedTimelineInteractor = getCachedTimelineInteractor;
    this.putLastStreamVisitInteractor = putLastStreamVisitInteractor;
    this.putTimelineRepositionInteractor = putTimelineRepositionInteractor;
    this.getTimelineRepositionInteractor = getTimelineRepositionInteractor;
    this.sessionRepository = sessionRepository;
    this.printableModelMapper = printableModelMapper;
    this.shotModelMapper = shotModelMapper;
    this.streamModelMapper = streamModelMapper;
    this.errorMessageFactory = errorMessageFactory;
    this.bus = bus;
    this.poller = poller;
  }

  public void setView(StreamTimelineView view, FixedItemView fixedItemView) {
    this.view = view;
    this.fixedItemView = fixedItemView;
  }

  public void initialize(StreamTimelineView view, FixedItemView fixedItemView, String idStream) {
    this.view = view;
    this.fixedItemView = fixedItemView;
    this.idStream = idStream;
    isFirstLoad = true;
    selectStream();
    loadType = TIMELINE_LOADING;
    currentTimelineType =
        sessionRepository.isTimelineFilterActivated() ? TimelineType.IMPORTANT : TimelineType.MAIN;
    view.showCheckingForShots();
    getCachedTimeline(idStream, currentTimelineType);
  }

  private void getCachedTimeline(String idStream, final String filterType) {
    Log.d("socket", "pido de cache");
    getCachedTimelineInteractor.getTimeline(idStream, filterType,
        new Interactor.Callback<StreamTimeline>() {
          @Override public void onLoaded(StreamTimeline streamTimeline) {

            if (streamTimeline != null) {
              onTimelineLoaded(streamTimeline);
            }
            subscribeTimeline(filterType);
          }
        });
  }

  private void subscribeTimeline(final String filterType) {
    subscribeTimelineInteractor.subscribe(idStream, filterType, new Interactor.Callback<Boolean>() {
      @Override public void onLoaded(Boolean shouldGetTimeline) {
        if (shouldGetTimeline) {
          Log.d("socket", "debo de pedir timeline!!");
          loadType = TIMELINE_LOADING;
          getTimeline(filterType, SINCE_TIMESTAMP, false);
        }
      }
    });
  }

  private void getTimeline(String filterType, long timestamp, boolean isPaginating) {
    getTimelineInteractor.getTimeline(idStream, filterType, timestamp, isPaginating,
        new Interactor.Callback<Boolean>() {
          @Override public void onLoaded(Boolean aBoolean) {
            //TODO
          }
        });
  }

  private void paginateTimeline() {
    loadType = TIMELINE_PAGINATING;
    view.showLoadingOldShots();
    getTimeline(currentTimelineType, maxTimestamp, true);
  }

  private void onTimelineLoaded(StreamTimeline streamTimeline) {
    loadType = TIMELINE_LOADED;
    maxTimestamp = streamTimeline.getItems().getMaxTimestamp();
    mightHaveMoreItems = maxTimestamp != 0L;
    setupTimeline(streamTimeline);
  }

  private void onPaginatedTimelineLoaded(StreamTimeline streamTimeline) {
    loadType = TIMELINE_LOADED;
    maxTimestamp = streamTimeline.getItems().getMaxTimestamp();
    mightHaveMoreItems = maxTimestamp != 0L;
    view.addOldItems(printableModelMapper.mapPrintableModel(streamTimeline.getItems().getData()));
  }

  private void setupTimeline(StreamTimeline streamTimeline) {
    setupStreamInfo(streamModelMapper.transform(streamTimeline.getStream()));
    setupConnected(streamTimeline.getStream().getTotalFollowers(),
        streamTimeline.getParticipantsNumber());
    setupImportantBadge(streamTimeline.isNewBadgeContent());
    setupPinnedItems(streamTimeline.getPinned().getData());
    setupFixedItems(streamTimeline.getFixed().getData());
    renderItems(streamTimeline);
  }

  private void setupImportantBadge(boolean show) {
    if (show) {
      view.showFilterAlert();
    }
  }

  private void setupPinnedItems(List<PrintableItem> pinneds) {
    List<PrintableModel> pinnedModels = printableModelMapper.mapPinnableModel(pinneds);
    view.renderPinnedItems(pinnedModels);
    storeCurrentTopic(pinnedModels);
  }

  private void storeCurrentTopic(List<PrintableModel> pinnedModels) {
    for (PrintableModel pinnedModel : pinnedModels) {
      if (pinnedModel instanceof TopicModel) {
        currentTopicText = ((TopicModel) pinnedModel).getComment();
      }
    }
  }

  private void renderItems(StreamTimeline streamTimeline) {
    if (streamTimeline.getItems().getData().isEmpty()) {
      view.showEmpty();
    } else {
      view.hideCheckingForShots();
      view.hideEmpty();
      if (streamTimeline.getTimelineReposition() != null) {
        view.renderItems(
            printableModelMapper.mapPrintableModel(streamTimeline.getItems().getData()),
            (PrintableModel) streamTimeline.getTimelineReposition().getFirstItem(),
            streamTimeline.getTimelineReposition().getOffset());
      } else {
        view.renderItems(
            printableModelMapper.mapPrintableModel(streamTimeline.getItems().getData()), null, 0);
      }
    }
  }

  private void addNewItems(StreamTimeline streamTimeline) {
    if (!streamTimeline.getItems().getData().isEmpty()) {
      view.addNewItems(printableModelMapper.mapPrintableModel(streamTimeline.getItems().getData()));
    } else {
      //view.refreshShotsInfo();
    }
  }

  public void onItemsInserted(int count) {
    newShotsNumber += count;
    if (!isFirstShotPosition && newShotsNumber != 0) {
      view.showNewShotsIndicator(newShotsNumber);
    }
    if (isFirstShotPosition) {
      view.smoothToTop();
    }
  }

  private void setupFixedItems(List<PrintableItem> pinneds) {
    if (!pinneds.isEmpty()) {
      List<PrintableModel> fixedModel = printableModelMapper.mapFixableModel(pinneds);
      setupFixedItemsIds(fixedModel);
      view.renderFixedItems(fixedModel);
    } else {
      view.removeHighlightedItem();
    }
  }

  private void setupFixedItemsIds(List<PrintableModel> fixedModel) {
    for (PrintableModel printableModel : fixedModel) {
      if (printableModel instanceof ShotModel && !fixedItemsIds.contains(
          ((ShotModel) printableModel).getIdShot())) {
        fixedItemsIds.add(((ShotModel) printableModel).getIdShot());
        storeViewCount(((ShotModel) printableModel).getIdShot());
      }
    }
  }

  private void setupConnected(int followings, int connected) {

    Integer[] count = new Integer[] { followings, connected };

    if (currentTimelineType.equals(TimelineType.MAIN)) {
      if (count[CONNECTED] > 0 || count[FOLLOWERS] > 0) {
        view.showWatchingPeopleCount(count);
      } else {
        view.hideWatchingPeopleCount();
      }
    }
  }

  protected void selectStream() {
    selectStreamInteractor.selectStream(idStream, new Interactor.Callback<StreamSearchResult>() {
      @Override public void onLoaded(StreamSearchResult streamSearchResult) {
        if (isFirstLoad) {
          StreamModel model = streamModelMapper.transform(streamSearchResult.getStream());
          streamModel = model;
          view.sendAnalythicsEnterTimeline();
          if (model.canWrite()) {
            view.setupFilterShowcase();
          }
          setupStreamInfo(model);
          handleFilterVisibility(streamModel);
        }
      }
    }, new Interactor.ErrorCallback() {
      @Override public void onError(ShootrException error) {
                /* no-op */
      }
    });
  }

  private void handleFilterVisibility(StreamModel streamModel) {
    if (streamModel.canWrite()) {
      if (sessionRepository.isTimelineFilterActivated()) {
        currentTimelineType = TimelineType.IMPORTANT;
        view.showGenericItemsMenuItem();
      } else {
        currentTimelineType = TimelineType.MAIN;
        view.showImportantItemsMenuItem();
      }
    }
  }

  private void setupStreamInfo(StreamModel streamModel) {
    this.canFixItem = streamModel.canFixItem();
    view.setTitle(streamModel.getTitle());
    view.sendAnalythicsEnterTimeline();
    setupNewShotTextBox(streamModel);
  }

  private void setupNewShotTextBox(StreamModel streamModel) {
    if (streamModel.canWrite()) {
      view.showNewShotTextBox();
    } else {
      view.showViewOnlyTextBox();
    }
  }

  //region streamMessage
  public void editStreamMessage(String topic) {
    if (topic.isEmpty()) {
      sendTopic(topic, false);
    } else {
      view.showPinMessageNotification(topic);
    }
  }

  public void notifyMessage(String topic, Boolean notify) {
    sendTopic(topic, notify);
  }

  private void sendTopic(String topic, Boolean notifyMessage) {
    topic = trimTopicAndNullWhenEmpty(topic);

    if (topic != null) {
      createTopicInteractor.createTopic(PrintableType.TOPIC, topic, idStream, notifyMessage,
          new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
              //TODO SHOW PINNED
            }
          });
    } else {
      deleteTopicInteractor.deleteTopic(PrintableType.TOPIC, idStream,
          new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
              //DELETE
            }
          });
    }
  }

  private String trimTopicAndNullWhenEmpty(String streamTopic) {
    String trimmedText = streamTopic;
    if (trimmedText != null) {
      trimmedText = trimmedText.trim();
      if (trimmedText.isEmpty()) {
        trimmedText = null;
      }
    }
    return trimmedText;
  }
  //endregion

  //region cta shot
  public void onCtaPressed(ShotModel shotModel) {
    if (shotModel.getCtaButtonLink().startsWith(SCHEMA) && shotModel.getType()
        .equals(ShotType.CTACHECKIN)) {
      callCheckIn(shotModel.getStreamId());
    } else {
      view.storeCtaClickLink(shotModel);
      view.openCtaAction(shotModel.getCtaButtonLink());
    }
  }

  public void onMenuCheckInClick() {
    if (streamModel != null) {
      callCheckIn(streamModel.getIdStream());
    }
  }

  private void callCheckIn(String streamId) {
    callCtaCheckInInteractor.checkIn(streamId, new Interactor.CompletedCallback() {
      @Override public void onCompleted() {
        view.showChecked();
      }
    }, new Interactor.ErrorCallback() {
      @Override public void onError(ShootrException error) {
        view.showError(errorMessageFactory.getMessageForError(error));
      }
    });
  }
  //endregion

  //region nice Item
  public void markNiceShot(final ShotModel shotModel) {
    markNiceShotInteractor.markNiceShot(shotModel.getIdShot(), new Interactor.CompletedCallback() {
      @Override public void onCompleted() {
        view.renderNice(shotModel);
      }
    }, new Interactor.ErrorCallback() {
      @Override public void onError(ShootrException error) {
                /* no-op */
      }
    });
  }

  public void unmarkNiceShot(final String idShot) {
    unmarkNiceShotInteractor.unmarkNiceShot(idShot, new Interactor.CompletedCallback() {
      @Override public void onCompleted() {
        view.renderUnnice(idShot);
      }
    }, new Interactor.ErrorCallback() {
      @Override public void onError(ShootrException error) {
                /* no-op */
      }
    });
  }
  //endregion

  public void reshoot(final ShotModel shotModel) {
    reshootInteractor.reshoot(shotModel.getIdShot(), new Interactor.CompletedCallback() {
      @Override public void onCompleted() {
        view.setReshoot(shotModel.getIdShot(), true);
      }
    }, new Interactor.ErrorCallback() {
      @Override public void onError(ShootrException error) {
        view.showError(errorMessageFactory.getMessageForError(error));
      }
    });
  }

  public void undoReshoot(final ShotModel shot) {
    undoReshootInteractor.undoReshoot(shot.getIdShot(), new Interactor.CompletedCallback() {
      @Override public void onCompleted() {
        view.setReshoot(shot.getIdShot(), false);
      }
    }, new Interactor.ErrorCallback() {
      @Override public void onError(ShootrException error) {
        view.showError(errorMessageFactory.getMessageForError(error));
      }
    });
  }

  public boolean canFixItem() {
    return canFixItem;
  }

  public void onDismissHighlightShot(String idShot, String streamAuthorIdUser) {
    if (canFixItem()) {
      fixedItemView.showDismissDialog(idShot);
    } else {
      dismissHighlightShot(idShot, false);
    }
  }

  private void dismissHighlightShot(final String idShot, Boolean isAdminOrContributor) {
    dismissHighlightShotInteractor.dismiss(idShot, isAdminOrContributor,
        new Interactor.CompletedCallback() {
          @Override public void onCompleted() {
            fixedItemView.hideFixedShot();
            fixedItemsIds.remove(idShot);
          }
        });
  }

  private void storeViewCount(String idShot) {
    if (!fixedItemsIds.isEmpty()) {
      viewHighlightedShotEventInteractor.countViewEvent(idShot, new Interactor.CompletedCallback() {
        @Override public void onCompleted() {
        /* no-op */
        }
      });
    }
  }

  public void storeClickCount() {
    if (!fixedItemsIds.isEmpty()) {
      clickShotLinkEventInteractor.countClickLinkEvent(fixedItemsIds.get(0),
          new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
        /* no-op */
            }
          });
    }
  }

  public void showingLastShot() {
    if (loadType == TIMELINE_LOADED && mightHaveMoreItems) {
      paginateTimeline();
    }
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

  @Override public void resume() {
    bus.register(this);
    if (hasBeenPaused) {
      isFirstLoad = false;
      selectStream();
      getCachedTimeline(idStream, currentTimelineType);
      //poller.startPolling();
    }
  }

  @Override public void pause() {
    bus.unregister(this);
    hasBeenPaused = true;
    putLastStreamVisit();
  }

  public void destroy() {
    if (poller != null && poller.isPolling()) {
      poller.stopPolling();
    }
  }

  @Subscribe @Override public void onShotSent(ShotSent.Event event) {
    //view.addMyItem(shotModelMapper.transform((Shot) event.getShot()));
  }

  public void setIsFirstShotPosition(boolean isFirstShotPosition) {
    this.isFirstShotPosition = isFirstShotPosition;
    if (isFirstShotPosition) {
      view.hideNewShotsIndicator();
      newShotsNumber = 0;
    }
  }

  public String getCurrentTopicText() {
    return currentTopicText;
  }

  //region textChanged
  public void textChanged(String currentText) {
    updateCharCounter(filterText(currentText));
  }

  private void updateCharCounter(String filteredText) {
    int remainingLength = MAX_LENGTH - filteredText.length();
    view.setRemainingCharactersCount(remainingLength);

    boolean isValidTopic = remainingLength > 0;
    if (isValidTopic) {
      view.setRemainingCharactersColorValid();
    } else {
      view.setRemainingCharactersColorInvalid();
    }
  }

  private String filterText(String originalText) {
    String trimmed = originalText.trim();
    while (trimmed.contains("\n\n\n")) {
      trimmed = trimmed.replace("\n\n\n", "\n\n");
    }
    return trimmed;
  }

  public void onActivateFilterClick() {
    setupFilterClick(true);
  }

  public void onDesactivateFilterClick() {
    setupFilterClick(false);
  }

  private void setupFilterClick(boolean activating) {
    isFirstLoad = true;
    loadType = TIMELINE_LOADING;
    view.clearTimeline();
    if (activating) {
      currentTimelineType = TimelineType.IMPORTANT;
      view.showGenericItemsMenuItem();
      sessionRepository.setTimelineFilterActivated(true);
      getCachedTimeline(idStream, currentTimelineType);
    } else {
      currentTimelineType = TimelineType.MAIN;
      view.showImportantItemsMenuItem();
      sessionRepository.setTimelineFilterActivated(false);
      getCachedTimeline(idStream, currentTimelineType);
    }
    sinceTimestamp = SINCE_TIMESTAMP;
  }
  //endregion

  public void onPollActionClick(PollModel pollModel) {
    switch (pollModel.getAction()) {
      case PollModel.RESULTS:
        if (pollModel.isHideResults() && !pollModel.canVote()) {
          fixedItemView.goToOptionVoted(pollModel);
        } else if (pollModel.isHideResults()) {
          fixedItemView.goToHiddenResults(pollModel.getQuestion());
        } else {
          fixedItemView.goToPollResults(pollModel.getIdPoll(), idStream);
        }
        break;
      case PollModel.VOTE:
        fixedItemView.goToPollVote(idStream, streamModel.getAuthorId());
        break;
      default:
        if (pollModel.isHideResults() && !pollModel.canVote()) {
          fixedItemView.goToOptionVoted(pollModel);
        } else if (pollModel.isHideResults()) {
          fixedItemView.goToHiddenResults(pollModel.getQuestion());
        } else {
          fixedItemView.goToPollLiveResults(pollModel.getIdPoll(), idStream);
        }
        break;
    }
  }

  public void highlightItem(final PrintableModel printableModel) {
    if (printableModel instanceof ShotModel) {
      highlightItemInteractor.highlightItem(PrintableType.SHOT,
          ((ShotModel) printableModel).getIdShot(), idStream, new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
              /* no-op */
            }
          });
    }
  }

  public void deleteHighlightedItem(PrintableModel printableModel) {
    if (canFixItem()) {
      if (printableModel instanceof ShotModel) {
        deleteHighlightedItemInteractor.deleteHighlightedItem(PrintableType.SHOT,
            ((ShotModel) printableModel).getIdShot(), idStream, new Interactor.CompletedCallback() {
              @Override public void onCompleted() {
                view.removeHighlightedItem();
              }
            });
      }
    }
  }

  private void putLastStreamVisit() {
    putLastStreamVisitInteractor.storeLastVisit(idStream, new Interactor.CompletedCallback() {
      @Override public void onCompleted() {
        /* no-op */
      }
    });
  }

  public void putItemForReposition(PrintableModel printableModel, int offset) {
    putTimelineRepositionInteractor.putTimelineReposition(idStream, currentTimelineType,
        printableModel, offset);
  }

  @Override @Subscribe public void onEvent(EventReceived.Event event) {

    switch (event.getMessage().getEventType()) {
      case SocketMessage.TIMELINE:
        view.hideCheckingForShots();
        view.hideLoadingOldShots();
        TimelineSocketMessage timelineSocketMessage = (TimelineSocketMessage) event.getMessage();
        if (idStream.equals(timelineSocketMessage.getData().getStream().getId())) {
          if (loadType == TIMELINE_PAGINATING) {
            onPaginatedTimelineLoaded(timelineSocketMessage.getData());
          } else {
            onTimelineLoaded(timelineSocketMessage.getData());
          }
        }
        break;

      case SocketMessage.NEW_ITEM_DATA:
        NewItemSocketMessage newItemSocketMessage = (NewItemSocketMessage) event.getMessage();
        ShotModel shotModel = shotModelMapper.transform((Shot) newItemSocketMessage.getData());
        shotModel.setTimelineGroup(PrintableModel.ITEMS_GROUP);
        if (idStream.equals(shotModel.getStreamId()) && newItemSocketMessage.isActiveSubscription) {
          view.addNewItems(Collections.<PrintableModel>singletonList(shotModel));
        }
        break;

      case SocketMessage.UPDATE_ITEM_DATA:
        UpdateItemSocketMessage updateItemSocketMessage =
            (UpdateItemSocketMessage) event.getMessage();
        ShotModel updatedShot = shotModelMapper.transform((Shot) updateItemSocketMessage.getData());
        updatedShot.setTimelineGroup(PrintableModel.ITEMS_GROUP);
        if (idStream.equals(updatedShot.getStreamId())
            && updateItemSocketMessage.isActiveSubscription) {
          view.updateItem(updatedShot);
          view.updateFixedItem(printableModelMapper.mapFixableModel(
              Collections.singletonList(updateItemSocketMessage.getData())));
        }
        break;

      case SocketMessage.PARTICIPANTS_UPDATE:
        ParticipantsSocketMessage participantsSocketMessage =
            (ParticipantsSocketMessage) event.getMessage();
        if (streamModel != null) {
          setupConnected(streamModel.getTotalFollowers(),
              participantsSocketMessage.getData().getTotal());
        }
        break;

      case SocketMessage.PINNED_ITEMS:
        PinnedItemSocketMessage pinnedItemSocketMessage =
            (PinnedItemSocketMessage) event.getMessage();
        if (pinnedItemSocketMessage.isActiveSubscription) {
          setupPinnedItems(pinnedItemSocketMessage.getData().getData());
        }
        break;

      case SocketMessage.FIXED_ITEMS:
        FixedItemSocketMessage fixedItemSocketMessage = (FixedItemSocketMessage) event.getMessage();
        if (fixedItemSocketMessage.isActiveSubscription) {
          setupFixedItems(fixedItemSocketMessage.getData().getData());
        }
        break;

      case SocketMessage.NEW_BADGE_CONTENT:
        NewBadgeContentSocketMessage newBadgeContentSocketMessage =
            (NewBadgeContentSocketMessage) event.getMessage();
        if (newBadgeContentSocketMessage.isActiveSubscription) {
          if (newBadgeContentSocketMessage.getData()
              .getBadgeType()
              .equals(BadgeContent.TIMELINE_TYPE) && currentTimelineType.equals(
              TimelineType.MAIN)) {
            setupImportantBadge(true);
          }
        }
        break;

      default:
        break;
    }
  }
}
