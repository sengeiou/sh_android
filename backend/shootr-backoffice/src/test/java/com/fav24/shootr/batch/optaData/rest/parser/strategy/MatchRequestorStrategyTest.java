package com.fav24.shootr.batch.optaData.rest.parser.strategy;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.management.modelmbean.XMLParseException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.fav24.shootr.batch.optaData.rest.DTO.GroupDTO;
import com.fav24.shootr.batch.optaData.rest.DTO.MatchDTO;
import com.fav24.shootr.batch.optaData.rest.DTO.RoundDTO;
import com.fav24.shootr.batch.optaData.rest.parser.strategy.MatchRequestorStrategy;

public class MatchRequestorStrategyTest {


    MatchRequestorStrategy matchRequestorStrategy = null;
    DocumentBuilderFactory xmlFactory = null;
    DocumentBuilder builder = null;

    RoundDTO roundTestSimple = null;

    @Before
    public void setUp() throws Exception {

        matchRequestorStrategy = new MatchRequestorStrategy();
        xmlFactory = DocumentBuilderFactory.newInstance();
        builder = xmlFactory.newDocumentBuilder();

        setUpRoundWithGroupTestSimple();
    }

    @Test
    public void singleNodeMatchInGroupRequestIsParseOK() throws ParserConfigurationException, IOException, SAXException, XMLParseException {

        Document document = givenXML("optaXML/get_matchesInGroupSingle.xml");

        List<RoundDTO> rounds = matchRequestorStrategy.getAllMatches(document, null);

        assertEquals(1, rounds.size());

        checkRoundNode(rounds, 0, roundTestSimple);
    }

    @Test
    public void completeMatchInGroupRequestIsParseOK() throws ParserConfigurationException, IOException, SAXException, XMLParseException {

        Document document = givenXML("optaXML/get_matchesInGroup.xml");

        List<RoundDTO> rounds = matchRequestorStrategy.getAllMatches(document, null);


        assertEquals(4, rounds.size());

        assertEquals(0, rounds.get(0).getAgregations().size());
        assertEquals(0, rounds.get(1).getAgregations().size());
        assertEquals(0, rounds.get(2).getAgregations().size());
        assertEquals(0, rounds.get(3).getAgregations().size());

        assertEquals(4, rounds.get(0).getGroupList().size());
        assertEquals(0, rounds.get(1).getGroupList().size());
        assertEquals(0, rounds.get(2).getGroupList().size());
        assertEquals(0, rounds.get(3).getGroupList().size());

        GroupDTO firstGroup = rounds.get(0).getGroupList().get(0);
        assertEquals(6, firstGroup.getMatches().size());
        assertEquals(1063209l, firstGroup.getMatches().get(5).getMatch_id().longValue());

        assertEquals(0, rounds.get(0).getMatches().size());
        assertEquals(4, rounds.get(1).getMatches().size());
        assertEquals(2, rounds.get(2).getMatches().size());
        assertEquals(1, rounds.get(3).getMatches().size());

        assertEquals(1063222l, rounds.get(3).getMatches().get(0).getMatch_id().longValue());


    }

    @Test
    public void completeMatchInLeagueRequestIsParseOK() throws ParserConfigurationException, IOException, SAXException, XMLParseException {

        Document document = givenXML("optaXML/get_matchesInLeague.xml");

        List<RoundDTO> rounds = matchRequestorStrategy.getAllMatches(document, null);

        assertEquals(1, rounds.size());

        assertEquals(0, rounds.get(0).getAgregations().size());
        assertEquals(0, rounds.get(0).getGroupList().size());
        assertEquals(306, rounds.get(0).getMatches().size());

        assertEquals(1713995l, rounds.get(0).getMatches().get(305).getMatch_id().longValue());

    }


    @Test
    public void completeMatchInTournamentIsParseOk() throws ParserConfigurationException, IOException, SAXException, XMLParseException {
        Document document = givenXML("optaXML/get_matchesInAggr.xml");

        List<RoundDTO> rounds = matchRequestorStrategy.getAllMatches(document, null);

        assertEquals(3, rounds.size());

        assertEquals(0, rounds.get(0).getAgregations().size());
        assertEquals(10, rounds.get(0).getGroupList().size());
        assertEquals(0, rounds.get(0).getMatches().size());

        assertEquals(7, rounds.get(1).getAgregations().size());
        assertEquals(0, rounds.get(1).getGroupList().size());
        assertEquals(0, rounds.get(1).getMatches().size());

        assertEquals(0, rounds.get(2).getAgregations().size());
        assertEquals(0, rounds.get(2).getGroupList().size());
        assertEquals(0, rounds.get(2).getMatches().size());
    }


