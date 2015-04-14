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
        dest.writeLong(shot.getIdShot() != null ? shot.getIdShot() : 0L);
        dest.writeString(shot.getComment());
        dest.writeString(shot.getImage());
        dest.writeLong(shot.getPublishDate() != null ? shot.getPublishDate().getTime() : 0L);
        dest.writeLong(shot.getIdQueue() != null ? shot.getIdQueue() : 0L);

        Shot.ShotUserInfo userInfo = shot.getUserInfo();
        dest.writeLong(userInfo != null ? userInfo.getIdUser() : 0L);
        dest.writeString(userInfo != null ? userInfo.getUsername() : null);
        dest.writeString(userInfo != null ? userInfo.getAvatar() : null);

        Shot.ShotEventInfo eventInfo = shot.getEventInfo();
        dest.writeLong(eventInfo != null ? eventInfo.getIdEvent() : 0L);
        dest.writeString(eventInfo != null ? eventInfo.getEventTitle() : null);
        dest.writeString(eventInfo != null ? eventInfo.getEventTag() : null);

        dest.writeLong(shot.getParentShotId()!=null ? shot.getParentShotId() : 0L);
        dest.writeLong(shot.getParentShotUserId()!=null ? shot.getParentShotUserId() : 0L);
        dest.writeString(shot.getParentShotUsername() != null ? shot.getParentShotUsername() : "");
    }

    public void readFromParcel(Parcel parcel) {
        long idShot = parcel.readLong();
        shot.setIdShot(idShot > 0 ? idShot : null);
        shot.setComment(parcel.readString());
        shot.setImage(parcel.readString());
        shot.setPublishDate(new Date(parcel.readLong()));
        long idQueued = parcel.readLong();
        shot.setIdQueue(idQueued > 0 ? idQueued : null);

        Shot.ShotUserInfo userInfo = new Shot.ShotUserInfo();
        userInfo.setIdUser(parcel.readLong());
        userInfo.setUsername(parcel.readString());
        userInfo.setAvatar(parcel.readString());
        if (userInfo.getIdUser() != null && userInfo.getIdUser() > 0L) {
            shot.setUserInfo(userInfo);
        }

        Shot.ShotEventInfo eventInfo = new Shot.ShotEventInfo();
        eventInfo.setIdEvent(parcel.readLong());
        eventInfo.setEventTitle(parcel.readString());
        eventInfo.setEventTag(parcel.readString());
        if (eventInfo.getIdEvent() != null && eventInfo.getIdEvent() > 0L) {
            shot.setEventInfo(eventInfo);
        }

        long idShotParent = parcel.readLong();
        if (idShotParent > 0) {
            shot.setParentShotId(idShotParent);
        }
        long idUserParent = parcel.readLong();
        if (idUserParent > 0) {
            shot.setParentShotUserId(idUserParent);
        }
        String usernameParent = parcel.readString();
        if (!usernameParent.isEmpty()) {
            shot.setParentShotUsername(usernameParent);
        }
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
