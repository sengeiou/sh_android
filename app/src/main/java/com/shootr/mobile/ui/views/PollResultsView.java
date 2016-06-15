package com.shootr.mobile.ui.views;

import com.shootr.mobile.ui.model.PollModel;

public interface PollResultsView {

  void renderPollResults(PollModel pollModel);

  void showError(String error);

  void ignorePoll();
}
