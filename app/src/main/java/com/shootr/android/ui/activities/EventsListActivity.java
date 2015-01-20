package com.shootr.android.ui.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.shootr.android.R;
import com.shootr.android.ui.adapters.DividerItemDecoration;
import com.shootr.android.ui.adapters.EventsListAdapter;
import com.shootr.android.ui.base.BaseSignedInActivity;
import com.shootr.android.ui.model.EventResultModel;
import com.shootr.android.ui.presenter.EventsListPresenter;
import com.shootr.android.ui.views.EventsListView;
import com.shootr.android.util.PicassoWrapper;
import java.util.List;
import javax.inject.Inject;

public class EventsListActivity extends BaseSignedInActivity implements EventsListView {

    @InjectView(R.id.events_list) RecyclerView eventsList;
    @Inject EventsListPresenter presenter;
    @Inject PicassoWrapper picasso;

    private EventsListAdapter adapter;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!restoreSessionOrLogin()) {
            return;
        }
        setContainerContent(R.layout.activity_events_list);
        setupActionbar();
        initializeViews();

        initializePresenter();
    }

    private void setupActionbar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
    }

    private void initializeViews() {
        ButterKnife.inject(this);
        eventsList.setLayoutManager(new LinearLayoutManager(this));
        eventsList.addItemDecoration(new DividerItemDecoration(this));
        adapter = new EventsListAdapter(picasso);
        eventsList.setAdapter(adapter);
    }

    private void initializePresenter() {
        presenter.initialize(this);
    }

    //region View methods
    @Override public void renderEvents(List<EventResultModel> events) {
        adapter.setEvents(events);
    }
    //endregion
}
