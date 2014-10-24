package com.fav24.shootr.batch.optaData.chunk;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.fav24.shootr.batch.common.PersistAction;
import com.fav24.shootr.batch.optaData.chunk.OptaTeamMatcher;
import com.fav24.shootr.batch.optaData.rest.DTO.DTOtoDomainTransformer;
import com.fav24.shootr.batch.optaData.rest.DTO.TeamDTO;
import com.fav24.shootr.dao.domain.Team;

@RunWith(MockitoJUnitRunner.class)
public class OptaTeamMatcherTest {
	
	OptaTeamMatcher matcher;
	
	@Before
	public void setUp(){
		matcher = new OptaTeamMatcher();
		matcher.setdTOtoDomainTransformer(new DTOtoDomainTransformer());
	}
	
	@Test
	public void whenDefaultisOptaTeamTypeDefaultReturnTrue(){
		TeamDTO dto = new TeamDTO();
		dto.setTeamtype("default");
		assertTrue(matcher.isOptaTeamTypeDefault(dto));
	}
	
	@Test
	public void whenNotDefaultisOptaTeamTypeDefaultReturnFalse(){
		TeamDTO dto = new TeamDTO();
		dto.setTeamtype("youth");
		assertFalse(matcher.isOptaTeamTypeDefault(dto));
	}
	
	@Test
	public void whenNullisOptaTeamTypeDefaultReturnFalse(){
		TeamDTO dto = new TeamDTO();
		assertFalse(matcher.isOptaTeamTypeDefault(dto));
	}

	
	@Test
	public void whenSameTeamReturnTrue(){
		Team domain = new Team();
		domain.setIdTeamOpta(1l);
		TeamDTO dto = new TeamDTO();
		dto.setTeam_id(1l);
		assertTrue(matcher.areTeamsTheSame(dto, domain));
	}
	
	@Test
	public void whenDifferentMatchReturnFalse(){
		Team domain = new Team();
		domain.setIdTeamOpta(1l);
		TeamDTO dto = new TeamDTO();
		dto.setTeam_id(2l);
		assertFalse(matcher.areTeamsTheSame(dto, domain));
	}
	
	@Test
	public void whenOptaIdIsNullReturnFalse(){
		Team domain = new Team();
		domain.setIdTeamOpta(1l);
		TeamDTO dto = new TeamDTO();
		assertFalse(matcher.areTeamsTheSame(dto, domain));
	}
	
	@Test
	public void whenNewTeamReturnCreate(){
		List<Team> domains = new ArrayList<Team>();
		TeamDTO dto = new TeamDTO();
		PersistAction persistAction = matcher.getPersistAction(domains, dto);
		assertEquals(persistAction.op, PersistAction.CREATE);
	}
	
	@Test
	public void whenNoChangesReturnIgnore(){
		Long idTeamOpta = 2l;
		Team domain = new Team();
		domain.setIdTeamOpta(idTeamOpta);
		
		List<Team> domains = Arrays.asList(domain);
		
		TeamDTO dto = new TeamDTO();
		dto.setTeam_id(idTeamOpta);

		PersistAction persistAction = matcher.getPersistAction(domains, dto);
		assertEquals(persistAction.op, PersistAction.IGNORE);
	}
}
