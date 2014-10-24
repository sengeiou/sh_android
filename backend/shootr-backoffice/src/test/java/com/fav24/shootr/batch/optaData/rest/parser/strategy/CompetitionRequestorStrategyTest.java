package com.fav24.shootr.batch.optaData.rest.parser.strategy;

import com.fav24.shootr.batch.optaData.rest.DTO.CompetitionDTO;
import com.fav24.shootr.batch.optaData.rest.DTO.SeasonDTO;
import com.fav24.shootr.batch.optaData.rest.parser.strategy.CompetitionRequestorStrategy;
import com.fav24.shootr.batch.rest.parser.Requestor;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class CompetitionRequestorStrategyTest {

    CompetitionRequestorStrategy competitionRequestorStrategy = null;
    DocumentBuilderFactory xmlFactory = null;
    DocumentBuilder builder = null;

    CompetitionDTO competitionTestSimple = null;
    CompetitionDTO competitionTestSimpleWithOutSeasons = null;

    @Before
    public void setUp() throws Exception {

        competitionRequestorStrategy = new CompetitionRequestorStrategy();
        xmlFactory = DocumentBuilderFactory.newInstance();
        builder = xmlFactory.newDocumentBuilder();

        setUpCompetitionTestSimple();
        setUpCompetitionTestSimpleWithOutSeasons();
    }

    private void setUpCompetitionTestSimple() {
        competitionTestSimple = new CompetitionDTO();

        competitionTestSimple.setCompetition_id(8l);
        competitionTestSimple.setOw_competition_id(8l);
        competitionTestSimple.setName("Premier League");

        competitionTestSimple.setTeamtype("default");
        competitionTestSimple.setDisplay_order(10l);
        competitionTestSimple.setType("club");
        competitionTestSimple.setArea_id(68l);
        competitionTestSimple.setArea_name("England");
        competitionTestSimple.setLast_updated("2014-09-28 18:50:56");
        competitionTestSimple.setSoccertype("default");
        competitionTestSimple.setFormat("Domestic league");


        SeasonDTO seasonTestSimple = new SeasonDTO();
        seasonTestSimple.setSeason_id(9641l);
        seasonTestSimple.setName("2014/2015");
        seasonTestSimple.setStart_date("2014-08-16");
        seasonTestSimple.setEnd_date("2015-05-24");
        seasonTestSimple.setService_level(0l);
        seasonTestSimple.setLast_updated("2014-09-28 18:50:56");

        competitionTestSimple.getSeasons().add(seasonTestSimple);

    }

    private void setUpCompetitionTestSimpleWithOutSeasons() {
        competitionTestSimpleWithOutSeasons = new CompetitionDTO();

        competitionTestSimpleWithOutSeasons.setCompetition_id(8l);
        competitionTestSimpleWithOutSeasons.setOw_competition_id(8l);
        competitionTestSimpleWithOutSeasons.setName("Premier League");

        competitionTestSimpleWithOutSeasons.setTeamtype("default");
        competitionTestSimpleWithOutSeasons.setDisplay_order(10l);
        competitionTestSimpleWithOutSeasons.setType("club");
        competitionTestSimpleWithOutSeasons.setArea_id(68l);
        competitionTestSimpleWithOutSeasons.setArea_name("England");
        competitionTestSimpleWithOutSeasons.setLast_updated("2014-09-28 18:50:56");
        competitionTestSimpleWithOutSeasons.setSoccertype("default");
        competitionTestSimpleWithOutSeasons.setFormat("Domestic league");

    }

    @Test
    public void URLGeneratedOK() {

        competitionRequestorStrategy.userName = "worldsl";
        competitionRequestorStrategy.token = "c7c0ace395d80182db07ae2c30f034";
        competitionRequestorStrategy.urlBase = "http://api.core.optasports.com";

        String response = "http://api.core.optasports.com/soccer/get_seasons?authorized=yes&username=" + "worldsl" + "&authkey=" + "c7c0ace395d80182db07ae2c30f034";

        assertEquals(response, competitionRequestorStrategy.generateRequestURL(Requestor.LanguageRequest.Spanish));

    }

    private void checkCompetitionNode(List<CompetitionDTO> competitions, int position, CompetitionDTO competition) {

        CompetitionDTO currentCompetition = competitions.get(position);

        assertEquals(competition.getCompetition_id(), currentCompetition.getCompetition_id());
        assertEquals(competition.getOw_competition_id(), currentCompetition.getOw_competition_id());

        assertEquals(competition.getName(), currentCompetition.getName());
        assertEquals(competition.getTeamtype(), currentCompetition.getTeamtype());

        assertEquals(competition.getSoccertype(), currentCompetition.getSoccertype());
        assertEquals(competition.getFormat(), currentCompetition.getFormat());
        assertEquals(competition.getDisplay_order(), currentCompetition.getDisplay_order());
        assertEquals(competition.getType(), currentCompetition.getType());

        assertEquals(competition.getArea_id(), currentCompetition.getArea_id());
        assertEquals(competition.getArea_name(), currentCompetition.getArea_name());

        assertEquals(competition.getLast_updated(), currentCompetition.getLast_updated());

        for (int i = 0; i < currentCompetition.getSeasons().size(); i++) {
            checkSeasonNode(currentCompetition.getSeasons(), i, competition.getSeasons().get(i));
        }

    }

    private void checkSeasonNode(List<SeasonDTO> seasons, int position, SeasonDTO season) {

        SeasonDTO currentSeason = seasons.get(position);

        assertEquals(season.getSeason_id(), currentSeason.getSeason_id());
        assertEquals(season.getName(), currentSeason.getName());
        assertEquals(season.getStart_date(), currentSeason.getStart_date());
        assertEquals(season.getEnd_date(), currentSeason.getEnd_date());

        assertEquals(season.getService_level(), currentSeason.getService_level());
        assertEquals(season.getLast_updated(), currentSeason.getLast_updated());
    }

    @Test
    public void NoneNodeRequestIsParseOK() throws ParserConfigurationException, IOException, SAXException {

        Document document = givenXML("optaXML/get_areasNone.xml");

        List<CompetitionDTO> competitions = competitionRequestorStrategy.getAllCompetitions(document, null);

        assertEquals(0, competitions.size());
    }

    @Test
    public void singleNodeCompetitionWithouthSeasonsRequestIsParseOK() throws ParserConfigurationException, IOException, SAXException {

        Document document = givenXML("optaXML/get_seasonsWithoutSeasonsSimple.xml");

        List<CompetitionDTO> competitions = competitionRequestorStrategy.getAllCompetitions(document, null);

        assertEquals(1, competitions.size());

        checkCompetitionNode(competitions, 0, competitionTestSimpleWithOutSeasons);
    }

    @Test
    public void singleNodeCompetitionRequestIsParseOK() throws ParserConfigurationException, IOException, SAXException {

        Document document = givenXML("optaXML/get_seasonsSimple.xml");

        List<CompetitionDTO> competitions = competitionRequestorStrategy.getAllCompetitions(document, null);

        assertEquals(1, competitions.size());

        checkCompetitionNode(competitions, 0, competitionTestSimple);
    }

    @Test
    public void tripleNodeCompetitionRequestIsParseOK() throws ParserConfigurationException, IOException, SAXException {

        Document document = givenXML("optaXML/get_seasonsThree.xml");

        List<CompetitionDTO> competitions = competitionRequestorStrategy.getAllCompetitions(document, null);

        assertEquals(3, competitions.size());

        //Cada competicion tiene 1 temporada
        for (CompetitionDTO competition : competitions) {
            assertEquals(1, competition.getSeasons().size());
        }
    }

    @Test
    public void completeNodeRequestIsParseOK() throws ParserConfigurationException, IOException, SAXException {

        Document document = givenXML("optaXML/get_seasonsComplete.xml");

        List<CompetitionDTO> competitions = competitionRequestorStrategy.getAllCompetitions(document, null);

        assertEquals(9, competitions.size());

        //El primer elemento ha de ser la Primera division
        assertEquals(7l, competitions.get(0).getCompetition_id().longValue());
        assertEquals("Primera División", competitions.get(0).getName());
        assertEquals(1, competitions.get(0).getSeasons().size());

        //El elemento 8 son amistosos internacionales
        assertEquals(430l, competitions.get(8).getCompetition_id().longValue());
        assertEquals("Friendlies", competitions.get(8).getName());
        assertEquals(106, competitions.get(8).getSeasons().size());
    }


    private Document givenXML(String xmlFileName) throws SAXException, IOException {
        InputStream xmlResource = Thread.currentThread().getContextClassLoader().getResourceAsStream(xmlFileName);
        return builder.parse(xmlResource);
    }


}