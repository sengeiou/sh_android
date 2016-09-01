package com.shootr.mobile.ui.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import butterknife.OnItemLongClick;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.ToolbarDecorator;
import com.shootr.mobile.ui.adapters.TimelineAdapter;
import com.shootr.mobile.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnHideClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnNiceShotListener;
import com.shootr.mobile.ui.adapters.listeners.OnReplyShotListener;
import com.shootr.mobile.ui.adapters.listeners.OnUsernameClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnVideoClickListener;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.ui.presenter.AllShotsPresenter;
import com.shootr.mobile.ui.presenter.ReportShotPresenter;
import com.shootr.mobile.ui.views.AllShotsView;
import com.shootr.mobile.ui.views.ReportShotView;
import com.shootr.mobile.ui.widgets.ListViewScrollObserver;
import com.shootr.mobile.util.AnalyticsTool;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.Clipboard;
import com.shootr.mobile.util.CustomContextMenu;
import com.shootr.mobile.util.FeedbackMessage;
import com.shootr.mobile.util.Intents;
import com.shootr.mobile.util.ShareManager;
import java.util.List;
import java.util.Locale;
import javax.inject.Inject;

import static com.shootr.mobile.domain.utils.Preconditions.checkNotNull;

public class AllShotsActivity extends BaseToolbarDecoratedActivity
    implements AllShotsView, ReportShotView {

  private static final String EXTRA_USER = "user";
  private static final String CURRENT_USER = "current_user";

  @Inject AllShotsPresenter presenter;
  @Inject ReportShotPresenter reportShotPresenter;
  @Inject AndroidTimeUtils timeUtils;
  @Inject ShareManager shareManager;
  @Inject FeedbackMessage feedbackMessage;
  @Inject AnalyticsTool analyticsTool;

  @Bind(R.id.all_shots_list) ListView listView;
  @Bind(R.id.timeline_empty) View emptyView;
  @Bind(R.id.all_shots_loading) View loadingView;
  @BindString(R.string.shot_shared_message) String shotShared;
  @BindString(R.string.confirmation_hide_shot_message) String confirmationMessage;
  @BindString(R.string.confirm_hide_shot) String confirmHideShotAlertDialogMessage;
  @BindString(R.string.cancel_hide_shot) String cancelHideShotAlertDialogMessage;
  @BindString(R.string.analytics_action_nice) String analyticsActionNice;
  @BindString(R.string.analytics_label_nice) String analyticsLabelNice;
  @BindString(R.string.analytics_action_share_shot) String analyticsActionShareShot;
  @BindString(R.string.analytics_label_share_shot) String analyticsLabelShareShot;
  @BindString(R.string.analytics_action_external_share) String analyticsActionExternalShare;
  @BindString(R.string.analytics_label_external_share) String analyticsLabelExternalShare;

  @BindString(R.string.report_base_url) String reportBaseUrl;

  @Deprecated private TimelineAdapter adapter;

  private View footerProgress;

  public static Intent newIntent(Context context, String userId, Boolean isCurrentUser) {
    Intent intent = new Intent(context, AllShotsActivity.class);
    intent.putExtra(EXTRA_USER, userId);
    intent.putExtra(CURRENT_USER, isCurrentUser);
    return intent;
  }

  @Override protected void setupToolbar(ToolbarDecorator toolbarDecorator) {
        /* no-op */
  }

  @Override protected int getLayoutResource() {
    return R.layout.activity_all_shots;
  }

  @Override protected void initializeViews(Bundle savedInstanceState) {
    ButterKnife.bind(this);
    setupListAdapter();
    setupListScrollListeners();
  }

  @Override protected void initializePresenter() {
    String userId = checkNotNull(getIntent().getStringExtra(EXTRA_USER));
    Boolean isCurrentUser = getIntent().getBooleanExtra(CURRENT_USER, false);
    presenter.initialize(this, userId, isCurrentUser);
    reportShotPresenter.initialize(this);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      finish();
      return true;
    } else {
      return super.onOptionsItemSelected(item);
    }
  }

  @Override protected void onResume() {
    super.onResume();
    presenter.resume();
  }

  @Override protected void onPause() {
    super.onPause();
    presenter.pause();
  }

  private void setupListScrollListeners() {
    new ListViewScrollObserver(listView).setOnScrollUpAndDownListener(
        new ListViewScrollObserver.OnListViewScrollListener() {
          @Override
          public void onScrollUpDownChanged(int delta, int scrollPosition, boolean exact) {
            if (delta < -10) {
              // going down
            } else if (delta > 10) {
              // going up
            }
          }

          @Override public void onScrollIdle() {
            checkIfEndOfListVisible();
          }
        });
  }

  private void checkIfEndOfListVisible() {
    int lastItemPosition = listView.getAdapter().getCount() - 1;
    int lastVisiblePosition = listView.getLastVisiblePosition();
    if (lastItemPosition == lastVisiblePosition && lastItemPosition >= 0) {
      presenter.showingLastShot(adapter.getLastShot());
    }
  }

  private void setupListAdapter() {
    OnAvatarClickListener avatarClickListener = new OnAvatarClickListener() {
      @Override public void onAvatarClick(String userId, View avatarView) {
        openProfile(userId);
      }
    };

    OnUsernameClickListener onUsernameClickListener = new OnUsernameClickListener() {
      @Override public void onUsernameClick(String username) {
        goToUserProfile(username);
      }
    };

    OnVideoClickListener videoClickListener = new OnVideoClickListener() {
      @Override public void onVideoClick(String url) {
        onShotVideoClick(url);
      }
    };

    OnNiceShotListener onNiceShotListener = new OnNiceShotListener() {
      @Override public void markNice(String idShot) {
        presenter.markNiceShot(idShot);
        analyticsTool.analyticsSendAction(getBaseContext(), analyticsActionNice,
            analyticsLabelNice);
      }

      @Override public void unmarkNice(String idShot) {
        presenter.unmarkNiceShot(idShot);
      }
    };

    OnHideClickListener onHideClickListener = new OnHideClickListener() {
      @Override public void onHideClick(String idShot) {
        presenter.showUnpinShotAlert(idShot);
      }
    };

    View footerView =
        LayoutInflater.from(this).inflate(R.layout.item_list_loading, listView, false);
    footerProgress = ButterKnife.findById(footerView, R.id.loading_progress);

    footerProgress.setVisibility(View.GONE);

    listView.addFooterView(footerView, null, false);

    adapter =
        new TimelineAdapter(this, imageLoader, timeUtils, avatarClickListener, videoClickListener,
            onNiceShotListener, onUsernameClickListener, new OnReplyShotListener() {
          @Override public void reply(ShotModel shotModel) {
            Intent newShotIntent = PostNewShotActivity.IntentBuilder //
                .from(getBaseContext()) //
                .inReplyTo(shotModel.getIdShot(), shotModel.getUsername()).build();
            startActivity(newShotIntent);
          }
        }, onHideClickListener, presenter.getIsCurrentUser()) {
          @Override protected boolean shouldShowTitle() {
            return true;
          }
        };
    listView.setAdapter(adapter);
  }

  private void shareShot(ShotModel shotModel) {
    Intent shareIntent = shareManager.shareShotIntent(this, shotModel);
    Intents.maybeStartActivity(this, shareIntent);
  }

  protected void openProfile(String idUser) {
    Intent profileIntent = ProfileActivity.getIntent(this, idUser);
    startActivity(profileIntent);
  }

  private void startProfileContainerActivity(String username) {
    Intent intentForUser = ProfileActivity.getIntentWithUsername(this, username);
    startActivity(intentForUser);
  }

  private void goToUserProfile(String username) {
    startProfileContainerActivity(username);
  }

  private void onShotVideoClick(String url) {
    Uri uri = Uri.parse(url);
    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
    startActivity(intent);
  }

  @Override public void showError(String messageForError) {
    feedbackMessage.show(getView(), messageForError);
  }

  @Override public void showContextMenuWithUnblock(final ShotModel shotModel) {
    getBaseContextMenu(shotModel).addAction(R.string.report_context_menu_unblock, new Runnable() {
      @Override public void run() {
        reportShotPresenter.unblockUser(shotModel);
      }
    }).show();
  }

  @Override public void showBlockFollowingUserAlert() {
    feedbackMessage.showLong(getView(), R.string.block_user_error);
  }

  @Override public void showUserBlocked() {
    feedbackMessage.show(getView(), R.string.user_blocked);
  }

  @Override public void showUserUnblocked() {
    feedbackMessage.show(getView(), R.string.user_unblocked);
  }

  @Override public void showBlockUserConfirmation() {
    new AlertDialog.Builder(this).setMessage(R.string.block_user_dialog_message)
        .setPositiveButton(getString(R.string.block_user_dialog_block),
            new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialog, int which) {
                reportShotPresenter.confirmBlock();
              }
            })
        .setNegativeButton(getString(R.string.block_user_dialog_cancel), null)
        .create()
        .show();
  }

  @Override public void showErrorLong(String messageForError) {
    feedbackMessage.showLong(getView(), messageForError);
  }

  @Override public void showUserBanned() {
        /* no-op */
  }

  @Override public void showUserUnbanned() {
        /* no-op */
  }

  @Override public void showAuthorContextMenuWithoutPin(final ShotModel shotModel) {
    getBaseContextMenu(shotModel).addAction(R.string.report_context_menu_delete, new Runnable() {
      @Override public void run() {
        openDeleteConfirmation(shotModel);
      }
    }).show();
  }

  @Override public void showAuthorContextMenuWithPinAndHighlight(ShotModel shot) {
    /* no-op */
  }

  @Override public void showAuthorContextMenuWithPinAndDismissHighlight(ShotModel shot) {
    /* no-op */
  }

  @Override public void showAuthorContextMenuWithoutPinAndHighlight(ShotModel shot) {
    /* no-op */
  }

  @Override public void showAuthorContextMenuWithoutPinAndDismissHighlight(ShotModel shot) {
    /* no-op */
  }

  @Override public void showContributorContextMenuWithPinAndHighlight(ShotModel shot) {
    /* no-op */
  }

  @Override public void showContributorContextMenuWithPinAndDismissHighlight(ShotModel shot) {
    /* no-op */
  }

  @Override public void showContributorContextMenuWithoutPinAndHighlight(ShotModel shot) {
    /* no-op */
  }

  @Override public void showContributorContextMenuWithoutPinAndDismissHighlight(ShotModel shot) {
    /* no-op */
  }

  @Override public void showContributorContextMenu(ShotModel shot) {
    /* no-op */
  }

  @Override public void showContributorContextMenuWithDismissHighlight(ShotModel shot) {
    /* no-op */
  }

  @Override public void hideLoading() {
    loadingView.setVisibility(View.GONE);
  }

  @Override public void setShots(List<ShotModel> shotModels) {
    adapter.setShots(shotModels);
  }

  @Override public void hideEmpty() {
    emptyView.setVisibility(View.GONE);
  }

  @Override public void showShots() {
    adapter.setIsCurrentUser(presenter.getIsCurrentUser());
    adapter.notifyDataSetChanged();
    listView.setVisibility(View.VISIBLE);
  }

  @Override public void showEmpty() {
    emptyView.setVisibility(View.VISIBLE);
  }

  @Override public void hideShots() {
    listView.setVisibility(View.GONE);
  }

  @Override public void showLoading() {
    loadingView.setVisibility(View.VISIBLE);
  }

  @Override public void showLoadingOldShots() {
    footerProgress.setVisibility(View.VISIBLE);
  }

  @Override public void hideLoadingOldShots() {
    footerProgress.setVisibility(View.GONE);
  }

  @Override public void addOldShots(List<ShotModel> shotModels) {
    adapter.addShotsBelow(shotModels);
  }

  @Override public void showShotShared() {
    feedbackMessage.show(getView(), shotShared);
  }

  @Override public void showHideShotConfirmation(final String idShot) {
    new AlertDialog.Builder(this).setMessage(confirmationMessage)
        .setPositiveButton(confirmHideShotAlertDialogMessage,
            new DialogInterface.OnClickListener() {

              public void onClick(DialogInterface dialog, int whichButton) {
                presenter.hideShot(idShot);
              }
            })
        .setNegativeButton(cancelHideShotAlertDialogMessage, null)
        .show();
  }

  @Override public void handleReport(String sessionToken, ShotModel shotModel) {
    reportShotPresenter.reportClicked(Locale.getDefault().getLanguage(), sessionToken, shotModel);
  }

  @Override
  public void showAlertLanguageSupportDialog(final String sessionToken, final ShotModel shotModel) {
    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
    alertDialogBuilder //
        .setMessage(getString(R.string.language_support_alert)) //
        .setPositiveButton(getString(R.string.email_confirmation_ok),
            new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialog, int which) {
                goToReport(sessionToken, shotModel);
              }
            }).show();
  }

  @Override public void showHolderContextMenu(ShotModel shot) {
    showAuthorContextMenuWithPin(shot);
  }

  @Override public void showHolderContextMenuWithDismissHighlight(ShotModel shot) {
    /* no-op */
  }

  @Override public void goToReport(String sessionToken, ShotModel shotModel) {
    Intent browserIntent = new Intent(Intent.ACTION_VIEW,
        Uri.parse(String.format(reportBaseUrl, sessionToken, shotModel.getIdShot())));
    startActivity(browserIntent);
  }

  @OnItemClick(R.id.all_shots_list) public void openShot(int position) {
    ShotModel shot = adapter.getItem(position);
    Intent intent = ShotDetailActivity.getIntentForActivity(this, shot);
    startActivity(intent);
  }

  @OnItemLongClick(R.id.all_shots_list) public boolean openContextMenu(int position) {
    ShotModel shot = adapter.getItem(position);
    reportShotPresenter.onShotLongPressed(shot);
    return true;
  }

  @Override public void showEmailNotConfirmedError() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);

    builder.setMessage(getString(R.string.alert_report_confirmed_email_message))
        .setTitle(getString(R.string.alert_report_confirmed_email_title))
        .setPositiveButton(getString(R.string.alert_report_confirmed_email_ok), null);

    builder.create().show();
  }

  @Override public void showContextMenu(final ShotModel shotModel) {
    getBaseContextMenu(shotModel).addAction(R.string.report_context_menu_report, new Runnable() {
      @Override public void run() {
        reportShotPresenter.report(shotModel);
      }
    }).addAction(R.string.report_context_menu_block, new Runnable() {
      @Override public void run() {
        reportShotPresenter.blockUserClicked(shotModel);
      }
    }).show();
  }

  @Override public void showAuthorContextMenuWithPin(final ShotModel shotModel) {
    getBaseContextMenu(shotModel).addAction(R.string.report_context_menu_delete, new Runnable() {
      @Override public void run() {
        openDeleteConfirmation(shotModel);
      }
    }).show();
  }

  private CustomContextMenu.Builder getBaseContextMenu(final ShotModel shotModel) {
    return new CustomContextMenu.Builder(this).addAction(R.string.menu_share_shot_via_shootr,
        new Runnable() {
          @Override public void run() {
            presenter.shareShot(shotModel);
            analyticsTool.analyticsSendAction(getBaseContext(),
                getString(R.string.menu_share_shot_via_shootr), analyticsActionShareShot,
                analyticsLabelShareShot);
          }
        }).addAction(R.string.menu_share_shot_via, new Runnable() {
      @Override public void run() {
        shareShot(shotModel);
        analyticsTool.analyticsSendAction(getBaseContext(), getString(R.string.menu_share_shot_via),
            analyticsActionExternalShare, analyticsLabelExternalShare);
      }
    }).addAction(R.string.menu_copy_text, new Runnable() {
      @Override public void run() {
        Clipboard.copyShotComment(AllShotsActivity.this, shotModel);
      }
    });
  }

  private void openDeleteConfirmation(final ShotModel shotModel) {
    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

    alertDialogBuilder.setMessage(R.string.delete_shot_confirmation_message);
    alertDialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int id) {
        reportShotPresenter.deleteShot(shotModel);
      }
    });
    alertDialogBuilder.setNegativeButton(R.string.cancel, null);
    AlertDialog alertDialog = alertDialogBuilder.create();
    alertDialog.show();
  }

  @Override public void notifyDeletedShot(ShotModel shotModel) {
    adapter.removeShot(shotModel);
  }
}
