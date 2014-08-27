package gm.mobi.android.exception;

import java.io.IOException;


/**
 * Excepción general de servidor.
 */
public class ServerException extends IOException {

    private static final long serialVersionUID = 7715129933110523169L;

    private String errorCode;
    private String message;
    private String htmlExplanation;

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
     * @param message Mensaje del la excepción.
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
     * @param message Mensaje del la excepción.
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
     * @param errorCode Código de error inicial de la excepción.
     * @param message Mensaje del la excepción.
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
     *  @return el mensaje de error asociado a la excepción.
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
