package com.shootr.mobile.ui.adapters.streamtimeline.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.ahamed.multiviewadapter.BaseViewHolder;
import com.shootr.mobile.R;
import com.shootr.mobile.domain.model.PrintableItem;
import com.shootr.mobile.domain.model.poll.PollStatus;
import com.shootr.mobile.ui.adapters.listeners.PromotedItemClickListener;
import com.shootr.mobile.ui.model.PollModel;
import com.shootr.mobile.util.ImageLoader;

public class TimelinePollViewHolder extends BaseViewHolder<PollModel> {

  private static final int DEFAULT_POLL_IMAGE = R.drawable.poll;

  private final PromotedItemClickListener promotedItemClickListener;
  private final ImageLoader imageLoader;

  @BindView(R.id.poll_avatar) ImageView avatar;
  @BindView(R.id.seen) View seen;
  @BindView(R.id.name) TextView name;
  @BindString(R.string.poll) String pollResource;

  public TimelinePollViewHolder(View itemView, PromotedItemClickListener promotedItemClickListener,
      ImageLoader imageLoader) {
    super(itemView);
    this.promotedItemClickListener = promotedItemClickListener;
    this.imageLoader = imageLoader;
    ButterKnife.bind(this, itemView);
  }

  public void render(final PollModel pollModel) {
    imageLoader.loadImageWithId(avatar, DEFAULT_POLL_IMAGE);
    avatar.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        promotedItemClickListener.markSeen(PrintableItem.POLL, pollModel.getIdPoll());
        promotedItemClickListener.onPollClick(pollModel);
      }
    });

    name.setText(pollResource);
    setupSeenMark(pollModel);
    setupPollAction(pollModel);
  }

  private void setupSeenMark(PollModel pollModel) {
    if (pollModel.getSeen()) {
      seen.setVisibility(View.INVISIBLE);
    } else {
      seen.setVisibility(View.VISIBLE);
    }
  }

  private void setupPollAction(PollModel pollModel) {
    if (pollModel.getStatus().equals(PollStatus.CLOSED)) {
      pollModel.setAction(PollModel.RESULTS);
      if (pollModel.isHideResults()) {
        pollModel.setAction(PollModel.VIEW);
      }
      return;
    }
    handleVoteStatus(pollModel);
  }

  private void handleVoteStatus(PollModel pollModel) {
    if (!pollModel.canVote() || pollModel.getVoteStatus().equals(PollStatus.HASSEENRESULTS)) {
      pollModel.setAction(PollModel.VIEW);
    } else {
      pollModel.setAction(PollModel.VOTE);
    }
  }
}
