package com.fav24.dataservices.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Map.Entry;

import com.fav24.dataservices.DataServicesContext;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.ConnectionPool;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;


/**
 * Clase de utilidades para el envío de peticiones http.
 */
public class FastHttpUtils {

	public static final String USER_AGENT_HEADER = "User-Agent";
	public static final String USER_AGENT = "DataServices";
	public static final String ACCEPT_CHARSET_HEADER = "Accept-Charset";
	public static final String ACCEPT_CHARSET = DataServicesContext.DEFAULT_ENCODING;


	public static final int MAX_IDLE_CONNECTIONS = 20; // Número máximo de conexiones inactivas.
	public static final long KEEP_ALIVE_DURATION = 500; // Tiempo máximo en milisegundos, en que una conexión puede permanecer inactiva.

	public static final MediaType JSON = MediaType.parse("application/json; charset=" + ACCEPT_CHARSET);
	public static final Callback DUMMY_CALLBACK = new Callback() {

		@Override
		public void onFailure(Request request, IOException e) {
		}

		@Override
		public void onResponse(Response response) throws IOException {
		}
	};

	private static ConnectionPool connectionPool;
	static {
		connectionPool = new ConnectionPool(MAX_IDLE_CONNECTIONS, KEEP_ALIVE_DURATION);
	}


	/**
	 * Envío de petición http síncrona por GET.
	 * 
	 * @param url URL de la dirección.
	 * 
	 * @return respuesta de la petición.
	 * 
	 * @throws IOException 
	 */
	public static Response sendGet(String url) throws IOException {

		OkHttpClient client = new OkHttpClient();
		client.setConnectionPool(connectionPool);

		Request request = new Request.Builder().
				// Se añaden las cabeceras a la petición.
				header(USER_AGENT_HEADER, USER_AGENT).
				header(ACCEPT_CHARSET_HEADER, ACCEPT_CHARSET).
				url(url).build();

		//La petición se ejecuta de forma síncrona.
		return client.newCall(request).execute();
	}

	/**
	 * Envío de petición asíncrona http por GET.
	 * 
	 * @param url URL de la dirección.
	 * @param callback Retrollamada para controlar la respuesta.
	 * 
	 * @throws IOException 
	 */
	public static void sendGet(String url, Callback callback) throws IOException {

		OkHttpClient client = new OkHttpClient();
		client.setConnectionPool(connectionPool);

		Request request = new Request.Builder().
				// Se añaden las cabeceras a la petición.
				header(USER_AGENT_HEADER, USER_AGENT).
				header(ACCEPT_CHARSET_HEADER, ACCEPT_CHARSET).
				url(url).build();

		//La petición se encola de forma asíncrona.
		client.newCall(request).enqueue(callback == null ? DUMMY_CALLBACK : callback);
	}

	/**
	 * Construye y retorna el cuerpo de una petición a partir de los parámetros indicados.
	 * 
	 * @param formValues Mapa de valores de formulario.
	 * @param jsonObject Cadena de texto que representa un objeto en notación JSON.
	 * 
	 * @return el cuerpo de una petición a partir de los parámetros indicados.
	 * 
	 * @throws UnsupportedEncodingException
	 */
	public static RequestBody constructRequestBody(Map<String, String> formValues, String jsonObject) throws UnsupportedEncodingException {

		RequestBody formBody = null;
		// Se construye la lista de valores de formulario, en caso de que esté informada.
		if (formValues != null) {

			FormEncodingBuilder formEncodingBuilder = new FormEncodingBuilder();

			for (Entry<String, String> parameter : formValues.entrySet()) {

				formEncodingBuilder.add(parameter.getKey(), parameter.getValue());
			}

			formBody = formEncodingBuilder.build();
		}

		RequestBody jsonBody = null;
		// Se construye el contenido del objeto json en caso de que esté informado.
		if (jsonObject != null) {
			jsonBody = RequestBody.create(JSON, jsonObject.getBytes(ACCEPT_CHARSET));
		}

		if (formBody == null) {

			if (jsonBody == null) {
				return null;
			}
			else {
				return jsonBody;
			}
		}
		else {

			if (jsonBody == null) {
				return formBody;
			}
			else {

				MultipartBuilder requestBodyBuilder = new MultipartBuilder();

				requestBodyBuilder.addPart(formBody);
				requestBodyBuilder.addPart(jsonBody);

				return requestBodyBuilder.build();
			}
		}
	}

	/**
	 * Envío de petición síncrona http por POST.
	 * 
	 * @param url URL de la dirección.
	 * @param formValues Mapa de valores de formulario.
	 * @param jsonObject Objeto a enviar serializado en formato json.
	 * 
	 * @return respuesta de la petición.
	 * 
	 * @throws IOException 
	 */
	public static Response sendPost(String url, Map<String, String> formValues, String jsonObject) throws IOException {

		OkHttpClient client = new OkHttpClient();
		client.setConnectionPool(connectionPool);

		Request request = new Request.Builder().
				// Se añaden las cabeceras a la petición.
				header(USER_AGENT_HEADER, USER_AGENT).
				header(ACCEPT_CHARSET_HEADER, ACCEPT_CHARSET).
				url(url).post(constructRequestBody(formValues, jsonObject)).build();

		//La petición se ejecuta de forma síncrona.
		return client.newCall(request).execute();
	}

	/**
	 * Envío de petición asíncrona http por POST.
	 * 
	 * @param url URL de la dirección.
	 * @param formValues Mapa de valores de formulario.
	 * @param jsonObject Objeto a enviar serializado en formato json.
	 * @param callback Retrollamada para controlar la respuesta.
	 * 
	 * @throws IOException 
	 */
	public static void sendPost(String url, Map<String, String> formValues, String jsonObject, Callback callback) throws IOException {

		OkHttpClient client = new OkHttpClient();
		client.setConnectionPool(connectionPool);

		Request request = new Request.Builder().
				// Se añaden las cabeceras a la petición.
				header(USER_AGENT_HEADER, USER_AGENT).
				header(ACCEPT_CHARSET_HEADER, ACCEPT_CHARSET).
				url(url).post(constructRequestBody(formValues, jsonObject)).build();

		//La petición se encola de forma asíncrona.
		client.newCall(request).enqueue(callback == null ? DUMMY_CALLBACK : callback);
	}
}
