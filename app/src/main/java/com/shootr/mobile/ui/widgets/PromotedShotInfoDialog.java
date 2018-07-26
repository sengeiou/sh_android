package com.shootr.mobile.ui.widgets;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.shootr.mobile.R;
import com.shootr.mobile.ShootrApplication;
import com.shootr.mobile.util.IntentFactory;
import com.shootr.mobile.util.Intents;
import javax.inject.Inject;

public class PromotedShotInfoDialog extends DialogFragment {

  public static final String TAG = "FullScreenDialog";

  @Inject IntentFactory intentFactory;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ShootrApplication.get(getActivity()).getObjectGraph().inject(this);
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

    View view = getActivity().getLayoutInflater().inflate(R.layout.promoted_shot_info_dialog, parent, false);

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

    return view;
  }
}
