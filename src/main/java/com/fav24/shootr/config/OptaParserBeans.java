package com.fav24.shootr.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fav24.shootr.batch.network.NetworkFactory;
import com.fav24.shootr.batch.optaData.rest.parser.strategy.AreaRequestorStrategy;
import com.fav24.shootr.batch.optaData.rest.parser.strategy.CompetitionRequestorStrategy;
import com.fav24.shootr.batch.optaData.rest.parser.strategy.MatchRequestorStrategy;
import com.fav24.shootr.batch.optaData.rest.parser.strategy.TeamByAreaRequestorStrategy;
import com.fav24.shootr.batch.optaData.rest.parser.strategy.TeamBySeasonRequestorStrategy;
import com.fav24.shootr.batch.optaData.rest.parser.strategy.TeamRequestorStrategy;
import com.fav24.shootr.batch.rest.parser.Requestor;
import com.fav24.shootr.batch.rest.parser.RequestorImpl;

@Configuration
public class OptaParserBeans {

	@Bean
	public Requestor competitionRequestor(CompetitionRequestorStrategy competitionRequestorStrategy, NetworkFactory networkFactory) {
		return new RequestorImpl(competitionRequestorStrategy, networkFactory);
	}

	@Bean
	public Requestor areaRequestor(AreaRequestorStrategy areaRequestorStrategy, NetworkFactory networkFactory) {
		return new RequestorImpl(areaRequestorStrategy, networkFactory);
	}

	@Bean
	public Requestor teamRequestor(TeamRequestorStrategy teamRequestorStrategy, NetworkFactory networkFactory) {
		return new RequestorImpl(teamRequestorStrategy, networkFactory);
	}

	@Bean
	public Requestor teamByAreaRequestor(TeamByAreaRequestorStrategy teamByAreaRequestorStrategy, NetworkFactory networkFactory) {
		return new RequestorImpl(teamByAreaRequestorStrategy, networkFactory);
	}
	
	@Bean
	public Requestor teamBySeasonRequestor(TeamBySeasonRequestorStrategy teamBySeasonRequestorStrategy, NetworkFactory networkFactory) {
		return new RequestorImpl(teamBySeasonRequestorStrategy, networkFactory);
	}
	
	@Bean
	public Requestor matchRequestor(MatchRequestorStrategy matchRequestorStrategy, NetworkFactory networkFactory) {
		return new RequestorImpl(matchRequestorStrategy, networkFactory);
	}

}
