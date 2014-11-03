package gm.mobi.android.task.jobs.follows;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import gm.mobi.android.RobolectricGradleTestRunner;
import gm.mobi.android.db.manager.FollowManager;
import gm.mobi.android.db.manager.UserManager;
import gm.mobi.android.exception.ServerException;
import gm.mobi.android.service.ShootrService;
import gm.mobi.android.task.events.CommunicationErrorEvent;
import gm.mobi.android.task.jobs.ShootrBaseJob;
import gm.mobi.android.task.jobs.ShootrBaseJobTestAbstract;
import gm.mobi.android.ui.model.mappers.UserModelMapper;
import java.io.IOException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Config(emulateSdk = 18)
@RunWith(RobolectricGradleTestRunner.class)
public class GetPeopleJobTest extends ShootrBaseJobTestAbstract {

    public static final long CURRENT_USER_ID = 1L;
    private UserModelMapper userVOMapper;
    private GetPeopleJob getPeopleJob;

    private ShootrService service;
    private SQLiteOpenHelper openHelper;
    private UserManager userManager;
    private FollowManager followManager;

    @Before
    public void setUp() throws IOException {
        super.setUp();
        service = mock(ShootrService.class);
        openHelper = mock(SQLiteOpenHelper.class);
        userManager = mock(UserManager.class);
        followManager = mock(FollowManager.class);

        userVOMapper = mock(UserModelMapper.class);
        getPeopleJob =
          new GetPeopleJob(Robolectric.application, bus, service, networkUtil, openHelper, userManager, followManager,
            userVOMapper);
        getPeopleJob.init(CURRENT_USER_ID);
    }

    @Test
    public void postErrorWhenCallToServiceFails() throws Throwable {

        when(service.getFollowing(anyLong(), anyLong())).thenThrow(new ServerException());

        when(networkUtil.isConnected(any(Context.class))).thenReturn(true);

        getPeopleJob.onRun();

        verify(bus).post(any(CommunicationErrorEvent.class));
    }
    @Override protected ShootrBaseJob getSystemUnderTest() {
        return getPeopleJob;
    }
}
