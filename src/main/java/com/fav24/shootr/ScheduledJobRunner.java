package com.fav24.shootr;

import org.springframework.batch.core.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fav24.shootr.batch.ShooterJobLauncher;

@Service
public class ScheduledJobRunner {

	@Autowired
	private Job getOptaDataJob;

	@Autowired
	private Job getOptaMatchesJob;

	@Autowired
	private ShooterJobLauncher jobLauncher;

	@Value("${batch.schedule.enabled}")
	public String enabled;

	@Scheduled(fixedDelayString = "${jobs.schedule.optaData.frequency}")
	public void getOptaDataJob() {
		jobLauncher.executeJob(getOptaDataJob, enabled);
	}

	@Scheduled(fixedDelayString = "${jobs.schedule.optaMatches.frequency}")
	public void getOptaMatchesJob() {
		jobLauncher.executeJob(getOptaMatchesJob, enabled);
	}
}
