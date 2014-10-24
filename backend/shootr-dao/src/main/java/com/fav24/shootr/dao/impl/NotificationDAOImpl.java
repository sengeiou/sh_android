package com.fav24.shootr.dao.impl;

import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;

import com.fav24.shootr.dao.NotificationDAO;
import com.fav24.shootr.dao.domain.Notification;
import com.fav24.shootr.dao.utils.PropertiesManager;

public class NotificationDAOImpl extends BaseDAOImpl implements NotificationDAO {

	@Override
	public List<Notification> getFollowersNotificationsByUserId(Long sourceIdUser) {
		String query = PropertiesManager.getProperty("notification.select.followersByUserId");
		return getJdbcTemplate().query(query, new Object[] { sourceIdUser }, new BeanPropertyRowMapper<Notification>(Notification.class));
	}

	@Override
	public List<Notification> getUserNotification(Long destinationIdUser) {
		String query = PropertiesManager.getProperty("notification.select.byUserId");
		return getJdbcTemplate().query(query, new Object[] { destinationIdUser }, new BeanPropertyRowMapper<Notification>(Notification.class));
	}

}
