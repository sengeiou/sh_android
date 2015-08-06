package com.shootr.android.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.Bind;
import com.shootr.android.R;
import com.shootr.android.ui.activities.StreamTimelineActivity;
import com.shootr.android.ui.adapters.FavoriteStreamsAdapter;
import com.shootr.android.ui.adapters.listeners.OnStreamClickListener;
import com.shootr.android.ui.base.BaseFragment;
import com.shootr.android.ui.model.StreamResultModel;
import com.shootr.android.ui.presenter.FavoritesListPresenter;
import com.shootr.android.ui.views.FavoritesListView;
import com.shootr.android.ui.views.nullview.NullFavoritesListView;
import com.shootr.android.util.PicassoWrapper;
import java.util.List;
import javax.inject.Inject;

public class FavoritesFragment extends BaseFragment implements FavoritesListView {

    @Inject FavoritesListPresenter favoritesListPresenter;
    @Inject PicassoWrapper picasso;

    @Bind(R.id.streams_list_swipe_refresh) SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.favorites_list) RecyclerView favoritesList;
    @Bind(R.id.favorites_empty) View empty;
    @Bind(R.id.favorites_loading) View loading;

    private FavoriteStreamsAdapter adapter;

    public static Fragment newInstance() {
        return new FavoritesFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_favorites, container, false);
        ButterKnife.bind(this, fragmentView);
        return fragmentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        favoritesListPresenter.setView(new NullFavoritesListView());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initializePresenter();
    }

    @Override
    public void onResume() {
        super.onResume();
        favoritesListPresenter.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        favoritesListPresenter.pause();
    }

    private void initializeViews() {
        favoritesList.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new FavoriteStreamsAdapter(picasso, new OnStreamClickListener() {
            @Override
            public void onStreamClick(StreamResultModel stream) {
                favoritesListPresenter.selectStream(stream);
            }
        });
        favoritesList.setAdapter(adapter);

        swipeRefreshLayout.setColorSchemeResources(R.color.refresh_1,
          R.color.refresh_2,
          R.color.refresh_3,
          R.color.refresh_4);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override public void onRefresh() {
                favoritesListPresenter.refresh();
            }
        });

    }

    private void initializePresenter() {
        favoritesListPresenter.initialize(this);
    }

    @Override
    public void renderFavorites(List<StreamResultModel> streamModels) {
        adapter.setStreams(streamModels);
    }

    @Override
    public void showContent() {
        favoritesList.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideContent() {
        favoritesList.setVisibility(View.GONE);
    }

    @Override
    public void navigateToStreamTimeline(String idStream, String title) {
        startActivity(StreamTimelineActivity.newIntent(getActivity(), idStream, title));
    }

    @Override public void hideLoadingSwipe() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showEmpty() {
        empty.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideEmpty() {
        empty.setVisibility(View.GONE);
    }

    @Override
    public void showLoading() {
        loading.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        loading.setVisibility(View.GONE);
    }

    @Override
    public void showError(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
