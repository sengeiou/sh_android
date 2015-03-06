package com.shootr.android.ui.views;

import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.ui.views.base.LoadDataView;
import java.util.List;

public interface DraftsView extends LoadDataView {

    void showDrafts(List<ShotModel> drafts);

    void hideShootAllButton();

    void showShootAllButton();
}
