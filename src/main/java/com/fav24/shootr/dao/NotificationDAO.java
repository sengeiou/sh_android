package com.fav24.shootr.dao;

import java.util.List;

import com.fav24.shootr.dao.domain.Notification;

public interface NotificationDAO extends BaseDAO {

	public List<Notification> getFollowersNotificationsByUserId(Long sourceIdUser);

	public List<Notification> getUserNotification(Long destinationIdUser);
}
