package com.fav24.shootr.batch.optaData.rest.parser.strategy;

import com.fav24.shootr.batch.optaData.rest.DTO.AreaDTO;
import com.fav24.shootr.batch.rest.parser.Requestor;
import com.fav24.shootr.util.NumberUtil;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

@Service
public class AreaRequestorStrategy extends BaseRequestorStrategy {


    @Override
    public String generateRequestURL(Requestor.LanguageRequest languageRequest, long... id) {
        return urlBase + "/soccer/get_areas?username=" + this.userName + "&authkey=" + this.token;
    }

    @SuppressWarnings("unchecked")
	@Override
    public List<AreaDTO> mapResponse(Document document) throws Exception {
        return getAllArea(document.getDocumentElement());
    }

    List<AreaDTO> getAllArea(Node node) {

        List<AreaDTO> areaDTOList = new ArrayList<AreaDTO>();

        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node currentNode = nodeList.item(i);
            if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                //calls this method for all the children which is Element

                String nodeName = currentNode.getNodeName();
                areaDTOList.addAll(getAllArea(currentNode));

                if ("area".equals(nodeName)) {
                    AreaDTO newAreaDTO = nodeToAreaDTO(currentNode);
                    areaDTOList.add(newAreaDTO);
                }
            }
        }

        return areaDTOList;
    }

    AreaDTO nodeToAreaDTO(Node areaNode) {

        Long area_id = null;
        String countryCode = null;
        String name = null;

        for (int i = 0; i < areaNode.getAttributes().getLength(); i++) {

            Node atrNode = areaNode.getAttributes().item(i);

            switch (atrNode.getNodeName()) {
                case "area_id":
                    if (NumberUtil.isValidLong(atrNode.getNodeValue())) {
                        area_id = Long.parseLong(atrNode.getNodeValue());
                    }
                    break;
                case "countrycode":
                    countryCode = atrNode.getNodeValue();
                    break;
                case "name":
                    name = atrNode.getNodeValue();
                    break;

            }
        }

        return new AreaDTO(area_id, countryCode, name);
    }

    @Override
    public String getValidationSchema() {
        return "/xsd/get_areas.xsd";
    }


}
