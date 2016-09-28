package com.shootr.mobile.ui.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import butterknife.BindString;
import butterknife.ButterKnife;
import com.eftimoff.androidplayer.Player;
import com.eftimoff.androidplayer.actions.property.PropertyAction;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.BottomBarBadge;
import com.roughike.bottombar.OnMenuTabClickListener;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.ToolbarDecorator;
import com.shootr.mobile.ui.fragments.ActivityTimelineContainerFragment;
import com.shootr.mobile.ui.fragments.FavoritesFragment;
import com.shootr.mobile.ui.fragments.StreamsListFragment;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.presenter.MainScreenPresenter;
import com.shootr.mobile.ui.views.MainScreenView;
import com.shootr.mobile.util.DeeplinkingNavigator;
import com.shootr.mobile.util.DefaultTabUtils;
import com.shootr.mobile.util.FeedbackMessage;
import javax.inject.Inject;

public class MainTabbedActivity extends BaseToolbarDecoratedActivity implements MainScreenView {

  private static final int ANIMATION_DURATION = 200;
  private static final int ANIMATION_TRANSLATION = 500;
  private static final String EXTRA_UPDATE_NEEDED = "update_needed";
  private static final String EXTRA_MULTIPLE_ACTIVITIES = "multiple_activities";
  private static final int ACTIVITY_FRAGMENT = 3;
  @BindString(R.string.update_shootr_version_url) String updateVersionUrl;
  @Inject MainScreenPresenter mainScreenPresenter;
  @Inject FeedbackMessage feedbackMessage;
  @Inject DeeplinkingNavigator deeplinkingNavigator;
  @Inject DefaultTabUtils defaultTabUtils;

  private ToolbarDecorator toolbarDecorator;
  private BottomBar bottomBar;
  private BottomBarBadge unreadActivities;
  private Fragment currentFragment;

  public static Intent getUpdateNeededIntent(Context context) {
    Intent intent = new Intent(context, MainTabbedActivity.class);
    intent.putExtra(EXTRA_UPDATE_NEEDED, true);
    return intent;
  }

  public static Intent getMultipleActivitiesIntent(Context context) {
    Intent intent = new Intent(context, MainTabbedActivity.class);
    intent.putExtra(EXTRA_MULTIPLE_ACTIVITIES, true);
    return intent;
  }

  @Override protected int getLayoutResource() {
    return R.layout.activity_main_tabbed;
  }

  @Override protected void initializeViews(Bundle savedInstanceState) {
    ButterKnife.bind(this);
    setupBottomBar(savedInstanceState);
    loadIntentData();
    handleUpdateVersion();
    handleMultipleActivitiesIntent();
    setupAnimation();
  }

  private void setupAnimation() {
    PropertyAction bottomBarAction = PropertyAction.newPropertyAction(bottomBar)
        .interpolator(new AccelerateDecelerateInterpolator())
        .translationY(ANIMATION_TRANSLATION)
        .duration(ANIMATION_DURATION)
        .build();
    Player.init().animate(bottomBarAction).play();
  }

  private void setupBottomBar(Bundle savedInstanceState) {
    bottomBar = BottomBar.attach(findViewById(R.id.container), savedInstanceState);
    bottomBar.noNavBarGoodness();
    bottomBar.noTopOffset();
    setupBottomMenu();
    bottomBar.setOnMenuTabClickListener(new OnMenuTabClickListener() {
      @Override public void onMenuTabSelected(@IdRes int menuItemId) {
        switch (menuItemId) {
          case R.id.bottombar_streams:
            StreamsListFragment streamsListFragment = StreamsListFragment.newInstance();
            currentFragment = streamsListFragment;
            switchTab(streamsListFragment);
            break;
          case R.id.bottombar_favorites:
            Fragment favoritesFragment = FavoritesFragment.newInstance();
            currentFragment = favoritesFragment;
            switchTab(favoritesFragment);
            break;
          case R.id.bottombar_discover:
            Fragment discoverFragment = DiscoverFragment.newInstance();
            currentFragment = discoverFragment;
            switchTab(discoverFragment);
            break;
          case R.id.bottombar_activity:
            ActivityTimelineContainerFragment activityTimelineFragment =
                ActivityTimelineContainerFragment.newInstance();
            currentFragment = activityTimelineFragment;
            switchTab(activityTimelineFragment);
            break;
          default:
            break;
        }
      }

      @Override public void onMenuTabReSelected(@IdRes int menuItemId) {
        scrollToTop(menuItemId);
      }
    });
    loadIntentData();
    handleUpdateVersion();
  }

