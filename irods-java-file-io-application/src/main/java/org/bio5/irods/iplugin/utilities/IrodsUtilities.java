package org.bio5.irods.iplugin.utilities;

import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.security.MessageDigest;
import java.util.Properties;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

public final class IrodsUtilities {

	/* Logger instantiation */
	static Logger log = Logger.getLogger(IrodsUtilities.class.getName());

	/* Calculate MD5 CheckSum of a File */
	public static String calculateMD5CheckSum(File file) {
		try {
			InputStream fin = new FileInputStream(file);
			java.security.MessageDigest md5er = MessageDigest
					.getInstance("MD5");
			byte[] buffer = new byte[1024];
			int read;
			do {
				read = fin.read(buffer);
				if (read > 0)
					md5er.update(buffer, 0, read);
			} while (read != -1);
			fin.close();
			byte[] digest = md5er.digest();
			if (digest == null)
				return null;
			String strDigest = "";
			for (int i = 0; i < digest.length; i++) {
				strDigest += Integer.toString((digest[i] & 0xff) + 0x100, 16)
						.substring(1).toLowerCase();
			}
			return strDigest;
		} catch (Exception e) {
			return null;
		}
	}

	/* Pull pathSeperator of the Operating System */
	public static String getPathSeperator() {
		String pathSeperator = Constants.DEFAULT_PATH_SEPERATOR;
		pathSeperator = System.getProperty("file.separator");
		return pathSeperator;
	}

	/* Get JTree node path depending on the Mouse selection */
	public static String getJtreeSelection(MouseEvent me,
			JTree userDirectoryTree) {
		String fullTreePath = "";
		TreePath tp = userDirectoryTree
				.getPathForLocation(me.getX(), me.getY());
		if (tp != null) {
			Object treepath[] = tp.getPath();
			for (int i = 0; i < treepath.length; i++) {
				fullTreePath += IrodsUtilities.getPathSeperator()
						+ treepath[i].toString();
			}
		}
		return fullTreePath;
	}

	public static File createFileFromTreePath(TreePath treePath) {
		StringBuilder sb = new StringBuilder();
		Object[] nodes = treePath.getPath();
		for (int i = 0; i < nodes.length; i++) {
			sb.append(File.separatorChar).append(nodes[i].toString());
		}
		return new File(sb.toString());
	}

	public static String createFilePathFromTreePath(TreePath treePath) {
		StringBuilder sb = new StringBuilder();
		Object[] nodes = treePath.getPath();
		for (int i = 0; i < nodes.length; i++) {
			sb.append(File.separatorChar).append(nodes[i].toString());
		}
		return sb.toString();
	}

	public static String getJtreeSelectionForSingleClick(MouseEvent me,
			JTree userDirectoryTree) {
		String fullTreePath = "";
		TreePath tp = userDirectoryTree
				.getPathForLocation(me.getX(), me.getY());
		if (tp != null) {
			DefaultMutableTreeNode lastPathComponentNode = (DefaultMutableTreeNode) tp
					.getLastPathComponent();
			if (lastPathComponentNode.isLeaf()) {
				tp = tp.getParentPath();
			}
			Object treepath[] = tp.getPath();
			for (int i = 0; i < treepath.length; i++) {
				fullTreePath += IrodsUtilities.getPathSeperator()
						+ treepath[i].toString();
			}
		}
		return fullTreePath;
	}

	public static boolean createDirectoryIfDoesntExist(String directoryPath) {
		boolean isDirectoryCreated = false;
		try {
			if (null != directoryPath && !"".equals(directoryPath)) {
				File file = new File(directoryPath);
				if (!file.exists()) {
					log.info("Cache folder doesn't exist- Creating folder");
					isDirectoryCreated = file.mkdirs();
				}
				if (file.exists()) {
					isDirectoryCreated = true;
				}
			}
		} catch (Exception e) {
			log.error("Error while creating ImageJ cache directory"
					+ e.getMessage());
			isDirectoryCreated = false;
		}
		return isDirectoryCreated;
	}

	public static String getFileNameFromDirectoryPath(String directoryPath) {
		String fileName = null;
		try {
			if (null != directoryPath && directoryPath != "") {
				String fullPath = directoryPath;
				int index = fullPath.lastIndexOf(getPathSeperator());
				fileName = fullPath.substring(index + 1);
				log.info("File name extracted from given directory path: "
						+ fileName);
			} else {
				log.error("Given directoryPath is either empty or null!");
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return fileName;
	}

	public static String getUserHomeFolderFromSystemProperty() {
		String userHomeFolderFromSystemProperty = null;
		userHomeFolderFromSystemProperty = System.getProperty("user.home");
		return userHomeFolderFromSystemProperty;
	}

	public static long getFolderSize(File dir) {
		long size = 0;
		for (File file : dir.listFiles()) {
			if (file.isFile()) {
				// System.out.println(file.getName() + " " + file.length());
				size += file.length();
			} else
				size += getFolderSize(file);
		}
		return size;
	}

	/*
	 * This method will load Tapas properties from either of below two file - 1.
	 * Local Property file in cache directory or 2. Property file in Jar. If
	 * local file is not available, then property file available in jar is
	 * loaded and then a copy is left in local path for future availability.
	 */

	public static Properties getTapasLoginConfiguration(
			String propertyFileName, String localPathForPropertyFile) {

		FileReader reader = null;
		if (null == propertyFileName) {
			propertyFileName = Constants.PROPERTY_FILE_NAME;
		}
		if (null == localPathForPropertyFile) {
			localPathForPropertyFile = Constants.IMAGEJ_CACHE_FOLDER;
		}
		Properties tapasConfigurationProperties = new Properties();
		tapasConfigurationProperties = loadLocalTapasPropertyFiles(localPathForPropertyFile);

		if (null != tapasConfigurationProperties) {
			return tapasConfigurationProperties;
		} else {
			try {
				reader = new FileReader(propertyFileName);
				if (null != reader) {
					File propertyFile = new File(propertyFileName);
					copyFileToNewPhysicalLocation(propertyFile,
							localPathForPropertyFile);
				}

			} catch (FileNotFoundException fileNotFoundException) {
				log.error("tapas.property file is not found"
						+ fileNotFoundException.getMessage());
			}

			try {
				tapasConfigurationProperties = new Properties();
				tapasConfigurationProperties.load(reader);
			} catch (IOException ioException) {
				log.error("Error while loading property file"
						+ ioException.getMessage());
			}
		}
		return tapasConfigurationProperties;
	}

	public static void copyFileToNewPhysicalLocation(File sourceFile,
			String destination) {
		File destinationFile = new File(destination);
		try {
			FileUtils.copyFileToDirectory(sourceFile, destinationFile);
		} catch (IOException e) {
			log.error("Error while copying file from source to destination"
					+ e.getMessage());
		}
	}

	public static Properties loadLocalTapasPropertyFiles(String filePath) {
		Properties tapasLocalConfigurationProperties = null;

		if (null != filePath) {
			String propertyFileName = filePath
					+ IrodsUtilities.getPathSeperator()
					+ Constants.PROPERTY_FILE_NAME;
			try {
				Reader reader = new FileReader(propertyFileName);
				tapasLocalConfigurationProperties = new Properties();
				tapasLocalConfigurationProperties.load(reader);
			} catch (FileNotFoundException e) {
				log.error("Error while reading localPropertyFileName "
						+ e.getMessage());
			} catch (IOException ioException) {
				log.error("Error while loading local property file"
						+ ioException.getMessage());
			}
		}
		return tapasLocalConfigurationProperties;
	}
}
