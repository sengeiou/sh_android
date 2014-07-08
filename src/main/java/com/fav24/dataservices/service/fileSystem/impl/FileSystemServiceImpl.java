package com.fav24.dataservices.service.fileSystem.impl;

import java.io.File;
import java.io.FileFilter;
import java.nio.file.Path;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fav24.dataservices.service.fileSystem.FileInformation;
import com.fav24.dataservices.service.fileSystem.FileSystemService;


/**
 * Implementación de servicio FileSystem. 
 */
@Scope("singleton")
@Component
public class FileSystemServiceImpl implements FileSystemService {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AbstractList<FileInformation> getFileInformationList(Path path, String pattern, final boolean directoriesOnly, final boolean filesOnly) {

		AbstractList<FileInformation> fileAttributes = new ArrayList<FileInformation>(); 
		File baseDir = path.toFile();

		if (baseDir.isDirectory()) {

			FileFilter filter = null;

			if (pattern != null) {
				final Pattern fileNamePattern = Pattern.compile(pattern);

				filter = new FileFilter() {

					@Override
					public boolean accept(File file) {

						if (file.isDirectory()) {
							return !filesOnly;
						}

						if (directoriesOnly) {
							return false;
						}

						Matcher matcher = fileNamePattern.matcher(file.getName());

						return matcher.find();
					}
				};
			}

			for (File file : baseDir.listFiles(filter)) {

				fileAttributes.add(new FileInformation(file));
			}
		}


		Collections.sort(fileAttributes);

		return fileAttributes;
	}
}
