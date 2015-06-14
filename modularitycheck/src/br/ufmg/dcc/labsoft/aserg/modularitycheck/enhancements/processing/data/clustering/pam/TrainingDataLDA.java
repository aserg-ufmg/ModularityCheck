package br.ufmg.dcc.labsoft.aserg.modularitycheck.enhancements.processing.data.clustering.pam;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import data.handler.CarryFileMemory;

import br.ufmg.dcc.labsoft.aserg.modularitycheck.enhancements.processing.data.TestData;
import br.ufmg.dcc.labsoft.aserg.modularitycheck.enhancements.properties.util.Properties;
import br.ufmg.dcc.labsoft.aserg.modularitycheck.enhancements.properties.util.Utils;

public class TrainingDataLDA extends TrainingData {

	public TrainingDataLDA(String absolutPath) {
		super(absolutPath);
		Properties.setClusterPath(Properties.LDA_DIRECTORY_NAME);
	}

	public void prepareFinalCluster() throws FileNotFoundException, IOException {
		transferContent();
		copyFileNames();
		TestData.createReport("TrainingLDA", 2);
	}

	/***
	 * Read the LDA file built after clustering the issues and identify the
	 * issues ID
	 * 
	 * @param pathClusteredData
	 * @param termsEntitiesMap
	 * @param comments
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	protected void readFile() throws FileNotFoundException, IOException {
		File[] comments = new File(Properties.getAbsolutPath()
				+ Properties.COMMENTS_ISSUES).listFiles();// Refresh the vector
		int topic = -1;
		List<String> issueFiles = new ArrayList<String>();
		;
		ArrayList<Integer> issues = new ArrayList<Integer>();
		;
		String[] openedClusterFile = new CarryFileMemory(
				Properties.getResultPath() + Properties.LDA_FILE_NAME)
				.carryCompleteFile();// File of lda
		for (String line : openedClusterFile) {
			if (line.startsWith(Properties.ISSUE)) {
				issues.add(Integer.parseInt(line.split(" ")[2]));
			} else if (line.startsWith(Properties.LDA_TOPIC_FILE)) {
				topic++;
				issueFiles.add(line);
			} else if (line.trim().endsWith(Properties.EQ)) {
				for (int idFile : issues) {
					String nameFile = comments[idFile - 1].getName();
					issueFiles.add(nameFile);
				}
				if (issueFiles.size() > 0) {
					termsEntitiesMap.put(String.valueOf(topic), issueFiles);
					issueFiles = new ArrayList<String>();
					issues = new ArrayList<Integer>();
				}

			}
		}
	}

	/***
	 * Copy files java and jsp into the topic
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	protected void copyFileNames() throws FileNotFoundException, IOException {
		int topic = 0;
		List<String> currentEntityTerms = null;
		StringBuilder clusterContent = null;
		Iterator<String> iteratorTopics = termsEntitiesMap.keySet().iterator();
		while (iteratorTopics.hasNext()) {// the key represents the topic number
			currentEntityTerms = termsEntitiesMap.get(iteratorTopics.next());
			clusterContent = new StringBuilder();
			getClasses(currentEntityTerms, clusterContent);
			Utils.writeFile(clusterContent.toString(),
					Properties.getClusterPath() + Properties.LDA_TOPIC_FILE
							+ topic);
			topic++;
		}
	}

	/***
	 * Retrieve the classes in the file to copy into the cluster
	 * 
	 * @param fileName
	 * @return
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private void getClasses(List<String> fileName, StringBuilder clusterContent)
			throws FileNotFoundException, IOException {
		String fileId = null;
		clusterContent.append(fileName.get(0)).append(Properties.NEW_LINE)
				.append("Files:");
		for (int i = 1; i < fileName.size(); i++)
			clusterContent.append(fileName.get(i).trim())
					.append(Properties.TAB);
		clusterContent.append(Properties.NEW_LINE);
		ArrayList<String> classes = new ArrayList<String>();
		for (int i = 1; i < fileName.size(); i++) {
			fileId = fileName.get(i).trim();
			String[] content = new CarryFileMemory(Properties.getFilesPath()
					+ fileId).carryCompleteFile();
			for (String line : content) {
				line = line.trim();
				if (Utils.isValid(line)) {
					String aux = Utils.getClass(line);
					if (!classes.contains(aux))
						classes.add(aux);
					// clusterContent.append(Utils.getClass(line)).append(Properties.NEW_LINE);
				}// if
			}// for
		}
		for (String value : classes) {
			clusterContent.append(value).append(Properties.NEW_LINE);
		}
	}

}
