package com.fav24.dataservices.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;

import com.fav24.dataservices.DataServicesContext;


/**
 * Esta clase es obsoleta.
 * <br/>
 * Para nuevos desarrollos usar la clase:
 * <br/>
 * {@linkplain com.fav24.dataservices.util.FastHttpUtils}.
 */
@Deprecated
public class HttpUtils {

	private static final String USER_AGENT = "DataServices";
	
	private static PoolingHttpClientConnectionManager connectionManager;
	private static HttpClientBuilder httpClientBuilder;
	
	static {
		connectionManager = new PoolingHttpClientConnectionManager();
		connectionManager.setMaxTotal(1000);
		httpClientBuilder = HttpClients.custom().setConnectionManager(connectionManager);
	}

	/**
	 * Envío de petición http por GET.
	 * 
	 * @param url URL de la dirección.
	 * 
	 * @return respuesta de la petición.
	 * 
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public static CloseableHttpResponse sendGet(String url) throws ClientProtocolException, IOException {

		CloseableHttpClient client = null;

		synchronized (httpClientBuilder) {
			client = httpClientBuilder.build();
		}

		HttpGet request = new HttpGet(url);

		// Se añaden las cabeceras a la petición.
		request.setHeader("User-Agent", USER_AGENT);
		request.addHeader("Accept-Charset", DataServicesContext.DEFAULT_ENCODING);

		return client.execute(request);
	}

	/**
	 * Envío de petición http por POST.
	 * 
	 * @param url URL de la dirección.
	 * @param parameters Mapa de parámetros en formato texto.
	 * @param jsonObject Objeto a enviar serializado en formato json.
	 * 
	 * @return respuesta de la petición.
	 * 
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public static CloseableHttpResponse sendPost(String url, Map<String, String> parameters, String jsonObject) throws ClientProtocolException, IOException {

		CloseableHttpClient client = null;

		synchronized (httpClientBuilder) {
			client = httpClientBuilder.build();
		}

		HttpPost post = new HttpPost(url);

		// Se añaden las cabeceras a la petición.
		post.setHeader("User-Agent", USER_AGENT);
		post.addHeader("Accept-Charset", DataServicesContext.DEFAULT_ENCODING);

		// Se envía la lista de parámetros, en caso de que esté informada.
		if (parameters != null) {

			ArrayList<NameValuePair> urlParameters = new ArrayList<NameValuePair>(parameters.size());

			for (Entry<String, String> parameter : parameters.entrySet()) {

				urlParameters.add(new BasicNameValuePair(parameter.getKey(), parameter.getValue()));
			}

			post.setEntity(new UrlEncodedFormEntity(urlParameters));
		}

		// Se envía el contenido del objeto json en caso de que esté informado.
		if (jsonObject != null) {

			StringEntity entity = new StringEntity(jsonObject, DataServicesContext.DEFAULT_ENCODING);
			entity.setContentType("application/json");

			post.setEntity(entity);
		}

		return client.execute(post);
	}
}
