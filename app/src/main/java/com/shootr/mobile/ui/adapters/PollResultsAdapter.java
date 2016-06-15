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
import com.shootr.mobile.ui.adapters.holders.PollResultViewHolder;
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

  private List<Object> items;
  private final ImageLoader imageLoader;
  private final InitialsLoader initialsLoader;
  private final PercentageUtils percentageUtils;
  private final Context context;

  private Long totalVotes = 0L;
  private int screenHeight;
  private int lastAnimatedPosition = -1;

  public PollResultsAdapter(Context context, ImageLoader imageLoader, InitialsLoader initialsLoader,
      PercentageUtils percentageUtils) {
    this.imageLoader = imageLoader;
    this.initialsLoader = initialsLoader;
    this.percentageUtils = percentageUtils;
    this.context = context;
  }

  @Override public int getItemViewType(int position) {
    if (items.get(position) instanceof PollModel) {
      return TYPE_POLL;
    } else {
      return TYPE_POLL_OPTION;
    }
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    RecyclerView.ViewHolder viewHolder;
    View view;

    if (viewType == TYPE_POLL) {
      view = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.poll_question_view_holder, parent, false);
      viewHolder = new PollQuestionViewHolder(view);
    } else {
      view = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.item_poll_result, parent, false);
      viewHolder =
          new PollResultViewHolder(view, imageLoader, initialsLoader, percentageUtils, totalVotes);
    }

    return viewHolder;
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    runEnterAnimation(holder.itemView, position);
    if (holder.getItemViewType() == TYPE_POLL) {
      ((PollQuestionViewHolder) holder).render((PollModel) items.get(position));
    } else {
      ((PollResultViewHolder) holder).render((PollOptionModel) items.get(position), position);
    }
  }

  @Override public int getItemCount() {
    return items != null ? items.size() : 0;
  }

  public void setPollModel(PollModel model) {
    items = new ArrayList<>();
    items.add(model);
    for (PollOptionModel pollOptionModel : model.getPollOptionModels()) {
      items.add(pollOptionModel);
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
