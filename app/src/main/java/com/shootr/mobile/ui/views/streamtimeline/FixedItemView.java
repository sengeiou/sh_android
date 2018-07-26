package com.shootr.mobile.ui.views.streamtimeline;

import com.shootr.mobile.ui.model.PollModel;
import com.shootr.mobile.ui.model.ShotModel;

public interface FixedItemView {

  void hideFixedShot();

  void showDismissDialog(ShotModel shotModel);

  void setHighlightShotBackground(Boolean isAdmin);

  void renderNewFixedItem(ShotModel shotModel);

  void goToPollResults(String idPoll, String idStream);

  void goToPollLiveResults(String idPoll, String idStream);

  void goToPollVote(String idStream, String authorId);

  void goToOptionVoted(PollModel pollModel);

  void goToHiddenResults(String question);
}
