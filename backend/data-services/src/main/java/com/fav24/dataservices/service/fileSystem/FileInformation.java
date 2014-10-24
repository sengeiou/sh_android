package com.fav24.dataservices.service.fileSystem;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.DosFileAttributes;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;

import com.fav24.dataservices.util.OSUtils;


/**
 * Clase para el almacenamiento de la información de archivos.
 */
public class FileInformation implements Comparable<FileInformation> {

	private String name;
	private long creationTime;
	private long lastModifiedTime;
	private long lastAccessTime;
	private boolean isDirectory;
	private boolean isSymbolicLink;
	private long size;
	private String group;
	private String owner;
	private String permissions;


	public FileInformation(File file) {

		this.name = file.getName();

		try {

			if (OSUtils.OSType.getOSType() == OSUtils.OSType.Windows) {

				DosFileAttributes fileAttributes = Files.readAttributes(file.toPath(), DosFileAttributes.class);

				this.creationTime = fileAttributes.creationTime().toMillis();
				this.lastModifiedTime = fileAttributes.lastModifiedTime().toMillis();
				this.lastAccessTime = fileAttributes.lastAccessTime().toMillis();

				this.isDirectory = fileAttributes.isDirectory();
				this.isSymbolicLink = fileAttributes.isSymbolicLink();
				this.size = fileAttributes.size();

				this.group = null;
				this.owner = null;
				char[] permissions = new char[]{'-', '-', '-'};

				if (fileAttributes.isSystem()) {
					permissions[0] = 's'; 
				}
				if (fileAttributes.isHidden()) {
					permissions[1] = 'h'; 
				}
				if (fileAttributes.isReadOnly()) {
					permissions[2] = 'r'; 
				}
				
				this.permissions = new String(permissions);
			}
			else {

				PosixFileAttributes fileAttributes = Files.readAttributes(file.toPath(), PosixFileAttributes.class);

				this.creationTime = fileAttributes.creationTime().toMillis();
				this.lastModifiedTime = fileAttributes.lastModifiedTime().toMillis();
				this.lastAccessTime = fileAttributes.lastAccessTime().toMillis();

				this.isDirectory = fileAttributes.isDirectory();
				this.isSymbolicLink = fileAttributes.isSymbolicLink();
				this.size = fileAttributes.size();

				this.group = fileAttributes.group().getName();
				this.owner = fileAttributes.owner().getName();
				
				char[]permissions = new char[]{'-', '-', '-', '-', '-', '-', '-', '-', '-'};

				Set<PosixFilePermission> permissionSet = fileAttributes.permissions();

				for (PosixFilePermission permission : permissionSet) {

					if (PosixFilePermission.GROUP_READ.equals(permission)) {
						permissions[0] = 'r';
					}
					else if (PosixFilePermission.GROUP_WRITE.equals(permission)) {
						permissions[1] = 'w';
					}
					else if (PosixFilePermission.GROUP_EXECUTE.equals(permission)) {
						permissions[2] = 'x';
					}
					else if (PosixFilePermission.OWNER_READ.equals(permission)) {
						permissions[3] = 'r';
					}
					else if (PosixFilePermission.OWNER_WRITE.equals(permission)) {
						permissions[4] = 'w';
					}
					else if (PosixFilePermission.OWNER_EXECUTE.equals(permission)) {
						permissions[5] = 'x';
					}
					else if (PosixFilePermission.OTHERS_READ.equals(permission)) {
						permissions[6] = 'r';
					}
					else if (PosixFilePermission.OTHERS_WRITE.equals(permission)) {
						permissions[7] = 'w';
					}
					else if (PosixFilePermission.OTHERS_EXECUTE.equals(permission)) {
						permissions[8] = 'x';
					}
				}
				
				this.permissions = new String(permissions);
			}

		} catch (IOException e) {
		}
	}

	/**
	 * Retorna el nombre del fichero.
	 * 
	 * @return el nombre del fichero.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Asigna el nombre del fichero.
	 * 
	 * @param name Nombre del fichero a asignar.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Retorna los milisegundos transcurridos desde la creación en base a epoch. 
	 *  
	 * @return los milisegundos transcurridos desde la creación en base a epoch.
	 */
	public long getCreationTime() {
		return creationTime;
	}

