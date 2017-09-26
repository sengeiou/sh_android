package com.shootr.mobile.ui.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.shootr.mobile.R;
import com.shootr.mobile.domain.model.shot.HighlightedShot;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.ui.ToolbarDecorator;
import com.shootr.mobile.ui.adapters.ProfileShotAdapter;
import com.shootr.mobile.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnNiceShotListener;
import com.shootr.mobile.ui.adapters.listeners.OnReshootClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnShotLongClick;
import com.shootr.mobile.ui.adapters.listeners.OnUrlClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnUsernameClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnVideoClickListener;
import com.shootr.mobile.ui.adapters.listeners.ShotClickListener;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.ui.presenter.AllShotsPresenter;
import com.shootr.mobile.ui.presenter.ReportShotPresenter;
import com.shootr.mobile.ui.views.AllShotsView;
import com.shootr.mobile.ui.views.ReportShotView;
import com.shootr.mobile.ui.widgets.PreCachingLayoutManager;
import com.shootr.mobile.util.AnalyticsTool;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.Clipboard;
import com.shootr.mobile.util.CustomContextMenu;
import com.shootr.mobile.util.FeedbackMessage;
import com.shootr.mobile.util.Intents;
import com.shootr.mobile.util.NumberFormatUtil;
import com.shootr.mobile.util.ShareManager;
import java.util.List;
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
  @Inject NumberFormatUtil numberFormatUtil;
  @Inject SessionRepository sessionRepository;

  @BindView(R.id.all_shots_list) RecyclerView shotsList;
  @BindView(R.id.timeline_empty) View emptyView;
  @BindView(R.id.all_shots_loading) View loadingView;
  @BindString(R.string.analytics_action_nice) String analyticsActionNice;
  @BindString(R.string.analytics_label_nice) String analyticsLabelNice;
  @BindString(R.string.analytics_action_share_shot) String analyticsActionShareShot;
  @BindString(R.string.analytics_label_share_shot) String analyticsLabelShareShot;
  @BindString(R.string.analytics_action_external_share) String analyticsActionExternalShare;
  @BindString(R.string.analytics_label_external_share) String analyticsLabelExternalShare;
  @BindString(R.string.report_base_url) String reportBaseUrl;
  @BindString(R.string.analytics_source_all_shots) String allShotsSource;

  private ProfileShotAdapter profileShotsAdapter;

  private PreCachingLayoutManager preCachingLayoutManager;

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
    shotsList.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
      }

      @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        if (shotsList != null) {
          checkIfEndOfListVisible();
        }
      }
    });
  }


  private void checkIfEndOfListVisible() {
    int lastItemPosition = shotsList.getAdapter().getItemCount() - 1;
    int lastVisiblePosition = preCachingLayoutManager.findLastVisibleItemPosition();
    if (lastItemPosition == lastVisiblePosition && lastItemPosition >= 0) {
      presenter.showingLastShot();
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
      @Override public void markNice(ShotModel shot) {
        presenter.markNiceShot(shot.getIdShot());
        sendNiceAnalytics(shot);
      }

      @Override public void unmarkNice(String idShot) {
        presenter.unmarkNiceShot(idShot);
      }
    };

    preCachingLayoutManager = new PreCachingLayoutManager(this);
    shotsList.setLayoutManager(preCachingLayoutManager);
    shotsList.setHasFixedSize(false);
    shotsList.setItemAnimator(new DefaultItemAnimator() {
      @Override
      public boolean canReuseUpdatedViewHolder(@NonNull RecyclerView.ViewHolder viewHolder) {
        return true;
      }

      @Override
      public boolean canReuseUpdatedViewHolder(@NonNull RecyclerView.ViewHolder viewHolder,
          @NonNull List<Object> payloads) {
        return true;
      }
    });

    profileShotsAdapter = new ProfileShotAdapter(imageLoader, avatarClickListener,
        videoClickListener, onNiceShotListener, timeUtils, new ShotClickListener() {
      @Override public void onClick(ShotModel shot) {
        openShot(shot);
      }
    }, new OnShotLongClick() {
      @Override public void onShotLongClick(ShotModel shot) {
        reportShotPresenter.onShotLongPressed(shot);
      }
    }, new OnUrlClickListener() {
      @Override public void onClick() {
        /* no-op */
      }
    }, new OnReshootClickListener() {
      @Override public void onReshootClick(ShotModel shotModel) {
        presenter.reshoot(shotModel);
        sendShareAnalytics(shotModel);
      }

      @Override public void onUndoReshootClick(ShotModel shot) {
        presenter.undoReshoot(shot);
      }
    }, numberFormatUtil);

    shotsList.setAdapter(profileShotsAdapter);
  }

  private void sendNiceAnalytics(ShotModel shot) {
    AnalyticsTool.Builder builder = new AnalyticsTool.Builder();
    builder.setContext(getBaseContext());
    builder.setActionId(analyticsActionNice);
    builder.setLabelId(analyticsLabelNice);
    builder.setSource(allShotsSource);
    builder.setUser(sessionRepository.getCurrentUser());
    builder.setIdTargetUser(shot.getIdUser());
    builder.setTargetUsername(shot.getUsername());
    analyticsTool.analyticsSendAction(builder);
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
    /* no-op */
  }

  @Override public void showBlockFollowingUserAlert() {
    /* no-op */
  }

  @Override public void showUserBlocked() {
    /* no-op */
  }

  @Override public void showUserUnblocked() {
    /* no-op */
  }

  @Override public void showBlockUserConfirmation() {
    /* no-op */
  }

  @Override public void showErrorLong(String messageForError) {
    feedbackMessage.showLong(getView(), messageForError);
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

  @Override public void showAuthorContextMenuWithPinAndDismissHighlight(ShotModel shot,
      HighlightedShot highlightedShot) {
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
    profileShotsAdapter.setShots(shotModels);
  }

  @Override public void hideEmpty() {
    emptyView.setVisibility(View.GONE);
  }

  @Override public void showShots() {
    profileShotsAdapter.notifyDataSetChanged();
    shotsList.setVisibility(View.VISIBLE);
  }

  @Override public void showEmpty() {
    emptyView.setVisibility(View.VISIBLE);
  }

  @Override public void hideShots() {
    shotsList.setVisibility(View.GONE);
  }

  @Override public void showLoading() {
    loadingView.setVisibility(View.VISIBLE);
  }

  @Override public void showLoadingOldShots() {
    /* no -op */
  }

  @Override public void hideLoadingOldShots() {
    /* no-op */
  }

  @Override public void addOldShots(List<ShotModel> shotModels) {
    profileShotsAdapter.addShotsBelow(shotModels);
  }

  @Override public void notifyReshot(String idShot, boolean mark) {
    profileShotsAdapter.reshoot(idShot, mark);
  }

  @Override public void handleReport(String sessionToken, ShotModel shotModel) {
    reportShotPresenter.reportClicked(sessionToken, shotModel);
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

  private void openShot(ShotModel shot) {
    Intent intent = ShotDetailActivity.getIntentForActivity(this, shot);
    startActivity(intent);
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
    return new CustomContextMenu.Builder(this).addAction(shotModel.isReshooted() ?
            R.string.undo_reshoot : R.string.menu_share_shot_via_shootr,
        new Runnable() {
          @Override public void run() {
            if (shotModel.isReshooted()) {
              presenter.undoReshoot(shotModel);
            } else {
              presenter.reshoot(shotModel);
              sendShareAnalytics(shotModel);
            }
          }
        }).addAction(R.string.menu_share_shot_via, new Runnable() {
      @Override public void run() {
        shareShot(shotModel);
        sendExternalShareAnalytics(shotModel);
      }
    }).addAction(R.string.menu_copy_text, new Runnable() {
      @Override public void run() {
        Clipboard.copyShotComment(AllShotsActivity.this, shotModel);
      }
    });
  }

  private void sendExternalShareAnalytics(ShotModel shot) {
    AnalyticsTool.Builder builder = new AnalyticsTool.Builder();
    builder.setContext(getBaseContext());
    builder.setActionId(analyticsActionExternalShare);
    builder.setLabelId(analyticsLabelExternalShare);
    builder.setSource(allShotsSource);
    builder.setUser(sessionRepository.getCurrentUser());
    builder.setIdTargetUser(shot.getIdUser());
    builder.setTargetUsername(shot.getUsername());
    analyticsTool.analyticsSendAction(builder);
  }

  private void sendShareAnalytics(ShotModel shot) {
    AnalyticsTool.Builder builder = new AnalyticsTool.Builder();
    builder.setContext(getBaseContext());
    builder.setActionId(analyticsActionShareShot);
    builder.setLabelId(analyticsLabelShareShot);
    builder.setSource(allShotsSource);
    builder.setUser(sessionRepository.getCurrentUser());
    builder.setIdTargetUser(shot.getIdUser());
    builder.setTargetUsername(shot.getUsername());
    analyticsTool.analyticsSendAction(builder);
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
    profileShotsAdapter.removeShot(shotModel);
  }
}
