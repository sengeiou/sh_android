package com.fav24.shootr.util;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectConverter {
	
	public final static ObjectMapper objectMapper;
	
	static {
		objectMapper = new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
	}
	
	/**
	 * Conversor generico. Intenta convertir al tipo que se asigna al hacer la llamada.
	 * ej: Map<String, String> map = ObjectConverter.convertValue(device, Device.class);
	 * @param obj objeto a convertir.
	 * @param toValueType tipo del objeto a convertir.
	 * @return objeto convertido.
	 */
	public static <T> T convertValue(Object obj, Class<T> toValueType){
		return objectMapper.convertValue(obj, toValueType);
	}

	public static <T> T fromJson(String json, Class<T> toValueType) throws JsonParseException, JsonMappingException, IOException {
		return objectMapper.readValue(json, toValueType);
	}

	public static String toJson(Object obj) throws JsonProcessingException {
		return objectMapper.writeValueAsString(obj);
	}
}
