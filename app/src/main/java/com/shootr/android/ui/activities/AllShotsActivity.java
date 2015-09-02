package com.shootr.android.ui.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import butterknife.OnItemLongClick;
import com.shootr.android.R;
import com.shootr.android.ui.ToolbarDecorator;
import com.shootr.android.ui.adapters.TimelineAdapter;
import com.shootr.android.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.android.ui.adapters.listeners.OnImageClickListener;
import com.shootr.android.ui.adapters.listeners.OnNiceShotListener;
import com.shootr.android.ui.adapters.listeners.OnUsernameClickListener;
import com.shootr.android.ui.adapters.listeners.OnVideoClickListener;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.ui.presenter.AllShotsPresenter;
import com.shootr.android.ui.presenter.ReportShotPresenter;
import com.shootr.android.ui.views.AllShotsView;
import com.shootr.android.ui.views.ReportShotView;
import com.shootr.android.ui.widgets.ListViewScrollObserver;
import com.shootr.android.util.AndroidTimeUtils;
import com.shootr.android.util.Clipboard;
import com.shootr.android.util.CustomContextMenu;
import com.shootr.android.util.IntentFactory;
import com.shootr.android.util.Intents;
import java.util.List;
import javax.inject.Inject;

import static com.shootr.android.domain.utils.Preconditions.checkNotNull;

public class AllShotsActivity extends BaseToolbarDecoratedActivity implements AllShotsView, ReportShotView {

    public static final String CLIPBOARD_LABEL = "Shot";

    private static final String EXTRA_USER = "user";

    @Inject AllShotsPresenter presenter;
    @Inject ReportShotPresenter reportShotPresenter;
    @Inject AndroidTimeUtils timeUtils;
    @Inject IntentFactory intentFactory;

    @Bind(R.id.all_shots_list) ListView listView;
    @Bind(R.id.timeline_empty) View emptyView;
    @Bind(R.id.all_shots_loading) View loadingView;

    @BindString(R.string.report_base_url) String reportBaseUrl;

    @Deprecated private TimelineAdapter adapter;

    private OnAvatarClickListener avatarClickListener;
    private OnImageClickListener imageClickListener;
    private OnVideoClickListener videoClickListener;
    private OnUsernameClickListener onUsernameClickListener;
    private OnNiceShotListener onNiceShotListener;
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
            @Override
            public void onScrollUpDownChanged(int delta, int scrollPosition, boolean exact) {
                if (delta < -10) {
                    // going down
                } else if (delta > 10) {
                    // going up
                }
            }

            @Override
            public void onScrollIdle() {
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
        avatarClickListener = new OnAvatarClickListener() {
            @Override
            public void onAvatarClick(String userId, View avatarView) {
                openProfile(userId);
            }
        };

        imageClickListener = new OnImageClickListener() {
            @Override
            public void onImageClick(String url) {
                openImage(url);
            }
        };

        onUsernameClickListener = new OnUsernameClickListener() {
            @Override
            public void onUsernameClick(String username) {
                goToUserProfile(username);
            }
        };

        videoClickListener = new OnVideoClickListener() {
            @Override
            public void onVideoClick(String url) {
                onShotVideoClick(url);
            }
        };

        onNiceShotListener = new OnNiceShotListener() {
            @Override
            public void markNice(String idShot) {
                presenter.markNiceShot(idShot);
            }

            @Override
            public void unmarkNice(String idShot) {
                presenter.unmarkNiceShot(idShot);
            }
        };

        View footerView = LayoutInflater.from(this).inflate(R.layout.item_list_loading, listView, false);
        footerProgress = ButterKnife.findById(footerView, R.id.loading_progress);

        footerProgress.setVisibility(View.GONE);

        listView.addFooterView(footerView, null, false);

        adapter = new TimelineAdapter(this, imageLoader, timeUtils, avatarClickListener,
          imageClickListener, videoClickListener, onNiceShotListener, onUsernameClickListener){
            @Override protected boolean shouldShowTag() {
                return true;
            }
        };
        listView.setAdapter(adapter);
    }

    private void openContextualMenu(final ShotModel shotModel) {
        new CustomContextMenu.Builder(this)
          .addAction(getString(R.string.menu_share_shot_via_shootr), new Runnable() {
              @Override public void run() {
                  presenter.shareShot(shotModel);
              }
          })
          .addAction(getString(R.string.menu_share_shot_via), new Runnable() {
              @Override public void run() {
                  shareShot(shotModel);
              }
          })
          .addAction(getString(R.string.menu_copy_text), new Runnable() {
                @Override public void run() {
                    Clipboard.copyShotComment(AllShotsActivity.this, shotModel);
                }
            }).addAction(this.getString(R.string.report_context_menu_report), new Runnable() {
            @Override public void run() {
                reportShotPresenter.report(shotModel);
            }
        }).show();
    }

    private void shareShot(ShotModel shotModel) {
        Intent shareIntent = intentFactory.shareShotIntent(this, shotModel);
        Intents.maybeStartActivity(this, shareIntent);
    }

    protected void openProfile(String idUser) {
        Intent profileIntent = ProfileContainerActivity.getIntent(this, idUser);
        startActivity(profileIntent);
    }

    protected void openImage(String imageUrl) {
        Intent intentForImage = PhotoViewActivity.getIntentForActivity(this, imageUrl);
        startActivity(intentForImage);
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
        Toast.makeText(this, messageForError, Toast.LENGTH_SHORT).show();
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
        Toast.makeText(this, getString(R.string.shot_shared_message), Toast.LENGTH_SHORT).show();
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
        openContextualMenu(shot);
        return true;
    }

    @Override public void showEmailNotConfirmedError() {
        AlertDialog.Builder builder =
          new AlertDialog.Builder(this);

        builder.setMessage(getString(R.string.alert_report_confirmed_email_message))
          .setTitle(getString(R.string.alert_report_confirmed_email_title))
          .setPositiveButton(getString(R.string.alert_report_confirmed_email_ok), null);

        builder.create().show();
    }
}
