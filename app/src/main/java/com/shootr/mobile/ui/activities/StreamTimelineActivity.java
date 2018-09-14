package com.shootr.mobile.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import com.shootr.mobile.R;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.ui.ToolbarDecorator;
import com.shootr.mobile.ui.base.BaseFragment;
import com.shootr.mobile.ui.fragments.streamtimeline.TimelineFragment;
import com.shootr.mobile.util.BackStackHandler;
import dagger.ObjectGraph;
import javax.inject.Inject;

public class StreamTimelineActivity extends BaseToolbarDecoratedActivity {

  @Inject BackStackHandler backStackHandler;
  @Inject SessionRepository sessionRepository;
  private Fragment currentFragment;

  public static Intent newIntent(Context context, String streamId, String streamTitle) {
    Intent intent = new Intent(context, StreamTimelineActivity.class);
    intent.putExtra(TimelineFragment.EXTRA_STREAM_ID, streamId);
    intent.putExtra(TimelineFragment.EXTRA_STREAM_TITLE, streamTitle);
    return intent;
  }

  public static Intent newIntent(Context context, String streamId) {
    Intent intent = new Intent(context, StreamTimelineActivity.class);
    intent.putExtra(TimelineFragment.EXTRA_STREAM_ID, streamId);
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

    Bundle fragmentArguments = getIntent().getExtras();
    TimelineFragment streamTimelineFragment = TimelineFragment.newInstance(fragmentArguments);
    setupTimelineFragment(fragmentAlreadyAddedBySystem, streamTimelineFragment);
  }

  private void setupTimelineFragment(boolean fragmentAlreadyAddedBySystem,
      BaseFragment baseFragment) {
    if (!fragmentAlreadyAddedBySystem) {
      currentFragment = baseFragment;
      FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
      transaction.add(R.id.container, baseFragment, TimelineFragment.TAG);
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
    boolean handled = false;

    if (currentFragment instanceof TimelineFragment) {
      handled = ((TimelineFragment) currentFragment).onBackPressed();
    }

    if (!handled) {
      super.onBackPressed();
      overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
    }
  }

  @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    if (requestCode == 1) {
      if (grantResults.length > 0) {
        if (currentFragment instanceof TimelineFragment) {
          ((TimelineFragment) currentFragment).pickImage();
        }
      }
    }
  }
}
