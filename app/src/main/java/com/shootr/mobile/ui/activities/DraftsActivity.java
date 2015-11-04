package com.shootr.mobile.ui.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.DraftAdapter;
import com.shootr.mobile.ui.base.BaseSignedInActivity;
import com.shootr.mobile.ui.model.DraftModel;
import com.shootr.mobile.ui.presenter.DraftsPresenter;
import com.shootr.mobile.ui.views.DraftsView;
import com.shootr.mobile.util.ImageLoader;
import java.util.List;
import javax.inject.Inject;

public class DraftsActivity extends BaseSignedInActivity implements DraftsView, DraftAdapter.DraftActionListener {

    @Inject DraftsPresenter presenter;
    @Inject ImageLoader imageLoader;

    @Bind(R.id.drafts_list) RecyclerView listView;
    @Bind(R.id.drafts_empty) View emptyView;

    private DraftAdapter timelineAdapter;
    private boolean showShootAll = false;
    private MenuItem shootAllMenuItem;

    //region Initialization
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!restoreSessionOrLogin()) {
            finish();
        }
        inflateLayout();
        initializeViews();
        setupActionbar();
        initializePresenter();
    }

    private void inflateLayout() {
        setContainerContent(R.layout.activity_drafts);
    }

    private void initializeViews() {
        ButterKnife.bind(this);
        timelineAdapter = new DraftAdapter(imageLoader, this);
        listView.setLayoutManager(new LinearLayoutManager(this));
        listView.setAdapter(timelineAdapter);
        listView.setItemAnimator(new DefaultItemAnimator());
    }

    private void setupActionbar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
    }

    private void initializePresenter() {
        presenter.initialize(this);
    }
    //endregion

    private void updateShootAllVisibility() {
        if (shootAllMenuItem != null) {
            shootAllMenuItem.setVisible(showShootAll);
        }
    }

    //region Activity methods
    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(com.shootr.mobile.R.menu.drafts, menu);
        shootAllMenuItem = menu.findItem(com.shootr.mobile.R.id.menu_shoot_all);
        updateShootAllVisibility();
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.menu_shoot_all) {
            presenter.shootAll();
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
    //endregion

    //region View methods
    @Override public void showEmpty() {
        emptyView.setVisibility(View.VISIBLE);
    }

    @Override public void hideEmpty() {
        emptyView.setVisibility(View.GONE);
    }

    @Override public void showLoading() {
        /* no-op */
    }

    @Override public void hideLoading() {
        /* no-op */
    }

    @Override public void showError(String message) {
        /* no-op */
    }

    @Override public void showDrafts(List<DraftModel> drafts) {
        timelineAdapter.setDrafts(drafts);
    }

    @Override public void hideShootAllButton() {
        showShootAll = false;
        updateShootAllVisibility();
    }

    @Override public void showShootAllButton() {
        showShootAll = true;
        updateShootAllVisibility();
    }
    //endregion

    //region List actions
    @Override public void onShootDraft(DraftModel draftModel) {
        presenter.sendDraft(draftModel);
    }

    @Override public void onDeleteDraft(DraftModel draftModel) {
        presenter.deleteDraft(draftModel);
    }
    //endregion
}
