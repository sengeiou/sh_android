package com.shootr.mobile.ui.widgets;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.shootr.mobile.R;

public class ByeByeDialog extends DialogFragment {

  public static final String TAG = "FullScreenDialog";

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

    View view = getActivity().getLayoutInflater().inflate(R.layout.byebye_dialog, parent, false);

    TextView bybyeTextView = view.findViewById(R.id.bye_bye_text);
    bybyeTextView.setMovementMethod(LinkMovementMethod.getInstance());
    setCancelable(false);

    return view;
  }


}
