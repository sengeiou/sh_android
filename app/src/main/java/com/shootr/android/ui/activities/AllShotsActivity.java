package com.shootr.android.ui.activities;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
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
import com.shootr.android.R;
import com.shootr.android.ui.ToolbarDecorator;
import com.shootr.android.ui.adapters.TimelineAdapter;
import com.shootr.android.ui.adapters.listeners.NiceShotListener;
import com.shootr.android.ui.adapters.listeners.OnShotClickListener;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.ui.presenter.AllShotsPresenter;
import com.shootr.android.ui.presenter.ReportShotPresenter;
import com.shootr.android.ui.views.AllShotsView;
import com.shootr.android.ui.views.ReportShotView;
import com.shootr.android.ui.widgets.ListViewScrollObserver;
import com.shootr.android.util.AndroidTimeUtils;
import com.shootr.android.util.CustomContextMenu;
import com.shootr.android.util.UsernameClickListener;
import java.util.List;
import javax.inject.Inject;

import static com.shootr.android.domain.utils.Preconditions.checkNotNull;

public class AllShotsActivity extends BaseToolbarDecoratedActivity implements AllShotsView, ReportShotView {

    public static final String CLIPBOARD_LABEL = "clipboard_label";

    private static final String EXTRA_USER = "user";

    @Inject AllShotsPresenter presenter;
    @Inject ReportShotPresenter reportShotPresenter;
    @Inject AndroidTimeUtils timeUtils;

    @Bind(R.id.all_shots_list) ListView listView;
    @Bind(R.id.timeline_empty) View emptyView;
    @Bind(R.id.all_shots_loading) View loadingView;

    @BindString(R.string.report_base_url) String reportBaseUrl;

    @Deprecated private TimelineAdapter adapter;

    private View.OnClickListener avatarClickListener;
    private View.OnClickListener imageClickListener;
    private TimelineAdapter.VideoClickListener videoClickListener;
    private UsernameClickListener usernameClickListener;
    private NiceShotListener niceShotListener;
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

    public void openShot(ShotModel shotModel) {
        Intent intent = ShotDetailActivity.getIntentForActivity(this, shotModel);
        startActivity(intent);
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
        avatarClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = ((TimelineAdapter.ViewHolder) v.getTag()).position;
                openProfile(position);
            }
        };

        imageClickListener = new View.OnClickListener() {
            @Override public void onClick(View v) {
                int position = ((TimelineAdapter.ViewHolder) v.getTag()).position;
                openImage(position);
            }
        };

        usernameClickListener = new UsernameClickListener() {
            @Override
            public void onClick(String username) {
                goToUserProfile(username);
            }
        };

        videoClickListener = new TimelineAdapter.VideoClickListener() {
            @Override
            public void onClick(String url) {
                onVideoClick(url);
            }
        };

        niceShotListener = new NiceShotListener() {
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

        adapter = new TimelineAdapter(this, picasso, avatarClickListener,
          imageClickListener, videoClickListener, niceShotListener,
          new OnShotClickListener() {
              @Override public boolean onShotLongClick(ShotModel shotModel) {
                  openContextualMenu(shotModel);
                  return true;
              }

              @Override public void onShotClick(ShotModel shot) {
                  openShot(shot);
              }
          },
          usernameClickListener, timeUtils){
            @Override protected boolean shouldShowTag() {
                return true;
            }
        };
        listView.setAdapter(adapter);
    }

    private void openContextualMenu(final ShotModel shotModel) {
        CustomContextMenu.Builder builder = new CustomContextMenu.Builder(this);
        builder.addAction(this.getString(R.string.report_context_menu_copy_text), new Runnable() {
            @Override public void run() {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(CLIPBOARD_LABEL, shotModel.getComment());
                clipboard.setPrimaryClip(clip);
            }
        });
        builder.addAction(this.getString(R.string.report_context_menu_report), new Runnable() {
            @Override
            public void run() {
                reportShotPresenter.loadReport(shotModel);
            }
        });
        builder.show();
    }

    public void openProfile(int position) {
        ShotModel shotVO = adapter.getItem(position);
        Intent profileIntent = ProfileContainerActivity.getIntent(this, shotVO.getIdUser());
        startActivity(profileIntent);
    }

    public void openImage(int position) {
        ShotModel shotVO = adapter.getItem(position);
        String imageUrl = shotVO.getImage();
        if (imageUrl != null) {
            Intent intentForImage = PhotoViewActivity.getIntentForActivity(this, imageUrl);
            startActivity(intentForImage);
        }
    }

    private void startProfileContainerActivity(String username) {
        Intent intentForUser = ProfileContainerActivity.getIntentWithUsername(this, username);
        startActivity(intentForUser);
    }

    private void goToUserProfile(String username) {
        startProfileContainerActivity(username);
    }

    private void onVideoClick(String url) {
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

    @Override public void goToReport(String sessionToken, ShotModel shotModel) {
        Uri.parse(String.format(reportBaseUrl, sessionToken, shotModel.getIdShot()));
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format(reportBaseUrl,
          sessionToken,
          shotModel.getIdShot())));
        startActivity(browserIntent);
    }

    @Override public void showConfirmationMessage() {
        AlertDialog.Builder builder =
          new AlertDialog.Builder(this);

        builder.setMessage(getString(R.string.alert_report_confirmed_email_message))
          .setTitle(getString(R.string.alert_report_confirmed_email_title))
          .setPositiveButton(getString(R.string.alert_report_confirmed_email_ok), null);

        builder.create().show();
    }
}
