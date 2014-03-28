package org.bio5.irods.imagej.utilities;

public class Constants {

	public static final String HOME_DIR = "/iplant/";
	public static final String DEFAULT_STORAGE_RESOURCE = "";
	public static final String HOME_DIR_IPLANT_HOME = "/iplant/home/";
	public static String IMAGEJ_LOCAL_WORKING_DIRECTORY = System
			.getProperty("user.home")
			+ IrodsUtilities.getPathSeperator()
			+ "irods_plugin";
	public static String DEFAULT_PATH_SEPERATOR = "/";
	public static String ZONE = "iplant";
	public static final String HOST = "data.iplantcollaborative.org";
	public static String PORT = "1247";
	public static String HOME = "home";
	public static int MAX_THREADS = 10;
	public static String FOLDER_SELECTION_CANCELED = "Folder Selection is canceled";
	public static boolean IS_HOME_DIRECTORY_THE_ROOT_NODE = true; /*
																 * true - if you
																 * want to pull
																 * everything
																 * from home
																 * directory (This includes shared files too).
																 * False- if you
																 * want to pull
																 * collections
																 * from only
																 * your account
																 */

}
