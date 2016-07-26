package com.shootr.mobile.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.holders.PollVoteViewHolder;
import com.shootr.mobile.ui.adapters.listeners.OnPollOptionClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnPollOptionLongClickListener;
import com.shootr.mobile.ui.model.PollOptionModel;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.InitialsLoader;
import java.util.List;

public class PollVoteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  private List<PollOptionModel> pollOptionModels;
  private final OnPollOptionClickListener pollOptionClickListener;
  private final OnPollOptionLongClickListener pollOptionLongClickListener;
  private final ImageLoader imageLoader;
  private final InitialsLoader initialsLoader;

  public PollVoteAdapter(OnPollOptionClickListener pollOptionClickListener,
      OnPollOptionLongClickListener pollOptionLongClickListener, ImageLoader imageLoader,
      InitialsLoader initialsLoader) {
    this.pollOptionClickListener = pollOptionClickListener;
    this.pollOptionLongClickListener = pollOptionLongClickListener;
    this.imageLoader = imageLoader;
    this.initialsLoader = initialsLoader;
  }

  public void setPollOptionModels(List<PollOptionModel> pollOptionModels) {
    this.pollOptionModels = pollOptionModels;
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.item_poll_option_vote, parent, false);
    return new PollVoteViewHolder(view, pollOptionClickListener,
        pollOptionLongClickListener, imageLoader, initialsLoader);
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    ((PollVoteViewHolder) holder).render(pollOptionModels.get(position));
  }

  @Override public int getItemCount() {
    return pollOptionModels != null ? pollOptionModels.size() : 0;
  }
}
