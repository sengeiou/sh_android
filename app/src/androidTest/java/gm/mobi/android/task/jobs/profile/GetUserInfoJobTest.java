package gm.mobi.android.task.jobs.profile;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import com.path.android.jobqueue.network.NetworkUtil;
import com.squareup.otto.Bus;
import gm.mobi.android.db.manager.FollowManager;
import gm.mobi.android.db.manager.TeamManager;
import gm.mobi.android.db.manager.UserManager;
import gm.mobi.android.db.objects.User;
import gm.mobi.android.service.BagdadService;
import java.io.IOException;
import java.sql.SQLException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import timber.log.Timber;
import timber.log.Timber.Tree;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class GetUserInfoJobTest {

    private static final Long USER_ID = 1L;
    private Bus bus;
    private SQLiteOpenHelper dbHelper;

    @org.junit.Before
    public void setup() {
        dbHelper = mock(SQLiteOpenHelper.class);

        bus = mock(Bus.class);
    }

    @Test
    public void logWhenUserIsNotFoundInDataBase() throws IOException, SQLException {
        UserManager userManager = mock(UserManager.class);
        when(userManager.getUserByIdUser(anyLong())).thenReturn(null);

        assertTrue(userManager != null);

        Tree mockTree = mock(Tree.class);
        Timber.plant(mockTree);

        NetworkUtil networkUtil = mock(NetworkUtil.class);
        when(networkUtil.isConnected(any(Context.class))).thenReturn(true);

        BagdadService service = mock(BagdadService.class);
        when(service.getUserByIdUser(USER_ID)).thenReturn(new User());

        GetUserInfoJob getUserInfoJob = new GetUserInfoJob(Robolectric.application,bus,dbHelper,service, networkUtil,userManager,null,null);

        assertTrue(getUserInfoJob.userManager != null);

        getUserInfoJob.init(1L,null);

        assertTrue(getUserInfoJob.userManager != null);

        getUserInfoJob.run();

        verify(mockTree).d(anyString(),anyObject());
    }


    @Test
    public void postResultInBusWhenUserIsFoundInDataBase() throws Throwable {
        UserManager userManager = mock(UserManager.class);
        when(userManager.getUserByIdUser(anyLong())).thenReturn(new User());

        FollowManager followManager = mock(FollowManager.class);

        TeamManager teamManager = mock(TeamManager.class);

        NetworkUtil networkUtil = mock(NetworkUtil.class);
        when(networkUtil.isConnected(any(Context.class))).thenReturn(true);

        BagdadService service = mock(BagdadService.class);
        when(service.getUserByIdUser(USER_ID)).thenReturn(new User());

        GetUserInfoJob getUserInfoJob =
          new GetUserInfoJob(Robolectric.application, bus, dbHelper, service, networkUtil, userManager, followManager,
            teamManager);
        getUserInfoJob.init(1L,null);
        getUserInfoJob.onRun();
        //TODO comprobar tipo de objeto posteado
        verify(bus, atLeastOnce()).post(anyObject());
    }

}
