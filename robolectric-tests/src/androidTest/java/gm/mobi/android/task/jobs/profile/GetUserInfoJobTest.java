package gm.mobi.android.task.jobs.profile;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import com.path.android.jobqueue.network.NetworkUtil;
import com.squareup.otto.Bus;
import gm.mobi.android.RobolectricGradleTestRunner;
import gm.mobi.android.db.manager.FollowManager;
import gm.mobi.android.db.manager.TeamManager;
import gm.mobi.android.db.manager.UserManager;
import gm.mobi.android.db.objects.Follow;
import gm.mobi.android.db.objects.User;
import gm.mobi.android.service.BagdadService;
import gm.mobi.android.task.jobs.BagdadBaseJob;
import gm.mobi.android.task.jobs.BagdadBaseJobTestAbstract;
import gm.mobi.android.ui.model.mappers.UserVOMapper;
import java.io.IOException;
import java.sql.SQLException;
import org.junit.Before;
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
@RunWith(RobolectricGradleTestRunner.class)
public class GetUserInfoJobTest extends BagdadBaseJobTestAbstract {

    private static final Long USER_ID = 1L;
    private static final Long CURRENT_USER_ID = 2L;
    private BagdadService service;
    private SQLiteOpenHelper openHelper;
    private FollowManager followManager;
    private UserManager userManager;
    private UserVOMapper userVOMapper;
    private TeamManager teamManager;
    private GetUserInfoJob getUserInfoJob;

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();
        service = mock(BagdadService.class);
        openHelper = mock(SQLiteOpenHelper.class);
        userManager = mock(UserManager.class);
        followManager = mock(FollowManager.class);
        teamManager = mock(TeamManager.class);

        userVOMapper = mock(UserVOMapper.class);

        when(service.getUserByIdUser(USER_ID)).thenReturn(new User());
        when(service.getFollowByIdUserFollowed(CURRENT_USER_ID, USER_ID)).thenReturn(new Follow());

        getUserInfoJob =
          new GetUserInfoJob(Robolectric.application,bus,openHelper,service, networkUtil,userManager,followManager,teamManager, userVOMapper);
        User currentUser = new User();
        currentUser.setIdUser(CURRENT_USER_ID);
        getUserInfoJob.init(USER_ID, currentUser);
    }

    @Test
    public void logWhenUserIsNotFoundInDataBase() throws IOException, SQLException {
        when(userManager.getUserByIdUser(anyLong())).thenReturn(null);

        Tree mockTree = mock(Tree.class);
        Timber.plant(mockTree);

        when(networkUtil.isConnected(any(Context.class))).thenReturn(true);

        getUserInfoJob.run();

        verify(mockTree).d(anyString(),anyObject());
    }


    @Test
    public void postResultInBusWhenUserIsFoundInDataBase() throws Throwable {
        when(userManager.getUserByIdUser(anyLong())).thenReturn(new User());

        when(networkUtil.isConnected(any(Context.class))).thenReturn(true);

        when(service.getUserByIdUser(USER_ID)).thenReturn(new User());

        getUserInfoJob.onRun();

        //TODO comprobar tipo de objeto posteado
        verify(bus, atLeastOnce()).post(anyObject());
    }

    @Override protected BagdadBaseJob getSystemUnderTest() {
        return getUserInfoJob;
    }
}
