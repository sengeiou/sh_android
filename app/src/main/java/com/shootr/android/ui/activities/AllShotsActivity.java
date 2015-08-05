package com.shootr.android.ui.activities;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import com.shootr.android.R;
import com.shootr.android.ui.ToolbarDecorator;
import com.shootr.android.ui.adapters.TimelineAdapter;
import com.shootr.android.ui.adapters.listeners.NiceShotListener;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.ui.presenter.AllShotsPresenter;
import com.shootr.android.ui.presenter.SessionUserPresenter;
import com.shootr.android.ui.views.AllShotsView;
import com.shootr.android.ui.views.SessionUserView;
import com.shootr.android.ui.widgets.ListViewScrollObserver;
import com.shootr.android.util.AndroidTimeUtils;
import com.shootr.android.util.UsernameClickListener;
import java.util.List;
import javax.inject.Inject;

import static com.shootr.android.domain.utils.Preconditions.checkNotNull;

public class AllShotsActivity extends BaseToolbarDecoratedActivity implements AllShotsView, SessionUserView {

    private static final String[] CONTEXT_MENU_OPTIONS = {"Copy", "Report"};
    private static final Integer[] CONTEXT_MENU_OPTIONS_INDEX = {0, 1};
    public static final String CLIPBOARD_LABEL = "clipboard_label";

    private static final String EXTRA_USER = "user";

    @Inject AllShotsPresenter presenter;
    @Inject SessionUserPresenter sessionUserPresenter;
    @Inject AndroidTimeUtils timeUtils;

    @Bind(R.id.all_shots_list) ListView listView;
    @Bind(R.id.timeline_empty) View emptyView;
    @Bind(R.id.all_shots_loading) View loadingView;

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

    public void onCreateContextMenu(ContextMenu menu, View v,
      ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        for(int i = 0; i<CONTEXT_MENU_OPTIONS.length; i++) {
            menu.add(0, CONTEXT_MENU_OPTIONS_INDEX[i], 0, CONTEXT_MENU_OPTIONS[i]);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        ContextMenu.ContextMenuInfo menuInfo = item.getMenuInfo();
        if(menuInfo instanceof AdapterView.AdapterContextMenuInfo) {
            AdapterView.AdapterContextMenuInfo adapterContextMenuInfo = (AdapterView.AdapterContextMenuInfo) menuInfo;
            int position = adapterContextMenuInfo.position;
            ShotModel shotModel = adapter.getItem(position);
            int itemId = item.getItemId();
            if(itemId == CONTEXT_MENU_OPTIONS_INDEX[0]) {
                ClipboardManager clipboard = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(CLIPBOARD_LABEL, shotModel.getComment());
                clipboard.setPrimaryClip(clip);
            } else if (itemId == CONTEXT_MENU_OPTIONS_INDEX[1]) {
                sessionUserPresenter.initialize(this, shotModel);
            }
        }
        return true;
    }

    @OnItemClick(R.id.all_shots_list)
    public void openShot(int position) {
        ShotModel shot = adapter.getItem(position);
        Intent intent = ShotDetailActivity.getIntentForActivity(this, shot);
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
          imageClickListener, videoClickListener, niceShotListener, usernameClickListener, timeUtils){
            @Override protected boolean shouldShowTag() {
                return true;
            }
        };
        listView.setAdapter(adapter);
        registerForContextMenu(listView);
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
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://report.shootr.com/#/?token=" + sessionToken + "&idShot=" + shotModel.getIdShot()));
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
