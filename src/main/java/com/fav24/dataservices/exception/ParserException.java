package com.fav24.dataservices.exception;


/**
 * Excepción generada por un mecanismo de parseo.
 */
public class ParserException extends ServerException 
{
	private static final long serialVersionUID = 8282327013867976657L;

	private int line;
	private int column;
	private String message;
	private String cause;


	/**
	 * Constructor.
	 * 
	 * @param exception Excepción base.
	 */
	public ParserException(ServerException exception) 
	{
		this(exception.getErrorCode(), -1, -1, exception.getMessage(), (String)null);
	}

	/**
	 * Constructor.
	 * 
	 * @param errorCode Código de error de la excepción.
	 */
	public ParserException(String errorCode) 
	{
		this(errorCode, -1, -1, null, (String)null);
	}

	/**
	 * Constructor.
	 * 
	 * @param errorCode Código de error de la excepción.
	 * @param line Número de línea en el que se produjo la excepción.
	 */
	public ParserException(String errorCode, int line) 
	{
		this(errorCode, line, -1, null, (String)null);
	}

	/**
	 * Constructor.
	 * 
	 * @param errorCode Código de error de la excepción.
	 * @param line Número de línea en el que se produjo la excepción.
	 * @param column Número de columna en el que se produjo la excepción.
	 */
	public ParserException(String errorCode, int line, int column) 
	{
		this(errorCode, line, column, null, (String)null);
	}

	/**
	 * Constructor.
	 * 
	 * @param errorCode Código de error de la excepción.
	 * @param message Mensaje acerca del contexto de ajuda a la deducción de la causa.
	 * @param cause Posible explicación de la causa (Excepción origen de esta).
	 */
	public ParserException(String errorCode, String message, Throwable cause) 
	{
		this(errorCode, -1, -1, message, cause.getMessage());
	}

	/**
	 * Constructor.
	 * 
	 * @param errorCode Código de error de la excepción.
	 * @param line Número de línea en el que se produjo la excepción.
	 * @param message Mensaje acerca del contexto de ajuda a la deducción de la causa.
	 * @param cause Posible explicación de la causa (Excepción origen de esta).
	 */
	public ParserException(String errorCode, int line, String message, Throwable cause) 
	{
		this(errorCode, line, -1, message, cause.getMessage());
	}

	/**
	 * Constructor.
	 * 
	 * @param errorCode Código de error de la excepción.
	 * @param line Número de línea en el que se produjo la excepción.
	 * @param column Número de columna en el que se produjo la excepción.
	 * @param message Mensaje acerca del contexto de ajuda a la deducción de la causa.
	 * @param cause Posible explicación de la causa (Excepción origen de esta).
	 */
	public ParserException(String errorCode, int line, int column, String message, Throwable cause) 
	{
		this(errorCode, line, column, message, cause.getMessage());
	}

	/**
	 * Constructor.
	 * 
	 * @param errorCode Código de error de la excepción.
	 * @param message Mensaje acerca del contexto de ajuda a la deducción de la causa.
	 * @param cause Posible explicación de la causa.
	 */
	public ParserException(String errorCode, String message, String cause) 
	{
		this(errorCode, -1, -1, message, cause);
	}

	/**
	 * Constructor.
	 * 
	 * @param errorCode Código de error de la excepción.
	 * @param line Número de línea en el que se produjo la excepción.
	 * @param message Mensaje acerca del contexto de ajuda a la deducción de la causa.
	 * @param cause Posible explicación de la causa.
	 */
	public ParserException(String errorCode, int line, String message, String cause) 
	{
		this(errorCode, line, -1, message, cause);
	}

	/**
	 * Constructor.
	 * 
	 * @param errorCode Código de error de la excepción.
	 * @param message Mensaje acerca del contexto de ajuda a la deducción de la causa.
	 */
	public ParserException(String errorCode, String message) 
	{
		this(errorCode, -1, -1, message, (String)null);
	}

	/**
	 * Constructor.
	 * 
	 * @param errorCode Código de error de la excepción.
	 * @param line Número de línea en el que se produjo la excepción.
	 * @param message Mensaje acerca del contexto de ajuda a la deducción de la causa.
	 */
	public ParserException(String errorCode, int line, String message) 
	{
		this(errorCode, line, -1, message, (String)null);
	}

	/**
	 * Constructor.
	 * 
	 * @param errorCode Código de error de la excepción.
	 * @param line Número de línea en el que se produjo la excepción.
	 * @param column Número de columna en el que se produjo la excepción.
	 * @param message Mensaje acerca del contexto de ajuda a la deducción de la causa.
	 * @param cause Posible explicación de la causa.
	 */
	public ParserException(String errorCode, int line, int column, String message, String cause) 
	{
		super(errorCode, constructMessage(line, column, message, cause));

		this.line = line;
		this.column = column;
		this.message = message;
		this.cause = cause;
	}

	/**
	 * Construye el mensaje de excepción interno a partir de los parámetros suministrados.
	 * 
	 * @param line Número de línea en el que se produjo la excepción.
	 * @param column Número de columna en el que se produjo la excepción.
	 * @param message Mensaje acerca del contexto de ajuda a la deducción de la causa.
	 * @param cause Posible explicación de la causa.
	 * 
	 * @return el mensaje de excepción interno a partir de los parámetros suministrados. 
	 */
	private static String constructMessage(int line, int column, String message, String cause)
	{
		StringBuilder outMessage = new StringBuilder();

		if (line >= 0)
		{
			outMessage.append("Error de parseo en la línea ").append(line);
			if (column >= 0)
				outMessage.append(" columna ").append(column);
		}
		else
			outMessage.append("Error de parseo");

		outMessage.append('.');

		if (message != null)
		{
			outMessage.append("\nConclusión del parser:\n");
			outMessage.append(message);
		}

		if (cause != null)
		{
			outMessage.append("\nPosible causa:\n");
			outMessage.append(cause);
		}

		return outMessage.toString();
	}

	/**
	 * Retorna el número de línea en la que ocurrió el error de parseo.
	 * 
	 * @return el número de línea en la que ocurrió el error de parseo.
	 */
	public int getLine() 
	{
		return line;
	}

	/**
	 * Retorna el número de columna en la que ocurrió el error de parseo.
	 * 
	 * @return el número de columna en la que ocurrió el error de parseo.
	 */
	public int getColumn() 
	{
		return column;
	}

	/**
	 * Retorna el mensage extra proporcionado por el contexto.
	 * 
	 * @return el mensage extra proporcionado por el contexto.
	 */
	public String getExtraMessage()
	{
		return message;
	}

	/**
	 * Retorna la posible causa proporcionada por el contexto.
	 * 
	 * @return el posible causa proporcionada por el contexto.
	 */
	public String getCauseMessage()
	{
		return cause;
	}
}
