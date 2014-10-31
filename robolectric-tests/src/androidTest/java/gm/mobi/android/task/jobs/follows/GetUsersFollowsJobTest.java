package gm.mobi.android.task.jobs.follows;

import android.database.sqlite.SQLiteOpenHelper;
import gm.mobi.android.RobolectricGradleTestRunner;
import gm.mobi.android.db.manager.FollowManager;
import gm.mobi.android.db.manager.UserManager;
import gm.mobi.android.service.BagdadService;
import gm.mobi.android.service.dataservice.dto.UserDtoFactory;
import gm.mobi.android.task.jobs.BagdadBaseJob;
import gm.mobi.android.task.jobs.BagdadBaseJobTestAbstract;
import gm.mobi.android.ui.model.mappers.UserModelMapper;
import java.io.IOException;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;

import static org.mockito.Mockito.mock;

@Config(emulateSdk = 18) @RunWith(RobolectricGradleTestRunner.class)
public class GetUsersFollowsJobTest extends BagdadBaseJobTestAbstract {

    public static final long USER_ID = 1L;

    private UserModelMapper userVOMapper;
    private GetUsersFollowsJob getUsersFollowsJob;

    private BagdadService service;
    private SQLiteOpenHelper openHelper;
    private UserManager userManager;
    private FollowManager followManager;


    @Before
    public void setUp() throws IOException {
        super.setUp();
        service = mock(BagdadService.class);
        openHelper = mock(SQLiteOpenHelper.class);
        userManager = mock(UserManager.class);
        followManager = mock(FollowManager.class);

        userVOMapper = mock(UserModelMapper.class);
        getUsersFollowsJob =
          new GetUsersFollowsJob(Robolectric.application, bus, openHelper,  service, networkUtil, followManager,
            userVOMapper);
        getUsersFollowsJob.init(USER_ID, UserDtoFactory.GET_FOLLOWING); //TODO test both relationships?
    }

    @Override protected BagdadBaseJob getSystemUnderTest() {
        return getUsersFollowsJob;
    }
}
