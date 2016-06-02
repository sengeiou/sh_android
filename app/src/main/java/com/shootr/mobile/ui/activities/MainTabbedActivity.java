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
import butterknife.BindString;
import butterknife.ButterKnife;
import com.ncapdevi.fragnav.FragNavController;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.BottomBarBadge;
import com.roughike.bottombar.OnMenuTabClickListener;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.ToolbarDecorator;
import com.shootr.mobile.ui.fragments.ActivityTimelineContainerFragment;
import com.shootr.mobile.ui.fragments.FavoritesFragment;
import com.shootr.mobile.ui.fragments.PeopleFragment;
import com.shootr.mobile.ui.fragments.StreamsListFragment;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.presenter.MainScreenPresenter;
import com.shootr.mobile.ui.views.MainScreenView;
import com.shootr.mobile.util.DeeplinkingNavigator;
import com.shootr.mobile.util.FeedbackMessage;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class MainTabbedActivity extends BaseToolbarDecoratedActivity implements MainScreenView {

  private static final String EXTRA_UPDATE_NEEDED = "update_needed";
  @BindString(R.string.multiple_activities_action) String multipleActivitiesAction;
  @BindString(R.string.update_shootr_version_url) String updateVersionUrl;
  @Inject MainScreenPresenter mainScreenPresenter;
  @Inject FeedbackMessage feedbackMessage;
  @Inject DeeplinkingNavigator deeplinkingNavigator;

  private ToolbarDecorator toolbarDecorator;
  private FragNavController fragNavController;
  private BottomBar bottomBar;
  private BottomBarBadge unreadActivities;

  public static Intent getUpdateNeededIntent(Context context) {
    Intent intent = new Intent(context, MainTabbedActivity.class);
    intent.putExtra(EXTRA_UPDATE_NEEDED, true);
    return intent;
  }

  @Override protected int getLayoutResource() {
    return R.layout.activity_main_tabbed;
  }

  @Override protected void initializeViews(Bundle savedInstanceState) {
    ButterKnife.bind(this);

    List<Fragment> fragments = new ArrayList<>(4);

    fragments.add(StreamsListFragment.newInstance());
    fragments.add(FavoritesFragment.newInstance());
    fragments.add(PeopleFragment.newInstance());
    fragments.add(ActivityTimelineContainerFragment.newInstance());

    fragNavController = new FragNavController(getSupportFragmentManager(),R.id.container,fragments);
    
    BottomBar bottomBar = BottomBar.attach(this, savedInstanceState);
    bottomBar.noNavBarGoodness();
    bottomBar.noTopOffset();
    bottomBar.setItemsFromMenu(R.menu.bottombar_menu, new OnMenuTabClickListener() {
      @Override
      public void onMenuTabSelected(@IdRes int menuItemId) {
        switch (menuItemId) {
          case R.id.bottombar_streams:
            fragNavController.switchTab(FragNavController.TAB1);
            break;
          case R.id.bottombar_favorites:
            fragNavController.switchTab(FragNavController.TAB2);
            break;
          case R.id.bottombar_friends:
            fragNavController.switchTab(FragNavController.TAB3);
            break;
          case R.id.bottombar_activity:
            fragNavController.switchTab(FragNavController.TAB4);
            break;
          default:
            break;
        }
      }

      @Override
      public void onMenuTabReSelected(@IdRes int menuItemId) {
      }
    });

    bottomBar.mapColorForTab(0, getResources().getColor(R.color.intro_create_background));
    bottomBar.mapColorForTab(1, getResources().getColor(R.color.primary));
    bottomBar.mapColorForTab(2, getResources().getColor(R.color.favorite_tab));
    loadIntentData();
    handleUpdateVersion();
  }

  private void handleUpdateVersion() {
    boolean needUpdate = getIntent().getBooleanExtra(EXTRA_UPDATE_NEEDED, false);
    if (needUpdate) {
      showUpdateDialog();
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

  private void navigateToActivity() {
    startActivity(new Intent(this, ActivityTimelinesContainerActivity.class));
  }

  @Override public void setUserData(final UserModel userModel) {
    toolbarDecorator.setTitle(userModel.getUsername());
    toolbarDecorator.setAvatarImage(userModel.getPhoto());
    setToolbarClickListener(userModel);
  }

  @Override public void showActivityBadge(int count) {
    if (unreadActivities == null) {
      unreadActivities = bottomBar.makeBadgeForTabAt(3, Color.TRANSPARENT, count);
    } else {
      unreadActivities.setCount(count);
    }
  }

  @Override public void showHasMultipleActivities(Integer badgeCount) {
    String multipleActivities = getString(R.string.multiple_activity_notification, badgeCount);
    feedbackMessage.showMultipleActivities(getView(), multipleActivities, multipleActivitiesAction,
        new View.OnClickListener() {
          @Override public void onClick(View view) {
            navigateToActivity();
          }
        });
  }

  private void setToolbarClickListener(final UserModel userModel) {
    toolbarDecorator.setTitleClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        Intent intent =
            ProfileContainerActivity.getIntent(view.getContext(), userModel.getIdUser());
        startActivity(intent);
      }
    });
  }
}
