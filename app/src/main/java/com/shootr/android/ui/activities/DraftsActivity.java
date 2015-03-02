package com.shootr.android.ui.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.widget.ListView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.shootr.android.R;
import com.shootr.android.ui.adapters.DraftAdapter;
import com.shootr.android.ui.adapters.TimelineAdapter;
import com.shootr.android.ui.base.BaseSignedInActivity;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.ui.presenter.DraftsPresenter;
import com.shootr.android.ui.views.DraftsView;
import com.shootr.android.util.AndroidTimeUtils;
import com.shootr.android.util.PicassoWrapper;
import java.util.List;
import javax.inject.Inject;

public class DraftsActivity extends BaseSignedInActivity implements DraftsView {

    @Inject DraftsPresenter presenter;
    @Inject PicassoWrapper picasso;
    @Inject AndroidTimeUtils androidTimeUtils;

    @InjectView(R.id.drafts_list) ListView listView;

    private DraftAdapter timelineAdapter;

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
        timelineAdapter = new DraftAdapter(this, picasso, null, null, androidTimeUtils);
        listView.setAdapter(timelineAdapter);
    }

    private void setupActionbar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
    }

    private void initializePresenter() {
        presenter.initialize(this);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override public void showEmpty() {
        /* no-op */
    }

    @Override public void hideEmpty() {
        /* no-op */
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

    @Override public void showDrafts(List<ShotModel> drafts) {
        timelineAdapter.setShots(drafts);
    }
}
