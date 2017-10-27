package com.shootr.mobile.ui.views;

import com.shootr.mobile.ui.model.PollModel;
import com.shootr.mobile.ui.model.PollOptionModel;

public interface PollOptionVotedView {

  void renderPollOptionVoted(PollOptionModel pollOptionVoted);

  void showPollVotesTimeToExpire(Long timeToExpire, boolean isExpired);

  void shareVoted(PollModel pollModel, PollOptionModel pollOptionModel);

  void showLegalText();
}
