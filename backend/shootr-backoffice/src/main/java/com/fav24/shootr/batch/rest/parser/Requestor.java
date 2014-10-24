package com.fav24.shootr.batch.rest.parser;


import com.fav24.shootr.batch.exception.OptaCommunicationException;
import com.fav24.shootr.batch.exception.OptaParsingException;

public interface Requestor {


    public static final String PARSE_ERROR = "GS001";
    public static final String PARSE_ERROR_MESSAGE = "Parse for entity <%s>, was finished with errors: %s";


    public static final String CONNECTION_ERROR = "GS002";
    public static final String CONNECTION_ERROR_MESSAGE = "There was a network error when making the request";

    public static final String ERROR_UNKNOWN = "GS999";
    public static final String ERROR_UNKNOWN_MESSAGE = "Error desconocido en la petición. Mensaje interno: <%s>";


    public enum LanguageRequest {

        Arabic("ar"),
        Portuguese_Brazilian("br"),
        Portuguese("pt"),
        Chinese("cn"),
        Chinese_Traditional("hk"),
        Chinese_Taiwanese("tw"),
        German("de"),
        Spanish("es"),
        Spanish_Mexican("mx"),
        Spanish_Argentinian("ea"),
        Spanish_Latin_American("el"),
        English("en"),
        English_American("us"),
        French("fr"),
        Italian("it"),
        Dutch("nl"),
        Polish("pl"),
        Hungarian("hu"),
        Hebrew("he"),
        Turkisch("tr"),
        Japenese("jp"),
        Suomi("fi"),
        Shqiptar("ab"),
        Bulgarian("bg"),
        Dansk("dk"),
        Greek("gr"),
        Korean("kr"),
        Norsk("no"),
        Kurdish("ku"),
        Russian("ru"),
        Swahili("sw"),
        Hausa("ha"),
        Bahasa_Indonesia("id"),
        Swedish("se"),
        Thai("th"),
        Romanian("ro");

        private final String code;

        LanguageRequest(String code) {
            this.code = code;
        }

        public String getCode() {
            return this.code;
        }


    }

    public <T> T doRequest(RequestorImpl.LanguageRequest languageRequest, long... id) throws OptaCommunicationException, OptaParsingException;
}
