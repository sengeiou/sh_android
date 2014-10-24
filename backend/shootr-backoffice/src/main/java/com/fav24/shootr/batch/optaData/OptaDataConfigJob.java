package com.fav24.shootr.batch.optaData;


import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.fav24.shootr.batch.optaData.chunk.OptaAreaProcessor;
import com.fav24.shootr.batch.optaData.chunk.OptaAreaReader;
import com.fav24.shootr.batch.optaData.chunk.OptaAreaWriter;
import com.fav24.shootr.batch.optaData.chunk.OptaCompetitionProcessor;
import com.fav24.shootr.batch.optaData.chunk.OptaCompetitionReader;
import com.fav24.shootr.batch.optaData.chunk.OptaCompetitionWriter;
import com.fav24.shootr.batch.optaData.chunk.OptaSeasonItem;
import com.fav24.shootr.batch.optaData.chunk.OptaSeasonProcessor;
import com.fav24.shootr.batch.optaData.chunk.OptaSeasonReader;
import com.fav24.shootr.batch.optaData.chunk.OptaSeasonWriter;
import com.fav24.shootr.batch.optaData.chunk.OptaSharedClearTasklet;
import com.fav24.shootr.batch.optaData.chunk.OptaSharedTasklet;
import com.fav24.shootr.batch.optaData.chunk.OptaTeamItem;
import com.fav24.shootr.batch.optaData.chunk.OptaTeamProcessor;
import com.fav24.shootr.batch.optaData.chunk.OptaTeamReader;
import com.fav24.shootr.batch.optaData.chunk.OptaTeamWriter;
import com.fav24.shootr.batch.optaData.rest.DTO.AreaDTO;
import com.fav24.shootr.batch.optaData.rest.DTO.CompetitionDTO;
import com.fav24.shootr.dao.domain.Area;
import com.fav24.shootr.dao.domain.Competition;

@Configuration
public class OptaDataConfigJob {

    @Autowired
    private JobBuilderFactory jobs;
    @Autowired
    private StepBuilderFactory stepBuilder;
    @Autowired
    private DataSource dataSource;
    
    private final static int areaCommitInterval = 100;
    private final static int maxThreads = 4;

    @Bean
    public TaskExecutor optaDataTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setMaxPoolSize(maxThreads);
        taskExecutor.setCorePoolSize(maxThreads);
        taskExecutor.afterPropertiesSet();
        return taskExecutor;
    }

    @Bean
    public Job getOptaDataJob(Step optaSharedStep, Step areaStep, Step competitionStep, Step seasonStep, Step teamStep, Step optaSharedClearStep){
        return jobs.get("getOptaDataJob")
                .start(optaSharedStep)
                .next(areaStep)
                .next(competitionStep)
                .next(seasonStep)
                .next(teamStep)
                .next(optaSharedClearStep)
                .build();
    }

    @Bean
    public Step optaSharedStep(OptaSharedTasklet optaSharedTasklet) throws Exception {
    	return stepBuilder.get("optaSharedStep").tasklet(optaSharedTasklet).build();
    }
    
    
    @Bean
    public Step areaStep(OptaAreaReader areaReader, OptaAreaProcessor areaProcessor, OptaAreaWriter areaWritter, TaskExecutor optaDataTaskExecutor) {
        return stepBuilder.get("areaStep").
                <AreaDTO, Area>chunk(areaCommitInterval)
                .reader(areaReader)
                .processor(areaProcessor)
                .writer(areaWritter)
                .taskExecutor(optaDataTaskExecutor)
                .throttleLimit(maxThreads)
                .build();
    }
    
    @Bean
    public Step competitionStep(OptaCompetitionReader teamReader, OptaCompetitionProcessor teamProcessor, OptaCompetitionWriter teamWriter, TaskExecutor optaDataTaskExecutor) {
        return stepBuilder.get("competitionStep").
                <CompetitionDTO, Competition>chunk(areaCommitInterval)
                .reader(teamReader)
                .processor(teamProcessor)
                .writer(teamWriter)
                .taskExecutor(optaDataTaskExecutor)
                .throttleLimit(maxThreads)
                .build();
    }
    
    @Bean
    public Step seasonStep(OptaSeasonReader seasonReader, OptaSeasonProcessor seasonProcessor, OptaSeasonWriter seasonWriter, TaskExecutor optaDataTaskExecutor) {
        return stepBuilder.get("seasonStep").
                <OptaSeasonItem, OptaSeasonItem>chunk(areaCommitInterval)
                .reader(seasonReader)
                .processor(seasonProcessor)
                .writer(seasonWriter)
                .taskExecutor(optaDataTaskExecutor)
                .throttleLimit(maxThreads)
                .build();
    }
    
    @Bean
    public Step teamStep(OptaTeamReader teamReader, OptaTeamProcessor teamProcessor, OptaTeamWriter teamWriter, TaskExecutor optaDataTaskExecutor) {
        return stepBuilder.get("teamStep").
                <AreaDTO, OptaTeamItem>chunk(areaCommitInterval)
                .reader(teamReader)
                .processor(teamProcessor)
                .writer(teamWriter)
                .taskExecutor(optaDataTaskExecutor)
                .throttleLimit(maxThreads)
                .build();
    }
    
    @Bean
    public Step optaSharedClearStep(OptaSharedClearTasklet optaSharedClearTasklet) throws Exception {
    	return stepBuilder.get("optaSharedClearStep").tasklet(optaSharedClearTasklet).build();
    }
}
