package com.shootr.mobile.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.holders.MessageChannelViewHolder;
import com.shootr.mobile.ui.adapters.listeners.ChannelClickListener;
import com.shootr.mobile.ui.model.PrivateMessageChannelModel;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.InitialsLoader;
import java.util.List;

public class MessageChannelListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  private List<PrivateMessageChannelModel> privateMessageChannelModels;
  private final ImageLoader imageLoader;
  private final InitialsLoader initialsLoader;
  private final ChannelClickListener channelClickListener;
  private final AndroidTimeUtils timeUtils;

  public MessageChannelListAdapter(ImageLoader imageLoader, InitialsLoader initialsLoader,
      ChannelClickListener channelClickListener, AndroidTimeUtils timeUtils) {
    this.imageLoader = imageLoader;
    this.initialsLoader = initialsLoader;
    this.channelClickListener = channelClickListener;
    this.timeUtils = timeUtils;
  }

  public void setPrivateMessageChannelModels(List<PrivateMessageChannelModel> models) {
    this.privateMessageChannelModels = models;
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.item_message_channel, parent, false);
    return new MessageChannelViewHolder(view, imageLoader, initialsLoader, channelClickListener,
        timeUtils);
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    ((MessageChannelViewHolder) holder).render(privateMessageChannelModels.get(position));
  }

  @Override public int getItemCount() {
    return privateMessageChannelModels != null ? privateMessageChannelModels.size() : 0;
  }

  public void clear() {
    if (privateMessageChannelModels != null) {
      privateMessageChannelModels.clear();
    }
  }
}
