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
import com.shootr.mobile.ui.adapters.listeners.OnNiceShotListener;
import com.shootr.mobile.ui.adapters.listeners.OnUsernameClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnVideoClickListener;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.ui.presenter.AllShotsPresenter;
import com.shootr.mobile.ui.presenter.ReportShotPresenter;
import com.shootr.mobile.ui.views.AllShotsView;
import com.shootr.mobile.ui.views.ReportShotView;
import com.shootr.mobile.ui.widgets.ListViewScrollObserver;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.Clipboard;
import com.shootr.mobile.util.CustomContextMenu;
import com.shootr.mobile.util.FeedbackMessage;
import com.shootr.mobile.util.IntentFactory;
import com.shootr.mobile.util.Intents;
import java.util.List;
import javax.inject.Inject;

import static com.shootr.mobile.domain.utils.Preconditions.checkNotNull;

public class AllShotsActivity extends BaseToolbarDecoratedActivity implements AllShotsView, ReportShotView {

    private static final String EXTRA_USER = "user";

    @Inject AllShotsPresenter presenter;
    @Inject ReportShotPresenter reportShotPresenter;
    @Inject AndroidTimeUtils timeUtils;
    @Inject IntentFactory intentFactory;
    @Inject FeedbackMessage feedbackMessage;

    @Bind(com.shootr.mobile.R.id.all_shots_list) ListView listView;
    @Bind(com.shootr.mobile.R.id.timeline_empty) View emptyView;
    @Bind(com.shootr.mobile.R.id.all_shots_loading) View loadingView;
    @BindString(R.string.shot_shared_message) String shotShared;

    @BindString(R.string.report_base_url) String reportBaseUrl;

    @Deprecated private TimelineAdapter adapter;

    private View footerProgress;

    public static Intent newIntent(Context context, String userId) {
        Intent intent = new Intent(context, AllShotsActivity.class);
        intent.putExtra(EXTRA_USER, userId);
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
        presenter.initialize(this, userId);
        reportShotPresenter.initialize(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }else{
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.pause();
    }

    private void setupListScrollListeners() {
        new ListViewScrollObserver(listView).setOnScrollUpAndDownListener(new ListViewScrollObserver.OnListViewScrollListener() {
            @Override public void onScrollUpDownChanged(int delta, int scrollPosition, boolean exact) {
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
        if (lastItemPosition == lastVisiblePosition) {
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
            }

            @Override public void unmarkNice(String idShot) {
                presenter.unmarkNiceShot(idShot);
            }
        };

        View footerView = LayoutInflater.from(this).inflate(R.layout.item_list_loading, listView, false);
        footerProgress = ButterKnife.findById(footerView, R.id.loading_progress);

        footerProgress.setVisibility(View.GONE);

        listView.addFooterView(footerView, null, false);

        adapter = new TimelineAdapter(this, imageLoader, timeUtils, avatarClickListener,
          videoClickListener, onNiceShotListener, onUsernameClickListener){
            @Override protected boolean shouldShowShortTitle() {
                return true;
            }
        };
        listView.setAdapter(adapter);
    }

    private void shareShot(ShotModel shotModel) {
        Intent shareIntent = intentFactory.shareShotIntent(this, shotModel);
        Intents.maybeStartActivity(this, shareIntent);
    }

    protected void openProfile(String idUser) {
        Intent profileIntent = ProfileContainerActivity.getIntent(this, idUser);
        startActivity(profileIntent);
    }

    private void startProfileContainerActivity(String username) {
        Intent intentForUser = ProfileContainerActivity.getIntentWithUsername(this, username);
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
        getBaseContextMenu(shotModel).addAction(com.shootr.mobile.R.string.report_context_menu_unblock,
          new Runnable() {
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
        feedbackMessage.show(getView(), com.shootr.mobile.R.string.user_unblocked);
    }

    @Override public void showBlockUserConfirmation() {
        new AlertDialog.Builder(this).setMessage(R.string.block_user_dialog_message)
          .setPositiveButton(getString(com.shootr.mobile.R.string.block_user_dialog_block), new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialog, int which) {
                  reportShotPresenter.confirmBlock();
              }
          })
          .setNegativeButton(getString(com.shootr.mobile.R.string.block_user_dialog_cancel), null)
          .create().show();
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

    @Override public void goToReport(String sessionToken, ShotModel shotModel) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format(reportBaseUrl,
          sessionToken,
          shotModel.getIdShot())));
        startActivity(browserIntent);
    }

    @OnItemClick(R.id.all_shots_list)
    public void openShot(int position) {
        ShotModel shot = adapter.getItem(position);
        Intent intent = ShotDetailActivity.getIntentForActivity(this, shot);
        startActivity(intent);
    }

    @OnItemLongClick(R.id.all_shots_list)
    public boolean openContextMenu(int position) {
        ShotModel shot = adapter.getItem(position);
        reportShotPresenter.onShotLongPressed(shot);
        return true;
    }

    @Override public void showEmailNotConfirmedError() {
        AlertDialog.Builder builder =
          new AlertDialog.Builder(this);

        builder.setMessage(getString(R.string.alert_report_confirmed_email_message))
          .setTitle(getString(com.shootr.mobile.R.string.alert_report_confirmed_email_title))
          .setPositiveButton(getString(R.string.alert_report_confirmed_email_ok), null);

        builder.create().show();
    }

    @Override public void showContextMenu(final ShotModel shotModel) {
        getBaseContextMenu(shotModel)
          .addAction(R.string.report_context_menu_report, new Runnable() {
              @Override public void run() {
                  reportShotPresenter.report(shotModel);
              }
          }).addAction(R.string.report_context_menu_block, new Runnable() {
            @Override public void run() {
                reportShotPresenter.blockUserClicked(shotModel);
            }
        }).show();
    }

    @Override public void showHolderContextMenu(final ShotModel shotModel) {
        getBaseContextMenu(shotModel)
          .addAction(R.string.report_context_menu_delete, new Runnable() {
              @Override public void run() {
                  openDeleteConfirmation(shotModel);
              }
          }).show();
    }

    private CustomContextMenu.Builder getBaseContextMenu(final ShotModel shotModel) {
        return new CustomContextMenu.Builder(this)
          .addAction(R.string.menu_share_shot_via_shootr, new Runnable() {
              @Override public void run() {
                  presenter.shareShot(shotModel);
              }
          })
          .addAction(com.shootr.mobile.R.string.menu_share_shot_via, new Runnable() {
              @Override public void run() {
                  shareShot(shotModel);
              }
          })
          .addAction(R.string.menu_copy_text, new Runnable() {
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
