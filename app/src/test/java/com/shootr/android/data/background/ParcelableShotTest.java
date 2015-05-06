package com.shootr.android.data.background;

import android.os.Parcel;
import com.shootr.android.domain.Shot;

import java.lang.String;
import java.util.Date;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;

public class ParcelableShotTest {

    //region Stub constants
    private static final Long SHOT_ID_STUB = 1L;
    private static final String COMMENT_STUB = "comment";
    private static final String IMAGE_STUB = "image_url";
    private static final Long USER_ID = 2L;
    private static final String USERNAME = "username";
    private static final String AVATAR = "avatar";
    private static final Long EVENT_ID = 3L;
    private static final String EVENT_TITLE = "title";
    private static final String EVENT_TAG = "tag";
    private static final Long QUEUE_ID = 4L;
    private static final Long PARENT_ID = 5L;
    private static final Long PARENT_USER_ID = 6L;
    private static final String PARENT_USERNAME = "username";
    //endregion

    @Test
    public void testShotFromParcelableHasIdCommentImageAndDate() throws Exception {
        Shot shotStub = shotStub();
        ParcelableShot parcelableShot = new ParcelableShot(shotStub);

        Parcel parcel = MockParcel.obtain();
        parcelableShot.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        ParcelableShot createdFromParcel = ParcelableShot.CREATOR.createFromParcel(parcel);
        Shot shotFromParcel = createdFromParcel.getShot();

        assertThat(shotFromParcel.getIdShot()).isEqualTo(shotStub.getIdShot());
        assertThat(shotFromParcel.getComment()).isEqualTo(shotStub.getComment());
        assertThat(shotFromParcel.getImage()).isEqualTo(shotStub.getImage());
        assertThat(shotFromParcel.getPublishDate()).isEqualTo(shotStub.getPublishDate());
    }

    @Test
    public void testShotWithoutIdIsObtainedFromParcel() throws Exception {
        Shot shotStub = shotStub();
        shotStub.setIdShot(null);
        ParcelableShot parcelableShot = new ParcelableShot(shotStub);

        Parcel parcel = MockParcel.obtain();
        parcelableShot.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        ParcelableShot createdFromParcel = ParcelableShot.CREATOR.createFromParcel(parcel);
        Shot shotFromParcel = createdFromParcel.getShot();
        assertThat(shotFromParcel.getIdShot()).isNull();
    }

    @Test
    public void testShotFromParcelableHasUserInfo() throws Exception {
        Shot shotStub = shotStub();
        Shot.ShotUserInfo userInfoStub = shotStub.getUserInfo();
        ParcelableShot parcelableShot = new ParcelableShot(shotStub);

        Parcel parcel = MockParcel.obtain();
        parcelableShot.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        ParcelableShot createdFromParcel = ParcelableShot.CREATOR.createFromParcel(parcel);
        Shot shotFromParcel = createdFromParcel.getShot();
        Shot.ShotUserInfo userInfoFromParcel = shotFromParcel.getUserInfo();

        assertThat(userInfoFromParcel.getIdUser()).isEqualTo(userInfoStub.getIdUser());
        assertThat(userInfoFromParcel.getUsername()).isEqualTo(userInfoStub.getUsername());
        assertThat(userInfoFromParcel.getAvatar()).isEqualTo(userInfoStub.getAvatar());
    }

    @Test
    public void testShotFromParcelableHasEventInfo() throws Exception {
        Shot shotStub = shotStub();
        Shot.ShotEventInfo eventInfoStub = shotStub.getEventInfo();
        ParcelableShot parcelableShot = new ParcelableShot(shotStub);

        Parcel parcel = MockParcel.obtain();
        parcelableShot.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        ParcelableShot createdFromParcel = ParcelableShot.CREATOR.createFromParcel(parcel);
        Shot shotFromParcel = createdFromParcel.getShot();
        Shot.ShotEventInfo eventInfoFromParcel = shotFromParcel.getEventInfo();

        assertThat(eventInfoFromParcel.getIdEvent()).isEqualTo(eventInfoStub.getIdEvent());
        assertThat(eventInfoFromParcel.getEventTitle()).isEqualTo(eventInfoStub.getEventTitle());
        assertThat(eventInfoFromParcel.getEventTag()).isEqualTo(eventInfoStub.getEventTag());
    }

