package com.shootr.mobile.ui.views.streamtimeline;

import com.shootr.mobile.ui.model.ShotModel;
import java.util.HashMap;

public interface LongPressView {

  void handleReport(String sessionToken, ShotModel shotModel);

  void showEmailNotConfirmedError();

  void notifyDeletedShot(ShotModel shotModel);

  void showError(String errorMessage);

  void goToReport(String sessionToken, ShotModel shotModel);

  void showAlertLanguageSupportDialog(String sessionToken, ShotModel shotModel);

  void showContextMenu(HashMap<Integer, Boolean> menus, ShotModel shotModel);
}
