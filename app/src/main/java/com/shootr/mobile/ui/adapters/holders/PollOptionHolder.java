package com.shootr.mobile.ui.adapters.holders;

import android.view.View;
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
    CircleImageView picture = (CircleImageView) view.findViewById(R.id.option_picture);
    if (pollOptionModel.getImageUrl() != null) {
      imageLoader.loadStreamPicture(pollOptionModel.getImageUrl(), picture);
    } else {
      setupInitials(pollOptionModel, picture);
    }
  }

  private void setupInitials(PollOptionModel pollOptionModel, CircleImageView picture) {
    String initials = initialsLoader.getLetters(pollOptionModel.getText());
    int backgroundColor = initialsLoader.getColorForLetters(initials);
    TextDrawable textDrawable = initialsLoader.getTextDrawable(initials, backgroundColor);
    picture.setImageDrawable(textDrawable);
  }
}
