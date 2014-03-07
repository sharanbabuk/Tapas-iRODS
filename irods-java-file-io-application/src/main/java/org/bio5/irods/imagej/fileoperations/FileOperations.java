package org.bio5.irods.imagej.fileoperations;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.bio5.irods.imagej.bean.IrodsImageJBean;
import org.bio5.irods.imagej.connection.IrodsConnection;
import org.bio5.irods.imagej.utilities.Constants;
import org.bio5.irods.imagej.utilities.IrodsUtilities;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.connection.IRODSSession;
import org.irods.jargon.core.connection.SettableJargonProperties;
import org.irods.jargon.core.exception.FileNotFoundException;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.CollectionAndDataObjectListAndSearchAO;
import org.irods.jargon.core.pub.IRODSFileSystem;
import org.irods.jargon.core.pub.IRODSFileSystemAOImpl;
import org.irods.jargon.core.pub.io.IRODSFile;
import org.irods.jargon.core.pub.io.IRODSFileFactory;
import org.irods.jargon.core.query.CollectionAndDataObjectListingEntry;

public class FileOperations {

	private static IRODSFileSystem irodsFileSystem;
	private static IRODSFile iRodsFile;
	private static IRODSSession iRODSSession =null;

	/* Logger instantiation */
	static Logger log = Logger.getLogger(FileOperations.class.getName());

	/*public static void getDirectoryContents(IRODSAccount iRODSAccount,String pathForInternalFiles, IrodsImageJ irodsImagej)
			throws JargonException, FileNotFoundException {

		iRODSSession = IrodsConnection.createDefaultiRodsSession();
		SettableJargonProperties jp = new SettableJargonProperties();
		log.info("Default threads : " + jp.getMaxParallelThreads());
		jp.setMaxParallelThreads(10);
		log.info("Threads upgraded to : " + jp.getMaxParallelThreads());



		IRODSFileFactory iRODSFileFactory = getIrodsAccountFileFactory(iRODSAccount);

		String parentFileName = iRODSAccount.getUserName();

		if(null!= pathForInternalFiles){
			iRodsFile = iRODSFileFactory.instanceIRODSFile(pathForInternalFiles);
		}
		else{
			iRodsFile = iRODSFileFactory.instanceIRODSFile(Constants.HOME_DIR_IPLANT_HOME
					+ parentFileName);

		}
		irodsImagej.setiRodsFile(iRodsFile);

		IRODSFileSystemAOImpl IRODSFileSystemAOImpl = new IRODSFileSystemAOImpl(
				iRODSSession, iRODSAccount);
		log.info("irods file path" + iRodsFile);

		List<String> listInDir = IRODSFileSystemAOImpl.getListInDir(iRodsFile);

		Iterator<String> listInDirectory = listInDir.iterator();
		int count = 1;
		while (listInDirectory.hasNext()) {
			log.info("Files in Dir:" +count +" " +listInDirectory.next());
			count++;
		}
		//return listInDir;
	}*/
	/**
	 * @param iRODSAccount
	 * @return
	 * @throws JargonException
	 */
	public static IRODSFileFactory getIrodsAccountFileFactory(
			IRODSAccount iRODSAccount) throws JargonException {

		irodsFileSystem = IRODSFileSystem.instance();
		return irodsFileSystem
				.getIRODSFileFactory(iRODSAccount);
	}

	public static List<CollectionAndDataObjectListingEntry> setIrodsFile(IRODSAccount iRODSAccount,String pathForInternalFiles, IrodsImageJBean irodsImagej) throws JargonException{

		/* Setting jargon properties */
		SettableJargonProperties jp = new SettableJargonProperties();
		log.info("Default threads : " + jp.getMaxParallelThreads());
		jp.setMaxParallelThreads(10);
		log.info("Threads upgraded to : " + jp.getMaxParallelThreads());

		IRODSFileFactory iRODSFileFactory = getIrodsAccountFileFactory(iRODSAccount);
		List<CollectionAndDataObjectListingEntry> collectionsUnderGivenAbsolutePath   = null;

		if(null!= pathForInternalFiles){
			
			iRodsFile = iRODSFileFactory.instanceIRODSFile(pathForInternalFiles);
			collectionsUnderGivenAbsolutePath= retrieveCollectionsUnderGivenPath(iRodsFile,irodsImagej);
		}
 else {
			pathForInternalFiles = irodsImagej.getIrodsAccount()
					.getHomeDirectory()
					+ IrodsUtilities.getPathSeperator()
					+ Constants.HOME
					+ IrodsUtilities.getPathSeperator()
					+ irodsImagej.getIrodsAccount().getUserName();

			iRodsFile = iRODSFileFactory
					.instanceIRODSFile(pathForInternalFiles);
			collectionsUnderGivenAbsolutePath= retrieveCollectionsUnderGivenPath(iRodsFile,irodsImagej);

		}
		irodsImagej.setiRodsFile(iRodsFile);
		
		return collectionsUnderGivenAbsolutePath;
	}
	
	private static List<CollectionAndDataObjectListingEntry> retrieveCollectionsUnderGivenPath(
			IRODSFile irodsFileForAbsolutePath, IrodsImageJBean irodsImagej) {

		CollectionAndDataObjectListAndSearchAO collectionAO;
		List<CollectionAndDataObjectListingEntry> collectionsUnderGivenAbsolutePath = null;
		try {
			collectionAO = irodsImagej
					.getIrodsFileSystem()
					.getIRODSAccessObjectFactory()
					.getCollectionAndDataObjectListAndSearchAO(
							irodsImagej.getIrodsAccount());

			collectionsUnderGivenAbsolutePath = collectionAO.listDataObjectsAndCollectionsUnderPath(
					irodsFileForAbsolutePath.getAbsolutePath());

		} catch (JargonException e) {
			log.error("Error while retrieving collectionsUnderGivenAbsolutePath: "
					+ e.getMessage());
		}
		return collectionsUnderGivenAbsolutePath;
	}
}