    private void setUpRoundWithGroupTestSimple() {

        roundTestSimple = new RoundDTO();
        roundTestSimple.setRound_id(13552l);
        roundTestSimple.setName("Group stage");
        roundTestSimple.setStart_date("2012-06-08");
        roundTestSimple.setEnd_date("2012-06-19");
        roundTestSimple.setType("table");
        roundTestSimple.setGroups(4l);

        roundTestSimple.setHas_outgroup_matches("no");
        roundTestSimple.setLast_updated("2014-09-28 18:16:04");


        GroupDTO group = new GroupDTO();
        group.setGroup_id(4277l);
        group.setName("Group A");
        group.setDetails("");
        group.setWinner("");
        group.setLast_updated("2014-09-28 18:16:04");


        MatchDTO match = new MatchDTO();
        match.setMatch_id(1063182l);
        match.setOw_match_id(426847l);
        match.setDate_utc("2012-06-08");
        match.setTime_utc("16:00:00");

        match.setTeam_A_id(1677l);
        match.setOw_team_A_id(511l);
        match.setTeam_A_name("Poland");

        match.setTeam_B_id(1091l);
        match.setOw_team_B_id(517l);
        match.setTeam_B_name("Greece");

        match.setStatus("Played");
        match.setGameweek(null);
        match.setWinner("draw");

        match.setFs_A(1l);
        match.setHts_A(1l);
        match.setEts_A(null);
        match.setPs_A(null);

        match.setFs_B(1l);
        match.setHts_B(0l);
        match.setEts_B(null);
        match.setPs_B(null);

        match.setLast_updated("2014-09-25 15:45:01");

        roundTestSimple.getGroupList().add(group);
        group.getMatches().add(match);

    }

    private void checkGroupNode(List<GroupDTO> groups, int position, GroupDTO group) {

        GroupDTO currentGroup = groups.get(position);

        assertEquals(group.getGroup_id(), currentGroup.getGroup_id());
        assertEquals(group.getName(), currentGroup.getName());
        assertEquals(group.getDetails(), currentGroup.getDetails());
        assertEquals(group.getWinner(), currentGroup.getWinner());
        assertEquals(group.getLast_updated(), currentGroup.getLast_updated());

        for (int i = 0; i < group.getMatches().size(); i++) {
            checkMatchNode(currentGroup.getMatches(), i, group.getMatches().get(i));
        }

    }

    private void checkRoundNode(List<RoundDTO> rounds, int position, RoundDTO round) {

        RoundDTO currentRound = rounds.get(position);

        assertEquals(round.getRound_id(), currentRound.getRound_id());
        assertEquals(round.getName(), currentRound.getName());
        assertEquals(round.getStart_date(), currentRound.getStart_date());
        assertEquals(round.getEnd_date(), currentRound.getEnd_date());
        assertEquals(round.getType(), currentRound.getType());
        assertEquals(round.getGroups(), currentRound.getGroups());
        assertEquals(round.getHas_outgroup_matches(), currentRound.getHas_outgroup_matches());
        assertEquals(round.getLast_updated(), currentRound.getLast_updated());

        for (int i = 0; i < round.getGroupList().size(); i++) {
            checkGroupNode(currentRound.getGroupList(), i, round.getGroupList().get(i));
        }

    }

//    private void checkAggrNode(List<AggrDTO> aggregations, int position, AggrDTO aggregation) {
//
//        AggrDTO currentAggretation = aggregations.get(position);
//
//        assertEquals(aggregation.getWinner_team_id(), currentAggretation.getWinner_team_id());
//        assertEquals(aggregation.getOw_winner_team_id(), currentAggretation.getOw_winner_team_id());
//        assertEquals(aggregation.getWinner(), currentAggretation.getWinner());
//    }

    private void checkMatchNode(List<MatchDTO> matches, int position, MatchDTO match) {

        MatchDTO currentMatch = matches.get(position);

        assertEquals(match.getMatch_id(), currentMatch.getMatch_id());
        assertEquals(match.getOw_match_id(), currentMatch.getOw_match_id());

        assertEquals(match.getDate_utc(), currentMatch.getDate_utc());
        assertEquals(match.getTime_utc(), currentMatch.getTime_utc());

        assertEquals(match.getTeam_A_id(), currentMatch.getTeam_A_id());
        assertEquals(match.getTeam_B_id(), currentMatch.getTeam_B_id());

        assertEquals(match.getTeam_A_name(), currentMatch.getTeam_A_name());
        assertEquals(match.getTeam_B_name(), currentMatch.getTeam_B_name());

        assertEquals(match.getOw_team_A_id(), currentMatch.getOw_team_A_id());
        assertEquals(match.getOw_team_B_id(), currentMatch.getOw_team_B_id());

        assertEquals(match.getStatus(), currentMatch.getStatus());
        assertEquals(match.getGameweek(), currentMatch.getGameweek());

        assertEquals(match.getWinner(), currentMatch.getWinner());

        assertEquals(match.getFs_A(), currentMatch.getFs_A());
        assertEquals(match.getFs_B(), currentMatch.getFs_B());

        assertEquals(match.getHts_A(), currentMatch.getHts_A());
        assertEquals(match.getHts_B(), currentMatch.getHts_B());

        assertEquals(match.getEts_A(), currentMatch.getEts_A());
        assertEquals(match.getEts_B(), currentMatch.getEts_B());

        assertEquals(match.getPs_A(), currentMatch.getPs_A());
        assertEquals(match.getPs_B(), currentMatch.getPs_B());

        assertEquals(match.getLast_updated(), currentMatch.getLast_updated());


    }

    private Document givenXML(String xmlFileName) throws SAXException, IOException {
        InputStream xmlResource = Thread.currentThread().getContextClassLoader().getResourceAsStream(xmlFileName);
        return builder.parse(xmlResource);
    }


}