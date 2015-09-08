package com.shootr.android.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import com.shootr.android.R;
import com.shootr.android.ui.activities.StreamTimelineActivity;
import com.shootr.android.ui.adapters.StreamsListAdapter;
import com.shootr.android.ui.adapters.listeners.OnStreamClickListener;
import com.shootr.android.ui.base.BaseFragment;
import com.shootr.android.ui.model.StreamResultModel;
import com.shootr.android.ui.presenter.FavoritesListPresenter;
import com.shootr.android.ui.views.FavoritesListView;
import com.shootr.android.ui.views.nullview.NullFavoritesListView;
import com.shootr.android.util.CustomContextMenu;
import com.shootr.android.util.FeedbackLoader;
import com.shootr.android.util.ImageLoader;
import com.shootr.android.util.IntentFactory;
import com.shootr.android.util.Intents;
import java.util.List;
import javax.inject.Inject;

public class FavoritesFragment extends BaseFragment implements FavoritesListView {

    @Inject FavoritesListPresenter favoritesListPresenter;
    @Inject ImageLoader imageLoader;
    @Inject IntentFactory intentFactory;
    @Inject FeedbackLoader feedbackLoader;

    @Bind(R.id.favorites_list) RecyclerView favoritesList;
    @Bind(R.id.favorites_empty) View empty;
    @Bind(R.id.favorites_loading) View loading;
    @BindString(R.string.shared_stream_notification) String sharedStream;

    private StreamsListAdapter adapter;

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
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        favoritesListPresenter.setView(new NullFavoritesListView());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initializePresenter();
        initializeViews();
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
        adapter = new StreamsListAdapter(imageLoader, new OnStreamClickListener() {
            @Override
            public void onStreamClick(StreamResultModel stream) {
                favoritesListPresenter.selectStream(stream);
            }

            @Override
            public boolean onStreamLongClick(StreamResultModel stream) {
                openContextualMenu(stream);
                return true;
            }
        });
        favoritesList.setAdapter(adapter);
    }

    private void initializePresenter() {
        favoritesListPresenter.initialize(this);
    }

    private void openContextualMenu(final StreamResultModel stream) {
        new CustomContextMenu.Builder(getActivity())
          .addAction((getActivity().getString(R.string.share_via_shootr)), new Runnable() {
              @Override public void run() {
                  favoritesListPresenter.shareStream(stream);
              }
          })
          .addAction((getActivity().getString(R.string.share_via)), new Runnable() {
              @Override public void run() {
                  shareStream(stream);
              }
          }).show();
    }

    private void shareStream(StreamResultModel stream) {
        Intent shareIntent = intentFactory.shareStreamIntent(getActivity(), stream.getStreamModel());
        Intents.maybeStartActivity(getActivity(), shareIntent);
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

    @Override public void showStreamShared() {
        feedbackLoader.show(getView(), sharedStream);
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
        feedbackLoader.show(getView(), message);
    }
}
