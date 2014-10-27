package gm.mobi.android.task.jobs;

import android.content.Context;
import com.path.android.jobqueue.network.NetworkUtil;
import com.squareup.otto.Bus;
import dagger.ObjectGraph;
import gm.mobi.android.task.events.ConnectionNotAvailableEvent;
import java.io.IOException;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.robolectric.annotation.Config;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Config(emulateSdk = 18)
public abstract class BagdadBaseJobTestAbstract {

    protected Bus bus;
    protected NetworkUtil networkUtil;

    public void setUp() throws IOException {
        bus = mock(Bus.class);
        networkUtil = mock(NetworkUtil.class);
    }

    @Test
    public void postConnectionNotAvailableEventWhenConnectionNotAvailable() throws Throwable {
        when(networkUtil.isConnected(any(Context.class))).thenReturn(false);

        BagdadBaseJob systemUnderTest = getSystemUnderTest();

        systemUnderTest.onRun();

        verify(bus, atLeastOnce()).post(argThat(new ConnectionNotAvailableMatcher()));
    }

    protected abstract BagdadBaseJob getSystemUnderTest();

    public static class ConnectionNotAvailableMatcher extends ArgumentMatcher<Object> {

        @Override public boolean matches(Object o) {
            return o instanceof ConnectionNotAvailableEvent;
        }
    }
}
