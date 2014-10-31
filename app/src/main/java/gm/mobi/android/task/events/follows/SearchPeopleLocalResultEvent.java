package gm.mobi.android.task.events.follows;

import gm.mobi.android.task.jobs.ShootrBaseJob;
import gm.mobi.android.ui.model.UserModel;
import java.util.List;

public class SearchPeopleLocalResultEvent extends ShootrBaseJob.SuccessEvent<List<UserModel>> {

    public SearchPeopleLocalResultEvent(List<UserModel> result) {
        super(result);
    }
}
