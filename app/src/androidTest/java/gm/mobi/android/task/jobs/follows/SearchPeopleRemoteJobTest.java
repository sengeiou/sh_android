package gm.mobi.android.task.jobs.follows;

import android.app.Application;
import com.path.android.jobqueue.network.NetworkUtil;
import com.squareup.otto.Bus;
import gm.mobi.android.task.events.ConnectionNotAvailableEvent;
import java.io.IOException;
import java.sql.SQLException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class SearchPeopleRemoteJobTest {

    @Test
    public void postEventOnConnectionNotAvailable() throws IOException, SQLException {
        //TODO adrián, otro test repetido
    }

}
