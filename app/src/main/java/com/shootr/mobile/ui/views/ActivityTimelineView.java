package com.shootr.mobile.ui.views;

import com.shootr.mobile.ui.model.ActivityModel;
import com.shootr.mobile.ui.views.base.LoadDataView;
import java.util.List;

public interface ActivityTimelineView extends LoadDataView {

    void setActivities(List<ActivityModel> activities, String currentUserId);

    void hideActivities();

    void showActivities();

    void addNewActivities(List<ActivityModel> newActivities);

    void addOldActivities(List<ActivityModel> oldActivities);

    void showLoadingOldActivities();

    void hideLoadingOldActivities();

    void showLoadingActivity();

    void hideLoadingActivity();
}
