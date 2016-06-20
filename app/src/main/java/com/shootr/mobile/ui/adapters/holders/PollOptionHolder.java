package com.shootr.mobile.ui.adapters.holders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.amulyakhare.textdrawable.TextDrawable;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.listeners.OnPollOptionClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnPollOptionLongClickListener;
import com.shootr.mobile.ui.model.PollOptionModel;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.InitialsLoader;
import de.hdodenhof.circleimageview.CircleImageView;
import javax.inject.Inject;

public class PollOptionHolder {

  private OnPollOptionClickListener pollOptionClickListener;
  private OnPollOptionLongClickListener pollOptionLongClickListener;
  private ImageLoader imageLoader;
  private InitialsLoader initialsLoader;

  @Inject public PollOptionHolder() {
  }

  public void bindModel(View view, PollOptionModel pollOptionModel,
      OnPollOptionClickListener pollOptionClickListener,
      OnPollOptionLongClickListener pollOptionLongClickListener, ImageLoader imageLoader,
      InitialsLoader initialsLoader) {
    this.pollOptionClickListener = pollOptionClickListener;
    this.pollOptionLongClickListener = pollOptionLongClickListener;
    this.imageLoader = imageLoader;
    this.initialsLoader = initialsLoader;
    setupPicture(view, pollOptionModel);
    setupText(view, pollOptionModel);
    setupListeners(view, pollOptionModel);
  }

  private void setupText(View view, PollOptionModel pollOptionModel) {
    TextView text = (TextView) view.findViewById(R.id.option_text);
    text.setText(pollOptionModel.getText());
  }

  private void setupListeners(View view, final PollOptionModel pollOptionModel) {
    view.setOnLongClickListener(new View.OnLongClickListener() {
      @Override public boolean onLongClick(View view) {
        pollOptionLongClickListener.onLongPress(pollOptionModel);
        return false;
      }
    });

    view.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        pollOptionClickListener.onClickPressed(pollOptionModel);
      }
    });
  }

  private void setupPicture(View view, PollOptionModel pollOptionModel) {
    CircleImageView circleImage = (CircleImageView) view.findViewById(R.id.option_picture_without_text);
    ImageView pictureWithText = (ImageView) view.findViewById(R.id.option_picture);
    if (pollOptionModel.getImageUrl() != null) {
      circleImage.setVisibility(View.VISIBLE);
      pictureWithText.setVisibility(View.GONE);
      imageLoader.loadStreamPicture(pollOptionModel.getImageUrl(), circleImage);
    } else {
      circleImage.setVisibility(View.GONE);
      pictureWithText.setVisibility(View.VISIBLE);
      setupInitials(pollOptionModel, pictureWithText);
    }
  }

  private void setupInitials(PollOptionModel pollOptionModel, ImageView picture) {
    String initials = initialsLoader.getLetters(pollOptionModel.getText());
    int backgroundColor = initialsLoader.getColorForLetters(initials);
    TextDrawable textDrawable = initialsLoader.getCustomTextDrawable(initials, backgroundColor, 64, 64, 24);
    picture.setImageDrawable(textDrawable);
  }
}
