package gm.mobi.android.task.events.follows;

import gm.mobi.android.task.jobs.BagdadBaseJob;
import gm.mobi.android.ui.model.UserModel;
import java.util.List;

public class SearchPeopleLocalResultEvent extends BagdadBaseJob.SuccessEvent<List<UserModel>> {

    public SearchPeopleLocalResultEvent(List<UserModel> result) {
        super(result);
    }
}
