package com.shootr.mobile.ui.adapters;

import android.content.Context;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.holders.LegalTextInPollViewHolder;
import com.shootr.mobile.ui.adapters.holders.PollResultViewHolder;
import com.shootr.mobile.ui.adapters.holders.SharePollVotedViewHolder;
import com.shootr.mobile.ui.adapters.listeners.OnPollOptionClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnPollStreamTitleClickListener;
import com.shootr.mobile.ui.model.PollModel;
import com.shootr.mobile.ui.model.PollOptionModel;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.InitialsLoader;
import com.shootr.mobile.util.PercentageUtils;
import java.util.ArrayList;
import java.util.List;

public class PollResultsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  private static final int TYPE_POLL = 0;
  private static final int TYPE_POLL_OPTION = 1;
  private static final int TYPE_SHARE_POLL = 2;
  private static final int TYPE_FOOTER_LEGAL_TEXT = 3;

  private List<Object> items;
  private final OnPollOptionClickListener onPollOptionClickListener;
  private final OnPollStreamTitleClickListener onPollStreamTitleClickListener;
  private final View.OnClickListener onClickListener;
  private final ImageLoader imageLoader;
  private final InitialsLoader initialsLoader;
  private final PercentageUtils percentageUtils;
  private final Context context;

  private Long totalVotes = 0L;
  private boolean showShared;
  private int screenHeight;
  private int lastAnimatedPosition = -1;

  public PollResultsAdapter(OnPollOptionClickListener onPollOptionClickListener,
      OnPollStreamTitleClickListener onPollStreamTitleClickListener,
      View.OnClickListener onClickListener, Context context, ImageLoader imageLoader,
      InitialsLoader initialsLoader, PercentageUtils percentageUtils) {
    this.onPollOptionClickListener = onPollOptionClickListener;
    this.onPollStreamTitleClickListener = onPollStreamTitleClickListener;
    this.onClickListener = onClickListener;
    this.imageLoader = imageLoader;
    this.initialsLoader = initialsLoader;
    this.percentageUtils = percentageUtils;
    this.context = context;
  }

  @Override public int getItemViewType(int position) {
    if (items.get(position) instanceof PollModel) {
      return TYPE_SHARE_POLL;
    } else if (items.get(position) instanceof Boolean) {
      return TYPE_POLL;
    } else if (items.get(position) instanceof PollOptionModel) {
      return TYPE_POLL_OPTION;
    } else if (items.get(position) instanceof String) {
      return TYPE_FOOTER_LEGAL_TEXT;
    }
    return -1;
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    RecyclerView.ViewHolder viewHolder = null;
    View view;

    if (viewType == TYPE_POLL) {
      view = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.poll_question_view_holder, parent, false);
      viewHolder = new PollQuestionViewHolder(view);
    } else if (viewType == TYPE_POLL_OPTION) {
      view = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.item_poll_result, parent, false);
      viewHolder =
          new PollResultViewHolder(view, onPollOptionClickListener, imageLoader, initialsLoader,
              percentageUtils, totalVotes, showShared);
    } else if (viewType == TYPE_SHARE_POLL) {
      view =
          LayoutInflater.from(parent.getContext()).inflate(R.layout.item_share_poll, parent, false);
      viewHolder = new SharePollVotedViewHolder(view, onClickListener, imageLoader);
    } else if (viewType == TYPE_FOOTER_LEGAL_TEXT) {
      view =
          LayoutInflater.from(parent.getContext()).inflate(R.layout.item_legal_text, parent, false);
      viewHolder = new LegalTextInPollViewHolder(view);
    }

    return viewHolder;
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    runEnterAnimation(holder.itemView, position);
    if (holder.getItemViewType() == TYPE_POLL) {
      ((PollQuestionViewHolder) holder).render();
    } else if (holder.getItemViewType() == TYPE_POLL_OPTION) {
      ((PollResultViewHolder) holder).render((PollOptionModel) items.get(position), position);
    } else if (holder.getItemViewType() == TYPE_SHARE_POLL) {
      ((SharePollVotedViewHolder) holder).render((PollModel) items.get(position));
    } else if (holder.getItemViewType() == TYPE_FOOTER_LEGAL_TEXT) {
      ((LegalTextInPollViewHolder) holder).render((String) items.get(position));
    }
  }

  @Override public int getItemCount() {
    return items != null ? items.size() : 0;
  }

  public void setPollModel(PollModel model, boolean showShare, String legalText) {
    this.showShared = showShare;
    items = new ArrayList<>();
    if (showShare) {
      items.add(model);
    }
    items.add(true);
    for (PollOptionModel pollOptionModel : model.getPollOptionModels()) {
      items.add(pollOptionModel);
    }
    if (model.isVerifiedPoll()) {
      items.add(legalText);
    }
    calculateTotalVotes(model.getPollOptionModels());
  }

  private void calculateTotalVotes(List<PollOptionModel> models) {
    totalVotes = 0L;
    for (PollOptionModel model : models) {
      totalVotes += model.getVotes();
    }
  }

  private void runEnterAnimation(View view, int position) {
    if (position >= getItemCount()) {
      return;
    }

    if (position > lastAnimatedPosition) {
      lastAnimatedPosition = position;
      view.setTranslationY(getScreenHeight(context));
      view.animate()
          .translationY(0)
          .setInterpolator(new DecelerateInterpolator(3.f))
          .setDuration(1000)
          .start();
    }
  }

  private int getScreenHeight(Context c) {
    if (screenHeight == 0) {
      WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
      Display display = wm.getDefaultDisplay();
      Point size = new Point();
      display.getSize(size);
      screenHeight = size.y;
    }
    return screenHeight;
  }
}
