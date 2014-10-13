package com.fav24.shootr.batch;

import org.apache.log4j.Logger;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ShooterJobLauncher {
	private static Logger logger = Logger.getLogger("task");

	@Autowired
	private JobLauncher jobLauncher;

	/**
	 * Lanza un job.
	 * @param job
	 * @return
	 */
	public JobResult executeJob(Job job, String enabled) {
		JobResult result = new JobResult();
		JobExecution execution = null;
		long jobId = System.nanoTime();
		Boolean isEnabled = Boolean.parseBoolean(enabled);
		try {
			if(isEnabled){
				logger.info("Launching job " + job.getName() + ". Id: " + jobId);
				execution = jobLauncher.run(job, new JobParametersBuilder().addLong("run.id", jobId).toJobParameters());
			} 
		} catch (Exception e) {
			logger.error("Error while executing job " + job.getName(), e);
		} finally {
			result.setName(job.getName());
			result.setJobId(jobId);
			if(isEnabled){
				if(execution != null){
					if(execution.getStatus() != null){
						result.setStatus(execution.getStatus().toString());
					}
					if(execution.getStartTime() != null && execution.getEndTime() != null){
						result.setDuration(execution.getEndTime().getTime() - execution.getStartTime().getTime());
					}
					if(execution.getExitStatus() != null){
						result.setExitDescription(execution.getExitStatus().getExitDescription());
					}
				}
			} else {
				result.setStatus("BATCH_DISABLED");
				result.setDuration(0l);
			}
		}
		if(isEnabled){
			logger.info("Job finished. Result: " + result);
		}
		return result;
	}
}
