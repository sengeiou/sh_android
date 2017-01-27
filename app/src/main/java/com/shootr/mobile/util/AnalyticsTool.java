package com.shootr.mobile.util;

import android.content.Context;
import com.shootr.mobile.domain.model.user.User;

public interface AnalyticsTool {

  void init(Context context);

  void setUser(User user);

  void analyticsStart(Context context, String name);

  void analyticsSendAction(Builder builder);

  class Builder {

    Context context;
    String action;
    String actionId;
    String labelId;
    String source;
    String idTargetUser;
    String targetUsername;
    String notificationName;
    String pushRedirection;
    String streamName;
    String idStream;
    String idPoll;
    User user;

    public Context getContext() {
      return context;
    }

    public void setContext(Context context) {
      this.context = context;
    }

    public String getAction() {
      return action;
    }

    public void setAction(String action) {
      this.action = action;
    }

    public String getActionId() {
      return actionId;
    }

    public void setActionId(String actionId) {
      this.actionId = actionId;
    }

    public String getLabelId() {
      return labelId;
    }

    public void setLabelId(String labelId) {
      this.labelId = labelId;
    }

    public String getSource() {
      return source;
    }

    public void setSource(String source) {
      this.source = source;
    }

    public String getIdTargetUser() {
      return idTargetUser;
    }

    public void setIdTargetUser(String idTargetUser) {
      this.idTargetUser = idTargetUser;
    }

    public User getUser() {
      return user;
    }

    public void setUser(User user) {
      this.user = user;
    }

    public String getNotificationName() {
      return notificationName;
    }

    public void setNotificationName(String notificationName) {
      this.notificationName = notificationName;
    }

    public String getPushRedirection() {
      return pushRedirection;
    }

    public void setPushRedirection(String pushRedirection) {
      this.pushRedirection = pushRedirection;
    }

    public String getTargetUsername() {
      return targetUsername;
    }

    public void setTargetUsername(String targetUsername) {
      this.targetUsername = targetUsername;
    }

    public String getStreamName() {
      return streamName;
    }

    public void setStreamName(String streamName) {
      this.streamName = streamName;
    }

    public String getIdStream() {
      return idStream;
    }

    public void setIdStream(String idStream) {
      this.idStream = idStream;
    }

    public String getIdPoll() {
      return idPoll;
    }

    public void setIdPoll(String idPoll) {
      this.idPoll = idPoll;
    }
  }
}
