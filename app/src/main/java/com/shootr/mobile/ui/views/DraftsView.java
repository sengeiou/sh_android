package com.shootr.mobile.ui.views;

import com.shootr.mobile.ui.model.DraftModel;
import com.shootr.mobile.ui.views.base.LoadDataView;
import java.util.List;

public interface DraftsView extends LoadDataView {

    void showDrafts(List<DraftModel> drafts);

    void hideShootAllButton();

    void showShootAllButton();
}
