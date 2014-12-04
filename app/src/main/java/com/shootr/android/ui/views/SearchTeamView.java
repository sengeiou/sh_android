package com.shootr.android.ui.views;

import com.shootr.android.ui.model.TeamModel;
import java.util.List;

public interface SearchTeamView {

    void setCurrentSearchText(String searchText);

    void deliverSelectedTeam(String teamName, Long teamId);

    void renderResults(List<TeamModel> teams);

    void hideResults();

    void showLoading();

    void hideLoading();

    void showEmpty();

    void hideEmpty();

    void showMaxResultsIndicator();

    void hideMaxResultsIndicator();

    void enableDeleteTeam(String teamName);

    void disableDeleteTeam();

    void alertComunicationError();

    void alertConnectionNotAvailable();

    void notifyMinimunThreeCharacters();

    void hideKeyboard();
}
