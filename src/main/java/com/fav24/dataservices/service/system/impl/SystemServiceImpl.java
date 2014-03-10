package com.fav24.dataservices.service.system.impl;

import java.util.AbstractList;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fav24.dataservices.monitoring.SystemMonitoring;
import com.fav24.dataservices.monitoring.SystemMonitoring.MonitorSample;
import com.fav24.dataservices.service.system.SystemService;


/**
 * Implementación de servicio System. 
 */
@Component
@Scope("prototype")
public class SystemServiceImpl implements SystemService {

	private static SystemMonitoring SystemActivityMonitoring;

	static {
		SystemActivityMonitoring = new SystemMonitoring();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AbstractList<MonitorSample> getSystemMemoryStatus(Long period, Long timeRange) {

		return SystemActivityMonitoring.getSystemMemoryStatus(period, timeRange);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AbstractList<MonitorSample> getSystemCpuActivity(Long period, Long timeRange) {

		return SystemActivityMonitoring.getSystemCpuActivity(period, timeRange);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, AbstractList<MonitorSample>> getSystemStorageStatus(Long period, Long timeRange) {

		return SystemActivityMonitoring.getSystemStorageStatus(period, timeRange);
	}

}
