package gm.mobi.android.gcm;

import android.app.Application;
import android.app.Notification;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import gm.mobi.android.RobolectricGradleTestRunner;
import gm.mobi.android.gcm.notifications.ShootrNotificationManager;
import gm.mobi.android.gcm.notifications.NotificationBuilderFactory;
import gm.mobi.android.ui.model.ShotModel;
import java.io.IOException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(emulateSdk = 18)
public class ShootrNotificationManagerTest {

    private static final Long USER_1_ID = 1L;
    private static final String USER_1_NAME = "Username";
    private static final Long SHOT_1_ID = 1L;
    private static final String COMMENT = "Comment";

    ShootrNotificationManager notificationManager;
    Application application;
    Picasso picasso;
    private NotificationManagerCompat androidNotificationManager;
    private NotificationBuilderFactory builderFactory;
    private NotificationCompat.Builder builder;

    @Before
    public void setUp() throws IOException {
        application = Robolectric.application;
        androidNotificationManager = mock(NotificationManagerCompat.class);
        builder = mock(NotificationCompat.Builder.class);
        builderFactory = mock(NotificationBuilderFactory.class);
        when(builderFactory.getNotificationBuilder(any(Context.class))).thenReturn(builder);
        picasso = mock(Picasso.class);
        RequestCreator requestCreator = mock(RequestCreator.class);
        when(picasso.load(anyString())).thenReturn(requestCreator);
        when(requestCreator.get()).thenReturn(null);

        notificationManager = new ShootrNotificationManager(application, androidNotificationManager, builderFactory, picasso);
    }

    @Test
    public void singleShotNotificationFiredWhenOneShot() {
        when(builderFactory.getNotificationBuilder(any(Context.class))).thenReturn(builder);

        ShotModel testShot = getTestShot1();
        notificationManager.sendNewShotNotification(testShot);

        ArgumentCaptor<Notification> argumentCaptor = ArgumentCaptor.forClass(Notification.class);
        verify(androidNotificationManager).notify(eq(ShootrNotificationManager.NOTIFICATION_SHOT),
          argumentCaptor.capture());

        verify(builder).setContentText(testShot.getComment());
        verify(builder).setContentTitle(testShot.getUsername());
    }

    private ShotModel getTestShot1() {
        ShotModel testShot = new ShotModel();
        testShot.setIdShot(SHOT_1_ID);
        testShot.setIdUser(USER_1_ID);
        testShot.setComment(COMMENT);

        testShot.setIdUser(USER_1_ID);
        testShot.setUsername(USER_1_NAME);
        testShot.setPhoto("");
        return testShot;
    }

}
