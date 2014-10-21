package gm.mobi.android.gcm;

import android.app.Application;
import android.app.Notification;
import android.support.v4.app.NotificationManagerCompat;
import gm.mobi.android.db.objects.ShotEntity;
import gm.mobi.android.db.objects.UserEntity;
import gm.mobi.android.gcm.notifications.BagdadNotificationManager;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.assertj.android.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
@Ignore
public class BagdadNotificationManagerTest {

    private static final Long USER_1_ID = 1L;
    private static final String USER_1_NAME = "Username";
    private static final Long SHOT_1_ID = 1L;
    private static final String COMMENT = "Comment";

    BagdadNotificationManager notificationManager;
    Application application = Robolectric.application;

    @Test
    public void singleShotNotificationFiredWhenOneShot() {
        NotificationManagerCompat androidNotificationManager = mock(NotificationManagerCompat.class);
        notificationManager = new BagdadNotificationManager(application, androidNotificationManager, null);

        ShotEntity testShot = getTestShot1();
        notificationManager.sendNewShotNotification(testShot);

        ArgumentCaptor<Notification> argumentCaptor = ArgumentCaptor.forClass(Notification.class);
        verify(androidNotificationManager).notify(BagdadNotificationManager.NOTIFICATION_SHOT, argumentCaptor.capture());

        Notification notification = argumentCaptor.getValue();

        //TODO y ahora qué? ...
    }

    private ShotEntity getTestShot1() {
        ShotEntity testShot = new ShotEntity();
        testShot.setIdShot(SHOT_1_ID);
        testShot.setIdUser(USER_1_ID);
        testShot.setComment(COMMENT);
        testShot.setUser(getTestUser1());
        return testShot;
    }

    private UserEntity getTestUser1() {
        UserEntity testUser = new UserEntity();
        testUser.setIdUser(USER_1_ID);
        testUser.setUserName(USER_1_NAME);
        testUser.setPhoto("");
        return testUser;
    }

}
