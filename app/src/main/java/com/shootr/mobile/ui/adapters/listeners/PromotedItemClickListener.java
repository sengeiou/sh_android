package com.shootr.mobile.ui.adapters.listeners;

import com.shootr.mobile.ui.model.PollModel;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.ui.model.UserModel;

public interface PromotedItemClickListener {

  void markSeen(String type, String idItem);

  void onPollClick(PollModel pollModel);

  void onHighlightedClick(ShotModel shotModel);

  void onPromotedShotClick(ShotModel shotModel);

  void onAddPromotedPressed();

  void onUserFollowingClick(UserModel user);
}
