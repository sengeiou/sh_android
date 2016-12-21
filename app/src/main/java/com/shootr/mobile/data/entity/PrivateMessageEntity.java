package com.shootr.mobile.data.entity;

public class PrivateMessageEntity extends BaseMessageEntity {

    private String idPrivateMessage;
    private String idPrivateMessageChannel;
    private String idTargetUser;

    public String getIdPrivateMessage() {
        return idPrivateMessage;
    }

    public void setIdPrivateMessage(String idPrivateMessage) {
        this.idPrivateMessage = idPrivateMessage;
    }

    public String getIdTargetUser() {
        return idTargetUser;
    }

    public void setIdTargetUser(String idTargetUser) {
        this.idTargetUser = idTargetUser;
    }

    public String getIdPrivateMessageChannel() {
        return idPrivateMessageChannel;
    }

    public void setIdPrivateMessageChannel(String idPrivateMessageChannel) {
        this.idPrivateMessageChannel = idPrivateMessageChannel;
    }


}
