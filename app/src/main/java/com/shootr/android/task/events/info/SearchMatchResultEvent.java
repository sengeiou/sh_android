package com.shootr.android.task.events.info;

import com.shootr.android.task.jobs.ShootrBaseJob;
import com.shootr.android.ui.model.MatchModel;
import java.util.List;

public class SearchMatchResultEvent extends ShootrBaseJob.SuccessEvent<List<MatchModel>>{

    public SearchMatchResultEvent(List<MatchModel> result) {
        super(result);
    }
}
