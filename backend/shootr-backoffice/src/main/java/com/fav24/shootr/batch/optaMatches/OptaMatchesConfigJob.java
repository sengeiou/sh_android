package com.fav24.shootr.batch.optaMatches;


import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.fav24.shootr.batch.optaMatches.chunk.OptaMatchItem;
import com.fav24.shootr.batch.optaMatches.chunk.OptaMatchProcessor;
import com.fav24.shootr.batch.optaMatches.chunk.OptaMatchWriter;
import com.fav24.shootr.dao.domain.Season;
import com.google.common.collect.ImmutableMap;

@Configuration
public class OptaMatchesConfigJob {

    @Autowired
    private JobBuilderFactory jobs;
    @Autowired
    private StepBuilderFactory stepBuilder;
    @Autowired
    private DataSource dataSource;
    
    private final static int areaCommitInterval = 100;
    private final static int maxThreads = 4;

    @Bean
    public TaskExecutor optaMatchesTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setMaxPoolSize(maxThreads);
        taskExecutor.setCorePoolSize(maxThreads);
        taskExecutor.afterPropertiesSet();
        return taskExecutor;
    }

    @Bean
    public Job getOptaMatchesJob(Step matchStep){
        return jobs.get("getOptaMatchesJob")
                .start(matchStep)
                .build();
    }

    @Bean
    public Step matchStep(JdbcPagingItemReader<Season> matchReader, OptaMatchProcessor matchProcessor, OptaMatchWriter matchWriter, TaskExecutor optaMatchesTaskExecutor) {
        return stepBuilder.get("matchStep").
                <Season, OptaMatchItem>chunk(areaCommitInterval)
                .reader(matchReader)
                .processor(matchProcessor)
                .writer(matchWriter)
                .taskExecutor(optaMatchesTaskExecutor)
                .throttleLimit(maxThreads)
                .build();
    }
        
    @Bean
	public JdbcPagingItemReader<Season> matchReader(){
		MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();
		queryProvider.setSelectClause("select *");
		queryProvider.setFromClause("from Season");
		queryProvider.setSortKeys(ImmutableMap.of("idSeason", Order.ASCENDING));
		
		JdbcPagingItemReader<Season> reader = new JdbcPagingItemReader<Season>();
		reader.setDataSource(dataSource);
		reader.setQueryProvider(queryProvider);
		reader.setPageSize(1);
		reader.setFetchSize(1);
		reader.setRowMapper(new BeanPropertyRowMapper<Season>(Season.class)); 
		return reader;
	}
}
