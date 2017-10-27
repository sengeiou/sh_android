package com.shootr.mobile.ui.views;

import com.shootr.mobile.ui.model.PollModel;
import com.shootr.mobile.ui.model.PollOptionModel;
import com.shootr.mobile.ui.views.base.LoadDataView;

public interface PollResultsView extends LoadDataView {

  void renderPollResults(PollModel pollModel, boolean showShare);

  void showError(String error);

  void ignorePoll();

  void share(PollModel pollModel);

  void shareVoted(PollModel pollModel, PollOptionModel pollOptionModel);

  void showPollVotesTimeToExpire(Long pollVotes, Long timeToExpire, boolean isExpired);

  void goToStreamTimeline(String idStream);

  void showClosed();

  void showSharedPoll();

  void hideFooter();
}
