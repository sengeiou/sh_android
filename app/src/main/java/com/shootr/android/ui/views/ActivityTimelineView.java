package com.shootr.android.ui.views;

import com.shootr.android.ui.model.ActivityModel;
import com.shootr.android.ui.views.base.LoadDataView;
import java.util.List;

public interface ActivityTimelineView extends LoadDataView {

    void setActivities(List<ActivityModel> activities);

    void hideActivities();

    void showActivities();

    void addNewActivities(List<ActivityModel> newActivities);

    void addOldActivities(List<ActivityModel> oldActivities);

    void showLoadingOldActivities();

    void hideLoadingOldActivities();

    void showLoadingActivity();

    void hideLoadingActivity();
}
