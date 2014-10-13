package com.fav24.shootr.batch.optaData.rest.parser.strategy;

import java.util.ArrayList;
import java.util.List;

import javax.management.modelmbean.XMLParseException;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.fav24.shootr.batch.optaData.rest.DTO.AggrDTO;
import com.fav24.shootr.batch.optaData.rest.DTO.GroupDTO;
import com.fav24.shootr.batch.optaData.rest.DTO.MatchDTO;
import com.fav24.shootr.batch.optaData.rest.DTO.RoundDTO;
import com.fav24.shootr.batch.rest.parser.Requestor;
import com.fav24.shootr.util.NumberUtil;

@Service
public class MatchRequestorStrategy extends BaseRequestorStrategy {


    @Override
    public String generateRequestURL(Requestor.LanguageRequest languageRequest, long... id) {
        long idSeason = id[0];
        return urlBase + "/soccer/get_matches?id=" + idSeason + "&type=season"+ getAuthUrl();
    }

    @SuppressWarnings("unchecked")
	@Override
    public List<RoundDTO> mapResponse(Document document) throws Exception {
        return getAllMatches(document.getDocumentElement(), null);
    }

    List<RoundDTO> getAllMatches(Node node, Object parent) throws XMLParseException {


        List<RoundDTO> roundDTOList = new ArrayList<RoundDTO>();

        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node currentNode = nodeList.item(i);
            if (currentNode.getNodeType() == Node.ELEMENT_NODE) {

                //calls this method for all the children which is Element
                String nodeName = currentNode.getNodeName();

                if ("round".equals(nodeName)) {
                    RoundDTO roundDTO = nodeToRoundDTO(currentNode);
                    getAllMatches(currentNode, roundDTO);
                    roundDTOList.add(roundDTO);
                } else if ("group".equals(nodeName)) {
                    GroupDTO groupDTO = nodeToGroupDTO(currentNode);

                    if (currentNode.getParentNode() != null && "round".equals(currentNode.getParentNode().getNodeName())) {
                        RoundDTO roundDTO = (RoundDTO) parent;
                        getAllMatches(currentNode, groupDTO);
                        roundDTO.getGroupList().add(groupDTO);
                    } else {
                        throw new XMLParseException("Caso no contemplado: Un Nodo de tipo Group tiene como padre un nodo de tipo " + currentNode.getParentNode());
                    }

                } else if ("aggr".equals(nodeName)) {
                    AggrDTO aggrDTO = nodeToAggrDTO(currentNode);

                    if (currentNode.getParentNode() != null && "round".equals(currentNode.getParentNode().getNodeName())) {
                        RoundDTO roundDTO = (RoundDTO) parent;
                        getAllMatches(currentNode, aggrDTO);
                        roundDTO.getAgregations().add(aggrDTO);
                    } else {
                        throw new XMLParseException("Caso no contemplado: Un Nodo de tipo Aggr tiene como padre un nodo de tipo " + currentNode.getParentNode());
                    }


                } else if ("match".equals(nodeName)) {
                    MatchDTO matchDTO = nodeToMatchDTO(currentNode);

                    if (currentNode.getParentNode() != null && "round".equals(currentNode.getParentNode().getNodeName())) {
                        RoundDTO roundDTO = (RoundDTO) parent;
                        roundDTO.getMatches().add(matchDTO);

                    } else if (currentNode.getParentNode() != null && "group".equals(currentNode.getParentNode().getNodeName())) {
                        GroupDTO groupDTO = (GroupDTO) parent;
                        groupDTO.getMatches().add(matchDTO);
                    } else if (currentNode.getParentNode() != null && "aggr".equals(currentNode.getParentNode().getNodeName())) {
                        AggrDTO aggrDTO = (AggrDTO) parent;
                        aggrDTO.getMatches().add(matchDTO);
                    } else {
                        throw new XMLParseException("Caso no contemplado: Un Nodo de tipo Match tiene como padre un nodo de tipo " + currentNode.getParentNode());
                    }

                } else {
                    roundDTOList.addAll(getAllMatches(currentNode, null));
                }
            }
        }

