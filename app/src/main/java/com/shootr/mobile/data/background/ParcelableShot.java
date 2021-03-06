package com.shootr.mobile.data.background;

import android.os.Parcel;
import android.os.Parcelable;
import com.shootr.mobile.domain.model.shot.BaseMessage;
import com.shootr.mobile.domain.model.shot.Shot;
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
        dest.writeString(shot.getIdShot());
        dest.writeString(shot.getComment());
        dest.writeString(shot.getImage());
        dest.writeLong(shot.getImageWidth() != null ? shot.getImageWidth() : 0L);
        dest.writeLong(shot.getImageHeight() != null ? shot.getImageHeight() : 0L);
        dest.writeLong(shot.getPublishDate() != null ? shot.getPublishDate().getTime() : 0L);
        dest.writeLong(shot.getIdQueue() != null ? shot.getIdQueue() : 0L);

        BaseMessage.BaseMessageUserInfo userInfo = shot.getUserInfo();
        dest.writeString(userInfo != null ? userInfo.getIdUser() : null);
        dest.writeString(userInfo != null ? userInfo.getUsername() : null);
        dest.writeString(userInfo != null ? userInfo.getAvatar() : null);

        Shot.ShotStreamInfo eventInfo = shot.getStreamInfo();
        dest.writeString(eventInfo != null ? eventInfo.getIdStream() : null);
        dest.writeString(eventInfo != null ? eventInfo.getStreamTitle() : null);

        dest.writeString(shot.getParentShotId());
        dest.writeString(shot.getParentShotUserId());
        dest.writeString(shot.getParentShotUsername());

        dest.writeString(shot.getVideoUrl());
        dest.writeString(shot.getVideoTitle());
        Long videoDuration = shot.getVideoDuration();
        dest.writeLong(videoDuration != null ? videoDuration : 0);

        dest.writeString(shot.getType());

        Shot.PromotedShotParams promotedShotParams = shot.getPromotedShotParams();
        dest.writeString(promotedShotParams != null ? promotedShotParams.getType() : null);
        dest.writeString(promotedShotParams != null ? promotedShotParams.getData() : null);
    }

    public void readFromParcel(Parcel parcel) {
        shot.setIdShot(parcel.readString());
        shot.setComment(parcel.readString());
        shot.setImage(parcel.readString());
        shot.setImageHeight(parcel.readLong());
        shot.setImageWidth(parcel.readLong());
        shot.setPublishDate(new Date(parcel.readLong()));
        Long idQueued = parcel.readLong();
        shot.setIdQueue(idQueued != 0 ? idQueued : null);

        BaseMessage.BaseMessageUserInfo userInfo = new BaseMessage.BaseMessageUserInfo();
        userInfo.setIdUser(parcel.readString());
        userInfo.setUsername(parcel.readString());
        userInfo.setAvatar(parcel.readString());
        if (userInfo.getIdUser() != null) {
            shot.setUserInfo(userInfo);
        }

        Shot.ShotStreamInfo eventInfo = new Shot.ShotStreamInfo();
        eventInfo.setIdStream(parcel.readString());
        eventInfo.setStreamTitle(parcel.readString());
        String idEvent = eventInfo.getIdStream();
        if (idEvent != null) {
            shot.setStreamInfo(eventInfo);
        }

        shot.setParentShotId(parcel.readString());
        shot.setParentShotUserId(parcel.readString());
        shot.setParentShotUsername(parcel.readString());

        shot.setVideoUrl(parcel.readString());
        shot.setVideoTitle(parcel.readString());
        shot.setVideoDuration(parcel.readLong());

        shot.setType(parcel.readString());

        Shot.PromotedShotParams promotedShotParams = new Shot.PromotedShotParams();
        promotedShotParams.setType(parcel.readString());
        promotedShotParams.setData(parcel.readString());
        String receipt = promotedShotParams.getData();
        if (receipt != null) {
            shot.setPromotedShotParams(promotedShotParams);
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
