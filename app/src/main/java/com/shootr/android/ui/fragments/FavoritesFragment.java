package com.shootr.android.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.shootr.android.R;
import com.shootr.android.ui.adapters.EventsListAdapter;
import com.shootr.android.ui.base.BaseFragment;
import com.shootr.android.ui.model.EventResultModel;
import com.shootr.android.ui.presenter.FavoritesListPresenter;
import com.shootr.android.ui.views.FavoritesListView;
import com.shootr.android.ui.views.nullview.NullFavoritesListView;
import com.shootr.android.util.PicassoWrapper;
import java.util.List;
import javax.inject.Inject;

public class FavoritesFragment extends BaseFragment implements FavoritesListView {

    @Inject FavoritesListPresenter favoritesListPresenter;
    @Inject PicassoWrapper picasso;

    @InjectView(R.id.favorites_list) RecyclerView favoritesList;
    @InjectView(R.id.favorites_empty) View empty;
    @InjectView(R.id.favorites_loading) View loading;

    private EventsListAdapter adapter;

    public static Fragment newInstance() {
        return new FavoritesFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_favorites, container, false);
        ButterKnife.inject(this, fragmentView);
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
        ButterKnife.reset(this);
        favoritesListPresenter.setView(new NullFavoritesListView());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initializePresenter();
    }

    private void initializeViews() {
        favoritesList.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new EventsListAdapter(picasso);
        favoritesList.setAdapter(adapter);
    }

    private void initializePresenter() {
        favoritesListPresenter.initialize(this);
    }

    @Override
    public void renderFavorites(List<EventResultModel> eventModels) {
        adapter.setEvents(eventModels);
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
