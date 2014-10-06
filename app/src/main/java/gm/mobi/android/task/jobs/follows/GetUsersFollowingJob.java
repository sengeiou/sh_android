package gm.mobi.android.task.jobs.follows;

import android.app.Application;
import com.path.android.jobqueue.Params;
import com.squareup.otto.Bus;
import gm.mobi.android.db.objects.Follow;
import gm.mobi.android.db.objects.User;
import gm.mobi.android.service.BagdadService;
import gm.mobi.android.service.dataservice.dto.UserDtoFactory;
import gm.mobi.android.task.events.ResultEvent;
import gm.mobi.android.task.events.follows.FollowsResultEvent;
import gm.mobi.android.task.jobs.CancellableJob;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class GetUsersFollowingJob extends CancellableJob {

    Application app;
    Bus bus;
    BagdadService service;

    private static final int PRIORITY = 5;
    private Long idUserToRetrieveFollowsFrom;

    @Inject public GetUsersFollowingJob(Application context, Bus bus, BagdadService service) {
        super(new Params(PRIORITY));
        this.app = context;
        this.service = service;
        this.bus = bus;
    }

    public void init(Long userId) {
        this.idUserToRetrieveFollowsFrom = userId;
    }

    @Override public void onAdded() {
        /*no-op*/
    }

    @Override
    protected void createDatabase() {
        /*no-op*/
    }

    @Override
    protected void setDatabaseToManagers() {
        /*no-op*/
    }

    @Override
    protected void run() throws IOException {
        try {
            List<Long> followingIds = getFollowingIdsFromService();
            List<User> users = getFollowingUsersFromServiceByIds(followingIds);
            sendSuccessfulResult(users);
        } catch (IOException e) {
            sendCommunicationError();
            throw e;
        }
    }

    private List<Long> getFollowingIdsFromService() throws IOException {
        List<Follow> followings =
            service.getFollows(idUserToRetrieveFollowsFrom, 0L, UserDtoFactory.GET_FOLLOWING,
                false);

        List<Long> followingsIds = new ArrayList<>(followings.size());
        for (Follow follow : followings) {
            followingsIds.add(follow.getFollowedUser());
        }

        return followingsIds;
    }

    private List<User> getFollowingUsersFromServiceByIds(List<Long> followingIds)
        throws IOException {
        return service.getUsersByUserIdList(followingIds);
    }

    private void sendSuccessfulResult(List<User> followingUsers) {
        bus.post(new FollowsResultEvent(ResultEvent.STATUS_SUCCESS).setSuccessful(followingUsers));
    }

    private void sendCommunicationError() {
        //TODO abstraer evento de CommunicationError y hacerlo general para los jobs
        bus.post(new FollowsResultEvent(ResultEvent.STATUS_INVALID));
    }

    @Override protected void onCancel() {

    }

    @Override protected boolean shouldReRunOnThrowable(Throwable throwable) {
        return false;
    }
}
