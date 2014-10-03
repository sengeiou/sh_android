package gm.mobi.android.task.jobs.follows;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.squareup.otto.Bus;
import gm.mobi.android.GolesApplication;
import gm.mobi.android.db.objects.Follow;
import gm.mobi.android.db.objects.User;
import gm.mobi.android.service.BagdadService;
import gm.mobi.android.service.dataservice.dto.UserDtoFactory;
import gm.mobi.android.task.events.ResultEvent;
import gm.mobi.android.task.events.follows.FollowsResultEvent;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class GetUsersFollowingJob extends Job {

    @Inject Bus bus;
    @Inject BagdadService service;

    private static final int PRIORITY = 5;
    private Long idUserToRetrieveFollowsFrom;

    public GetUsersFollowingJob(Context context, Long idUserToRetrieveFollowsFrom) {
        super(new Params(PRIORITY));
        this.idUserToRetrieveFollowsFrom = idUserToRetrieveFollowsFrom;
        GolesApplication.get(context).inject(this);
    }

    @Override public void onAdded() {
        // noop
    }

    @Override public void onRun() throws Throwable {

        List<Follow> followings =
            service.getFollows(idUserToRetrieveFollowsFrom, 0L, UserDtoFactory.GET_FOLLOWING, false);

        List<Long> followingsIds = new ArrayList<>(followings.size());
        for (Follow follow : followings) {
            followingsIds.add(follow.getFollowedUser());
        }
        List<User> users = service.getUsersByUserIdList(followingsIds);

        bus.post(new FollowsResultEvent(ResultEvent.STATUS_SUCCESS).setSuccessful(users));
    }

    @Override protected void onCancel() {

    }

    @Override protected boolean shouldReRunOnThrowable(Throwable throwable) {
        return false;
    }
}
