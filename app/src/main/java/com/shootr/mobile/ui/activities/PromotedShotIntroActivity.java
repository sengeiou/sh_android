package com.shootr.mobile.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.base.BaseActivity;

public class PromotedShotIntroActivity extends BaseActivity {

  public static final String EXTRA_ID_STREAM = "idStream";
  public static final String EXTRA_STREAM_TITLE = "streamTitle";

  private String idStream;
  private String streamTitle;

  @Override protected int getLayoutResource() {
    return R.layout.promoted_shot_intro;
  }

  public static Intent getIntentForActivity(Context context, String idStream, String streamTitle) {
    Intent intent = new Intent(context, PromotedShotIntroActivity.class);
    intent.putExtra(EXTRA_ID_STREAM, idStream);
    intent.putExtra(EXTRA_STREAM_TITLE, streamTitle);
    return intent;
  }

  @Override protected void initializeViews(Bundle savedInstanceState) {
    ButterKnife.bind(this);
  }

  @Override protected void initializePresenter() {
    idStream = getIntent().getStringExtra(EXTRA_ID_STREAM);
    streamTitle = getIntent().getStringExtra(EXTRA_STREAM_TITLE);
  }

  @OnClick(R.id.skip) void onClickContinue() {
    Intent newShotIntent = PostPromotedShotActivity.IntentBuilder //
        .from(this) //
        .setStreamData(idStream, streamTitle).build();
    startActivity(newShotIntent);
  }
}
