package com.fav24.dataservices.monitoring;

import java.io.IOException;
import java.nio.file.FileStore;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.apache.commons.lang.ArrayUtils;

/**
 * Clase para obtener el estado de los espacios en los dispositivos de almacemamiento.
 */
public final class StorageMeter extends Meter
{

	public static final String TOTAL_STORAGE_SPACE = "TotalStorageSpace"; //Capacidad total de almacenamiento.
	public static final String TOTAL_USABLE_STORAGE_SPACE = "TotalUsableStorageSpace"; //Espacio disponible de almacenamiento.

	private FileStore store;
	private String storeMeterName;

	/** 
	 * Constructor.
	 * 
	 * @param store Almacén de archivos del que se obtendrán las medidas.
	 */
	public StorageMeter(FileStore store) {

		this.store = store;

		byte[] storeName = store.name().replace('/', '.').replace('\\', '.').replace(':', '.').getBytes();
		int i=0;
		while(i<storeName.length && storeName[i] == '.') {
			i++;
		}

		if (i != storeName.length) {
			this.storeMeterName = "storage-meter_" + new String(ArrayUtils.subarray(storeName, i, storeName.length));
		}
		else {
			this.storeMeterName = "storage-meter";
		}
	}

	/**
	 * Retorna un mapa con la información asociada al elemento de almacenamiento.
	 * 
	 * {@linkplain #TOTAL_STORAGE_SPACE}
	 * {@linkplain #TOTAL_USABLE_STORAGE_SPACE}
	 * 
	 * @return un mapa con la información asociada al elemento de almacenamiento.
	 */
	public NavigableMap<String, Double> getSystemStorageStatus() {

		NavigableMap<String, Double> storageStatus = new TreeMap<String, Double>();

		try	{
			storageStatus.put(TOTAL_STORAGE_SPACE, Double.valueOf(store.getTotalSpace()));
			storageStatus.put(TOTAL_USABLE_STORAGE_SPACE, Double.valueOf(store.getUsableSpace()));
		}
		catch (IOException e) {
		}

		return storageStatus;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getMeterName() {
		return storeMeterName;
	}

	/**
	 * Retorna el nombre del almacén al que está asociado este medidor.
	 * 
	 * @return el nombre del almacén al que está asociado este medidor.
	 */
	public String getStoreName() {
		return store.name();
	}
}