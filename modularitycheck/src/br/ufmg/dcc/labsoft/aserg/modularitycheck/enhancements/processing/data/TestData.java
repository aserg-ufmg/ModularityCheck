package br.ufmg.dcc.labsoft.aserg.modularitycheck.enhancements.processing.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import br.ufmg.dcc.labsoft.aserg.modularitycheck.enhancements.properties.util.Properties;
import br.ufmg.dcc.labsoft.aserg.modularitycheck.enhancements.properties.util.Utils;
import data.handler.CarryFileMemory;

public class TestData {
	private static Map<String, ArrayList<String>> hashFiles = new HashMap<String, ArrayList<String>>();
	private static ArrayList<String> packages = new ArrayList<String>();

	public static Map<String, ArrayList<String>> getHashFiles() {
		return hashFiles;
	}

	/***
	 * Create the report containing the number of packages and in how many
	 * clusters the classes is spread for each commit
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void createReport(String nameReport, int indexResultFile)
			throws FileNotFoundException, IOException {
		hashFiles = new HashMap<String, ArrayList<String>>();
		StringBuilder finalReport = new StringBuilder();
		getClassesIntoClusters(hashFiles, indexResultFile);
		File[] listing = new File(Properties.getFilesPath()).listFiles();
		for (File commit : listing) {
			reportCommit(commit, finalReport, hashFiles);
		}
		Properties.setReportPath();
		Utils.writeFile(finalReport.toString(), Properties.getReportPath()
				+ Properties.REPORT_FILE_NAME + nameReport);
	}

	/***
	 * 
	 * @param commit
	 * @param finalReport
	 * @param hashFiles
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private static void reportCommit(File commit, StringBuilder finalReport,
			Map<String, ArrayList<String>> hashFiles)
			throws FileNotFoundException, IOException {
		Map<String, Integer> hash = new HashMap<String, Integer>();
		String[] openCommit = new CarryFileMemory(commit.getAbsolutePath()
				.toString()).carryCompleteFile();
		ArrayList<String> classes = countDistinctPackagesCoarseGrained(
				openCommit, hash);
		int countCluster = verifyCommit(classes, hashFiles);
		if (hash.keySet().size() > 0) {
			finalReport.append(commit.getName()).append(Properties.TAB)
					.append(hash.keySet().size()).append(Properties.TAB)
					.append(String.valueOf(countCluster))
					.append(Properties.NEW_LINE);
			System.out.println(commit.getName());
		}
	}

	/***
	 * Carry into memory all name of the clusters in order to read their
	 * contents
	 * 
	 * @param hashFiles
	 * @param indexResultFile
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private static void getClassesIntoClusters(
			Map<String, ArrayList<String>> hashFiles, int indexResultFile)
			throws FileNotFoundException, IOException {
		File[] clusters = new File(Properties.getClusterPath()).listFiles();
		verifyAllClusters(hashFiles, clusters, indexResultFile);
	}

	/***
	 * Make the union between the clusters and count how many clusters a commit
	 * is spread
	 * 
	 * @param classes
	 * @param hashFiles
	 * @return
	 */
	private static int verifyCommit(ArrayList<String> classes,
			Map<String, ArrayList<String>> hashFiles) {
		ArrayList<String> union = new ArrayList<String>();
		ArrayList<String> clusters = null;
		for (String className : classes) {
			if (hashFiles.containsKey(className)) {
				clusters = hashFiles.get(className);
				for (String cluster : clusters) {
					if (!union.contains(cluster))
						union.add(cluster);
				}
			}
		}
		return union.size();
	}

	/***
	 * For each cluster retrieve the classes and find out all clusters that a
	 * specific class is present
	 * 
	 * @param hashFiles
	 * @param clusters
	 * @param indexResultFile
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private static void verifyAllClusters(
			Map<String, ArrayList<String>> hashFiles, File[] clusters,
			int indexResultFile) throws FileNotFoundException, IOException {
		String key = null;
		for (File cluster : clusters) {
			Map<String, Integer> hashTemp = new HashMap<String, Integer>();
			String[] clusterContent = new CarryFileMemory(
					cluster.getAbsolutePath()).carryCompleteFile();
			for (int i = indexResultFile; i < clusterContent.length; i++) {
				key = clusterContent[i].trim();
				if (!key.isEmpty()
						&& !key.startsWith(AssociationRule.SUB_TITLE)
						&& !hashTemp.containsKey(key))
					hashTemp.put(key, 1);
			}
			saveClusterId(hashTemp.keySet().iterator(), cluster.getName(),
					hashFiles);
		}
	}

	/***
	 * For each class insert the cluster id
	 * 
	 * @param iterator
	 * @param cluster
	 * @param hashFiles
	 */
	private static void saveClusterId(Iterator<String> iterator,
			String cluster, Map<String, ArrayList<String>> hashFiles) {
		ArrayList<String> clustersId = null;
		String key = null;

		while (iterator.hasNext()) {
			String packageNm = "";
			key = iterator.next();
			String[] vet = key.split(Properties.DOT);
			for (int i = 0; i < vet.length - 2; i++) {
				packageNm += Properties.PERIOD + vet[i];
			}
			if (!packages.contains(packageNm))
				packages.add(packageNm);
			clustersId = new ArrayList<String>();
			clustersId.add(cluster);
			if (hashFiles.containsKey(key)) {
				clustersId.addAll(hashFiles.get(key));
				hashFiles.put(key, clustersId);
			} else
				hashFiles.put(key, clustersId);
		}

	}

	/***
	 * Count how many distinct packages exist and get the classes belonging into
	 * the commit
	 * 
	 * @param openCommit
	 * @param hash
	 * @param hashFiles
	 */
	@SuppressWarnings("unused")
	private static ArrayList<String> countDistinctPackages(String[] openCommit,
			Map<String, Integer> hash) {
		ArrayList<String> classes = new ArrayList<String>();
		for (String line : openCommit) {
			line = line.trim();
			if (Utils.isValid(line)) {
				String pkg = Utils.getPackage(line);
				if (!pkg.isEmpty()) {
					if (hash.containsKey(pkg))
						hash.put(pkg, hash.get(pkg) + 1);
					else
						hash.put(pkg, 1);
					line = Utils.getClass(line);
					if (!classes.contains(line))
						classes.add(line);
				}
			}
		}
		return classes;
	}

	/***
	 * Count how many distinct coarse grained packages exist and get the classes
	 * belonging into the commit
	 * 
	 * @param openCommit
	 * @param hash
	 * @param hashFiles
	 */
	private static ArrayList<String> countDistinctPackagesCoarseGrained(
			String[] openCommit, Map<String, Integer> hash) {
		ArrayList<String> classes = new ArrayList<String>();
		for (String line : openCommit) {
			line = line.trim();
			if (Utils.isValid(line) && line.contains(Properties.TRUNK)) {
				String pkg = Utils.readPackage(line);
				if (!pkg.isEmpty()) {
					if (hash.containsKey(pkg))
						hash.put(pkg, hash.get(pkg) + 1);
					else
						hash.put(pkg, 1);
					line = Utils.getClass(line);
					// String temp = pkg+Properties.PERIOD+line;
					if (!classes.contains(line))
						classes.add(line);
				}
			}
		}
		return classes;
	}
}
