package com.fav24.shootr.batch.optaMatches.chunk;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.fav24.shootr.batch.common.PersistAction;
import com.fav24.shootr.batch.optaData.rest.DTO.DTOtoDomainTransformer;
import com.fav24.shootr.batch.optaData.rest.DTO.MatchDTO;
import com.fav24.shootr.batch.optaMatches.chunk.OptaMatchMatcher;
import com.fav24.shootr.dao.domain.Match;

@RunWith(MockitoJUnitRunner.class)
public class OptaMatchMatcherTest {
	
	OptaMatchMatcher matcher;
	private final Long season = 1l;
	
	@Before
	public void setUp(){
		matcher = new OptaMatchMatcher();
		matcher.setdTOtoDomainTransformer(new DTOtoDomainTransformer());
	}
	
	@Test
	public void whenDatesAreTheSameIsOptaMatchDateOlderThanOursReturnsFalse(){
		Date date = new Date();
		Match domain = new Match();
		domain.setLastUpdated(date);
		MatchDTO optaMatch = new MatchDTO();
		String last_updated = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
		optaMatch.setLast_updated(last_updated);
		assertFalse(matcher.isOptaMatchDateOlderThanOurs(domain, optaMatch));
	}
	
	@Test
	public void whenOptaIsOlderReturnTrue(){
		Date date = new Date();
		Match domain = new Match();
		domain.setLastUpdated(date);
		MatchDTO optaMatch = new MatchDTO();
		String last_updated = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(date.getTime()+1000));
		optaMatch.setLast_updated(last_updated);
		assertTrue(matcher.isOptaMatchDateOlderThanOurs(domain, optaMatch));
	}
	
	@Test
	public void whenOptaIsOlderReturnFalse(){
		Date date = new Date();
		Match domain = new Match();
		domain.setLastUpdated(date);
		MatchDTO optaMatch = new MatchDTO();
		String last_updated = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(date.getTime()-1000));
		optaMatch.setLast_updated(last_updated);
		assertFalse(matcher.isOptaMatchDateOlderThanOurs(domain, optaMatch));
	}
	
	@Test
	public void whenOptaDateIsNullReturnFalse(){
		Date date = new Date();
		Match domain = new Match();
		domain.setLastUpdated(date);
		MatchDTO optaMatch = new MatchDTO();
		String last_updated = null;
		optaMatch.setLast_updated(last_updated);
		assertFalse(matcher.isOptaMatchDateOlderThanOurs(domain, optaMatch));
	}
	
	@Test
	public void whenMatchDateIsNullReturnTrue(){
		Match domain = new Match();
		MatchDTO optaMatch = new MatchDTO();
		String last_updated = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		optaMatch.setLast_updated(last_updated);
		assertTrue(matcher.isOptaMatchDateOlderThanOurs(domain, optaMatch));
	}
	
	@Test
	public void whenSameMatchReturnTrue(){
		Match domain = new Match();
		domain.setIdMatchOpta(new Long(1l));
		MatchDTO optaMatch = new MatchDTO();
		optaMatch.setMatch_id(new Long(1l));
		assertTrue(matcher.areMatchesTheSame(optaMatch, domain));
	}
	
	@Test
	public void whenDifferentMatchReturnFalse(){
		Match domain = new Match();
		domain.setIdMatchOpta(new Long(1l));
		MatchDTO optaMatch = new MatchDTO();
		optaMatch.setMatch_id(new Long(2l));
		assertFalse(matcher.areMatchesTheSame(optaMatch, domain));
	}
	
	@Test
	public void whenOptaIdIsNullReturnFalse(){
		Match domain = new Match();
		domain.setIdMatchOpta(new Long(1l));
		MatchDTO optaMatch = new MatchDTO();
		assertFalse(matcher.areMatchesTheSame(optaMatch, domain));
	}
	
	@Test
	public void whenNewMatcheReturnCreate(){
		List<Match> domains = new ArrayList<Match>();
		MatchDTO optaMatch = new MatchDTO();
		PersistAction persistAction = matcher.getPersistAction(domains, season, optaMatch);
		assertEquals(persistAction.op, PersistAction.CREATE);
	}
	
	@Test
	public void whenNoChangesReturnIgnore(){
		Long idMatchOpta = 2l;
		Date date = new Date();
		
		Match match = new Match();
		match.setLastUpdated(date);
		match.setIdMatchOpta(idMatchOpta);
		
		List<Match> domains = Arrays.asList(match);
		
		MatchDTO optaMatch = new MatchDTO();
		optaMatch.setMatch_id(idMatchOpta);
		optaMatch.setLast_updated(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date));

		PersistAction persistAction = matcher.getPersistAction(domains, season, optaMatch);
		assertEquals(persistAction.op, PersistAction.IGNORE);
	}
	
	@Test
	public void whenChangedReturnUpdateAndDomainId(){
		Long idMatchOpta = 2l;
		Long idMatch = 1l;
		Date date = new Date();
		
		Match match = new Match();
		match.setLastUpdated(date);
		match.setIdMatchOpta(idMatchOpta);
		match.setIdMatch(idMatch);
		
		List<Match> domains = Arrays.asList(match);
		
		MatchDTO optaMatch = new MatchDTO();
		optaMatch.setMatch_id(idMatchOpta);
		optaMatch.setLast_updated(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(date.getTime()+1000)));

		PersistAction persistAction = matcher.getPersistAction(domains, season, optaMatch);
		assertEquals(persistAction.op, PersistAction.UPDATE);
		assertEquals(persistAction.domainId, idMatch);
	}
}
