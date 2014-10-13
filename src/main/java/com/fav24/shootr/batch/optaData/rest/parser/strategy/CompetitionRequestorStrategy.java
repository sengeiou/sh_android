package com.fav24.shootr.batch.optaData.rest.parser.strategy;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.fav24.shootr.batch.optaData.rest.DTO.CompetitionDTO;
import com.fav24.shootr.batch.optaData.rest.DTO.SeasonDTO;
import com.fav24.shootr.batch.rest.parser.Requestor;
import com.fav24.shootr.util.NumberUtil;

/**
 * Este Requestor se descarga a la vez las competiciones y las temporadas de cada una de las temporadas
 */
@Service
public class CompetitionRequestorStrategy extends BaseRequestorStrategy {


    @Override
    public String generateRequestURL(Requestor.LanguageRequest languageRequest, long... id) {
        return urlBase + "/soccer/get_seasons?authorized=yes"+ getAuthUrl();
    }

    @SuppressWarnings("unchecked")
	@Override
    public List<CompetitionDTO> mapResponse(Document document) throws Exception {
        return getAllCompetitions(document.getDocumentElement(), null);
    }

    /**
     * A partir de un Nodo recupera todas las competiciones y Temporadas ( seasons ) que cuelguen de él
     *
     * @param node
     * @param parentCompetitionDTO
     * @return
     */
    List<CompetitionDTO> getAllCompetitions(Node node, CompetitionDTO parentCompetitionDTO) {

        List<CompetitionDTO> competitionDTOList = new ArrayList<CompetitionDTO>();

        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node currentNode = nodeList.item(i);
            if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                //calls this method for all the children which is Element
                String nodeName = currentNode.getNodeName();

                if ("competition".equals(nodeName)) {
                    CompetitionDTO competitionDTO = nodeToCompetitionDTO(currentNode);
                    competitionDTOList.addAll(getAllCompetitions(currentNode, competitionDTO));
                    competitionDTOList.add(competitionDTO);
                } else if ("season".equals(nodeName)) {
                    SeasonDTO seasonDTO = nodeToSeasonDTO(currentNode);
                    parentCompetitionDTO.getSeasons().add(seasonDTO);
                } else {
                    competitionDTOList.addAll(getAllCompetitions(currentNode, null));
                }
            }
        }

        return competitionDTOList;
    }

    CompetitionDTO nodeToCompetitionDTO(Node competitionNode) {

        Long competition_id = null;
        Long ow_competition_id = null;

        String name = null;
        String teamtype = null;

        String soccertype = null;
        String format = null;

        Long display_order = null;
        String type = null;

        Long area_id = null;
        String area_name = null;

        String last_updated = null;

        for (int i = 0; i < competitionNode.getAttributes().getLength(); i++) {

            Node atrNode = competitionNode.getAttributes().item(i);

            switch (atrNode.getNodeName()) {

                case "competition_id":
                    if (NumberUtil.isValidLong(competitionNode.getAttributes().item(i).getNodeValue())) {
                        competition_id = Long.parseLong(competitionNode.getAttributes().item(i).getNodeValue());
                    }
                    break;

                case "ow_competition_id":
                    if (NumberUtil.isValidLong(competitionNode.getAttributes().item(i).getNodeValue())) {
                        ow_competition_id = Long.parseLong(competitionNode.getAttributes().item(i).getNodeValue());
                    }
                    break;

                case "name":
                    name = competitionNode.getAttributes().item(i).getNodeValue();
                    break;
                case "teamtype":
                    teamtype = competitionNode.getAttributes().item(i).getNodeValue();
                    break;
                case "soccertype":
                    soccertype = competitionNode.getAttributes().item(i).getNodeValue();
                    break;
                case "format":
                    format = competitionNode.getAttributes().item(i).getNodeValue();
                    break;
                case "display_order":
                    if (NumberUtil.isValidLong(competitionNode.getAttributes().item(i).getNodeValue())) {
                        display_order = Long.parseLong(competitionNode.getAttributes().item(i).getNodeValue());
                    }
                    break;
                case "type":
                    type = competitionNode.getAttributes().item(i).getNodeValue();
                    break;
                case "area_id":
                    if (NumberUtil.isValidLong(competitionNode.getAttributes().item(i).getNodeValue())) {
                        area_id = Long.parseLong(competitionNode.getAttributes().item(i).getNodeValue());
                    }
                    break;
                case "area_name":
                    area_name = competitionNode.getAttributes().item(i).getNodeValue();
                    break;
                case "last_updated":
                    last_updated = competitionNode.getAttributes().item(i).getNodeValue();
                    break;

            }
        }

        return new CompetitionDTO(
                competition_id,
                ow_competition_id,
                name,
                teamtype,
                soccertype,
                format,
                display_order,
                type,
                area_id,
                area_name,
                last_updated
        );
    }

    SeasonDTO nodeToSeasonDTO(Node seasonDTO) {


        Long season_id = null;
        String name = null;

        String start_date = null;
        String end_date = null;

        Long service_level = null;
        String last_updated = null;


        for (int i = 0; i < seasonDTO.getAttributes().getLength(); i++) {

            Node atrNode = seasonDTO.getAttributes().item(i);

            switch (atrNode.getNodeName()) {

                case "season_id":
                    if (NumberUtil.isValidLong(seasonDTO.getAttributes().item(i).getNodeValue())) {
                        season_id = Long.parseLong(seasonDTO.getAttributes().item(i).getNodeValue());
                    }
                    break;

                case "name":
                    name = seasonDTO.getAttributes().item(i).getNodeValue();
                    break;

                case "start_date":
                    start_date = seasonDTO.getAttributes().item(i).getNodeValue();
                    break;

                case "end_date":
                    end_date = seasonDTO.getAttributes().item(i).getNodeValue();
                    break;

                case "service_level":
                    if (NumberUtil.isValidLong(seasonDTO.getAttributes().item(i).getNodeValue())) {
                        service_level = Long.parseLong(seasonDTO.getAttributes().item(i).getNodeValue());
                    }
                    break;

                case "last_updated":
                    last_updated = seasonDTO.getAttributes().item(i).getNodeValue();
                    break;

            }
        }

        return new SeasonDTO(
                season_id,
                name,
                start_date,
                end_date,
                service_level,
                last_updated
        );

    }

    @Override
    public String getValidationSchema() {
        return "/xsd/get_seasons.xsd";
    }

}
