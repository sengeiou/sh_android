package com.shootr.android.data.background;

import android.os.Parcel;
import android.os.Parcelable;
import com.shootr.android.domain.Shot;
import java.util.Date;

public class ParcelableShot implements Parcelable {

    private Shot shot;

    public ParcelableShot(Shot shot) {
        this.shot = shot;
    }

    public ParcelableShot(Parcel parcel) {
        shot = new Shot();
        readFromParcel(parcel);
    }

    public Shot getShot() {
        return shot;
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        String idShot = shot.getIdShot();
        dest.writeString(idShot != null ? idShot : null);
        dest.writeString(shot.getComment());
        dest.writeString(shot.getImage());
        dest.writeLong(shot.getPublishDate() != null ? shot.getPublishDate().getTime() : 0L);
        dest.writeLong(shot.getIdQueue() != null ? shot.getIdQueue() : 0L);

        Shot.ShotUserInfo userInfo = shot.getUserInfo();
        dest.writeString(userInfo != null ? userInfo.getIdUser() : null);
        dest.writeString(userInfo != null ? userInfo.getUsername() : null);
        dest.writeString(userInfo != null ? userInfo.getAvatar() : null);

        Shot.ShotEventInfo eventInfo = shot.getEventInfo();
        dest.writeString(eventInfo != null ? eventInfo.getIdEvent() : null);
        dest.writeString(eventInfo != null ? eventInfo.getEventTitle() : null);
        dest.writeString(eventInfo != null ? eventInfo.getEventTag() : null);

        String parentShotId = shot.getParentShotId();
        dest.writeString(parentShotId !=null ? parentShotId : null);
        String parentShotUserId = shot.getParentShotUserId();
        dest.writeString(parentShotUserId !=null ? parentShotUserId : null);
        dest.writeString(shot.getParentShotUsername() != null ? shot.getParentShotUsername() : null);
    }

    public void readFromParcel(Parcel parcel) {
        shot.setIdShot(parcel.readString());
        shot.setComment(parcel.readString());
        shot.setImage(parcel.readString());
        shot.setPublishDate(new Date(parcel.readLong()));
        Long idQueued = parcel.readLong();
        shot.setIdQueue(idQueued != 0 ? idQueued : null);

        Shot.ShotUserInfo userInfo = new Shot.ShotUserInfo();
        userInfo.setIdUser(parcel.readString());
        userInfo.setUsername(parcel.readString());
        userInfo.setAvatar(parcel.readString());
        if (userInfo.getIdUser() != null) {
            shot.setUserInfo(userInfo);
        }

        Shot.ShotEventInfo eventInfo = new Shot.ShotEventInfo();
        eventInfo.setIdEvent(parcel.readString());
        eventInfo.setEventTitle(parcel.readString());
        eventInfo.setEventTag(parcel.readString());
        String idEvent = eventInfo.getIdEvent();
        if (idEvent != null) {
            shot.setEventInfo(eventInfo);
        }

        String idShotParent = parcel.readString();
        shot.setParentShotId(idShotParent);
        String idUserParent = parcel.readString();
        shot.setParentShotUserId(idUserParent);
        String usernameParent = parcel.readString();
        shot.setParentShotUsername(usernameParent);
    }

    public static final Creator<ParcelableShot> CREATOR = new Creator<ParcelableShot>() {
        @Override public ParcelableShot createFromParcel(Parcel source) {
            return new ParcelableShot(source);
        }

        @Override public ParcelableShot[] newArray(int size) {
            return new ParcelableShot[size];
        }
    };
}
