package com.shootr.mobile.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.holders.MessageViewHolder;
import com.shootr.mobile.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnImageClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnUrlClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnUsernameClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnVideoClickListener;
import com.shootr.mobile.ui.model.PrivateMessageModel;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.ShotTextSpannableBuilder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MessagesTimelineAdapter extends RecyclerView.Adapter<MessageViewHolder> {

  private static final int ITEM_POSITION_WITHOUT_HEADER = 0;

  private final ImageLoader imageLoader;
  private final OnAvatarClickListener avatarClickListener;
  private final OnVideoClickListener videoClickListener;
  private final OnUsernameClickListener onUsernameClickListener;
  private final AndroidTimeUtils timeUtils;
  private final ShotTextSpannableBuilder shotTextSpannableBuilder;
  private final OnImageClickListener onImageClickListener;
  private final OnUrlClickListener onShotUrlClickListener;

  private List<PrivateMessageModel> messages;

  public MessagesTimelineAdapter(ImageLoader imageLoader, AndroidTimeUtils timeUtils,
      OnAvatarClickListener avatarClickListener, OnVideoClickListener videoClickListener,
      OnUsernameClickListener onUsernameClickListener, OnImageClickListener onImageClickListener,
      OnUrlClickListener onShotUrlClickListener) {
    this.imageLoader = imageLoader;
    this.avatarClickListener = avatarClickListener;
    this.videoClickListener = videoClickListener;
    this.onUsernameClickListener = onUsernameClickListener;
    this.timeUtils = timeUtils;
    this.onShotUrlClickListener = onShotUrlClickListener;
    this.messages = new ArrayList<>();
    this.shotTextSpannableBuilder = new ShotTextSpannableBuilder();
    this.onImageClickListener = onImageClickListener;
  }

  @Override public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View v = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.item_message_timeline, parent, false);
    return new MessageViewHolder(v, avatarClickListener, videoClickListener,
        onUsernameClickListener, timeUtils, imageLoader, shotTextSpannableBuilder);
  }

  @Override public void onBindViewHolder(MessageViewHolder holder, int position) {
    holder.render(messages.get(position), onImageClickListener, onShotUrlClickListener);
  }

  @Override public int getItemCount() {
    return messages.size();
  }

  public void addMessagesBelow(List<PrivateMessageModel> newShots) {
    this.messages.addAll(newShots);
    notifyDataSetChanged();
  }

  public void removeMessage(PrivateMessageModel privateMessageModel) {
    this.messages.remove(privateMessageModel);
    notifyDataSetChanged();
  }

  public void setMessages(List<PrivateMessageModel> messageModels) {
    this.messages = messageModels;
  }

  public void addMessagesAbove(List<PrivateMessageModel> messageModels) {
    List<PrivateMessageModel> newMessages = new ArrayList<>(messageModels);
    newMessages.addAll(this.messages);
    this.messages = newMessages;
  }

  public void addMessages(List<PrivateMessageModel> privateMessageModels) {
    List<PrivateMessageModel> newMessages = new ArrayList<>(privateMessageModels);
    insertNewMessages(newMessages, ITEM_POSITION_WITHOUT_HEADER, newMessages.size());
  }

  private void insertNewMessages(List<PrivateMessageModel> newMessages, int position, int size) {
    Iterator<PrivateMessageModel> iterator = newMessages.iterator();
    while (iterator.hasNext()) {
      if (messages.contains(iterator.next())) {
        iterator.remove();
      }
    }
    if (newMessages.size() > 0) {
      messages.addAll(position, newMessages);
      try {
        notifyItemRangeInserted(position, size);
      } catch (Exception e) {
        notifyDataSetChanged();
      }
    }
  }

  public PrivateMessageModel getItem(int position) {
    return messages.get(position);
  }

  public PrivateMessageModel getLastMessage() {
    Integer shotsNumber = messages.size();
    if (shotsNumber > 0) {
      return messages.get(messages.size() - 1);
    } else {
      return messages.get(0);
    }
  }
}
