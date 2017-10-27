package com.shootr.mobile.ui.views;

import com.shootr.mobile.ui.model.PollModel;

public interface StreamPollView {

  void showPollIndicatorWithViewAction(PollModel pollModel);

  void showPollIndicatorWithVoteAction(PollModel pollModel);

  void showPollIndicatorWithResultsAction(PollModel pollModel);

  void hidePollIndicator();

  void showError(String message);

  void goToPollVote(String idStre, String streamAuthorIdUser);

  void goToPollResults(String idPoll, String idStream);

  void goToPollLiveResults(String idPoll, String idStream);

  void goToOptionVoted(PollModel pollModel);

  void goToHiddenResults(String question);
}
