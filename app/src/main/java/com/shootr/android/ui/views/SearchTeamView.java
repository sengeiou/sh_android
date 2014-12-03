package com.shootr.android.ui.views;

import java.util.List;

public interface SearchTeamView {

    void setCurrentSearchText(String searchText);

    void renderResults(List<String> teams);
}
