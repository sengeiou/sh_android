package com.shootr.android.ui.presenter;

import com.shootr.android.data.bus.Main;
import com.shootr.android.domain.StreamSearchResult;
import com.shootr.android.domain.bus.FavoriteAdded;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.stream.GetFavoriteStreamsInteractor;
import com.shootr.android.ui.model.StreamResultModel;
import com.shootr.android.ui.model.mappers.StreamResultModelMapper;
import com.shootr.android.ui.views.FavoritesListView;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import java.util.List;
import javax.inject.Inject;

public class FavoritesListPresenter implements Presenter, FavoriteAdded.Receiver{

    private final GetFavoriteStreamsInteractor getFavoriteStreamsInteractor;
    private final StreamResultModelMapper streamResultModelMapper;
    private final Bus bus;

    private FavoritesListView favoritesListView;
    private boolean hasBeenPaused = false;

    @Inject public FavoritesListPresenter(GetFavoriteStreamsInteractor getFavoriteStreamsInteractor,
      StreamResultModelMapper streamResultModelMapper, @Main Bus bus) {
        this.getFavoriteStreamsInteractor = getFavoriteStreamsInteractor;
        this.streamResultModelMapper = streamResultModelMapper;
        this.bus = bus;
    }

    public void setView(FavoritesListView favoritesListView) {
        this.favoritesListView = favoritesListView;
    }

    public void initialize(FavoritesListView favoritesListView) {
        setView(favoritesListView);
        this.loadFavorites();
    }

    protected void loadFavorites() {
        favoritesListView.showLoading();
        getFavoriteStreamsInteractor.loadFavoriteStreams(new Interactor.Callback<List<StreamSearchResult>>() {
            @Override public void onLoaded(List<StreamSearchResult> streams) {
                favoritesListView.hideLoading();
                if (streams.isEmpty()) {
                    favoritesListView.showEmpty();
                    favoritesListView.hideContent();
                } else {
                    List<StreamResultModel> streamModels = streamResultModelMapper.transform(streams);
                    favoritesListView.renderFavorites(streamModels);
                    favoritesListView.showContent();
                    favoritesListView.hideEmpty();
                }
            }
        });
    }

    public void selectStream(StreamResultModel stream) {
        selectStream(stream.getStreamModel().getIdStream(), stream.getStreamModel().getTitle());
    }

    private void selectStream(final String idStream, String streamTitle) {
        favoritesListView.navigateToStreamTimeline(idStream, streamTitle);
    }

    @Override
    public void resume() {
        bus.register(this);
        if (hasBeenPaused) {
            this.loadFavorites();
        }
    }

    @Override
    public void pause() {
        bus.unregister(this);
        hasBeenPaused = true;
    }

    @Subscribe
    @Override
    public void onFavoriteAdded(FavoriteAdded.Event event) {
        loadFavorites();
    }
}
