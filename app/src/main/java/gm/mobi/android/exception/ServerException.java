package gm.mobi.android.exception;

import java.io.IOException;


/**
 * Excepción general de servidor.
 */
public class ServerException extends IOException {

    private static final long serialVersionUID = 7715129933110523169L;

    /**
     * OK - OK
     */
    public static final String OK = "OK";
    /**
     * HK001 - ERROR_HOOK_SOURCE_ACCESS.
     */
    public static final String HK001 = "HK001";
    /**
     * HK002 - ERROR_HOOK_COMPILATION_ERRORS_FOUND
     */
    public static final String HK002 = "HK002";
    /**
     * HK003 - ERROR_HOOK_CLASS_NOT_FOUND
     */
    public static final String HK003 = "HK003";
    /**
     * HK004 - ERROR_HOOK_CLASS_INSTANCE
     */
    public static final String HK004 = "HK004";
    /**
     * HK005 - ERROR_HOOK_DIAGNOSTIC
     */
    public static final String HK005 = "HK005";
    /**
     * HK006 - ERROR_INVALID_HOOK_FILE_URL
     */
    public static final String HK006 = "HK006";
    /**
     * HK007 - ERROR_NO_DEFAULT_HOOK_FILES_TO_LOAD
     */
    public static final String HK007 = "HK007";
    /**
     * HK008 - ERROR_INVALID_HOOK_CLASSPATH_URL
     */
    public static final String HK008 = "HK008";
    /**
     * HK009 - ERROR_INVALID_HOOK_DEPENDENCY_FILE_URL
     */
    public static final String HK009 = "HK009";
    /**
     * HK010 - ERROR_HOOK_CLASS_INSTANCE_DEPENDENCY_NOT_FOUND
     */
    public static final String HK010 = "HK010";

    /**
     * G001 - ERROR_OPERATION_NOT_AVAILABLE
     */
    public static final String G001 = "G001";
    /**
     * G002 - ERROR_START_TRANSACTION
     */
    public static final String G002 = "G002";
    /**
     * G003 - ERROR_END_TRANSACTION
     */
    public static final String G003 = "G003";
    /**
     * G004 - ERROR_MALFORMED_REQUEST
     */
    public static final String G004 = "G004";
    /**
     * G005 - ERROR_UNCOMPLETE_KEY_FILTER_REQUEST
     */
    public static final String G005 = "G005";
    /**
     * G006 - ERROR_INVALID_REQUEST_NO_KEY
     */
    public static final String G006 = "G006";
    /**
     * G007 - ERROR_INVALID_REQUEST_KEY
     */
    public static final String G007 = "G007";
    /**
     * G008 - ERROR_INVALID_REQUEST_NO_FILTER
     */
    public static final String G008 = "G008";
    /**
     * G009 - ERROR_INVALID_REQUEST_FILTER
     */
    public static final String G009 = "G009";
    /**
     * G010 - ERROR_INVALID_REQUEST_FILTER_ATTRIBUTE
     */
    public static final String G010 = "G010";
    /**
     * G011 - ERROR_INVALID_REQUEST_KEY_ATTRIBUTE"
     */
    public static final String G011 = "G011";
    /**
     * G012 - ERROR_OPERATION
     */
    public static final String G012 = "G012";
    /**
     * G013 - ERROR_OPERATION
     */
    public static final String G013 = "G013";
    /**
     * G014 - ERROR_CREATE_DUPLICATE_ROW
     */
    public static final String G014 = "G014";
    /**
     * G015 - ERROR_REFURBISHING_ROW
     */
    public static final String G015 = "G015";
    /**
     * G016 - ERROR_UPDATING_ROW
     */
    public static final String G016 = "G016";
    /**
     * G017 - ERROR_REFURBISHED_ROW_LOST
     */
    public static final String G017 = "G017";
    /**
     * G018 - ERROR_INVALID_UPDATE_REQUEST
     */
    public static final String G018 = "G018";
    /**
     * G019 - ERROR_UPDATE_ENTITY_LACKS_PRIMARY_KEY
     */
    public static final String G019 = "G019";
    /**
     * G020 - ERROR_INVALID_CREATEUPDATE_REQUEST
     */
    public static final String G020 = "G020";
    /**
     * G021 - ERROR_INVALID_UPDATECREATE_REQUEST
     */
    public static final String G021 = "G021";
    /**
     * G022 - ERROR_DUPLICATED_ROW
     */
    public static final String G022 = "G022";
    /**
     * G023 - ERROR_INVALID_READONLY_POLICY
     */
    public static final String G023 = "G023";
    /**
     * G024 - ERROR_HOOK_NOT_LOADED
     */
    public static final String G024 = "G024";
    /**
     * G025 - ERROR_HOOK_STOP_KO
     */
    public static final String G025 = "G025";
    /**
     * PS000 - ERROR_LOADING_POLICY_FILES
     */
    public static final String PS000 = "PS000";
    /**
     * PS001 - ERROR_INVALID_POLICY_FILE_URL
     */
    public static final String PS001 = "PS001";
    /**
     * PS002 - ERROR_NO_DEFAULT_POLICY_FILES_TO_LOAD
     */
    public static final String PS002 = "PS002";
    /**
     * PS003 - ERROR_NO_CURRENT_POLICY_DEFINED
     */
    public static final String PS003 = "PS003";
    /**
     * PS004 - ERROR_NO_CURRENT_POLICY_DEFINED_FOR_ENTITY
     */
    public static final String PS004 = "PS004";
    /**
     * PS005 - ERROR_ENTITY_ATTRIBUTES_NOT_ALLOWED
     */
    public static final String PS005 = "PS005";
    /**
     * V999 - HTTP_ERROR
     */
    public static final String V999 = "V999";


