package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.data.bus.Main;
import com.shootr.mobile.domain.bus.EventReceived;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.GetCachedShotDetailInteractor;
import com.shootr.mobile.domain.interactor.GetNewShotDetailInteractor;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.SubscribeShotDetailInteractor;
import com.shootr.mobile.domain.interactor.UnsubscribeShotDetailInteractor;
import com.shootr.mobile.domain.interactor.shot.CallCtaCheckInInteractor;
import com.shootr.mobile.domain.interactor.shot.ClickShotLinkEventInteractor;
import com.shootr.mobile.domain.interactor.shot.MarkNiceShotInteractor;
import com.shootr.mobile.domain.interactor.shot.ReshootInteractor;
import com.shootr.mobile.domain.interactor.shot.UndoReshootInteractor;
import com.shootr.mobile.domain.interactor.shot.UnmarkNiceShotInteractor;
import com.shootr.mobile.domain.interactor.shot.ViewShotDetailEventInteractor;
import com.shootr.mobile.domain.model.ListType;
import com.shootr.mobile.domain.model.NewItemSocketMessage;
import com.shootr.mobile.domain.model.PrintableItem;
import com.shootr.mobile.domain.model.ShotDetailSocketMessage;
import com.shootr.mobile.domain.model.SocketMessage;
import com.shootr.mobile.domain.model.UpdateItemSocketMessage;
import com.shootr.mobile.domain.model.shot.NewShotDetail;
import com.shootr.mobile.domain.model.shot.Shot;
import com.shootr.mobile.ui.model.PrintableModel;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.ui.model.StreamModel;
import com.shootr.mobile.ui.model.mappers.PrintableModelMapper;
import com.shootr.mobile.ui.model.mappers.ShotModelMapper;
import com.shootr.mobile.ui.model.mappers.StreamModelMapper;
import com.shootr.mobile.ui.views.NewShotDetailView;
import com.shootr.mobile.util.ErrorMessageFactory;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;

public class NewShotDetailPresenter implements Presenter, EventReceived.Receiver {

  private final GetNewShotDetailInteractor getNewShotDetailInteractor;
  private final GetCachedShotDetailInteractor getCachedShotDetailInteractor;
  private final SubscribeShotDetailInteractor subscribeShotDetailInteractor;
  private final UnsubscribeShotDetailInteractor unsubscribeShotDetailInteractor;
  private final MarkNiceShotInteractor markNiceShotInteractor;
  private final UnmarkNiceShotInteractor unmarkNiceShotInteractor;
  private final ReshootInteractor reshootInteractor;
  private final UndoReshootInteractor undoReshootInteractor;
  private final ViewShotDetailEventInteractor viewShotEventInteractor;
  private final ClickShotLinkEventInteractor clickShotLinkEventInteractor;
  private final CallCtaCheckInInteractor callCtaCheckInInteractor;
  private final ShotModelMapper shotModelMapper;
  private final PrintableModelMapper printableModelMapper;
  private final StreamModelMapper streamModelMapper;
  private final ErrorMessageFactory errorMessageFactory;
  private final Bus bus;

  private NewShotDetailView view;
  private String idShot;
  private ShotModel mainShot;
  private boolean hasBeenPaused = false;
  private boolean showingParents = false;

  @Inject public NewShotDetailPresenter(GetNewShotDetailInteractor getNewShotDetailInteractor,
      GetCachedShotDetailInteractor getCachedShotDetailInteractor,
      SubscribeShotDetailInteractor subscribeShotDetailInteractor,
      UnsubscribeShotDetailInteractor unsubscribeShotDetailInteractor, MarkNiceShotInteractor markNiceShotInteractor,
      UnmarkNiceShotInteractor unmarkNiceShotInteractor, ReshootInteractor reshootInteractor,
      UndoReshootInteractor undoReshootInteractor,
      ViewShotDetailEventInteractor viewShotEventInteractor,
      ClickShotLinkEventInteractor clickShotLinkEventInteractor,
      CallCtaCheckInInteractor callCtaCheckInInteractor, ShotModelMapper shotModelMapper,
      PrintableModelMapper printableModelMapper, StreamModelMapper streamModelMapper,
      ErrorMessageFactory errorMessageFactory, @Main Bus bus) {
    this.getNewShotDetailInteractor = getNewShotDetailInteractor;
    this.getCachedShotDetailInteractor = getCachedShotDetailInteractor;
    this.subscribeShotDetailInteractor = subscribeShotDetailInteractor;
    this.unsubscribeShotDetailInteractor = unsubscribeShotDetailInteractor;
    this.markNiceShotInteractor = markNiceShotInteractor;
    this.unmarkNiceShotInteractor = unmarkNiceShotInteractor;
    this.reshootInteractor = reshootInteractor;
    this.undoReshootInteractor = undoReshootInteractor;
    this.viewShotEventInteractor = viewShotEventInteractor;
    this.clickShotLinkEventInteractor = clickShotLinkEventInteractor;
    this.callCtaCheckInInteractor = callCtaCheckInInteractor;
    this.shotModelMapper = shotModelMapper;
    this.printableModelMapper = printableModelMapper;
    this.streamModelMapper = streamModelMapper;
    this.errorMessageFactory = errorMessageFactory;
    this.bus = bus;
  }

