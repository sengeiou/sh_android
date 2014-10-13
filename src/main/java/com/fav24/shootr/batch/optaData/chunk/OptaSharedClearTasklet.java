package com.fav24.shootr.batch.optaData.chunk;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OptaSharedClearTasklet implements Tasklet {

	@Autowired
	public OptaDataShared optaDataShared;

	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext arg1) throws Exception {
		optaDataShared.clear();
		return RepeatStatus.FINISHED;
	}

}
