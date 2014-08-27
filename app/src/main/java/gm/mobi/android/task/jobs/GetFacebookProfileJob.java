package gm.mobi.android.task.jobs;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;
import com.squareup.otto.Bus;

import gm.mobi.android.exception.FacebookException;
import gm.mobi.android.task.BusProvider;
import gm.mobi.android.task.events.FacebookProfileEvent;

public class GetFacebookProfileJob extends Job {

    private static final int PRIORITY = 1;
    private Session fbSession;

    public GetFacebookProfileJob(Session session) {
        super(new Params(PRIORITY)
//                .delayInMs(2000)
        );
        this.fbSession = session;
    }



    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {
        Request.newMeRequest(fbSession, new Request.GraphUserCallback() {
            @Override
            public void onCompleted(GraphUser graphUser, Response response) {
                Bus bus = BusProvider.getInstance();
                if (graphUser != null) {
                    // Post event
                    FacebookProfileEvent facebookProfileEvent = new FacebookProfileEvent(graphUser);
                    bus.post(facebookProfileEvent);
                } else {
                    bus.post(new FacebookProfileEvent(new FacebookException("Received null graphUser")));
                }
                // Logout from facebook locally
            }
        }).executeAndWait();
    }

    @Override
    protected void onCancel() {

    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        return true;
    }
}
