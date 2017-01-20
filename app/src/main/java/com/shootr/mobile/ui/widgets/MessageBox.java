package com.shootr.mobile.ui.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.shootr.mobile.R;

public class MessageBox extends RelativeLayout {

  @BindView(R.id.shot_bar_text) EditText newShotText;
  @BindView(R.id.shot_bar_drafts) ImageButton draftButton;
  @BindView(R.id.shot_bar_photo) ImageButton sendImageButton;

  public MessageBox(Context context) {
    super(context);
    init();
  }

  public MessageBox(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public MessageBox(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  public MessageBox(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    init();
  }

  private void init() {
    setClickable(true);
    LayoutInflater inflater = LayoutInflater.from(getContext());
    inflater.inflate(R.layout.new_message_box, this);
    ButterKnife.bind(this);
  }


}
