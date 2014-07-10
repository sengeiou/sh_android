package com.fav24.dataservices.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.utils.IOUtils;

/**
 * Utilidad para comprimir y descomprimir directorios de forma recursiva.
 */
public class ZipUtils {

	/**
	 * Crea un fichero zip en la ubicación especificada, con el contenido del directorio especificado.
	 *
	 * @param directoryPath Ruta de del directorio a comprimir.
	 * @param zipPath Ruta completa de destino del fichero comprimido. p.e. c:/temp/archive.zip
	 * 
	 * @throws IOException
	 */
	public static void createZip(String directoryPath, String zipPath) throws IOException {

		FileOutputStream fOut = null;
		BufferedOutputStream bOut = null;
		ZipArchiveOutputStream tOut = null;

		try {

			fOut = new FileOutputStream(new File(zipPath));
			bOut = new BufferedOutputStream(fOut);
			tOut = new ZipArchiveOutputStream(bOut);

			addFileToZip(tOut, directoryPath, "");

		} finally {

			tOut.finish();
			tOut.close();
			bOut.close();
			fOut.close();
		}
	}

	/**
	 * Crea una entrada zip para la ruta especificada con un nombre construido a partir de la base indicada y el nombre del archivo o directorio.
	 * Si se trata de un directorio, se realizará una llamada recursiva hasta que el directorio esté completamente añadido en el zip.
	 *
	 * @param zOut El stream de salida (escritura) del archivo zip de destino.
	 * @param path La ruta del archivo o directorio a añadir al zip.
	 * @param base Prefijo base para el nombre de la entrada en el zip.
	 *
	 * @throws IOException
	 */
	private static void addFileToZip(ZipArchiveOutputStream zOut, String path, String base) throws IOException {

		File f = new File(path);
		String entryName = base + f.getName();
		
		System.out.println("Anadiendo la entrada <" + entryName +">");
		
		ZipArchiveEntry zipEntry = new ZipArchiveEntry(f, entryName);

		zOut.putArchiveEntry(zipEntry);

		if (f.isFile()) {

			FileInputStream fInputStream = null;

			try {
				fInputStream = new FileInputStream(f);
				IOUtils.copy(fInputStream, zOut);
				zOut.closeArchiveEntry();
			} finally {
				IOUtils.closeQuietly(fInputStream);
			}

		} else {

			zOut.closeArchiveEntry();
			File[] children = f.listFiles();

			if (children != null) {

				for (File child : children) {
					addFileToZip(zOut, child.getAbsolutePath(), entryName + "/");
				}
			}
		}
	}

	/**
	 * Extrae el contenido del fichero zip indicado, en la ruta de destino indicada.
	 * 
	 * Nota: El zip debe contener una únnica carpeta raiz, con todo el contenido comprimido. 
	 * 
	 * @param archivePath path to zip file
	 * @param destinationPath path to extract zip file to. Created if it doesn't exist.
	 * 
	 * @throws Exception 
	 */
	public static void extractZip(String archivePath, String destinationPath) throws Exception {

		File archiveFile = new File(archivePath);
		File unzipDestFolder = null;

		unzipDestFolder = new File(destinationPath);
		String[] zipRootFolder = new String[]{null};

		unzipFolder(archiveFile, unzipDestFolder, zipRootFolder);
	}

	/**
	 * Descromprime un fichero zip en el directorio de destino indicado.
	 * 
	 * Nota: El zip debe contener una únnica carpeta raiz, con todo el contenido comprimido.
	 * Esta carpeta será ignorada en la descompresión.
	 * 
	 * @return true en caso de que la carpeta se haya descomprimido correctamente.
	 * 
	 * @throws Exception 
	 */
	private static boolean unzipFolder(File archiveFile, File zipDestinationFolder, String[] outputZipRootFolder) throws Exception {

		ZipFile zipFile = null;
		
		try {
			
			zipFile = new ZipFile(archiveFile);
			byte[] buf = new byte[65536];

			Enumeration<ZipArchiveEntry> entries = zipFile.getEntries();
			while (entries.hasMoreElements()) {
				
				ZipArchiveEntry zipEntry = entries.nextElement();
				String name = zipEntry.getName();
				
				System.out.println("Descomprimiendo la entrada <" + name +">");
				
				name = name.replace('\\', '/');
				int i = name.indexOf('/');
				if (i > 0) {
					outputZipRootFolder[0] = name.substring(0, i);
				}
				name = name.substring(i + 1);

				File destinationFile = new File(zipDestinationFolder, name);
				if (name.endsWith("/")) {
					
					if (!destinationFile.isDirectory() && !destinationFile.mkdirs()) {
						throw new Exception("Error cerrando el directorio temporal:" + destinationFile.getPath());
					}
					
					continue;
					
				} else if (name.indexOf('/') != -1) {
					
					// Create the the parent directory if it doesn't exist
					File parentFolder = destinationFile.getParentFile();
					
					if (!parentFolder.isDirectory()) {
						
						if (!parentFolder.mkdirs()) {
							throw new Exception("Error cerrando el directorio temporal:" + parentFolder.getPath());
						}
					}
				}

				FileOutputStream fos = null;
				try {
					fos = new FileOutputStream(destinationFile);
					int n;
					InputStream entryContent = zipFile.getInputStream(zipEntry);
					while ((n = entryContent.read(buf)) != -1) {
						if (n > 0) {
							fos.write(buf, 0, n);
						}
					}
				} finally {
					if (fos != null) {
						fos.close();
					}
				}
			}
			return true;

		} catch (IOException e) {

		} finally {

			if (zipFile != null) {
				try {
					zipFile.close();
				} catch (IOException e) {
					throw new IOException("Error cerrando en archivo Zip, debido a: " + e.getMessage());
				}
			}
		}

		return false;
	}

	/**
	 * Method for testing zipping and unzipping.
	 * @param args 
	 */
	public static void main(String[] args) throws IOException {
		try {
		createZip("/Users/jmvera/development/workspaces/backend/data-services/src", "/Users/jmvera/development/workspaces/backend/data-services/src.zip");
		extractZip("/Users/jmvera/development/workspaces/backend/data-services/src/src.zip", "/Users/jmvera/development/workspaces/backend/data-services/srcUnzip");
		}
		catch (Exception e) {
			System.err.print("Error: " + e.getMessage());
		}
		finally {
			
		}
	}
}