  private void setupBottomMenu() {
    if (defaultTabUtils.getDefaultTabPosition(getSessionHandler().getCurrentUserId())
        == DefaultTabUtils.DEFAULT_POSITION) {
      bottomBar.setItems(R.menu.bottombar_menu);
    } else {
      bottomBar.setItems(R.menu.bottombar_menu_discover);
    }
  }

  protected void switchTab(Fragment fragment) {
    getSupportFragmentManager()
        .beginTransaction()
        .replace(R.id.container, fragment, fragment.getClass().getName())
        .commit();
  }

  private void scrollToTop(@IdRes int menuItemId) {
    switch (menuItemId) {
      case R.id.bottombar_streams:
        ((StreamsListFragment) currentFragment).scrollListToTop();
        break;
      case R.id.bottombar_favorites:
        ((FavoritesFragment) currentFragment).scrollListToTop();
        break;
      case R.id.bottombar_discover:
        ((DiscoverFragment) currentFragment).scrollListToTop();
        break;
      default:
        break;
    }
  }

  private void handleUpdateVersion() {
    boolean needUpdate = getIntent().getBooleanExtra(EXTRA_UPDATE_NEEDED, false);
    if (needUpdate) {
      showUpdateDialog();
    }
  }

  private void handleMultipleActivitiesIntent() {
    boolean openActivitiesFragment = getIntent().getBooleanExtra(EXTRA_MULTIPLE_ACTIVITIES, false);
    if (openActivitiesFragment) {
      bottomBar.selectTabAtPosition(ACTIVITY_FRAGMENT, false);
    }
  }

  private void showUpdateDialog() {
    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
    alertDialogBuilder //
        .setMessage(getString(R.string.alert_shootr_update)) //
        .setPositiveButton(getString(R.string.email_confirmation_ok),
            new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialog, int which) {
                goToAppDownload(updateVersionUrl);
              }
            }).show();
  }

  private void goToAppDownload(String url) {
    Uri uriUrl = Uri.parse(url);
    Intent lastVersionDownload = new Intent(Intent.ACTION_VIEW, uriUrl);
    startActivity(lastVersionDownload);
  }

  private void loadIntentData() {
    Uri data = getIntent().getData();
    if (data != null) {
      String address = data.toString();
      deeplinkingNavigator.navigate(this, address);
    }
  }

  @Override protected void initializePresenter() {
    mainScreenPresenter.initialize(this);
  }

  @Override protected void setupToolbar(ToolbarDecorator toolbarDecorator) {
    this.toolbarDecorator = toolbarDecorator;
    this.toolbarDecorator.getActionBar().setDisplayShowHomeEnabled(false);
    this.toolbarDecorator.getActionBar().setDisplayHomeAsUpEnabled(false);
  }

  @Override protected void onResume() {
    super.onResume();
    mainScreenPresenter.resume();
  }

  @Override protected void onPause() {
    super.onPause();
    mainScreenPresenter.pause();
  }

  @Override public void setUserData(final UserModel userModel) {
    toolbarDecorator.setTitle(userModel.getUsername());
    toolbarDecorator.setAvatarImage(userModel.getPhoto());
    setToolbarClickListener(userModel);
  }

  @Override public void showActivityBadge(int count) {
    if (unreadActivities == null) {
      unreadActivities = bottomBar.makeBadgeForTabAt(ACTIVITY_FRAGMENT, Color.TRANSPARENT, count);
    } else {
      unreadActivities.setCount(count);
    }
  }

  @Override protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    bottomBar.onSaveInstanceState(outState);
  }

  private void setToolbarClickListener(final UserModel userModel) {
    toolbarDecorator.setTitleClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        Intent intent =
            ProfileActivity.getIntent(view.getContext(), userModel.getIdUser());
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_up, R.anim.stay);
      }
    });
  }
}
