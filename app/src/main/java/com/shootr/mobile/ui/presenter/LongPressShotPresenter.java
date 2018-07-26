package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.shot.DeleteShotInteractor;
import com.shootr.mobile.domain.interactor.stream.GetLocalStreamInteractor;
import com.shootr.mobile.domain.model.stream.Stream;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.ui.model.PrintableModel;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.model.mappers.UserModelMapper;
import com.shootr.mobile.ui.views.streamtimeline.LongPressView;
import com.shootr.mobile.util.ErrorMessageFactory;
import java.util.ArrayList;
import java.util.HashMap;
import javax.inject.Inject;

public class LongPressShotPresenter implements Presenter {

  public static final int HIGHLIGHT = 0;
  public static final int DISMISS_HIGHLIGHT = 1;
  public static final int RESHOOT = 2;
  public static final int UNDO_RESHOOT = 3;
  public static final int SHARE_VIA = 4;
  public static final int COPY_TEXT = 5;
  public static final int DELETE = 6;
  public static final int REPORT = 7;

  private static final String EN_LOCALE = "en";
  private final DeleteShotInteractor deleteShotInteractor;
  private final ErrorMessageFactory errorMessageFactory;
  private final SessionRepository sessionRepository;
  private final UserModelMapper userModelMapper;
  private final GetLocalStreamInteractor getLocalStreamInteractor;
  private ArrayList<String> fixedItemsIds;

  private LongPressView longPressView;
  private String idStream;

  @Inject public LongPressShotPresenter(DeleteShotInteractor deleteShotInteractor,
      ErrorMessageFactory errorMessageFactory, SessionRepository sessionRepository,
      UserModelMapper userModelMapper, GetLocalStreamInteractor getLocalStreamInteractor) {
    this.deleteShotInteractor = deleteShotInteractor;
    this.errorMessageFactory = errorMessageFactory;
    this.sessionRepository = sessionRepository;
    this.userModelMapper = userModelMapper;
    this.getLocalStreamInteractor = getLocalStreamInteractor;
  }

  protected void setView(LongPressView longPressView) {
    this.longPressView = longPressView;
  }

  public void initialize(LongPressView longPressView, String idStream) {
    setView(longPressView);
    fixedItemsIds = new ArrayList<>();
    this.idStream = idStream;
  }

  public void setFixedItemsIds(ArrayList fixedItemsIds) {
    this.fixedItemsIds = fixedItemsIds;
  }

  public void report(ShotModel shotModel) {
    UserModel userModel = userModelMapper.transform(sessionRepository.getCurrentUser());
    if (userModel.isEmailConfirmed()) {
      longPressView.handleReport(sessionRepository.getSessionToken(), shotModel);
    } else {
      longPressView.showEmailNotConfirmedError();
    }
  }

  public boolean isEnglishLocale(String locale) {
    return locale.equals(EN_LOCALE);
  }

  public void onLongClickPressed(final ShotModel shotModel) {
    getLocalStreamInteractor.loadStream(idStream, new GetLocalStreamInteractor.Callback() {
      @Override public void onLoaded(Stream stream) {

        HashMap<Integer, Boolean> menus = new HashMap<>();

        menus.put(HIGHLIGHT, stream.canPinItem() && !shotModel.getTimelineGroup().equals(
            PrintableModel.HIGHLIGHTED_GROUP));
        menus.put(DISMISS_HIGHLIGHT, stream.canPinItem() && shotModel.getTimelineGroup().equals(
            PrintableModel.HIGHLIGHTED_GROUP) || stream.canPinItem() && fixedItemsIds.contains(shotModel.getIdShot()));
        menus.put(RESHOOT, !shotModel.isReshooted());
        menus.put(UNDO_RESHOOT, shotModel.isReshooted());
        menus.put(SHARE_VIA, true);
        menus.put(COPY_TEXT, true);
        if (shotModel.isMine() || sessionRepository.getCurrentUserId()
            .equals(stream.getAuthorId())) {
          menus.put(DELETE, true);
        } else {
          menus.put(DELETE, false);
        }

        menus.put(REPORT, !shotModel.isMine());

        longPressView.showContextMenu(menus, shotModel);
      }
    });
  }

  public void deleteShot(final ShotModel shotModel) {
    deleteShotInteractor.deleteShot(shotModel.getIdShot(), new Interactor.CompletedCallback() {
      @Override public void onCompleted() {
        longPressView.notifyDeletedShot(shotModel);
      }
    }, new Interactor.ErrorCallback() {
      @Override public void onError(ShootrException error) {
        String errorMessage = errorMessageFactory.getMessageForError(error);
        longPressView.showError(errorMessage);
      }
    });
  }

  public void reportClicked(String language, String sessionToken, ShotModel shotModel) {
    if (isEnglishLocale(language)) {
      longPressView.goToReport(sessionToken, shotModel);
    } else {
      longPressView.showAlertLanguageSupportDialog(sessionToken, shotModel);
    }
  }

  @Override public void resume() {
    /* no-op */
  }

  @Override public void pause() {
    /* no-op */
  }
}
