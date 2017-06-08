package com.shootr.mobile.ui.views;

import com.shootr.mobile.ui.model.PollModel;
import com.shootr.mobile.ui.views.base.LoadDataView;

public interface PollResultsView extends LoadDataView {

  void renderPollResults(PollModel pollModel);

  void showError(String error);

  void ignorePoll();

  void share(PollModel pollModel);

  void showPollVotesTimeToExpire(Long pollVotes, Long timeToExpire);

  void goToStreamTimeline(String idStream);

}
