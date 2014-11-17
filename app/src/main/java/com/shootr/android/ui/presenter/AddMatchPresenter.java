package com.shootr.android.ui.presenter;

import com.shootr.android.ui.model.MatchModel;
import com.shootr.android.ui.views.AddMatchView;
import com.squareup.otto.Subscribe;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class AddMatchPresenter {

    private AddMatchView addMatchView;
    private String currentSearchQuery;

    @Inject public AddMatchPresenter() {

    }

    public void initialize(AddMatchView addMatchView) {
        this.addMatchView = addMatchView;
    }

    public void search(String searchQuery) {
        if (!searchQuery.equals(currentSearchQuery)) {
            currentSearchQuery = searchQuery;
            this.executeSearch();
        }
        this.hideKeyboard();
    }

    private void hideKeyboard() {
        this.addMatchView.hideKeyboard();
    }

    private void executeSearch() {
        //TODO
        this.addMatchView.showLoading();
        this.addMatchView.hideResults();
        onSearchResultsReceived(getMockResults());
    }

    @Subscribe
    public void onSearchResultsReceived(List<MatchModel> matchModels) {
        //TODO
        this.addMatchView.renderResults(matchModels);
        this.addMatchView.hideEmpty();
        this.addMatchView.hideLoading();
    }

    public List<MatchModel> getMockResults() {
        List<MatchModel> mockResults = new ArrayList<>();
        MatchModel mm1 = new MatchModel();
        mm1.setIdMatch(1L);
        mm1.setTitle("Barcelona-Sevilla");
        mm1.setDatetime("20/11");
        MatchModel mm2 = new MatchModel();
        mm2.setIdMatch(2L);
        mm2.setTitle("Sevruposki-Palatinesko");
        mm2.setDatetime("29/11");
        MatchModel mm3 = new MatchModel();
        mm3.setIdMatch(3L);
        mm3.setTitle("La Palma-Sevilla");
        mm3.setDatetime("11/12");
        mockResults.add(mm1);
        mockResults.add(mm2);
        mockResults.add(mm3);
        return mockResults;
    }
}