  public void initialize(NewShotDetailView view, String idShot) {
    this.view = view;
    this.idShot = idShot;
    storeViewCount();
    getCachedShotDetail();
    view.showLoading();
  }

  private void getCachedShotDetail() {
    getCachedShotDetailInteractor.getTimeline(idShot, new Interactor.Callback<NewShotDetail>() {
      @Override public void onLoaded(NewShotDetail shotDetail) {
        if (shotDetail != null) {
          renderShotDetail(shotDetail);
        }
        subscribeShotDetail();
      }
    });
  }

  private void subscribeShotDetail() {
    subscribeShotDetailInteractor.subscribe(idShot, new Interactor.Callback<Boolean>() {
      @Override public void onLoaded(Boolean shouldGetShotDetail) {
        if (shouldGetShotDetail) {
          getShotDetail();
        }
      }
    });
  }

  private void getShotDetail() {
    getNewShotDetailInteractor.getShotDetail(idShot, new Interactor.Callback<Boolean>() {
      @Override public void onLoaded(Boolean aBoolean) {
        /* no-op */
      }
    });
  }

  private void renderShotDetail(NewShotDetail shotDetail) {
    view.hideLoading();
    mainShot = shotModelMapper.transform((Shot) shotDetail.getShot());
    initializeNewShotBarDelegate(mainShot, streamModelMapper.transform(shotDetail.getStream()));
    List<PrintableModel> mainShot =
        printableModelMapper.mapMainShot(Collections.singletonList(shotDetail.getShot()));

    List<PrintableModel> promotedItems =
        printableModelMapper.mapResponseModel(shotDetail.getReplies().getPromoted().getData());
    List<PrintableModel> subscriberItems =
        printableModelMapper.mapResponseModel(shotDetail.getReplies().getSubscribers().getData());
    List<PrintableModel> basicItems =
        printableModelMapper.mapResponseModel(shotDetail.getReplies().getBasic().getData());
    List<PrintableModel> parents =
        printableModelMapper.mapResponseModel(shotDetail.getParents().getData());

    view.renderShotDetail(mainShot, promotedItems, subscriberItems, basicItems, parents);
    view.renderStreamTitle(streamModelMapper.transform(shotDetail.getStream()));
    view.setReplyUsername(((Shot) shotDetail.getShot()).getUserInfo().getUsername());

    if (shotDetail.getParents().getData().size() > 0) {
      view.renderShowParents();
    }
    setupNewShotBox(shotDetail);

    if (((Shot) shotDetail.getShot()).getReshooted()) {
      view.showUndoReshootMenu();
    } else {
      view.showReshootMenu();
    }
  }

  private void setupNewShotBox(NewShotDetail shotDetail) {
    if (shotDetail.getStream().canWrite()) {
      view.showNewShotTextBox();
    } else {
      view.showViewOnlyTextBox();
    }

    if (shotDetail.getStream().canPostPromoted()) {
      view.showPromotedButton();
    }
  }

  private void addNewItem(PrintableItem newItem, String list) {
    if (newItem instanceof Shot) {
      ShotModel shotModel = shotModelMapper.transform((Shot) newItem);
      shotModel.setTimelineGroup(PrintableModel.REPLY);
      switch (list) {
        case ListType.PROMOTED_REPLIES:
          view.addPromotedShot(shotModel);
          break;
        case ListType.SUBSCRIBERS_REPLIES:
          view.addSubscriberShot(shotModel);
          break;
        case ListType.OTHER_REPLIES:
          view.addOtherShot(shotModel);
          break;
        default:
          break;
      }
    }

  }

  private void updateItem(PrintableItem updatedItem, String list) {
    if (updatedItem instanceof Shot) {
      switch (list) {
        case ListType.ITEM_DETAIL:
          view.updateMainItem(shotModelMapper.transform((Shot) updatedItem));
          break;

        case ListType.PARENTS_DETAIL:
          view.updateParent(shotModelMapper.transform((Shot) updatedItem));
          break;

        case ListType.PROMOTED_REPLIES:
          view.updatePromoted(shotModelMapper.transform((Shot) updatedItem));
          break;

        case ListType.SUBSCRIBERS_REPLIES:
          view.updateSubscribers(shotModelMapper.transform((Shot) updatedItem));
          break;

        case ListType.OTHER_REPLIES:
          view.updateOther(shotModelMapper.transform((Shot) updatedItem));
          break;
        default:
          break;
      }
    }
  }

