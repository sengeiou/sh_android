package com.shootr.android.ui.views;

import com.shootr.android.ui.model.TeamModel;
import java.util.List;

public interface SearchTeamView {

    void setCurrentSearchText(String searchText);

    void renderResults(List<TeamModel> teams);
}
