package com.shootr.mobile.ui.views;

import com.shootr.mobile.ui.model.PollModel;
import com.shootr.mobile.ui.views.base.LoadDataView;

public interface PollVoteView  extends LoadDataView {

  void renderPoll(PollModel pollModel);

  void showPollVotesTimeToExpire(Long votes, Long timeToExpire);

  void ignorePoll();

  void goToResults(String idPoll, String idStream);

  void goToStreamTimeline(String idStream);

  void showError(String message);

  void showTimeoutAlert();

  void showViewResultsButton();

  void showResultsWithoutVotingDialog();

  void showPublicVotePrivacy();

  void showPrivateVotePrivacy();

  void showPrivateVotePrivacyDisabled();
}