        return roundDTOList;
    }

    GroupDTO nodeToGroupDTO(Node groupNode) {

        Long group_id = null;
        String name = null;
        String details = null;
        String winner = null;
        String last_updated = null;

        for (int i = 0; i < groupNode.getAttributes().getLength(); i++) {
            Node atrNode = groupNode.getAttributes().item(i);
            switch (atrNode.getNodeName()) {

                case "group_id":
                    if (NumberUtil.isValidLong(groupNode.getAttributes().item(i).getNodeValue())) {
                        group_id = Long.parseLong(groupNode.getAttributes().item(i).getNodeValue());
                    }
                    break;
                case "name":
                    name = groupNode.getAttributes().item(i).getNodeValue();
                    break;
                case "details":
                    details = groupNode.getAttributes().item(i).getNodeValue();
                    break;
                case "winner":
                    winner = groupNode.getAttributes().item(i).getNodeValue();
                    break;
                case "last_updated":
                    last_updated = groupNode.getAttributes().item(i).getNodeValue();
                    break;
            }
        }


        return new GroupDTO(group_id, name, details, winner, last_updated);
    }

    AggrDTO nodeToAggrDTO(Node aggrNode) {


        String winner = null;
        Long winner_team_id = null;
        Long ow_winner_team_id = null;

        for (int i = 0; i < aggrNode.getAttributes().getLength(); i++) {

            Node atrNode = aggrNode.getAttributes().item(i);

            switch (atrNode.getNodeName()) {
                case "winner":
                    winner = aggrNode.getAttributes().item(i).getNodeValue();
                    break;

                case "winner_team_id":
                    if (NumberUtil.isValidLong(aggrNode.getAttributes().item(i).getNodeValue())) {
                        winner_team_id = Long.parseLong(aggrNode.getAttributes().item(i).getNodeValue());
                    }
                    break;

                case "ow_winner_team_id":
                    if (NumberUtil.isValidLong(aggrNode.getAttributes().item(i).getNodeValue())) {
                        ow_winner_team_id = Long.parseLong(aggrNode.getAttributes().item(i).getNodeValue());
                    }
                    break;
            }
        }

        return new AggrDTO(winner, winner_team_id, ow_winner_team_id);
    }

    RoundDTO nodeToRoundDTO(Node roundNode) {

        Long round_id = null;
        String name = null;
        String start_date = null;
        String end_date = null;
        String type = null;
        Long groups = null;
        String has_outgroup_matches = null;

        String last_updated = null;

        for (int i = 0; i < roundNode.getAttributes().getLength(); i++) {

            Node atrNode = roundNode.getAttributes().item(i);

            switch (atrNode.getNodeName()) {

                case "round_id":
                    if (NumberUtil.isValidLong(roundNode.getAttributes().item(i).getNodeValue())) {
                        round_id = Long.parseLong(roundNode.getAttributes().item(i).getNodeValue());
                    }
                    break;
                case "name":
                    name = roundNode.getAttributes().item(i).getNodeValue();
                    break;
                case "start_date":
                    start_date = roundNode.getAttributes().item(i).getNodeValue();
                    break;
                case "end_date":
                    end_date = roundNode.getAttributes().item(i).getNodeValue();
                    break;
                case "type":
                    type = roundNode.getAttributes().item(i).getNodeValue();
                    break;
                case "groups":
                    if (NumberUtil.isValidLong(roundNode.getAttributes().item(i).getNodeValue())) {
                        groups = Long.parseLong(roundNode.getAttributes().item(i).getNodeValue());
                    }
                    break;
                case "has_outgroup_matches":
                    has_outgroup_matches = roundNode.getAttributes().item(i).getNodeValue();
                    break;
                case "last_updated":
                    last_updated = roundNode.getAttributes().item(i).getNodeValue();
                    break;
            }
        }


        return new RoundDTO(round_id, name, start_date, end_date, type, groups, has_outgroup_matches, last_updated);

    }

    MatchDTO nodeToMatchDTO(Node matchNode) {


        Long match_id = null;
        Long ow_match_id = null;

        String date_utc = null;
        String time_utc = null;

        Long team_A_id = null;
        Long ow_team_A_id = null;
        String team_A_name = null;

        Long team_B_id = null;
        Long ow_team_B_id = null;
        String team_B_name = null;

        String status = null;
        Long gameweek = null;
        String winner = null;

        Long fs_A = null;
        Long fs_B = null;

        Long hts_A = null;
        Long hts_B = null;
        Long ets_A = null;
        Long ets_B = null;
        Long ps_A = null;
        Long ps_B = null;

        String last_updated = null;

//        Long minute = null;
//        Long minute_extra = null;
//        String match_period = null;


        for (int i = 0; i < matchNode.getAttributes().getLength(); i++) {

            Node atrNode = matchNode.getAttributes().item(i);

            switch (atrNode.getNodeName()) {

                case "match_id":
                    if (NumberUtil.isValidLong(matchNode.getAttributes().item(i).getNodeValue())) {
                        match_id = Long.parseLong(matchNode.getAttributes().item(i).getNodeValue());
                    }
                    break;
                case "ow_match_id":
                    if (NumberUtil.isValidLong(matchNode.getAttributes().item(i).getNodeValue())) {
                        ow_match_id = Long.parseLong(matchNode.getAttributes().item(i).getNodeValue());
                    }
                    break;
                case "date_utc":
                    date_utc = matchNode.getAttributes().item(i).getNodeValue();
                    break;
                case "time_utc":
                    time_utc = matchNode.getAttributes().item(i).getNodeValue();
                    break;

                case "team_A_id":
                    if (NumberUtil.isValidLong(matchNode.getAttributes().item(i).getNodeValue())) {
                        team_A_id = Long.parseLong(matchNode.getAttributes().item(i).getNodeValue());
                    }
                    break;
                case "ow_team_A_id":
                    if (NumberUtil.isValidLong(matchNode.getAttributes().item(i).getNodeValue())) {
                        ow_team_A_id = Long.parseLong(matchNode.getAttributes().item(i).getNodeValue());
                    }
                    break;
                case "team_A_name":
                    team_A_name = matchNode.getAttributes().item(i).getNodeValue();
                    break;

                case "team_B_id":
                    if (NumberUtil.isValidLong(matchNode.getAttributes().item(i).getNodeValue())) {
                        team_B_id = Long.parseLong(matchNode.getAttributes().item(i).getNodeValue());
                    }
                    break;
                case "ow_team_B_id":
                    if (NumberUtil.isValidLong(matchNode.getAttributes().item(i).getNodeValue())) {
                        ow_team_B_id = Long.parseLong(matchNode.getAttributes().item(i).getNodeValue());
                    }
                    break;
                case "team_B_name":
                    team_B_name = matchNode.getAttributes().item(i).getNodeValue();
                    break;

                case "status":
                    status = matchNode.getAttributes().item(i).getNodeValue();
                    break;
                case "gameweek":
                    if (NumberUtil.isValidLong(matchNode.getAttributes().item(i).getNodeValue())) {
                        gameweek = Long.parseLong(matchNode.getAttributes().item(i).getNodeValue());
                    }
                    break;
                case "winner":
                    winner = matchNode.getAttributes().item(i).getNodeValue();
                    break;
                case "fs_A":
                    if (NumberUtil.isValidLong(matchNode.getAttributes().item(i).getNodeValue())) {
                        fs_A = Long.parseLong(matchNode.getAttributes().item(i).getNodeValue());
                    }
                    break;
                case "fs_B":
                    if (NumberUtil.isValidLong(matchNode.getAttributes().item(i).getNodeValue())) {
                        fs_B = Long.parseLong(matchNode.getAttributes().item(i).getNodeValue());
                    }
                    break;
                case "hts_A":
                    if (NumberUtil.isValidLong(matchNode.getAttributes().item(i).getNodeValue())) {
                        hts_A = Long.parseLong(matchNode.getAttributes().item(i).getNodeValue());
                    }
                    break;
                case "hts_B":
                    if (NumberUtil.isValidLong(matchNode.getAttributes().item(i).getNodeValue())) {
                        hts_B = Long.parseLong(matchNode.getAttributes().item(i).getNodeValue());
                    }
                    break;
                case "ets_A":
                    if (NumberUtil.isValidLong(matchNode.getAttributes().item(i).getNodeValue())) {
                        ets_A = Long.parseLong(matchNode.getAttributes().item(i).getNodeValue());
                    }
                    break;
                case "ets_B":
                    if (NumberUtil.isValidLong(matchNode.getAttributes().item(i).getNodeValue())) {
                        ets_B = Long.parseLong(matchNode.getAttributes().item(i).getNodeValue());
                    }
                    break;
                case "ps_A":
                    if (NumberUtil.isValidLong(matchNode.getAttributes().item(i).getNodeValue())) {
                        ps_A = Long.parseLong(matchNode.getAttributes().item(i).getNodeValue());
                    }
                    break;
                case "ps_B":
                    if (NumberUtil.isValidLong(matchNode.getAttributes().item(i).getNodeValue())) {
                        ps_B = Long.parseLong(matchNode.getAttributes().item(i).getNodeValue());
                    }
                    break;
                case "last_updated":
                    last_updated = matchNode.getAttributes().item(i).getNodeValue();
                    break;
//                case "minute":
//                    if (NumberUtil.isValidLong(matchNode.getAttributes().item(i).getNodeValue())) {
//                        minute = Long.parseLong(matchNode.getAttributes().item(i).getNodeValue());
//                    }
//                    break;
//                case "minute_extra":
//                    if (NumberUtil.isValidLong(matchNode.getAttributes().item(i).getNodeValue())) {
//                        minute_extra = Long.parseLong(matchNode.getAttributes().item(i).getNodeValue());
//                    }
//                    break;
//                case "match_period":
//                    match_period = matchNode.getAttributes().item(i).getNodeValue();
//                    break;

            }
        }

        return new MatchDTO(match_id, ow_match_id, date_utc, time_utc, team_A_id, ow_team_A_id, team_A_name, team_B_id, ow_team_B_id, team_B_name, status, gameweek, winner, fs_A, fs_B, hts_A, hts_B, ets_A, ets_B, ps_A, ps_B, last_updated);
    }


    @Override
    public String getValidationSchema() {
        return "/xsd/get_matches.xsd";
    }


}
