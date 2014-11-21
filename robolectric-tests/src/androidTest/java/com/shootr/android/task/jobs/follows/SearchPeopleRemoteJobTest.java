package com.shootr.android.task.jobs.follows;

import android.database.sqlite.SQLiteOpenHelper;
import com.shootr.android.RobolectricGradleTestRunner;
import com.shootr.android.data.SessionManager;
import com.shootr.android.db.manager.FollowManager;
import com.shootr.android.db.manager.UserManager;
import com.shootr.android.db.objects.UserEntity;
import com.shootr.android.service.ShootrService;
import com.shootr.android.task.jobs.ShootrBaseJob;
import com.shootr.android.task.jobs.ShootrBaseJobTestAbstract;
import com.shootr.android.ui.model.mappers.UserModelMapper;
import java.io.IOException;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(emulateSdk = 18)
public class SearchPeopleRemoteJobTest extends ShootrBaseJobTestAbstract {

    public static final long CURRENT_USER_ID = 1L;
    private UserModelMapper userVOMapper;
    private SearchPeopleRemoteJob searchPeopleRemoteJob;
    private ShootrService service;
    private SQLiteOpenHelper openHelper;
    private UserManager userManager;
    private FollowManager followManager;
    private SessionManager sessionManager;

    @Before
    public void setUp() throws IOException {
        super.setUp();
        service = mock(ShootrService.class);
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
            userVOMapper, sessionManager);
        searchPeopleRemoteJob.init("", 0);
    }

    @Override protected ShootrBaseJob getSystemUnderTest() {
        return searchPeopleRemoteJob;
    }
}
