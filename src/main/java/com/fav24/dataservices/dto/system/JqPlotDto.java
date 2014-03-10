package com.fav24.dataservices.dto.system;

import java.io.Serializable;
import java.util.NavigableMap;
import java.util.TreeMap;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Objeto de transferencia de una gráfica de un elemento del sistema.
 *  
 * @author Fav24
 */
@JsonIgnoreProperties(ignoreUnknown=true)
@JsonAutoDetect(
		creatorVisibility = JsonAutoDetect.Visibility.ANY,
		fieldVisibility = JsonAutoDetect.Visibility.ANY, 
		getterVisibility = JsonAutoDetect.Visibility.NONE, 
		isGetterVisibility = JsonAutoDetect.Visibility.NONE, 
		setterVisibility = JsonAutoDetect.Visibility.NONE)
public class JqPlotDto implements Serializable {

	private static final long serialVersionUID = 6557890098016497204L;

	private Long period;
	private Long timeRange;
	private String name;
	private NavigableMap <String, Object[][]> data;


	/**
	 * Constructor por defecto.
	 */
	public JqPlotDto() {

		this.period = null;
		this.timeRange = null;
		this.name = null;
		this.data = new TreeMap<String, Object[][]>();
	}

	/**
	 * Retorna el periodo.
	 * 
	 * @return el periodo
	 */
	public Long getPeriod() {
		return period;
	}

	/**
	 * Asigna el periodo.
	 * 
	 * @param period El periodo a asignar.
	 */
	public void setPeriod(Long period) {
		this.period = period;
	}

	/**
	 * Retorna el rango de tiempo de la información.
	 * 
	 * @return el rango de tiempo de la información.
	 */
	public Long getTimeRange() {
		return timeRange;
	}

	/**
	 * Asigna el rango de tiempo de la información.
	 * 
	 * @param timeRange El rango de tiempo de la información.
	 */
	public void setTimeRange(Long timeRange) {
		this.timeRange = timeRange;
	}

	/**
	 * Retorna el nombre del elemento al que pertenece la información.
	 * 
	 * @return el nombre del elemento al que pertenece la información.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Asigna el nombre del elemento al que pertenece la información.
	 * 
	 * @param name El nombre del elemento a asignar.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Retorna la información asociada a esta gráfica.
	 * 
	 * @return la información asociada a esta gráfica.
	 */
	public NavigableMap<String, Object[][]> getData() {
		return data;
	}

	/**
	 * Asigna la información asociada a esta gráfica.
	 * 
	 * @param data La información a asignar.
	 */
	public void setData(NavigableMap<String, Object[][]> data) {
		this.data = data;
	}
}
