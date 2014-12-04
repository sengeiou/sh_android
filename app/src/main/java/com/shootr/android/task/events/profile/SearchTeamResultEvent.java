package com.shootr.android.task.events.profile;

import com.shootr.android.task.jobs.ShootrBaseJob;
import com.shootr.android.ui.model.TeamModel;
import java.util.List;

public class SearchTeamResultEvent extends ShootrBaseJob.SuccessEvent<List<TeamModel>> {

    public SearchTeamResultEvent(List<TeamModel> result) {
        super(result);
    }
}
