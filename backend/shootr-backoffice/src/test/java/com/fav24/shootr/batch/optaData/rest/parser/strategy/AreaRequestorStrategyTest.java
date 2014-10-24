package com.fav24.shootr.batch.optaData.rest.parser.strategy;

import com.fav24.shootr.batch.optaData.rest.DTO.AreaDTO;
import com.fav24.shootr.batch.optaData.rest.parser.strategy.AreaRequestorStrategy;
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
import static org.junit.Assert.assertTrue;


public class AreaRequestorStrategyTest {


    AreaRequestorStrategy areaRequestorStrategy = null;
    DocumentBuilderFactory xmlFactory = null;
    DocumentBuilder builder = null;

    @Before
    public void setUp() throws Exception {
        areaRequestorStrategy = new AreaRequestorStrategy();
        xmlFactory = DocumentBuilderFactory.newInstance();
        builder = xmlFactory.newDocumentBuilder();
    }


    @Test
    public void URLGeneratedOK() {

        areaRequestorStrategy.userName = "worldsl";
        areaRequestorStrategy.token = "c7c0ace395d80182db07ae2c30f034";
        areaRequestorStrategy.urlBase = "http://api.core.optasports.com";

        String response = "http://api.core.optasports.com/soccer/get_areas?username=worldsl&authkey=c7c0ace395d80182db07ae2c30f034";

        assertEquals(response, areaRequestorStrategy.generateRequestURL(Requestor.LanguageRequest.Spanish));

    }


    @Test
    public void singleNodeRequestIsParseOK() throws ParserConfigurationException, IOException, SAXException {

        Document document = givenAreaXML("optaXML/get_areasSingle.xml");

        List<AreaDTO> areaDTOList = areaRequestorStrategy.getAllArea(document);

        assertEquals(1, areaDTOList.size());

        checkAreaNode(areaDTOList, 0, "World", 1, "");
    }

    @Test
    public void tripleNodeRequestIsParseOK() throws ParserConfigurationException, IOException, SAXException {

        Document document = givenAreaXML("optaXML/get_areasThree.xml");

        List<AreaDTO> areaDTOList = areaRequestorStrategy.getAllArea(document);

        assertEquals(3, areaDTOList.size());

        checkAreaNode(areaDTOList, 0, "Afghanistan", 8, "AFG");
        checkAreaNode(areaDTOList, 1, "Asia", 2, "");
        checkAreaNode(areaDTOList, 2, "World", 1, "");

    }

    private void checkAreaNode(List<AreaDTO> areaDTOList, int position, String name, long area_id, String countryCode) {

        assertEquals(name, areaDTOList.get(position).getName());
        assertEquals(new Long(area_id), areaDTOList.get(position).getArea_id());
        assertEquals(countryCode, areaDTOList.get(position).getCountrycode());
    }

    @Test
    public void completeNodeRequestIsParseOK() throws ParserConfigurationException, IOException, SAXException {

        Document document = givenAreaXML("optaXML/get_areasComplete.xml");

        List<AreaDTO> areaDTOList = areaRequestorStrategy.getAllArea(document);

        assertEquals(244, areaDTOList.size());

    }


    @Test
    public void NoneNodeRequestIsParseOK() throws ParserConfigurationException, IOException, SAXException {

        Document document = givenAreaXML("optaXML/get_areasNone.xml");

        List<AreaDTO> areaDTOList = areaRequestorStrategy.getAllArea(document);

        assertTrue(areaDTOList.isEmpty());
    }


    private Document givenAreaXML(String xmlFileName) throws SAXException, IOException {
        InputStream xmlResource = Thread.currentThread().getContextClassLoader().getResourceAsStream(xmlFileName);
        return builder.parse(xmlResource);
    }


}