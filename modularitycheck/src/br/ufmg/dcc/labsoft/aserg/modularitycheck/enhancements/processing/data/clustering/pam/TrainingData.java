package br.ufmg.dcc.labsoft.aserg.modularitycheck.enhancements.processing.data.clustering.pam;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import data.handler.CarryFileMemory;
import br.ufmg.dcc.labsoft.aserg.modularitycheck.enhancements.processing.data.TestData;
import br.ufmg.dcc.labsoft.aserg.modularitycheck.enhancements.properties.util.Properties;
import br.ufmg.dcc.labsoft.aserg.modularitycheck.enhancements.properties.util.Utils;

public abstract class TrainingData {

	Map<String, List<String>> termsEntitiesMap;

	public TrainingData(String absolutPath) {
		Properties.setDefaultPaths(absolutPath);
		Properties.setFilesPath(Properties.FILES_COMMITS);
		termsEntitiesMap = new HashMap<String, List<String>>();
	}

	public void prepareFinalCluster() throws FileNotFoundException, IOException {
		transferContent();
		copyFileNames();
		TestData.createReport(Properties.TRAINING_PHASE, 1);
	}

	/***
	 * Copy classes from the commit files to clusters pathCluster = arg[0]
	 * 
	 * @param pathCluster
	 * @throws IOException
	 */
	protected void transferContent() throws IOException {
		Utils.compareFiles();
		readFile();
		saveCluster();
	}

	protected void readFile() throws FileNotFoundException, IOException {
	}

	/***
	 * Save a file where each line represents a cluster which contains the
	 * issues ID belonging to this cluster.
	 * 
	 * @param termsEntitiesMap
	 * @throws IOException
	 */
	protected void saveCluster() throws IOException {
		Set<String> entry = termsEntitiesMap.keySet();
		StringBuilder content = new StringBuilder();
		Iterator<String> iterator = entry.iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			content.append(termsEntitiesMap.get(key).toString()).append(
					Properties.NEW_LINE);
		}
		Utils.writeFile(content.toString(), Properties.getResultPath()
				+ Properties.PAM_CLUSTER_FILE_NAME);
	}

	/***
	 * For each line of this file contains the name of the files (issues and
	 * commits)
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	protected void copyFileNames() throws FileNotFoundException, IOException {
		String[] openedClusterFile = new CarryFileMemory(
				Properties.getResultPath() + Properties.PAM_CLUSTER_FILE_NAME)
				.carryCompleteFile();
		for (int i = 0; i < openedClusterFile.length; i++) {// For each cluster
			String cluster = openedClusterFile[i].trim().substring(1,
					openedClusterFile[i].length() - 2);// Retrieves the i-file
														// name
			StringBuilder clusterContent = new StringBuilder();
			String[] fileName = cluster.split(Properties.COMMA);
			clusterContent.append("Files: ").append(cluster)
					.append(Properties.NEW_LINE);

			getClasses(fileName, clusterContent);// Open the file which contains
													// the names of changed
													// files
			Utils.writeFile(clusterContent.toString(),
					Properties.getClusterPath() + Properties.PAM_CLUSTER_FILE
							+ i);
		}

	}

	/***
	 * Retrieve the classes in the file to copy into the cluster
	 * 
	 * @param fileName
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private void getClasses(String[] fileName, StringBuilder clusterContent)
			throws FileNotFoundException, IOException {
		StringBuilder storeClasses = null;
		for (String fileId : fileName) {
			storeClasses = new StringBuilder();
			fileId = fileId.trim();
			String[] content = new CarryFileMemory(Properties.getFilesPath()
					+ fileId).carryCompleteFile();
			for (String line : content) {
				line = line.trim();
				// qualquer coisa voltar o q era antes tirando o readPackage
				if (Utils.isValid(line)) {
					// String pkg = Utils.readPackage(line);
					// if(!pkg.isEmpty()){
					// storeClasses.append(pkg+Properties.PERIOD+Utils.getClass(line)).append(Properties.NEW_LINE);
					// }
					storeClasses.append(Utils.getClass(line)).append(
							Properties.NEW_LINE);
				}
			}
			clusterContent.append(storeClasses.toString()).append(
					Properties.NEW_LINE);
		}
	}
}
