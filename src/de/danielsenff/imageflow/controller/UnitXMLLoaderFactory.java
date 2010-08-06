package de.danielsenff.imageflow.controller;

/**
 * Factory for conviniently getting the right UnitXMLLoader.
 * @author dahie
 *
 */
public class UnitXMLLoaderFactory {

	/**
	 * Creates a new FolderUnitXMLLoader.
	 * @param controller
	 * @return
	 */
	public static UnitDelegateLoader createFolderUnitXMLLoader(DelegatesController controller) {
		return new FolderUnitXMLLoader(controller);
	}
	
	/**
	 * Creates a new JarUnitXMLLoader.
	 * @param controller
	 * @return
	 */
	public static UnitDelegateLoader createJarUnitXMLLoader(DelegatesController controller) {
		return new JarUnitXMLLoader(controller);
	}

	/**
	 * Creates the suitable {@link UnitDelegateLoader} for the given protocol.
	 * @param controller
	 * @param protocol
	 * @return
	 */
	public static UnitDelegateLoader createUnitXMLLoaderByProtocol(DelegatesController controller, 
			final String protocol) {
		if (protocol.equals("file")) {
			return UnitXMLLoaderFactory.createFolderUnitXMLLoader(controller);
		} else if (protocol.equals("jar")) {
			return UnitXMLLoaderFactory.createJarUnitXMLLoader(controller);
		} else {
			throw new RuntimeException("Currenty only jar and file are valid resource protocols!");
		}
	}
	
}