    @Test
    public void testEventInfoFromParcelableIsNullIfOriginalWasNull() throws Exception {
        Shot shotStub = shotStub();
        shotStub.setEventInfo(null);
        ParcelableShot parcelableShot = new ParcelableShot(shotStub);

        Parcel parcel = MockParcel.obtain();
        parcelableShot.writeToParcel(parcel, 0);

        ParcelableShot createdFromParcel = ParcelableShot.CREATOR.createFromParcel(parcel);
        Shot shotFromParcel = createdFromParcel.getShot();
        Shot.ShotEventInfo eventInfoFromParcel = shotFromParcel.getEventInfo();
        assertThat(eventInfoFromParcel).isNull();
    }

    @Test
    public void testShotFromparcelableHasIdQueueIfOriginalDoes() throws Exception {
        Shot shotStub = shotStub();
        shotStub.setIdQueue(QUEUE_ID);
        ParcelableShot parcelableShot = new ParcelableShot(shotStub);

        Parcel parcel = MockParcel.obtain();
        parcelableShot.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        ParcelableShot createdFromParcel = ParcelableShot.CREATOR.createFromParcel(parcel);
        Shot shotFromParcel = createdFromParcel.getShot();

        assertThat(shotFromParcel.getIdQueue()).isEqualTo(shotStub.getIdQueue());
    }

    @Test public void testShotFromParcelableHasParentInfo() throws Exception {
        Shot shotStub = shotStub();

        shotStub.setParentShotId(String.valueOf(PARENT_ID));
        shotStub.setParentShotUserId(String.valueOf(PARENT_USER_ID));
        shotStub.setParentShotUsername(PARENT_USERNAME);

        ParcelableShot parcelableShot = new ParcelableShot(shotStub);

        Parcel parcel = MockParcel.obtain();
        parcelableShot.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        ParcelableShot createdFromParcel = ParcelableShot.CREATOR.createFromParcel(parcel);
        Shot shotFromParcel = createdFromParcel.getShot();

        assertThat(shotFromParcel.getParentShotId()).isEqualTo(String.valueOf(PARENT_ID));
        assertThat(shotFromParcel.getParentShotUserId()).isEqualTo(String.valueOf(PARENT_USER_ID));
        assertThat(shotFromParcel.getParentShotUsername()).isEqualTo(String.valueOf(PARENT_USERNAME));
    }

    @Test public void testShotFromParcelableKeepsNullParentInfo() throws Exception {
        Shot shotStub = shotStub();

        shotStub.setParentShotId(null);
        shotStub.setParentShotUserId(null);
        shotStub.setParentShotUsername(null);

        ParcelableShot parcelableShot = new ParcelableShot(shotStub);

        Parcel parcel = MockParcel.obtain();
        parcelableShot.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        ParcelableShot createdFromParcel = ParcelableShot.CREATOR.createFromParcel(parcel);
        Shot shotFromParcel = createdFromParcel.getShot();

        assertThat(shotFromParcel.getParentShotId()).isEqualTo(null);
        assertThat(shotFromParcel.getParentShotUserId()).isEqualTo(null);
        assertThat(shotFromParcel.getParentShotUsername()).isEqualTo(null);
    }

    //region Stubs
    private Shot shotStub() {
        Shot shot = new Shot();
        shot.setIdShot(String.valueOf(SHOT_ID_STUB));
        shot.setComment(COMMENT_STUB);
        shot.setImage(IMAGE_STUB);
        shot.setPublishDate(new Date());
        shot.setUserInfo(userInfo());
        shot.setEventInfo(eventInfo());
        return shot;
    }

    private Shot.ShotEventInfo eventInfo() {
        Shot.ShotEventInfo eventInfo = new Shot.ShotEventInfo();
        eventInfo.setIdEvent(String.valueOf(EVENT_ID));
        eventInfo.setEventTitle(EVENT_TITLE);
        eventInfo.setEventTag(EVENT_TAG);
        return eventInfo;
    }

    private Shot.ShotUserInfo userInfo() {
        Shot.ShotUserInfo userInfo = new Shot.ShotUserInfo();
        userInfo.setIdUser(String.valueOf(USER_ID));
        userInfo.setUsername(USERNAME);
        userInfo.setAvatar(AVATAR);
        return userInfo;
    }
    //endregion
}