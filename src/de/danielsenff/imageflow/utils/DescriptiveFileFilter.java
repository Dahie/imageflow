package de.danielsenff.imageflow.utils;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/** This is a substitute for FileNameExtensionFilter, which is
 * only available on Java SE 6.
 */
public class DescriptiveFileFilter extends FileFilter {

	private final String description;
	private final String extension; 

	public DescriptiveFileFilter(String extension, String description) {
		this.extension = extension;
		this.description = extension + " - " +description;
	}

	@Override
	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}
		String fileName = f.getName();
		int i = fileName.lastIndexOf('.');
		if ((i > 0) && (i < (fileName.length() - 1))) {
			String fileExt = fileName.substring(i + 1);
			if (extension.equalsIgnoreCase(fileExt)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String getDescription() {
		return description;
	}
}