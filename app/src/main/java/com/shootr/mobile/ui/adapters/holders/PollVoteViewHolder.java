package com.shootr.mobile.ui.adapters.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.amulyakhare.textdrawable.TextDrawable;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.listeners.OnPollOptionClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnPollOptionLongClickListener;
import com.shootr.mobile.ui.model.PollOptionModel;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.InitialsLoader;
import de.hdodenhof.circleimageview.CircleImageView;

public class PollVoteViewHolder extends RecyclerView.ViewHolder {

  @BindView(R.id.option_picture_without_text) CircleImageView picture;
  @BindView(R.id.option_picture) ImageView pictureWithText;
  @BindView(R.id.option_text) TextView option;

  private final OnPollOptionClickListener pollOptionClickListener;
  private final OnPollOptionLongClickListener pollOptionLongClickListener;
  private final ImageLoader imageLoader;
  private final InitialsLoader initialsLoader;

  public PollVoteViewHolder(View itemView, OnPollOptionClickListener pollOptionClickListener,
      OnPollOptionLongClickListener pollOptionLongClickListener, ImageLoader imageLoader,
      InitialsLoader initialsLoader) {
    super(itemView);
    ButterKnife.bind(this, itemView);
    this.pollOptionClickListener = pollOptionClickListener;
    this.pollOptionLongClickListener = pollOptionLongClickListener;
    this.imageLoader = imageLoader;
    this.initialsLoader = initialsLoader;
  }

  public void render(PollOptionModel model) {
    setupImage(model);
    option.setText(model.getText());
  }

  private void setupImage(final PollOptionModel model) {
    if (model.getImageUrl() != null) {
      pictureWithText.setVisibility(View.GONE);
      picture.setVisibility(View.VISIBLE);
      imageLoader.loadStreamPicture(model.getImageUrl(), picture);
      picture.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
          pollOptionClickListener.onClickPressed(model);
        }
      });
      picture.setOnLongClickListener(new View.OnLongClickListener() {
        @Override public boolean onLongClick(View view) {
          pollOptionLongClickListener.onLongPress(model);
          return false;
        }
      });
    } else {
      pictureWithText.setVisibility(View.VISIBLE);
      picture.setVisibility(View.GONE);
      setupInitials(model, pictureWithText);
      pictureWithText.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
          pollOptionClickListener.onClickPressed(model);
        }
      });
      pictureWithText.setOnLongClickListener(new View.OnLongClickListener() {
        @Override public boolean onLongClick(View view) {
          pollOptionLongClickListener.onLongPress(model);
          return false;
        }
      });
    }
  }

  private void setupInitials(PollOptionModel model, ImageView picture) {
    String initials = initialsLoader.getLetters(model.getText());
    int backgroundColor = initialsLoader.getColorForLetters(initials);
    TextDrawable
        textDrawable = initialsLoader.getCustomTextDrawable(initials, backgroundColor, 56, 56, 24);
    picture.setImageDrawable(textDrawable);
  }
}
