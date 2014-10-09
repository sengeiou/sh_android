package gm.mobi.android.task.jobs.profile;

import android.database.sqlite.SQLiteOpenHelper;
import com.squareup.otto.Bus;
import gm.mobi.android.db.manager.FollowManager;
import gm.mobi.android.db.manager.TeamManager;
import gm.mobi.android.db.manager.UserManager;
import gm.mobi.android.db.mappers.TeamMapper;
import gm.mobi.android.db.objects.Team;
import gm.mobi.android.db.objects.User;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class GetUserInfoJobTest {

    private Bus bus;
    private SQLiteOpenHelper dbHelper;

    @org.junit.Before
    public void setup() {
        dbHelper = mock(SQLiteOpenHelper.class);

        bus = mock(Bus.class);

    }

    @Test
    public void logWhenUserIsNotFoundInDataBase() {
        UserManager userManager = mock(UserManager.class);
        when(userManager.getUserByIdUser(anyLong())).thenReturn(null);

        assertTrue(userManager != null);

        Tree mockTree = mock(Tree.class);
        Timber.plant(mockTree);

        GetUserInfoJob getUserInfoJob = new GetUserInfoJob(Robolectric.application,null,null,null, null,userManager,null,null);

        assertTrue(getUserInfoJob.userManager != null);

        getUserInfoJob.init(1L,null);

        assertTrue(getUserInfoJob.userManager != null);

        getUserInfoJob.retrieveDataFromDatabase();

        verify(mockTree).i(anyString(),anyObject());
    }


    @Test
    public void postResultInBusWhenUserIsFoundInDataBase(){
        UserManager userManager = mock(UserManager.class);

        when(userManager.getUserByIdUser(anyLong())).thenReturn(new User());

        FollowManager followManager = mock(FollowManager.class);

        TeamManager teamManager = mock(TeamManager.class);



        GetUserInfoJob getUserInfoJob = new GetUserInfoJob(Robolectric.application,bus,dbHelper,null,null, userManager,followManager,teamManager);
        getUserInfoJob.init(1L,null);
        getUserInfoJob.retrieveDataFromDatabase();
        verify(bus).post(anyObject());
    }

}
