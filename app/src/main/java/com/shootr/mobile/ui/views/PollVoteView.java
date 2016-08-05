package com.shootr.mobile.ui.views;

import com.shootr.mobile.ui.model.PollModel;
import com.shootr.mobile.ui.views.base.LoadDataView;

public interface PollVoteView  extends LoadDataView {

  void renderPoll(PollModel pollModel);

  void showPollVotes(Long votes);

  void ignorePoll();

  void goToResults(String idStream);

  void showError(String message);

  void showTimeoutAlert();

  void showViewResultsButton();

  void showResultsWithoutVotingDialog();
}
