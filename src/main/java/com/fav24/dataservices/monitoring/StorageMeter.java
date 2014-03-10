package com.fav24.dataservices.monitoring;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * Clase para obtener el estado de los espacios en los dispositivos de almacemamiento.
 */
public final class StorageMeter
{

	public static final String TOTAL_STORAGE_SPACE = "TotalStorageSpace"; //Capacidad total de almacenamiento.
	public static final String TOTAL_USABLE_STORAGE_SPACE = "TotalUsableStorageSpace"; //Espacio disponible de almacenamiento.


	/** 
	 * Constructor por defecto.
	 */
	public StorageMeter() {

	}

	/**
	 * Retorna un mapa con el conjunto de elementos de almacenamiento, y su información asociada en cuanto a:
	 * 
	 * {@linkplain #TOTAL_STORAGE_SPACE}
	 * {@linkplain #TOTAL_USABLE_STORAGE_SPACE}
	 * 
	 * @return un mapa con el conjunto de elementos de almacenamiento, y su información asociada.
	 */
	public NavigableMap<String, NavigableMap<String, Double>> getSystemStorageStatus() {

		NavigableMap<String, NavigableMap<String, Double>> systemStorageStatus = new TreeMap<String, NavigableMap<String, Double>>();

		for (Path root : FileSystems.getDefault().getRootDirectories())
		{
			NavigableMap<String, Double> storageStatus = new TreeMap<String, Double>();

			try	{

				FileStore store = Files.getFileStore(root);

				storageStatus.put(TOTAL_STORAGE_SPACE, Double.valueOf(store.getTotalSpace()));
				storageStatus.put(TOTAL_USABLE_STORAGE_SPACE, Double.valueOf(store.getUsableSpace()));

				systemStorageStatus.put(root.toString(), storageStatus);
			}
			catch (IOException e) {
			}
		}

		return systemStorageStatus;
	}
}