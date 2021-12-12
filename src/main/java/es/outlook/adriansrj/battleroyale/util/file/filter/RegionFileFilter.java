package es.outlook.adriansrj.battleroyale.util.file.filter;

import es.outlook.adriansrj.core.util.file.filter.FileExtensionFilter;

import java.io.File;
import java.io.FileFilter;

/**
 * Region (.mca) file filter.
 *
 * @author AdrianSR / 02/12/2021 / 10:19 a. m.
 */
public class RegionFileFilter implements FileFilter {
	
	public static final String MCA_EXTENSION = "mca";
	
	@Override
	public boolean accept ( File pathname ) {
		return FileExtensionFilter.of ( MCA_EXTENSION ).accept ( pathname );
	}
}