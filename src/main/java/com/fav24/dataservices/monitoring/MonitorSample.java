package com.fav24.dataservices.monitoring;

import java.util.NavigableMap;
import java.util.TreeMap;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Clase que almacena una muestra de un medidor.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
@JsonAutoDetect(
creatorVisibility = JsonAutoDetect.Visibility.ANY,
fieldVisibility = JsonAutoDetect.Visibility.ANY, 
getterVisibility = JsonAutoDetect.Visibility.NONE, 
isGetterVisibility = JsonAutoDetect.Visibility.NONE, 
setterVisibility = JsonAutoDetect.Visibility.NONE)
public class MonitorSample implements Comparable<MonitorSample> {

	private Long time;
	private NavigableMap<String, Double> data;


	/**
	 * Constructor por defecto.
	 */
	public MonitorSample() {
		time = null;
		data = null;
	}

	/**
	 * Constructor con parámetros.
	 * 
	 * @param data Información que contiene la muestra.
	 */
	public MonitorSample(NavigableMap<String, Double> data) {

		this.time = System.currentTimeMillis();
		this.data = data;
	}

	/**
	 * Retorna el instante en el que se tomó la muestra.
	 * 
	 * Nota: El valor es en milisegundos desde epoch. 
	 * 
	 * @return el instante en el que se tomó la muestra.
	 */
	public Long getTime() {
		return time;
	}

	/**
	 * Asigna el instante en el que se tomó la muestra.
	 * 
	 * @param time El instante a asignar.
	 */
	public void setTime(Long time) {
		this.time = time;
	}
	
	/**
	 * Retorna el valor asociado al aspecto de medición del recurso.
	 * 
	 * @param aspect Aspecto del que se desea obtener la información.
	 * 
	 * @return el valor asociado al aspecto de medición del recurso.
	 */
	public Double getData(String aspect) {
		return data != null ? data.get(aspect) : null;
	}

	/**
	 * Asigna el valor asociado al aspecto de medición del recurso.
	 * 
	 * @param aspect El aspecto al que hace referencia el valor.
	 * @param value El valor a asignar.
	 */
	public void setData(String aspect, Double value) {

		if (data == null) {
			data = new TreeMap<String, Double>();
		}

		this.data.put(aspect, value);
	}

	/**
	 *  Retorna -1, 0 o 1 en función de si esta muestra se tomó antes, 
	 *  al mismo tiempo, o después de la especificada por parámetro.
	 *  
	 *  @return -1, 0 o 1 en función de si esta muestra se tomó antes, 
	 *  al mismo tiempo, o después de la especificada por parámetro.
	 */
	public int compareTo(MonitorSample other) {

		if (time == other.time) {
			return 0;
		}

		if (time != null) {

			if (other.time != null) {

				return time < other.time ? -1 : 1;
			}
			else {
				return 1;
			}
		}
		else {
			return -1;
		}
	}
}
