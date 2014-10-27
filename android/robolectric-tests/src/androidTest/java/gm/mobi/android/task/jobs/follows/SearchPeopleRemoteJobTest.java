package gm.mobi.android.task.jobs.follows;

import android.database.sqlite.SQLiteOpenHelper;
import gm.mobi.android.RobolectricGradleTestRunner;
import gm.mobi.android.data.SessionManager;
import gm.mobi.android.db.manager.FollowManager;
import gm.mobi.android.db.manager.UserManager;
import gm.mobi.android.db.objects.UserEntity;
import gm.mobi.android.service.BagdadService;
import gm.mobi.android.task.jobs.BagdadBaseJob;
import gm.mobi.android.task.jobs.BagdadBaseJobTestAbstract;
import gm.mobi.android.ui.model.mappers.UserModelMapper;
import java.io.IOException;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(emulateSdk = 18)
public class SearchPeopleRemoteJobTest extends BagdadBaseJobTestAbstract {

    public static final long CURRENT_USER_ID = 1L;
    private UserModelMapper userVOMapper;
    private SearchPeopleRemoteJob searchPeopleRemoteJob;
    private BagdadService service;
    private SQLiteOpenHelper openHelper;
    private UserManager userManager;
    private FollowManager followManager;
    private SessionManager sessionManager;

    @Before
    public void setUp() throws IOException {
        super.setUp();
        service = mock(BagdadService.class);
        openHelper = mock(SQLiteOpenHelper.class);
        userManager = mock(UserManager.class);
        followManager = mock(FollowManager.class);
        sessionManager = mock(SessionManager.class);
        userVOMapper = mock(UserModelMapper.class);

        UserEntity currentUser = new UserEntity();
        currentUser.setIdUser(CURRENT_USER_ID);
        when(sessionManager.getCurrentUser()).thenReturn(currentUser);

        searchPeopleRemoteJob =
          new SearchPeopleRemoteJob(Robolectric.application, bus, service, networkUtil, followManager,
            userVOMapper, openHelper, sessionManager);
        searchPeopleRemoteJob.init("", 0);
    }

    @Override protected BagdadBaseJob getSystemUnderTest() {
        return searchPeopleRemoteJob;
    }
}
