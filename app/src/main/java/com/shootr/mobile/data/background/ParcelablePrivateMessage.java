package com.shootr.mobile.data.background;

import android.os.Parcel;
import android.os.Parcelable;
import com.shootr.mobile.domain.model.privateMessage.PrivateMessage;
import com.shootr.mobile.domain.model.shot.BaseMessage;
import java.util.Date;

public class ParcelablePrivateMessage implements Parcelable {

  private PrivateMessage privateMessage;

  public ParcelablePrivateMessage(PrivateMessage privateMessage) {
    this.privateMessage = privateMessage;
  }

  public ParcelablePrivateMessage(Parcel parcel) {
    privateMessage = new PrivateMessage();
    readFromParcel(parcel);
  }

  public PrivateMessage getPrivateMessage() {
    return privateMessage;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(privateMessage.getComment());
    dest.writeString(privateMessage.getImage());
    dest.writeLong(privateMessage.getImageWidth() != null ? privateMessage.getImageWidth() : 0L);
    dest.writeLong(privateMessage.getImageHeight() != null ? privateMessage.getImageHeight() : 0L);
    dest.writeLong(
        privateMessage.getPublishDate() != null ? privateMessage.getPublishDate().getTime() : 0L);
    dest.writeLong(privateMessage.getIdQueue() != null ? privateMessage.getIdQueue() : 0L);

    BaseMessage.BaseMessageUserInfo userInfo = privateMessage.getUserInfo();
    dest.writeString(userInfo != null ? userInfo.getIdUser() : null);
    dest.writeString(userInfo != null ? userInfo.getUsername() : null);
    dest.writeString(userInfo != null ? userInfo.getAvatar() : null);

    dest.writeString(privateMessage.getVideoUrl());
    dest.writeString(privateMessage.getVideoTitle());
    Long videoDuration = privateMessage.getVideoDuration();
    dest.writeLong(videoDuration != null ? videoDuration : 0);
    dest.writeString(privateMessage.getIdTargetUser());
  }

  public void readFromParcel(Parcel parcel) {
    privateMessage.setComment(parcel.readString());
    privateMessage.setImage(parcel.readString());
    privateMessage.setImageHeight(parcel.readLong());
    privateMessage.setImageWidth(parcel.readLong());
    privateMessage.setPublishDate(new Date(parcel.readLong()));
    Long idQueued = parcel.readLong();
    privateMessage.setIdQueue(idQueued != 0 ? idQueued : null);

    BaseMessage.BaseMessageUserInfo userInfo = new BaseMessage.BaseMessageUserInfo();
    userInfo.setIdUser(parcel.readString());
    userInfo.setUsername(parcel.readString());
    userInfo.setAvatar(parcel.readString());
    if (userInfo.getIdUser() != null) {
      privateMessage.setUserInfo(userInfo);
    }
    privateMessage.setVideoUrl(parcel.readString());
    privateMessage.setVideoTitle(parcel.readString());
    privateMessage.setVideoDuration(parcel.readLong());
    privateMessage.setIdTargetUser(parcel.readString());
  }

  public static final Creator<ParcelablePrivateMessage> CREATOR = new Creator<ParcelablePrivateMessage>() {
    @Override public ParcelablePrivateMessage createFromParcel(Parcel source) {
      return new ParcelablePrivateMessage(source);
    }

    @Override public ParcelablePrivateMessage[] newArray(int size) {
      return new ParcelablePrivateMessage[size];
    }
  };
}
