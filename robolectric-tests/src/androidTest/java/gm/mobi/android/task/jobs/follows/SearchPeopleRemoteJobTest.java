package gm.mobi.android.task.jobs.follows;

import gm.mobi.android.RobolectricGradleTestRunner;
import java.io.IOException;
import java.sql.SQLException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricGradleTestRunner.class)
@Config(emulateSdk = 18)
public class SearchPeopleRemoteJobTest {

    @Test
    public void postEventOnConnectionNotAvailable() throws IOException, SQLException {
        //TODO adrián, otro test repetido
    }

}
