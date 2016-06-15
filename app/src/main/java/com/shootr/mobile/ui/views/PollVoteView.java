package com.shootr.mobile.ui.views;

import com.shootr.mobile.ui.model.PollModel;

public interface PollVoteView {

  void renderPoll(PollModel pollModel);

  void ignorePoll();

  void goToResults(String idStream);

  void showError(String message);
}
