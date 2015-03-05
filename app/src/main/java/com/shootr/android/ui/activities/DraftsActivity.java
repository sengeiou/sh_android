package com.shootr.android.ui.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.shootr.android.R;
import com.shootr.android.ui.adapters.DraftAdapter;
import com.shootr.android.ui.base.BaseSignedInActivity;
import com.shootr.android.ui.model.DraftModel;
import com.shootr.android.ui.presenter.DraftsPresenter;
import com.shootr.android.ui.views.DraftsView;
import com.shootr.android.util.PicassoWrapper;
import java.util.List;
import javax.inject.Inject;

public class DraftsActivity extends BaseSignedInActivity implements DraftsView, DraftAdapter.DraftActionListener {

    @Inject DraftsPresenter presenter;
    @Inject PicassoWrapper picasso;

    @InjectView(R.id.drafts_list) RecyclerView listView;
    @InjectView(R.id.drafts_empty) View emptyView;

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
        ButterKnife.inject(this);
        timelineAdapter = new DraftAdapter(picasso, this);
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
        getMenuInflater().inflate(R.menu.drafts, menu);
        shootAllMenuItem = menu.findItem(R.id.menu_shoot_all);
        updateShootAllVisibility();
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
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
        // TODO
    }

    @Override public void onEditDraft(DraftModel draftModel) {
        // TODO
    }

    @Override public void onRemoveDraft(DraftModel draftModel) {
        // TODO
    }
    //endregion
}
