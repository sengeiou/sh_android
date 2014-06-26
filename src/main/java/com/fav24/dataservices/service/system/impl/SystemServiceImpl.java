package com.fav24.dataservices.service.system.impl;

import java.util.AbstractList;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fav24.dataservices.exception.ServerException;
import com.fav24.dataservices.monitoring.MonitorSample;
import com.fav24.dataservices.monitoring.SystemMonitoring;
import com.fav24.dataservices.monitoring.meter.CpuMeter;
import com.fav24.dataservices.monitoring.meter.WorkloadMeter;
import com.fav24.dataservices.service.system.SystemService;


/**
 * Implementación de servicio System. 
 */
@Scope("singleton")
@Component
public class SystemServiceImpl implements SystemService {

	private static SystemMonitoring SystemActivityMonitoring;


	/**
	 * Retorna el monitor de actividad del sistema.
	 * 
	 * @return el monitor de actividad del sistema.
	 */
	public SystemMonitoring getSystemActivityMonitoring() {

		if (SystemActivityMonitoring == null) {
			SystemActivityMonitoring = new SystemMonitoring();
		}

		return SystemActivityMonitoring;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MonitorSample getSystemMemoryStatus() {

		return getSystemActivityMonitoring().getSystemMemoryStatus();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AbstractList<MonitorSample> getSystemMemoryStatus(Long offset, Long timeRange, Long period) throws ServerException {

		return getSystemActivityMonitoring().getSystemMemoryStatus(offset, timeRange, period);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CpuMeter getCpuMeter() {
		return getSystemActivityMonitoring().getCpuMeter();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MonitorSample getSystemCpuActivity() {

		return getSystemActivityMonitoring().getSystemCpuActivity();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AbstractList<MonitorSample> getSystemCpuActivity(Long offset, Long timeRange, Long period) throws ServerException {

		return getSystemActivityMonitoring().getSystemCpuActivity(offset, timeRange, period);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public WorkloadMeter getWorkloadMeter() {

		return getSystemActivityMonitoring().getWorkloadMeter();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MonitorSample getSystemWorkload() {

		return getSystemActivityMonitoring().getSystemWorkload();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AbstractList<MonitorSample> getSystemWorkload(Long offset, Long timeRange, Long period) throws ServerException {

		return getSystemActivityMonitoring().getSystemWorkload(offset, timeRange, period);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MonitorSample getSystemStorageStatus(String storeName) {

		return getSystemActivityMonitoring().getSystemStorageStatus(storeName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AbstractList<MonitorSample> getSystemStorageStatus(String storeName, Long offset, Long timeRange, Long period) throws ServerException {

		return getSystemActivityMonitoring().getSystemStorageStatus(storeName, offset, timeRange, period);
	}
}
