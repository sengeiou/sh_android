package com.shootr.mobile.ui.views;

import com.shootr.mobile.ui.model.PollModel;
import com.shootr.mobile.ui.views.base.LoadDataView;

public interface PollVoteView  extends LoadDataView {

  void renderPoll(PollModel pollModel);

  void showPollVotesTimeToExpire(Long votes, Long timeToExpire, boolean isExpired);

  void ignorePoll();

  void goToResults(String idPoll, String idStream, boolean hasVoted);

  void goToHiddenResults(String pollQuestion);

  void showError(String message);

  void showTimeoutAlert();

  void showResultsWithoutVotingDialog();

  void showPublicVotePrivacy();

  void showPrivateVotePrivacy();

  void showPrivateVotePrivacyDisabled();

  void showNotificationsScreen();

  void showUserCannotVoteAlert();

  void hideFooter();

  void hideShowResultsMenu();

  void goToVotedOption(PollModel pollModel);
}
