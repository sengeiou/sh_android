package com.shootr.android.task.events.info;

import com.shootr.android.task.jobs.ShootrBaseJob;
import com.shootr.android.ui.model.MatchModel;
import com.shootr.android.ui.model.MatchSearchResultModel;
import java.util.List;

public class SearchMatchResultEvent extends ShootrBaseJob.SuccessEvent<List<MatchSearchResultModel>>{

    public SearchMatchResultEvent(List<MatchSearchResultModel> result) {
        super(result);
    }
}
