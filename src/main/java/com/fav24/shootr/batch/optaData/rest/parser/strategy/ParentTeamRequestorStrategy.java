package com.fav24.shootr.batch.optaData.rest.parser.strategy;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.fav24.shootr.batch.optaData.rest.DTO.TeamDTO;
import com.fav24.shootr.batch.rest.parser.Requestor;
import com.fav24.shootr.util.NumberUtil;

public abstract class ParentTeamRequestorStrategy extends BaseRequestorStrategy {

	public abstract String generateRequestURL(Requestor.LanguageRequest languageRequest, long... id);
	
    @SuppressWarnings("unchecked")
	@Override
    public List<TeamDTO> mapResponse(Document document) throws Exception {
        return getAllTeams(document.getDocumentElement());
    }

    List<TeamDTO> getAllTeams(Node node) {

        List<TeamDTO> teamDTOList = new ArrayList<TeamDTO>();
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node currentNode = nodeList.item(i);
            if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                //calls this method for all the children which is Element

                String nodeName = currentNode.getNodeName();
                teamDTOList.addAll(getAllTeams(currentNode));

                if ("team".equals(nodeName)) {
                    TeamDTO teamDTO = nodeToTeamDTO(currentNode);
                    teamDTOList.add(teamDTO);
                }
            }
        }

        return teamDTOList;
    }


    TeamDTO nodeToTeamDTO(Node teamNode) {


        Long team_id = null;
        Long ow_team_id = null;
        String type = null;
        String soccertype = null;
        String teamtype = null;
        String club_name = null;

        String official_name = null;
        String short_name = null;
        String tla_name = null;

        String last_updated = null;

        for (int i = 0; i < teamNode.getAttributes().getLength(); i++) {

            Node atrNode = teamNode.getAttributes().item(i);

            switch (atrNode.getNodeName()) {

                case "club_name":
                    club_name = teamNode.getAttributes().item(i).getNodeValue();
                    break;
                case "official_name":
                    official_name = teamNode.getAttributes().item(i).getNodeValue();
                    break;
                case "short_name":
                    short_name = teamNode.getAttributes().item(i).getNodeValue();
                    break;
                case "tla_name":
                    tla_name = teamNode.getAttributes().item(i).getNodeValue();
                    break;
                case "teamtype":
                    teamtype = teamNode.getAttributes().item(i).getNodeValue();
                    break;
                case "team_id":
                    if (NumberUtil.isValidLong(teamNode.getAttributes().item(i).getNodeValue())) {
                        team_id = Long.parseLong(teamNode.getAttributes().item(i).getNodeValue());
                    }
                    break;
                case "ow_team_id":
                    if (NumberUtil.isValidLong(teamNode.getAttributes().item(i).getNodeValue())) {
                        ow_team_id = Long.parseLong(teamNode.getAttributes().item(i).getNodeValue());
                    }
                    break;
                case "type":
                    type = teamNode.getAttributes().item(i).getNodeValue();
                    break;
                case "soccertype":
                    soccertype = teamNode.getAttributes().item(i).getNodeValue();
                    break;
                case "last_updated":
                    last_updated = teamNode.getAttributes().item(i).getNodeValue();
                    break;
            }
        }

        return new TeamDTO(
                team_id,
                ow_team_id,
                type,
                soccertype,
                teamtype,
                club_name,
                official_name,
                short_name,
                tla_name,
                last_updated
        );
    }


    @Override
    public String getValidationSchema() {
        return "/xsd/get_teams.xsd";
    }


}
