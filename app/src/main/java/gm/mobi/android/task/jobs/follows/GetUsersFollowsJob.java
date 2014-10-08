package gm.mobi.android.task.jobs.follows;

import android.app.Application;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.squareup.otto.Bus;
import gm.mobi.android.db.objects.User;
import gm.mobi.android.service.BagdadService;
import gm.mobi.android.task.events.ConnectionNotAvailableEvent;
import gm.mobi.android.task.events.ResultEvent;
import gm.mobi.android.task.events.follows.FollowsResultEvent;
import gm.mobi.android.task.jobs.CancellableJob;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.inject.Inject;

public class GetUsersFollowsJob extends CancellableJob {

    Application app;
    Bus bus;
    NetworkUtil networkUtil;
    BagdadService service;

    private static final int PRIORITY = 5;
    private Long idUserToRetrieveFollowsFrom;

    @Inject public GetUsersFollowsJob(Application context, Bus bus, BagdadService service, NetworkUtil networkUtil) {
        super(new Params(PRIORITY));
        this.app = context;
        this.service = service;
        this.bus = bus;
        this.networkUtil = networkUtil;
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
    protected void run() throws IOException, SQLException {
        if (!checkNetwork()) return;
        try {
            List<User> users = getFollowsUsersFromService(idUserToRetrieveFollowsFrom, 0L);
            sendSuccessfulResult(users);
        } catch (IOException e) {
            sendCommunicationError();
            throw e;
        }
    }

    private List<User> getFollowsUsersFromService(Long idUser, Long lastModifiedDate) throws IOException {
        List<User> users = service.getFollowings(idUser, lastModifiedDate);
        Collections.sort(users,new NameComparator());
        return users;
    }

    protected void sendSuccessfulResult(List<User> followingUsers) {
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


    class NameComparator implements Comparator<User> {

        @Override public int compare(User user1, User user2) {
            return user1.getName().compareTo(user2.getName());
        }

    }
}
