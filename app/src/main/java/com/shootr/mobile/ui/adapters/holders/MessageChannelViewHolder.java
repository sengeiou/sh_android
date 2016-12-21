package com.shootr.mobile.ui.adapters.holders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.amulyakhare.textdrawable.TextDrawable;
import com.shootr.mobile.R;
import com.shootr.mobile.domain.model.privateMessageChannel.LastMessageType;
import com.shootr.mobile.ui.adapters.listeners.ChannelClickListener;
import com.shootr.mobile.ui.model.PrivateMessageChannelModel;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.InitialsLoader;
import de.hdodenhof.circleimageview.CircleImageView;

public class MessageChannelViewHolder extends RecyclerView.ViewHolder {

  private static final String UNREAD_MESSAGES = " ";

  private final ImageLoader imageLoader;
  private final InitialsLoader initialsLoader;
  private final ChannelClickListener channelClickListener;
  private final AndroidTimeUtils timeUtils;

  @BindView(R.id.channel_picture) CircleImageView picture;
  @BindView(R.id.channel_picture_without_text) ImageView pictureWithoutText;
  @BindView(R.id.channel_title) TextView title;
  @BindView(R.id.channel_subtitle_description) TextView subtitle;
  @BindView(R.id.container) LinearLayout container;
  @BindView(R.id.unread_messages_indicator) TextView unreadMessages;
  @BindView(R.id.channel_timestamp) TextView timestamp;

  @BindString(R.string.private_message_video) String video;
  @BindString(R.string.private_message_image) String image;

  private Context context;

  public MessageChannelViewHolder(View itemView, ImageLoader imageLoader,
      InitialsLoader initialsLoader, final ChannelClickListener channelClickListener,
      AndroidTimeUtils timeUtils) {
    super(itemView);
    context = itemView.getContext();
    this.imageLoader = imageLoader;
    this.initialsLoader = initialsLoader;
    this.channelClickListener = channelClickListener;
    this.timeUtils = timeUtils;
    ButterKnife.bind(this, itemView);
  }

  public void render(final PrivateMessageChannelModel privateMessageChannelModel) {
    title.setText(privateMessageChannelModel.getTitle());
    setupLastComment(privateMessageChannelModel);
    setupChannelPicture(privateMessageChannelModel);
    container.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        channelClickListener.onChannelClick(privateMessageChannelModel.getIdPrivateMessageChannel(),
            privateMessageChannelModel.getIdTargetUser());
      }
    });
    setupRead(privateMessageChannelModel);
    bindElapsedTime(privateMessageChannelModel);
  }

  private void setupLastComment(PrivateMessageChannelModel privateMessageChannelModel) {
    String comment = privateMessageChannelModel.getLastMessageComment();
    if (comment != null) {
      switch (comment) {
        case LastMessageType.IMAGE:
          subtitle.setText(image);
          break;
        case LastMessageType.VIDEO:
          subtitle.setText(video);
          break;
        default:
          subtitle.setText(comment);
          break;
      }
    }
  }

  private void bindElapsedTime(PrivateMessageChannelModel messageChannelModel) {
    long messageTimestamp = messageChannelModel.getLastMessageTime();
    this.timestamp.setText(timeUtils.getHourWithDate(messageTimestamp));
  }

  private void setupRead(PrivateMessageChannelModel privateMessageChannelModel) {
    if (!privateMessageChannelModel.getRead()) {
      timestamp.setTextColor(context.getResources().getColor(R.color.primary));
      unreadMessages.setText(UNREAD_MESSAGES);
      unreadMessages.setVisibility(View.VISIBLE);
    } else {
      timestamp.setTextColor(context.getResources().getColor(R.color.gray_80));
      unreadMessages.setVisibility(View.INVISIBLE);
    }
  }

  private void setupChannelPicture(PrivateMessageChannelModel privateMessageChannelModel) {
    String pictureUrl = privateMessageChannelModel.getImageUrl();
    if (pictureUrl != null) {
      picture.setVisibility(View.VISIBLE);
      pictureWithoutText.setVisibility(View.GONE);
      imageLoader.loadStreamPicture(pictureUrl, picture);
    } else {
      picture.setVisibility(View.GONE);
      pictureWithoutText.setVisibility(View.VISIBLE);
      setupInitials(privateMessageChannelModel);
    }
  }

  private void setupInitials(PrivateMessageChannelModel privateMessageChannelModel) {
    String initials = initialsLoader.getLetters(privateMessageChannelModel.getTitle());
    int backgroundColor = initialsLoader.getColorForLetters(initials);
    TextDrawable textDrawable = initialsLoader.getTextDrawable(initials, backgroundColor);
    pictureWithoutText.setImageDrawable(textDrawable);
  }

}
