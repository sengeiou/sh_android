package com.fav24.shootr.batch.optaData.chunk;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
import com.fav24.shootr.batch.optaData.chunk.OptaSeasonMatcher;
import com.fav24.shootr.batch.optaData.rest.DTO.DTOtoDomainTransformer;
import com.fav24.shootr.batch.optaData.rest.DTO.SeasonDTO;
import com.fav24.shootr.dao.domain.Season;

@RunWith(MockitoJUnitRunner.class)
public class OptaSeasonMatcherTest {
	
	OptaSeasonMatcher matcher;
	
	
	@Before
	public void setUp(){
		matcher = new OptaSeasonMatcher();
		matcher.setdTOtoDomainTransformer(new DTOtoDomainTransformer());
	}
	
	@Test
	public void whenDatesAreTheSameIsOptaSeasonDateOlderThanOursReturnsFalse(){
		Date date = new Date();
		Season domain = new Season();
		domain.setLastUpdated(date);
		SeasonDTO dto = new SeasonDTO();
		String last_updated = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
		dto.setLast_updated(last_updated);
		assertFalse(matcher.isOptaSeasonDateOlderThanOurs(domain, dto));
	}
	
	@Test
	public void whenOptaIsOlderReturnTrue(){
		Date date = new Date();
		Season domain = new Season();
		domain.setLastUpdated(date);
		SeasonDTO dto = new SeasonDTO();
		String last_updated = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(date.getTime()+1000));
		dto.setLast_updated(last_updated);
		assertTrue(matcher.isOptaSeasonDateOlderThanOurs(domain, dto));
	}
	
	@Test
	public void whenOptaIsOlderReturnFalse(){
		Date date = new Date();
		Season domain = new Season();
		domain.setLastUpdated(date);
		SeasonDTO dto = new SeasonDTO();
		String last_updated = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(date.getTime()-1000));
		dto.setLast_updated(last_updated);
		assertFalse(matcher.isOptaSeasonDateOlderThanOurs(domain, dto));
	}
	
	@Test
	public void whenOptaDateIsNullReturnFalse(){
		Date date = new Date();
		Season domain = new Season();
		domain.setLastUpdated(date);
		SeasonDTO dto = new SeasonDTO();
		String last_updated = null;
		dto.setLast_updated(last_updated);
		assertFalse(matcher.isOptaSeasonDateOlderThanOurs(domain, dto));
	}
	
	@Test
	public void whenSeasonDateIsNullReturnTrue(){
		Season domain = new Season();
		SeasonDTO dto = new SeasonDTO();
		String last_updated = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		dto.setLast_updated(last_updated);
		assertTrue(matcher.isOptaSeasonDateOlderThanOurs(domain, dto));
	}
	
	@Test
	public void whenSameSeasonReturnTrue(){
		Season domain = new Season();
		domain.setIdSeasonOpta(new Long(1l));
		SeasonDTO dto = new SeasonDTO();
		dto.setSeason_id(new Long(1l));
		assertTrue(matcher.areSeasonsTheSame(dto, domain));
	}
	
	@Test
	public void whenDifferentSeasonReturnFalse(){
		Season domain = new Season();
		domain.setIdSeasonOpta(new Long(1l));
		SeasonDTO dto = new SeasonDTO();
		dto.setSeason_id(new Long(2l));
		assertFalse(matcher.areSeasonsTheSame(dto, domain));
	}
	
	@Test
	public void whenOptaIdIsNullReturnFalse(){
		Season domain = new Season();
		domain.setIdSeasonOpta(new Long(1l));
		SeasonDTO dto = new SeasonDTO();
		assertFalse(matcher.areSeasonsTheSame(dto, domain));
	}
	
	@Test
	public void whenNewSeasoneReturnCreate(){
		List<Season> domains = new ArrayList<Season>();
		SeasonDTO dto = new SeasonDTO();
		PersistAction persistAction = matcher.getPersistAction(domains, dto);
		assertEquals(persistAction.op, PersistAction.CREATE);
	}
	
	@Test
	public void whenNoChangesReturnIgnore(){
		Long idSeasonOpta = 2l;
		Date date = new Date();
		
		Season match = new Season();
		match.setLastUpdated(date);
		match.setIdSeasonOpta(idSeasonOpta);
		
		List<Season> domains = Arrays.asList(match);
		
		SeasonDTO dto = new SeasonDTO();
		dto.setSeason_id(idSeasonOpta);
		dto.setLast_updated(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date));

		PersistAction persistAction = matcher.getPersistAction(domains, dto);
		assertEquals(persistAction.op, PersistAction.IGNORE);
	}
	
	@Test
	public void whenChangedReturnUpdateAndDomainId(){
		Long idSeasonOpta = 2l;
		Long idSeason = 1l;
		Date date = new Date();
		
		Season match = new Season();
		match.setLastUpdated(date);
		match.setIdSeasonOpta(idSeasonOpta);
		match.setIdSeason(idSeason);
		
		List<Season> domains = Arrays.asList(match);
		
		SeasonDTO dto = new SeasonDTO();
		dto.setSeason_id(idSeasonOpta);
		dto.setLast_updated(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(date.getTime()+1000)));

		PersistAction persistAction = matcher.getPersistAction(domains, dto);
		assertEquals(persistAction.op, PersistAction.UPDATE);
		assertEquals(persistAction.domainId, idSeason);
	}
}