  private void initializeNewShotBarDelegate(ShotModel shotModel, StreamModel streamModel) {
    view.initializeNewShotBarPresenter(shotModel.getStreamId());
    view.setupNewShotBarDelegate(shotModel, streamModel);
  }

  public void markNiceShot(final ShotModel shotModel) {
    markNiceShotInteractor.markNiceShot(shotModel.getIdShot(), new Interactor.CompletedCallback() {
      @Override public void onCompleted() {
        //view.renderNice(shotModel);
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
        //view.renderUnnice(idShot);
      }
    }, new Interactor.ErrorCallback() {
      @Override public void onError(ShootrException error) {
        /* no-op */
      }
    });
  }

  public void reshoot() {
    if (mainShot != null) {
      reshootInteractor.reshoot(mainShot.getIdShot(), new Interactor.CompletedCallback() {
        @Override public void onCompleted() {
          //view.setReshoot(shotModel.getIdShot(), true);
        }
      }, new Interactor.ErrorCallback() {
        @Override public void onError(ShootrException error) {
          view.showError(errorMessageFactory.getMessageForError(error));
        }
      });
    }
  }

  public void undoReshoot() {
    undoReshootInteractor.undoReshoot(mainShot.getIdShot(), new Interactor.CompletedCallback() {
      @Override public void onCompleted() {
        //view.setReshoot(shot.getIdShot(), false);
      }
    }, new Interactor.ErrorCallback() {
      @Override public void onError(ShootrException error) {
        view.showError(errorMessageFactory.getMessageForError(error));
      }
    });
  }

  public void reshoot(final ShotModel shotModel) {
    reshootInteractor.reshoot(shotModel.getIdShot(), new Interactor.CompletedCallback() {
      @Override public void onCompleted() {
        //view.setReshoot(shotModel.getIdShot(), true);
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
        //view.setReshoot(shot.getIdShot(), false);
      }
    }, new Interactor.ErrorCallback() {
      @Override public void onError(ShootrException error) {
        view.showError(errorMessageFactory.getMessageForError(error));
      }
    });
  }

  private void storeViewCount() {
    viewShotEventInteractor.countViewEvent(idShot, new Interactor.CompletedCallback() {
      @Override public void onCompleted() {
                /* no-op */
      }
    });
  }

  public void storeClickCount() {
    clickShotLinkEventInteractor.countClickLinkEvent(idShot, new Interactor.CompletedCallback() {
      @Override public void onCompleted() {
                /* no-op */
      }
    });
  }

  public void shareShot() {
    if (mainShot != null) {
      view.shareShot(mainShot);
    }
  }

  public void callCheckIn() {
    if (mainShot != null) {
      callCtaCheckInInteractor.checkIn(mainShot.getStreamId(), new Interactor.CompletedCallback() {
        @Override public void onCompleted() {
          view.showChecked();
        }
      }, new Interactor.ErrorCallback() {
        @Override public void onError(ShootrException error) {
          view.showError(errorMessageFactory.getMessageForError(error));
        }
      });
    }
  }

  @Override public void resume() {
    bus.register(this);
    if (hasBeenPaused) {
      getCachedShotDetail();
    }

  }

  @Override public void pause() {
    hasBeenPaused = true;
    bus.unregister(this);
  }

  private void unsubscribeShotDetail() {

  }

  @Subscribe @Override public void onEvent(EventReceived.Event event) {
    switch (event.getMessage().getEventType()) {

      case SocketMessage.SHOT_DETAIL:
        ShotDetailSocketMessage shotDetailSocketMessage =
            (ShotDetailSocketMessage) event.getMessage();
        renderShotDetail(shotDetailSocketMessage.getData());
        break;

      case SocketMessage.UPDATE_ITEM_DATA:
        UpdateItemSocketMessage updateItemSocketMessage =
            (UpdateItemSocketMessage) event.getMessage();
        if (updateItemSocketMessage.getData().getItem() instanceof Shot) {
          updateItem(updateItemSocketMessage.getData().getItem(),
              updateItemSocketMessage.getData().getList());
        }
        break;

      case SocketMessage.NEW_ITEM_DATA:
        NewItemSocketMessage newItemSocketMessage = (NewItemSocketMessage) event.getMessage();
        if (newItemSocketMessage.getData().getItem() instanceof Shot) {
          addNewItem(newItemSocketMessage.getData().getItem(),
              newItemSocketMessage.getData().getList());
        }
        break;
      default:
        break;
    }
  }

  public void showParentsPressed() {
    if (showingParents) {
      showingParents = false;
      view.hideParents();
      view.renderShowParents();
    } else {
      showingParents = true;
      view.showParents();
      view.renderHideParents();
    }
  }
}
