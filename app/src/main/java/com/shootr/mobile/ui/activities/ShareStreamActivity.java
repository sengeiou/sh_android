package com.shootr.mobile.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.base.BaseActivity;
import com.shootr.mobile.ui.model.StreamModel;
import com.shootr.mobile.ui.presenter.ShareStreamPresenter;
import com.shootr.mobile.ui.views.ShareStreamView;
import com.shootr.mobile.ui.widgets.AvatarView;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.Intents;
import com.shootr.mobile.util.ShareManager;
import javax.inject.Inject;

public class ShareStreamActivity extends BaseActivity implements ShareStreamView {

  private static final String EXTRA_STREAM = "idStream";

  @BindView(R.id.stream_photo) AvatarView streamPhoto;
  @BindView(R.id.stream_title) TextView streamTitle;

  @Inject ImageLoader imageLoader;
  @Inject ShareStreamPresenter presenter;
  @Inject ShareManager shareManager;


  public static Intent newIntent(Context context, String idStream) {
    Intent intent = new Intent(context, ShareStreamActivity.class);
    intent.putExtra(EXTRA_STREAM, idStream);
    return intent;
  }


  @Override protected int getLayoutResource() {
    return R.layout.activity_share_stream;
  }

  @Override protected void initializeViews(Bundle savedInstanceState) {
    ButterKnife.bind(this);
  }

  @Override protected void initializePresenter() {
    String idStream = getIntent().getStringExtra(EXTRA_STREAM);
    presenter.initialize(this, idStream);
  }

  @Override public void renderStreamInfo(StreamModel streamModel) {
    imageLoader.loadProfilePhoto(streamModel.getPicture(), streamPhoto, streamModel.getTitle());
  }

  @Override public void shareStreamVia(StreamModel stream) {
    Intent shareIntent = shareManager.shareStreamIntent(this, stream);
    Intents.maybeStartActivity(this, shareIntent);
  }

  @OnClick(R.id.share_stream) void onShareStreamClick() {
    presenter.onShareClick();
  }

  @OnClick(R.id.go_to_stream_text) void onGoToStreamClick() {
    startActivity(StreamTimelineActivity.newIntent(this, getIntent().getStringExtra(EXTRA_STREAM)));
    finish();
  }
}
