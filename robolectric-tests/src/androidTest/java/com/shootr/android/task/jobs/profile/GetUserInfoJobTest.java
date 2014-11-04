package com.shootr.android.task.jobs.profile;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import com.shootr.android.RobolectricGradleTestRunner;
import com.shootr.android.db.manager.FollowManager;
import com.shootr.android.db.manager.UserManager;
import com.shootr.android.db.objects.FollowEntity;
import com.shootr.android.db.objects.UserEntity;
import com.shootr.android.service.ShootrService;
import com.shootr.android.task.jobs.ShootrBaseJob;
import com.shootr.android.task.jobs.ShootrBaseJobTestAbstract;
import com.shootr.android.ui.model.mappers.UserModelMapper;
import java.io.IOException;
import java.sql.SQLException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;
import timber.log.Timber;
import timber.log.Timber.Tree;

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
public class GetUserInfoJobTest extends ShootrBaseJobTestAbstract {

    private static final Long USER_ID = 1L;
    private static final Long CURRENT_USER_ID = 2L;
    private ShootrService service;
    private SQLiteOpenHelper openHelper;
    private FollowManager followManager;
    private UserManager userManager;
    private UserModelMapper userVOMapper;
    private GetUserInfoJob getUserInfoJob;

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();
        service = mock(ShootrService.class);
        openHelper = mock(SQLiteOpenHelper.class);
        userManager = mock(UserManager.class);
        followManager = mock(FollowManager.class);

        userVOMapper = mock(UserModelMapper.class);

        when(service.getUserByIdUser(USER_ID)).thenReturn(new UserEntity());
        when(service.getFollowByIdUserFollowed(CURRENT_USER_ID, USER_ID)).thenReturn(new FollowEntity());

        getUserInfoJob =
          new GetUserInfoJob(Robolectric.application,bus,openHelper,service, networkUtil,userManager,followManager, userVOMapper);
        UserEntity currentUser = new UserEntity();
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
        when(userManager.getUserByIdUser(anyLong())).thenReturn(new UserEntity());

        when(networkUtil.isConnected(any(Context.class))).thenReturn(true);

        when(service.getUserByIdUser(USER_ID)).thenReturn(new UserEntity());

        getUserInfoJob.onRun();

        //TODO comprobar tipo de objeto posteado
        verify(bus, atLeastOnce()).post(anyObject());
    }

    @Override protected ShootrBaseJob getSystemUnderTest() {
        return getUserInfoJob;
    }
}
