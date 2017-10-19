package com.shootr.mobile.util;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.labo.kaji.swipeawaydialog.SwipeAwayDialogFragment;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.model.PollOptionModel;

public class SwipeDialog extends SwipeAwayDialogFragment {

  public static final String DIALOG = "dialog";
  public static final int POLL_IMAGE_TYPE = 0;
  private static final String TYPE = "type";
  private static final String POLL_OPTION = "pollOption";

  private final ImageLoader imageLoader;

  private LayoutInflater layoutInflater;

  @SuppressLint("ValidFragment")
  public SwipeDialog(ImageLoader imageLoader) {
    this.imageLoader = imageLoader;
  }

  public SwipeDialog() {
    imageLoader = null;
  }

  public static SwipeDialog newPollImageDialog(PollOptionModel pollOptionModel,
      ImageLoader imageLoader, LayoutInflater layoutInflater) {
    SwipeDialog swipeDialog = new SwipeDialog(imageLoader);
    swipeDialog.layoutInflater = layoutInflater;
    Bundle args = new Bundle();
    args.putInt(TYPE, POLL_IMAGE_TYPE);
    args.putSerializable(POLL_OPTION, pollOptionModel);
    swipeDialog.setArguments(args);
    return swipeDialog;
  }

  @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
    Integer type = getArguments().getInt(TYPE);
    if (type == POLL_IMAGE_TYPE) {
      PollOptionModel pollOptionModel =
          (PollOptionModel) getArguments().getSerializable(POLL_OPTION);
      return createPollImageDialog(getActivity(), pollOptionModel);
    }
    return null;
  }

  private Dialog createPollImageDialog(Context context, PollOptionModel pollOptionModel) {
    if (layoutInflater == null) {
      layoutInflater = LayoutInflater.from(context);
    }
    View dialogView = layoutInflater.inflate(R.layout.dialog_poll_option_image, null);
    setupPicturePollOption(pollOptionModel, dialogView);
    setupTextPollOption(pollOptionModel, dialogView);

    return new AlertDialog.Builder(context).setView(dialogView).create();
  }

  private void setupTextPollOption(PollOptionModel pollOptionModel, View dialogView) {
    TextView pollOptionText = (TextView) dialogView.findViewById(R.id.poll_option_text);
    pollOptionText.setText(pollOptionModel.getText());
  }

  private void setupPicturePollOption(PollOptionModel pollOptionModel, View dialogView) {
    ImageView pollOptionImage = (ImageView) dialogView.findViewById(R.id.poll_option_image);
    if (imageLoader != null && pollOptionModel.getOptionImage() != null) {
      imageLoader.load(pollOptionModel.getOptionImage().getSizes().getHigh().getUrl(),
          pollOptionImage);
    }
  }
}
