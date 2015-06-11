package com.shootr.android.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.shootr.android.R;
import com.shootr.android.ui.ToolbarDecorator;
import com.shootr.android.ui.adapters.EventsListAdapter;
import com.shootr.android.ui.model.EventModel;
import com.shootr.android.ui.model.EventResultModel;
import com.shootr.android.ui.presenter.ListingListPresenter;
import com.shootr.android.ui.views.ListingView;
import java.util.List;
import java.util.Locale;
import javax.inject.Inject;

public class ListingActivity extends BaseToolbarDecoratedActivity implements ListingView {

    public static final String EXTRA_ID_USER = "idUser";

    @InjectView(R.id.listing_list) RecyclerView listingList;
    @InjectView(R.id.listing_loading) View loadingView;

    @Inject ListingListPresenter presenter;

    private EventsListAdapter adapter;

    @Override protected int getLayoutResource() {
        return R.layout.activity_listing;
    }

    @Override protected void initializeViews(Bundle savedInstanceState) {
        ButterKnife.inject(this);

        listingList.setLayoutManager(new LinearLayoutManager(this));

        adapter = new EventsListAdapter(picasso);
        listingList.setAdapter(adapter);

        adapter.setOnEventClickListener(new EventsListAdapter.OnEventClickListener() {
            @Override public void onEventClick(EventModel event) {
                presenter.selectEvent(event);
            }
        });
    }

    @Override protected void initializePresenter() {
        Intent intent = getIntent();
        String idUser = intent.getStringExtra(EXTRA_ID_USER);
        presenter.initialize(this, idUser);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override protected void setupToolbar(ToolbarDecorator toolbarDecorator) {
        /* no-op */
    }

    @Override public void renderEvents(List<EventResultModel> events) {
        adapter.setEvents(events);
    }

    @Override public void navigateToEventTimeline(String idEvent, String title) {
        startActivity(EventTimelineActivity.newIntent(this, idEvent, title));
    }

    @Override public void hideLoading() {
        loadingView.setVisibility(View.GONE);
    }

    @Override public void showLoading() {
        loadingView.setVisibility(View.VISIBLE);
    }

    @Override public Locale getLocale() {
        return getResources().getConfiguration().locale;
    }

    @Override public void hideContent() {
        listingList.setVisibility(View.GONE);
    }

    @Override public void showContent() {
        listingList.setVisibility(View.VISIBLE);
    }
}
