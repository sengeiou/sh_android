package com.shootr.mobile.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.holders.PollOptionHolder;
import com.shootr.mobile.ui.adapters.listeners.OnPollOptionClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnPollOptionLongClickListener;
import com.shootr.mobile.ui.model.PollOptionModel;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.InitialsLoader;
import java.util.List;

public class PollOptionAdapter extends BaseAdapter {

  private Context context;
  private List<PollOptionModel> pollOptionModels;
  private OnPollOptionClickListener pollOptionClickListener;
  private OnPollOptionLongClickListener pollOptionLongClickListener;
  private ImageLoader imageLoader;
  private InitialsLoader initialsLoader;
  private final PollOptionHolder pollOptionHolder;

  public PollOptionAdapter(Context context, OnPollOptionClickListener pollOptionClickListener,
      OnPollOptionLongClickListener pollOptionLongClickListener, ImageLoader imageLoader,
      InitialsLoader initialsLoader, PollOptionHolder pollOptionHolder) {
    this.context = context;
    this.pollOptionClickListener = pollOptionClickListener;
    this.pollOptionLongClickListener = pollOptionLongClickListener;
    this.imageLoader = imageLoader;
    this.initialsLoader = initialsLoader;
    this.pollOptionHolder = pollOptionHolder;
  }

  public void setPollOptionModels(List<PollOptionModel> pollOptionModels) {
    this.pollOptionModels = pollOptionModels;
  }

  @Override public int getCount() {
    return pollOptionModels == null ? 0 : pollOptionModels.size();
  }

  @Override public Object getItem(int position) {
    return pollOptionModels == null ? null : pollOptionModels.get(position);
  }

  @Override public long getItemId(int position) {
    return 0;
  }

  @Override public View getView(int position, View view, ViewGroup viewGroup) {
    if (view == null) {
      LayoutInflater inflater =
          (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      view = inflater.inflate(R.layout.item_poll_option_vote, viewGroup, false);
    }
    final PollOptionModel pollOptionModel = pollOptionModels.get(position);
    pollOptionHolder.bindModel(view, pollOptionModel, pollOptionClickListener,
        pollOptionLongClickListener, imageLoader, initialsLoader);
    return view;
  }
}
