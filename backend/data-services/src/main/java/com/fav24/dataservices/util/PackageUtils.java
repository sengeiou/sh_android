package com.fav24.dataservices.util;

import java.io.File;
import java.io.FileInputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.apache.commons.io.IOUtils;

public class PackageUtils {

	private static final String CLASS_POSTFIX = ".class";


	/**
	 * Retorna la lista de clases definidas en el paquete indicado, del fichero .jar indicado.
	 * 
	 * @param jarFileName Fichero .jar a prospectar.
	 * @param packageName Nombre del paquete que contiene las clases a obtener.
	 *  
	 * @return la lista de clases definidas en el paquete indicado, del fichero .jar indicado.
	 */
	public static AbstractList<String> getClasseNamesInPackage(String jarFileName, String packageName) {

		AbstractList<String> classes = new ArrayList<String>();

		packageName = packageName.replace('.', '/');

		JarInputStream jarFile = null;

		try {
			jarFile = new JarInputStream(new FileInputStream(jarFileName));
			JarEntry jarEntry;

			while((jarEntry = jarFile.getNextJarEntry()) != null) {

				String entryName = jarEntry.getName();

				if (entryName.startsWith(packageName) && entryName.endsWith(CLASS_POSTFIX)) {

					entryName = entryName.replace('/', '.');
					entryName = entryName.substring(0, entryName.length() - CLASS_POSTFIX.length());

					classes.add(entryName);
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace ();
		}
		finally {
			IOUtils.closeQuietly(jarFile);
		}

		return classes;
	}

	/**
	 * Retorna la lista de clases definidas en el paquete indicado.
	 * 
	 * @param packageName Nombre del paquete que contiene las clases a obtener.
	 *  
	 * @return la lista de clases definidas en el paquete indicado.
	 */
	public static AbstractList<String> getClasseNamesInPackage(String packageName) throws ClassNotFoundException { 

		AbstractList<String> classes = new ArrayList<String>();
		
		// Get a File object for the package 
		File directory = null;
		String packageAsPath = '/' + packageName.replace('.', '/');
		
		URL resource = PackageUtils.class.getResource(packageAsPath);
		
		try { 
			directory = new File(resource.toURI()); 
		} 
		catch(NullPointerException | URISyntaxException x) { 
			throw new ClassNotFoundException(packageName + " does not appear to be a valid package"); 
		} 

		if (directory.exists()) {
			
			// Get the list of the files contained in the package 
			String[] files = directory.list();
			
			for(int i=0; i<files.length; i++) { 
			
				// we are only interested in .class files 
				if(files[i].endsWith(CLASS_POSTFIX)) { 
					// removes the .class extension 
					classes.add(packageName + '.' + files[i].substring(0, files[i].length() - CLASS_POSTFIX.length())); 
				} 
			} 
		} 
		else { 
			throw new ClassNotFoundException(packageName + " does not appear to be a valid package"); 
		} 

		return classes; 
	}
}
