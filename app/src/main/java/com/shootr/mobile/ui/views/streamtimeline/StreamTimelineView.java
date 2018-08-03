package com.shootr.mobile.ui.views.streamtimeline;

import com.shootr.mobile.ui.model.ExternalVideoModel;
import com.shootr.mobile.ui.model.PrintableModel;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.ui.model.StreamModel;
import java.util.ArrayList;
import java.util.List;

public interface StreamTimelineView {

  void renderItems(List<PrintableModel> items, PrintableModel itemForReposition, int offset);

  void renderExternalVideo(ExternalVideoModel externalVideoModel);

  void renderPolls(List<PrintableModel> items);

  void renderHighlightedItems(List<PrintableModel> items);

  void addNewItems(List<PrintableModel> items);

  void addOldItems(List<PrintableModel> oldItems);

  void updateItem(PrintableModel updatedItem);

  void updateNicestItem(PrintableModel updatedItem);

  void showGenericItemsMenuItem();

  void showImportantItemsMenuItem();

  void showNicestItemsMenuItem();

  void hidePinnedMessage();

  void showPinMessageNotification(String message);

  void showViewOnlyTextBox();

  void showNewShotTextBox();

  void showChecked();

  void openCtaAction(String link);

  void storeCtaClickLink(ShotModel shotModel);

  void showFilterAlert();

  void renderNice(PrintableModel shotModel);

  void renderUnnice(String idShot);

  void setReshoot(String idShot, boolean mark);

  void showLoadingOldShots();

  void hideLoadingOldShots();

  void showCheckingForShots();

  void setFixedItemsIds(ArrayList<String> fixedItemsIds);

  void hideCheckingForShots();

  void setTitle(String title);

  void sendAnalythicsEnterTimeline();

  void showNewShotsIndicator(Integer numberNewShots);

  void hideNewShotsIndicator();

  void setRemainingCharactersCount(int remainingCharacters);

  void setRemainingCharactersColorValid();

  void setRemainingCharactersColorInvalid();

  void showEmpty();

  void showEmptyNicest();

  void showError(String errorMessage);

  void addMyItem(PrintableModel shotModel);

  void smoothToTop();

  void clearTimeline();

  void removeHighlightedItem();

  void goToTop();

  void refreshShotsInfo();

  void setupFilterShowcase();

  void showWatchingPeopleCount(Integer[] peopleWatchingCount);

  void hideWatchingPeopleCount();

  void hideEmpty();

  void updateFixedItem(List<PrintableModel> printableModels);

  void showVideoVisibilityMenu();

  void resumeVideo();

  void handleNewNicestItem(PrintableModel shotModel);

  void hideExternalVideo();

  void showPromotedButton();

  void addNewHighlighted(List<PrintableModel> printableModels);

  void addNewPoll(List<PrintableModel> printableModels);

  void addNewPromoted(List<PrintableModel> printableModels);

  void showPromotedList();

  void updateHighlighted(PrintableModel printableModel);

  void updatePoll(PrintableModel printableModel);

  void updatePromoted(PrintableModel printableModel);

  void renderPromoteds(List<PrintableModel> printableModels);

  void hidePromotedButton();

  void renderFollowings(List<PrintableModel> printableModels);

  void addNewFollowing(List<PrintableModel> printableModels);

  void updateFollowing(PrintableModel printableModel);

  void goToNewPromotedShot(StreamModel streamModel);
}
