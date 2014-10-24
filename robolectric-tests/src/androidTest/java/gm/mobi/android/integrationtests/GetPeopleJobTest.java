package gm.mobi.android.integrationtests;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.squareup.otto.Bus;
import dagger.ObjectGraph;
import gm.mobi.android.RobolectricGradleTestRunner;
import gm.mobi.android.TestGolesApplication;
import gm.mobi.android.data.SessionManager;
import gm.mobi.android.db.manager.FollowManager;
import gm.mobi.android.db.manager.UserManager;
import gm.mobi.android.db.objects.Follow;
import gm.mobi.android.db.objects.User;
import gm.mobi.android.service.BagdadService;
import gm.mobi.android.task.events.follows.FollowsResultEvent;
import gm.mobi.android.task.jobs.follows.GetPeopleJob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Config(emulateSdk = 18)
@RunWith(RobolectricGradleTestRunner.class)
public class GetPeopleJobTest {

    public static final long FOLLOWED_USER = 1L;
    public static final long CURRENT_USER = 2L;
    @Inject GetPeopleJob getPeopleJob;
    @Inject SQLiteOpenHelper openHelper;
    @Inject SessionManager sessionManager;
    private ObjectGraph objectGraph;
    private TestGolesApplication testGolesApplication;
    private UserManager userManager;
    private SQLiteDatabase database;

    @Before
    public void setup() throws SQLException {
        testGolesApplication = (TestGolesApplication) Robolectric.application;
        objectGraph = testGolesApplication.getObjectGraph();
        testGolesApplication.inject(this);

        database = openHelper.getWritableDatabase();


        userManager = objectGraph.get(UserManager.class);
        userManager.setDataBase(database);


        User currentUser = new User();
        currentUser.setIdUser(CURRENT_USER);
        currentUser.setName("Rafa");
        currentUser.setSessionToken("9283569275");
        currentUser.setEmail("saluydhgasfda");
        currentUser.setFavoriteTeamId(1L);
        currentUser.setPhoto("iuhadsfiu");
        currentUser.setPoints(2L);
        currentUser.setNumFollowers(3L);
        currentUser.setNumFollowings(5L);
        currentUser.setCsys_birth(new Date());
        currentUser.setCsys_modified(new Date());
        currentUser.setCsys_revision(0);

        userManager.saveUser(currentUser);
        //TODO currenUser desde el SessionManager, no Application
        sessionManager.setCurrentUser(currentUser);
    }

    @Test
    public void whenDatabaseEmptyPostResponseOnce() throws Throwable {

        Bus bus = mock(Bus.class);
        BagdadService service = mock(BagdadService.class);
        when(service.getFollowing(anyLong(), anyLong())).thenReturn(new ArrayList<User>());

        getPeopleJob.setBus(bus);
        getPeopleJob.setService(service);
        getPeopleJob.init(CURRENT_USER);
        getPeopleJob.onRun();
        verify(bus, atMost(1)).post(any(FollowsResultEvent.class));
    }

    @Test
    //TODO after refactoring jobs, check that the response came exactly from database
    public void postResponseTwiceWhenDatabasePopulated() throws Throwable {

        User followedUser = new User();
        followedUser.setIdUser(FOLLOWED_USER);
        followedUser.setName("Inma");
        followedUser.setFavoriteTeamId(1L);
        followedUser.setPhoto("iuhadsfiu");
        followedUser.setPoints(2L);
        followedUser.setNumFollowers(3L);
        followedUser.setNumFollowings(5L);
        followedUser.setCsys_birth(new Date());
        followedUser.setCsys_modified(new Date());
        followedUser.setCsys_revision(0);

        userManager.saveUser(followedUser);


        Follow followRelationship = new Follow();
        followRelationship.setIdUser(CURRENT_USER);
        followRelationship.setFollowedUser(FOLLOWED_USER);
        followRelationship.setCsys_birth(new Date());
        followRelationship.setCsys_modified(new Date());
        followRelationship.setCsys_revision(0);


        FollowManager followManager = objectGraph.get(FollowManager.class);
        followManager.setDataBase(database);
        followManager.saveFollowFromServer(followRelationship);

        Bus bus = mock(Bus.class);
        BagdadService service = mock(BagdadService.class);
        when(service.getFollowing(anyLong(), anyLong())).thenReturn(new ArrayList<User>());


        getPeopleJob.setBus(bus);
        getPeopleJob.setService(service);
        getPeopleJob.init(CURRENT_USER);

        getPeopleJob.onRun();

        //TODO check a flag saying that the results came from db instead (refactor needed)
        verify(bus, times(2)).post(any(FollowsResultEvent.class));
    }



}