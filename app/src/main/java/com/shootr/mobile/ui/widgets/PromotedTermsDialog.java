package com.shootr.mobile.ui.widgets;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.shootr.mobile.R;
import com.shootr.mobile.ShootrApplication;
import com.shootr.mobile.ui.model.StreamModel;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.IntentFactory;
import com.shootr.mobile.util.Intents;
import javax.inject.Inject;

public class PromotedTermsDialog extends DialogFragment {

  public static final String TAG = "FullScreenDialog";

  public static final String STREAM = "STREAM";
  public static final String TERMS = "TERMS";


  @Inject IntentFactory intentFactory;
  @Inject ImageLoader imageLoader;

  private StreamModel streamModel;
  private String terms;
  private OnAcceptClickListener onAcceptClickListener;


  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ShootrApplication.get(getActivity()).getObjectGraph().inject(this);
    streamModel = (StreamModel) getArguments().getSerializable(STREAM);
    terms = getArguments().getString(TERMS);
  }

  @Override
  public void onStart() {
    super.onStart();

    Dialog dialog = getDialog();
    if (dialog != null) {
      int width = ViewGroup.LayoutParams.MATCH_PARENT;
      int height = ViewGroup.LayoutParams.MATCH_PARENT;
      dialog.getWindow().setLayout(width, height);
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle state) {
    super.onCreateView(inflater, parent, state);

    View view = getActivity().getLayoutInflater().inflate(R.layout.promoted_shot_terms, parent, false);

    View back = view.findViewById(R.id.back);

    back.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        dismiss();
      }
    });

    View learnMore = view.findViewById(R.id.learn_more);

    learnMore.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        String termsUrl = getActivity().getResources().getString(R.string.promoted_info);
        Intent termsIntent = intentFactory.openEmbededUrlIntent(getActivity(), termsUrl);
        Intents.maybeStartActivity(getActivity(), termsIntent);
      }
    });

    TextView termsText = view.findViewById(R.id.terms_text);
    termsText.setText(terms);

    AvatarView avatar = view.findViewById(R.id.stream_image);
    imageLoader.loadProfilePhoto(streamModel.getPicture(), avatar, streamModel.getTitle());

    View acceptButton = view.findViewById(R.id.accept_button);
    acceptButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if (onAcceptClickListener != null) {
          onAcceptClickListener.onClick();
          dismiss();
        }
      }
    });

    TextView title = view.findViewById(R.id.title);

    title.setText(streamModel.getTitle());
    return view;
  }

  public void setOnAcceptClickListener(OnAcceptClickListener onAcceptClickListener) {
    this.onAcceptClickListener = onAcceptClickListener;
  }

  public interface OnAcceptClickListener {
    void onClick();
  }
}