    private String errorCode;
    private String message;
    private String htmlExplanation;
    private int serverCode;


    public ServerException(int serverCode) {
        this.serverCode = serverCode;
    }

    /**
     * Constructor por defecto.
     */
    public ServerException() {
        this(null);
    }

    /**
     * Constructor
     *
     * @param errorCode Código de error inicial de la excepción.
     */
    public ServerException(String errorCode) {
        this.setErrorCode(errorCode);
        this.setMessage(null);
        this.setHtmlExplanation(null);
    }

    /**
     * Constructor
     *
     * @param errorCode Código de error inicial de la excepción.
     * @param message   Mensaje del la excepción.
     */
    public ServerException(String errorCode, String message) {
        this.setErrorCode(errorCode);
        this.setMessage(message);
        this.setHtmlExplanation(null);
    }

    /**
     * Constructor
     *
     * @param errorCode Código de error inicial de la excepción.
     * @param message   Mensaje del la excepción.
     * @param throwable Causa de esta excepción.
     */
    public ServerException(String errorCode, String message, Throwable throwable) {

        this.setErrorCode(errorCode);
        this.setMessage(message);
        this.setHtmlExplanation(null);
        this.setStackTrace(throwable.getStackTrace());
    }

    /**
     * Constructor
     *
     * @param errorCode       Código de error inicial de la excepción.
     * @param message         Mensaje del la excepción.
     * @param htmlExplanation Una explicación completa de la excepción en formato HTML.
     */
    public ServerException(String errorCode, String message, String htmlExplanation) {

        this.setErrorCode(errorCode);
        this.setMessage(message);
        this.setHtmlExplanation(htmlExplanation);
    }

    /**
     * Retorna el código de error asociado a la excepción.
     *
     * @return el código de error asociado a la excepción.
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Asigna el código de error asociado a la excepción.
     *
     * @param errorCode Código de error a asociar.
     */
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * Retorna el mensaje de error asociado a la excepción.
     *
     * @return el mensaje de error asociado a la excepción.
     */
    public String getMessage() {
        return message == null ? super.getMessage() : message;
    }

    /**
     * Asigna el mensaje de error asociado a la excepción.
     *
     * @param message Mensaje a asignar
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Retorna una explicación completa de la excepción en formato HTML.
     *
     * @return una explicación completa de la excepción en formato HTML.
     */
    public String getHtmlExplanation() {
        return htmlExplanation;
    }

    /**
     * Asigna una explicación completa de la excepción en formato HTML.
     *
     * @param htmlExplanation Explicación completa de la excepción en formato HTML.
     */
    public void setHtmlExplanation(String htmlExplanation) {
        this.htmlExplanation = htmlExplanation;
    }
}


