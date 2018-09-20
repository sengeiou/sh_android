package com.shootr.mobile.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import butterknife.BindString;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.ToolbarDecorator;
import com.shootr.mobile.ui.fragments.PrivateMessageTimelineFragment;
import com.shootr.mobile.ui.fragments.streamtimeline.TimelineFragment;
import com.shootr.mobile.util.AnalyticsTool;
import com.shootr.mobile.util.BackStackHandler;
import dagger.ObjectGraph;
import javax.inject.Inject;

public class PrivateMessageTimelineActivity extends BaseToolbarDecoratedActivity {

  @Inject BackStackHandler backStackHandler;
  @Inject AnalyticsTool analyticsTool;

  @BindString(R.string.analytics_screen_private_message_timeline) String
      analyticsPrivateMessageTimeline;

  public static Intent newIntent(Context context, String targetUserId) {
    Intent intent = new Intent(context, PrivateMessageTimelineActivity.class);
    intent.putExtra(PrivateMessageTimelineFragment.EXTRA_ID_TARGET_USER, targetUserId);
    return intent;
  }

  public static Intent newIntent(Context context, String idChannel, String targetUserId) {
    Intent intent = new Intent(context, PrivateMessageTimelineActivity.class);
    intent.putExtra(PrivateMessageTimelineFragment.EXTRA_ID_TARGET_USER, targetUserId);
    intent.putExtra(PrivateMessageTimelineFragment.EXTRA_ID_CHANNEL, idChannel);
    return intent;
  }

  @Override protected int getLayoutResource() {
    return R.layout.stream_timeline_fragment_container;
  }

  @Override protected void initializeViews(Bundle savedInstanceState) {
    getIntent().getExtras();
    setupAndAddFragment(savedInstanceState);
  }

  @Override public void onResume() {
    super.onResume();
  }

  @Override public void onPause() {
    super.onPause();
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      backStackHandler.handleBackStack(this);
      overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
      return true;
    } else {
      return super.onOptionsItemSelected(item);
    }
  }

  private void setupAndAddFragment(Bundle savedInstanceState) {
    boolean fragmentAlreadyAddedBySystem = savedInstanceState != null;

    if (!fragmentAlreadyAddedBySystem) {
      Bundle fragmentArguments = getIntent().getExtras();
      PrivateMessageTimelineFragment fragment =
          PrivateMessageTimelineFragment.newInstance(fragmentArguments);

      FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
      transaction.add(R.id.container, fragment, TimelineFragment.TAG);
      transaction.commit();
    }
  }

  @Override protected void initializePresenter() {
        /* no-op: no presenter here, just a dummy container activity */
  }

  @Override protected void setupToolbar(ToolbarDecorator toolbarDecorator) {
        /* no-op */
  }

  @Override protected ObjectGraph buildObjectGraph() {
    return super.buildObjectGraph();
  }

  @Override public void onBackPressed() {
    super.onBackPressed();
    overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
  }

  @Override public void onStart() {
    super.onStart();
    analyticsTool.analyticsStart(getBaseContext(), analyticsPrivateMessageTimeline);
  }
}
