package com.fav24.shootr.batch.rest.parser;

import com.fav24.shootr.batch.network.NetworkFactory;
import com.fav24.shootr.batch.optaData.rest.DTO.AreaDTO;
import com.fav24.shootr.batch.rest.parser.Requestor;
import com.fav24.shootr.batch.rest.parser.RequestorImpl;
import com.fav24.shootr.batch.rest.parser.RequestorStrategy;

import org.junit.Test;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class RequestorImplTest {


    @Test
    public void whenRequestingWithoutIdsProcessIsOK() throws Exception {

        String url = "a Nice URL";
        List<AreaDTO> areas = new ArrayList<>();
        areas.add(new AreaDTO(1l, "countryCode", "name"));

        NetworkFactory mockedNetworkFactory = mock(NetworkFactory.class);
        InputStream inputGetTeamTest = Thread.currentThread().getContextClassLoader().getResourceAsStream("optaXML/get_areasComplete.xml");
        when(mockedNetworkFactory.getInputStreamFromUrl(url)).thenReturn(inputGetTeamTest);

        RequestorStrategy mockedRequestorStrategy = mock(RequestorStrategy.class);
        when(mockedRequestorStrategy.generateRequestURL(anyObject())).thenReturn(url);
        when(mockedRequestorStrategy.mapResponse(anyObject())).thenReturn(areas);
        when(mockedRequestorStrategy.getValidationSchema()).thenReturn("/xsd/get_areas.xsd");


        Requestor requestor = new RequestorImpl(mockedRequestorStrategy, mockedNetworkFactory);

        List<AreaDTO> returnedAreas = requestor.doRequest(Requestor.LanguageRequest.Spanish);

        assertThat(returnedAreas).containsAll(areas).hasSize(1);

    }

    @Test(expected = Exception.class)
    public void throwsExceptionWhenXMLisIncorrect() throws Exception {

        String url = "a Nice URL";
        List<AreaDTO> areas = new ArrayList<>();
        areas.add(new AreaDTO(1l, "countryCode", "name"));

        NetworkFactory mockedNetworkFactory = mock(NetworkFactory.class);
        InputStream inputGetTeamTest = Thread.currentThread().getContextClassLoader().getResourceAsStream("optaXML/get_areasIncorrect.xml");
        when(mockedNetworkFactory.getInputStreamFromUrl(url)).thenReturn(inputGetTeamTest);

        RequestorStrategy mockedRequestorStrategy = mock(RequestorStrategy.class);
        when(mockedRequestorStrategy.generateRequestURL(anyObject())).thenReturn(url);
        when(mockedRequestorStrategy.mapResponse(anyObject())).thenReturn(areas);
        when(mockedRequestorStrategy.getValidationSchema()).thenReturn("/xsd/get_areas.xsd");

        Requestor requestor = new RequestorImpl(mockedRequestorStrategy, mockedNetworkFactory);

        @SuppressWarnings("unused")
		List<AreaDTO> returnedAreas = requestor.doRequest(Requestor.LanguageRequest.Spanish);
    }


}