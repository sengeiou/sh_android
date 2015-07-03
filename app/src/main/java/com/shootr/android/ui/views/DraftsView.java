package com.shootr.android.ui.views;

import com.shootr.android.ui.model.DraftModel;
import com.shootr.android.ui.views.base.LoadDataView;
import java.util.List;

public interface DraftsView extends LoadDataView {

    void showDrafts(List<DraftModel> drafts);

    void hideShootAllButton();

    void showShootAllButton();
}
