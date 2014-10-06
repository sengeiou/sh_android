package gm.mobi.android.task.jobs.follows;

import android.app.Application;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.squareup.otto.Bus;
import gm.mobi.android.db.objects.Follow;
import gm.mobi.android.db.objects.User;
import gm.mobi.android.service.BagdadService;
import gm.mobi.android.service.dataservice.dto.UserDtoFactory;
import gm.mobi.android.task.events.ConnectionNotAvailableEvent;
import gm.mobi.android.task.events.ResultEvent;
import gm.mobi.android.task.events.follows.FollowsResultEvent;
import gm.mobi.android.task.jobs.CancellableJob;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class GetUsersFollowsJob extends CancellableJob {

    Application app;
    Bus bus;
    NetworkUtil networkUtil;
    BagdadService service;

    private static final int PRIORITY = 5;
    private Long idUserToRetrieveFollowsFrom;
    private int followType;

    @Inject public GetUsersFollowsJob(Application context, Bus bus, BagdadService service, NetworkUtil networkUtil) {
        super(new Params(PRIORITY));
        this.app = context;
        this.service = service;
        this.bus = bus;
        this.networkUtil = networkUtil;
    }

    public void init(Long userId, int followType) {
        this.idUserToRetrieveFollowsFrom = userId;
        this.followType = followType;
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
        if (!checkNetwork()) return;
        try {
            List<Long> followingIds = getFollowsIdsFromService();
            List<User> users = getFollowsUsersFromServiceByIds(followingIds);
            sendSuccessfulResult(users);
        } catch (IOException e) {
            sendCommunicationError();
            throw e;
        }
    }

    private List<Long> getFollowsIdsFromService() throws IOException {
        List<Follow> follows = service.getFollows(idUserToRetrieveFollowsFrom, 0L, followType, false);
        return getIdsFromFollows(follows);
    }

    private List<Long> getIdsFromFollows(List<Follow> follows) {
        List<Long> followsIds = new ArrayList<>(follows.size());
        for (Follow follow : follows) {
            if (followType == UserDtoFactory.GET_FOLLOWING) {
                followsIds.add(follow.getFollowedUser());
            } else {
                followsIds.add(follow.getIdUser());
            }

        }
        return followsIds;
    }

    private List<User> getFollowsUsersFromServiceByIds(List<Long> followingIds) throws IOException {
        return service.getUsersByUserIdList(followingIds);
    }

    private void sendSuccessfulResult(List<User> followingUsers) {
        bus.post(new FollowsResultEvent(ResultEvent.STATUS_SUCCESS).setSuccessful(followingUsers));
    }

    private void sendCommunicationError() {
        //TODO abstraer evento de CommunicationError y hacerlo general para los jobs
        bus.post(new FollowsResultEvent(ResultEvent.STATUS_INVALID));
    }

    private boolean checkNetwork() {
        if (!networkUtil.isConnected(app)) {
            bus.post(new ConnectionNotAvailableEvent());
            return false;
        }
        return true;
    }

    @Override protected void onCancel() {

    }

    @Override protected boolean shouldReRunOnThrowable(Throwable throwable) {
        return false;
    }
}
