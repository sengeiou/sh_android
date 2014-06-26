package com.fav24.dataservices.service.system.impl;

import java.util.AbstractList;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fav24.dataservices.exception.ServerException;
import com.fav24.dataservices.monitoring.MonitorSample;
import com.fav24.dataservices.monitoring.SamplesRegister;
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
	 * {@inheritDoc}
	 */
	public void initSystemService() throws ServerException {

		SamplesRegister.initSamplesRegister();
		
		SystemActivityMonitoring = new SystemMonitoring();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MonitorSample getSystemMemoryStatus() {

		return SystemActivityMonitoring.getSystemMemoryStatus();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AbstractList<MonitorSample> getSystemMemoryStatus(Long offset, Long timeRange, Long period) throws ServerException {

		return SystemActivityMonitoring.getSystemMemoryStatus(offset, timeRange, period);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CpuMeter getCpuMeter() {
		return SystemActivityMonitoring.getCpuMeter();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MonitorSample getSystemCpuActivity() {

		return SystemActivityMonitoring.getSystemCpuActivity();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AbstractList<MonitorSample> getSystemCpuActivity(Long offset, Long timeRange, Long period) throws ServerException {

		return SystemActivityMonitoring.getSystemCpuActivity(offset, timeRange, period);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public WorkloadMeter getWorkloadMeter() {

		return SystemActivityMonitoring.getWorkloadMeter();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MonitorSample getSystemWorkload() {

		return SystemActivityMonitoring.getSystemWorkload();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AbstractList<MonitorSample> getSystemWorkload(Long offset, Long timeRange, Long period) throws ServerException {

		return SystemActivityMonitoring.getSystemWorkload(offset, timeRange, period);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MonitorSample getSystemStorageStatus(String storeName) {

		return SystemActivityMonitoring.getSystemStorageStatus(storeName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AbstractList<MonitorSample> getSystemStorageStatus(String storeName, Long offset, Long timeRange, Long period) throws ServerException {

		return SystemActivityMonitoring.getSystemStorageStatus(storeName, offset, timeRange, period);
	}
}
