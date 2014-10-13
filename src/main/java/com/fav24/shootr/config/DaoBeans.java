package com.fav24.shootr.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fav24.shootr.dao.AreaDAO;
import com.fav24.shootr.dao.CompetitionDAO;
import com.fav24.shootr.dao.MatchDAO;
import com.fav24.shootr.dao.SeasonDAO;
import com.fav24.shootr.dao.TeamDAO;
import com.fav24.shootr.dao.impl.AreaDAOImpl;
import com.fav24.shootr.dao.impl.CompetitionDAOImpl;
import com.fav24.shootr.dao.impl.MatchDAOImpl;
import com.fav24.shootr.dao.impl.SeasonDAOImpl;
import com.fav24.shootr.dao.impl.TeamDAOImpl;

@Configuration
public class DaoBeans {

	@Autowired
	private DataSource dataSource;

	@Bean
	public TeamDAO teamDAO() {
		TeamDAO dao = new TeamDAOImpl();
		dao.setDs(dataSource);
		return dao;
	}

	@Bean
	public AreaDAO areaDAO() {
		AreaDAO dao = new AreaDAOImpl();
		dao.setDs(dataSource);
		return dao;
	}

	@Bean
	public CompetitionDAO competitionDAO() {
		CompetitionDAO dao = new CompetitionDAOImpl();
		dao.setDs(dataSource);
		return dao;
	}

	@Bean
	public SeasonDAO seasonDAO() {
		SeasonDAO dao = new SeasonDAOImpl();
		dao.setDs(dataSource);
		return dao;
	}
	
	@Bean
	public MatchDAO matchDAO() {
		MatchDAO dao = new MatchDAOImpl();
		dao.setDs(dataSource);
		return dao;
	} 
}
