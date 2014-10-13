package com.fav24.shootr.batch.optaData.rest.parser.strategy;

import com.fav24.shootr.batch.optaData.rest.DTO.TeamDTO;
import com.fav24.shootr.batch.optaData.rest.parser.strategy.TeamByAreaRequestorStrategy;
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

public class TeamByAreaRequestorStrategyTest {

    TeamByAreaRequestorStrategy teamByAreaRequestorStrategy = null;
    DocumentBuilderFactory xmlFactory = null;
    DocumentBuilder builder = null;

    TeamDTO teamTest = null;

    @Before
    public void setUp() throws Exception {

        teamByAreaRequestorStrategy = new TeamByAreaRequestorStrategy();
        xmlFactory = DocumentBuilderFactory.newInstance();
        builder = xmlFactory.newDocumentBuilder();

        setUpCompetitionTestSimple();
    }

    private void setUpCompetitionTestSimple() {

        teamTest = new TeamDTO();

        teamTest.setTeam_id(2016l);
        teamTest.setOw_team_id(186l);
        teamTest.setType("club");
        teamTest.setSoccertype("default");
        teamTest.setTeamtype("default");

        teamTest.setClub_name("Real Madrid");

        teamTest.setLast_updated("2014-09-23 13:10:55");

    }

    private void checkTeamNode(List<TeamDTO> teams, int position, TeamDTO team) {

        TeamDTO currentTeam = teams.get(position);

        assertEquals(team.getTeam_id(), currentTeam.getTeam_id());
        assertEquals(team.getOw_team_id(), currentTeam.getOw_team_id());
        assertEquals(team.getType(), currentTeam.getType());
        assertEquals(team.getSoccertype(), currentTeam.getSoccertype());
        assertEquals(team.getTeamtype(), currentTeam.getTeamtype());

         assertEquals(team.getLast_updated(), currentTeam.getLast_updated());

    }

    @Test
    public void URLGeneratedOK() {

        teamByAreaRequestorStrategy.userName = "worldsl";
        teamByAreaRequestorStrategy.token = "c7c0ace395d80182db07ae2c30f034";
        teamByAreaRequestorStrategy.urlBase = "http://api.core.optasports.com";

        String response = "http://api.core.optasports.com/soccer/get_teams?type=area&id=7&username=worldsl&authkey=c7c0ace395d80182db07ae2c30f034";

        assertEquals(response, teamByAreaRequestorStrategy.generateRequestURL(Requestor.LanguageRequest.Spanish, 7));
    }

    @Test
    public void NoneNodeRequestIsParseOK() throws ParserConfigurationException, IOException, SAXException {

        Document document = givenXML("optaXML/get_teamsNone.xml");

        List<TeamDTO> teams = teamByAreaRequestorStrategy.getAllTeams(document);

        assertEquals(0, teams.size());
    }

    @Test
    public void singleNodeTeamRequestIsParseOK() throws ParserConfigurationException, IOException, SAXException {

        Document document = givenXML("optaXML/get_teamsSingle.xml");

        List<TeamDTO> teams = teamByAreaRequestorStrategy.getAllTeams(document);

        assertEquals(1, teams.size());

        checkTeamNode(teams, 0, teamTest);
    }

    @Test
    public void tripleNodeTeamRequestIsParseOK() throws ParserConfigurationException, IOException, SAXException {

        Document document = givenXML("optaXML/get_teamsThree.xml");

        List<TeamDTO> teams = teamByAreaRequestorStrategy.getAllTeams(document);

        assertEquals(3, teams.size());

        checkTeamNode(teams, 0, teamTest);
    }

    @Test
    public void completeNodeRequestIsParseOK() throws ParserConfigurationException, IOException, SAXException {

        Document document = givenXML("optaXML/get_teamsComplete.xml");

        List<TeamDTO> teams = teamByAreaRequestorStrategy.getAllTeams(document);

        assertEquals(18677, teams.size());
    }


    private Document givenXML(String xmlFileName) throws SAXException, IOException {
        InputStream xmlResource = Thread.currentThread().getContextClassLoader().getResourceAsStream(xmlFileName);
        return builder.parse(xmlResource);
    }
}