package com.shootr.mobile.ui.views;

import com.shootr.mobile.ui.model.PrintableModel;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.ui.model.StreamModel;
import java.util.List;

public interface NewShotDetailView {

  void renderShotDetail(List<PrintableModel> mainShot, List<PrintableModel> promotedItem,
      List<PrintableModel> subscribersItem, List<PrintableModel> basicItems,
      List<PrintableModel> parents);

  void showError(String messageForError);

  void initializeNewShotBarPresenter(String idStream);

  void setupNewShotBarDelegate(ShotModel shotModel);

  void shareShot(ShotModel mainShot);

  void showChecked();

  void renderStreamTitle(StreamModel streamModel);

  void setReplyUsername(String username);

  void renderShowParents();

  void renderHideParents();

  void hideParents();

  void showParents();

  void updateMainItem(ShotModel shotModel);

  void updateParent(ShotModel shotModel);

  void updatePromoted(ShotModel shotModel);

  void updateSubscribers(ShotModel shotModel);

  void updateOther(ShotModel shotModel);

  void addPromotedShot(ShotModel shotModel);

  void addSubscriberShot(ShotModel shotModel);

  void addOtherShot(ShotModel shotModel);

  void showLoading();

  void hideLoading();

  void showNewShotTextBox();

  void showViewOnlyTextBox();

  void showUndoReshootMenu();

  void showReshootMenu();
}
