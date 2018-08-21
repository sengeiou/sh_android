package com.shootr.mobile.ui.widgets;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import com.shootr.mobile.R;
import com.shootr.mobile.ShootrApplication;
import com.shootr.mobile.ui.model.StreamModel;
import com.shootr.mobile.ui.presenter.PromotedActivationPresenter;
import com.shootr.mobile.ui.views.PromotedActivationView;
import com.shootr.mobile.util.IntentFactory;
import com.shootr.mobile.util.Intents;
import javax.inject.Inject;

public class PromotedShotActivationInfoDialog extends DialogFragment implements
    PromotedActivationView {

  public static final String TAG = "FullScreenDialog";
  public static final String STREAM = "STREAM";

  @Inject IntentFactory intentFactory;
  @Inject PromotedActivationPresenter presenter;

  private Switch switchWidget;
  private TextView switchText;
  private StreamModel streamModel;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ShootrApplication.get(getActivity()).getObjectGraph().inject(this);
    streamModel = (StreamModel) getArguments().getSerializable(STREAM);
  }

  @Override
  public void onStart() {
    super.onStart();

    try {
      Dialog dialog = getDialog();
      if (dialog != null) {
        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setLayout(width, height);
      }
    } catch (NullPointerException error) {
      /* no-op */
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle state) {
    super.onCreateView(inflater, parent, state);

    View view = getActivity().getLayoutInflater().inflate(R.layout.promoted_shot_activation_info_dialog, parent, false);

    View back = view.findViewById(R.id.back);
    View learnMore = view.findViewById(R.id.learn_more);
    switchWidget = view.findViewById(R.id.activation_switch);
    switchText = view.findViewById(R.id.switch_text);

    back.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        dismiss();
      }
    });

    learnMore.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        String termsUrl = getActivity().getResources().getString(R.string.promoted_info);
        Intent termsIntent = intentFactory.openEmbededUrlIntent(getActivity(), termsUrl);
        Intents.maybeStartActivity(getActivity(), termsIntent);
      }
    });

    switchWidget.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
          setOnText();
          presenter.onActivatePromoted();
        } else {
          setOffText();
          presenter.onDesactivatePromoted();
        }
      }
    });

    presenter.initialize(this, streamModel);
    return view;
  }

  @Override public void enableSwitch() {
    if (switchWidget != null) {
      switchWidget.setEnabled(true);
    }
  }

  @Override public void disableSwitch() {
    if (switchWidget != null) {
      switchWidget.setEnabled(false);
    }
  }

  @Override public void setOffText() {
    switchText.setText(getText(R.string.switch_off));
    switchWidget.setChecked(false);
  }

  @Override public void setOnText() {
    switchText.setText(getText(R.string.switch_on));
    switchWidget.setChecked(true);
  }
}
