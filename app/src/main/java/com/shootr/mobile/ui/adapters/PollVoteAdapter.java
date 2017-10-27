package com.shootr.mobile.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.holders.LegalTextInPollViewHolder;
import com.shootr.mobile.ui.adapters.holders.PollVoteViewHolder;
import com.shootr.mobile.ui.adapters.listeners.OnPollOptionClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnPollOptionLongClickListener;
import com.shootr.mobile.ui.model.PollOptionModel;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.InitialsLoader;
import java.util.ArrayList;
import java.util.List;

public class PollVoteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  public static final int TYPE_POLL_OPTION = 0;
  public static final int TYPE_FOOTER_LEGAL_TEXT = 1;
  private List<Object> items;
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

  @Override public int getItemViewType(int position) {
    if (items.get(position) instanceof PollOptionModel) {
      return TYPE_POLL_OPTION;
    } else if (items.get(position) instanceof String) {
      return TYPE_FOOTER_LEGAL_TEXT;
    }
    return -1;
  }

  public void setPollOptionModels(List<PollOptionModel> pollOptionModels, String legalText) {
    items = new ArrayList<>();
    for (PollOptionModel pollOptionModel : pollOptionModels) {
      items.add(pollOptionModel);
    }
    if (legalText != null) {
      items.add(legalText);
    }
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    RecyclerView.ViewHolder viewHolder = null;
    View view;
    if (viewType == TYPE_POLL_OPTION) {
      view = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.item_poll_option_vote, parent, false);
      viewHolder =
          new PollVoteViewHolder(view, pollOptionClickListener, pollOptionLongClickListener,
              imageLoader, initialsLoader);
    } else if (viewType == TYPE_FOOTER_LEGAL_TEXT) {
      view =
          LayoutInflater.from(parent.getContext()).inflate(R.layout.item_legal_text, parent, false);
      viewHolder = new LegalTextInPollViewHolder(view);
    }
    return viewHolder;
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    if (holder.getItemViewType() == TYPE_POLL_OPTION) {
      ((PollVoteViewHolder) holder).render((PollOptionModel) items.get(position));
    } else if (holder.getItemViewType() == TYPE_FOOTER_LEGAL_TEXT) {
      ((LegalTextInPollViewHolder) holder).render((String) items.get(position));
    }
  }

  @Override public int getItemCount() {
    return items != null ? items.size() : 0;
  }
}
