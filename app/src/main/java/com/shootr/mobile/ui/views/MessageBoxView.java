package com.shootr.mobile.ui.views;

import com.shootr.mobile.ui.model.UserModel;
import java.util.List;

public interface MessageBoxView {

  void showSendButton();

  void hideSendButton();

  void hideKeyboard();

  void setRemainingCharactersCount(int remainingCharacters);

  void setRemainingCharactersColorValid();

  void setRemainingCharactersColorInvalid();

  void renderMentionSuggestions(List<UserModel> mentionSuggestions);

  void showMentionSuggestions();

  void hideMentionSuggestions();

  void mentionUser(String comment);

  void setCursorToEndOfText();

  void showError(String message);

  void clearTextBox();

  void enableSendButton();

  void disableSendButton();

  void hidePromotedButton();

  void showPromotedButton();

  void showPromotedBadge();
}