	/**
	 * Asigna los milisegundos transcurridos desde la creación en base a epoch. 
	 *  
	 * @param creationTime Los milisegundos a asignar.
	 */
	public void setCreationTime(long creationTime) {
		this.creationTime = creationTime;
	}

	/**
	 * Retorna los milisegundos transcurridos desde la última modificación en base a epoch. 
	 *  
	 * @return los milisegundos transcurridos desde la última modificación en base a epoch.
	 */
	public long getLastModifiedTime() {
		return lastModifiedTime;
	}

	/**
	 * Asigna los milisegundos transcurridos desde la última modificación en base a epoch. 
	 *  
	 * @param lastModifiedTime Los milisegundos a asignar.
	 */
	public void setLastModifiedTime(long lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}

	/**
	 * Retorna los milisegundos transcurridos desde el último acceso en base a epoch. 
	 *  
	 * @return los milisegundos transcurridos desde el último acceso en base a epoch.
	 */
	public long getLastAccessTime() {
		return lastAccessTime;
	}

	/**
	 * Asigna los milisegundos transcurridos desde el último acceso en base a epoch. 
	 *  
	 * @param lastAccessTime Los milisegundos a asignar.
	 */
	public void setLastAccessTime(long lastAccessTime) {
		this.lastAccessTime = lastAccessTime;
	}

	/**
	 * Retorna true o false en función de si se trata o no de un directorio.
	 * 
	 * @return true o false en función de si se trata o no de un directorio.
	 */
	public boolean isDirectory() {
		return isDirectory;
	}

	/**
	 * Asigna true o false en función de si se trata o no de un directorio.
	 * 
	 * @param isDirectory Valor a asignar.
	 */
	public void setDirectory(boolean isDirectory) {
		this.isDirectory = isDirectory;
	}

	/**
	 * Retorna true o false en función de si se trata o no de un link.
	 * 
	 * @return true o false en función de si se trata o no de un link.
	 */
	public boolean isSymbolicLink() {
		return isSymbolicLink;
	}

	/**
	 * Asigna true o false en función de si se trata o no de un link.
	 * 
	 * @param isSymbolicLink Valor a asignar.
	 */
	public void setSymbolicLink(boolean isSymbolicLink) {
		this.isSymbolicLink = isSymbolicLink;
	}

	/**
	 * Retorna el tamaño en bytes del fichero.
	 * 
	 * @return el tamaño en bytes del fichero.
	 */
	public long getSize() {
		return size;
	}

	/**
	 * Asigna el tamaño en bytes del fichero.
	 * 
	 * @param size El tamaño a asignar en bytes.
	 */
	public void setSize(long size) {
		this.size = size;
	}

	/**
	 * Retorna el grupo al que pertenece el fichero.
	 * 
	 * @return el grupo al que pertenece el fichero.
	 */
	public String getGroup() {
		return group;
	}

	/**
	 * Asign el grupo al que pertenece el fichero.
	 * 
	 * @param group El grupo a asignar.
	 */
	public void setGroup(String group) {
		this.group = group;
	}

	/**
	 * Retorna el usuario al que pertenece el fichero.
	 * 
	 * @return el usuario al que pertenece el fichero.
	 */
	public String getOwner() {
		return owner;
	}

	/**
	 * Asigna el usuario al que pertenece el fichero.
	 * 
	 * @param owner El propietario a asignar.
	 */
	public void setOwner(String owner) {
		this.owner = owner;
	}

	/**
	 * Retorna el array de permisos del fichero.
	 * 
	 * @return el array de permisos del fichero.
	 */
	public String getPermissions() {
		return permissions;
	}

	/**
	 * Asignar el array de permisos del fichero.
	 * 
	 * @param permissions El array de permisos a asignar.
	 */
	public void setPermissions(String permissions) {
		this.permissions = permissions;
	}


	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * Ordena primero los directorios y luego los ficheros.
	 * Dentro de cada gurpo, ordena primero por nombre y luego por fecha de creación. 
	 */
	@Override
	public int compareTo(FileInformation o) {

		if (isDirectory && !o.isDirectory) {
			return -1;
		}

		if (!isDirectory && o.isDirectory) {
			return 1;
		}

		int comparison = name.compareTo(o.name);

		if (comparison == 0) {
			return creationTime < o.creationTime ? -1 : (creationTime > o.creationTime ? 1 : 0);
		}

		return comparison;
	}
}