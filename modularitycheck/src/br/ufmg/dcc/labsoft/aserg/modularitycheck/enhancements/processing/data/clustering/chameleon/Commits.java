package br.ufmg.dcc.labsoft.aserg.modularitycheck.enhancements.processing.data.clustering.chameleon;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import data.handler.CarryFileMemory;

import br.ufmg.dcc.labsoft.aserg.modularitycheck.enhancements.properties.util.Properties;
import br.ufmg.dcc.labsoft.aserg.modularitycheck.enhancements.properties.util.Utils;

public abstract class Commits implements IRepository {

	protected ArrayList<String> packages = new ArrayList<String>();
	protected Map<Integer, Integer[]> packageMapping;
	protected ArrayList<String[]> edges;
	protected ArrayList<String> vertexes;

	public void readCommits(String path) throws FileNotFoundException,
			IOException {
		initialize(path);
		openCommits();
	}

	public ArrayList<String> getPackages() {
		return packages;
	}

	public Map<Integer, Integer[]> getMapping() {
		return packageMapping;
	}

	/******* Private Methods **********/

	/***
	 * Opens all commit files and retrieves the package names and link each of
	 * them by commit
	 * 
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private void openCommits() throws FileNotFoundException, IOException {
		int controller = 0;
		packageMapping = new HashMap<Integer, Integer[]>();
		Map<Integer, Integer> histogram = new HashMap<Integer, Integer>();
		System.out.println("Starting to open commits.");
		File[] listing = new File(Properties.getFilesPath()).listFiles();
		for (File commitFile : listing) {

			Integer[] indexes = this.readCommit(commitFile.getAbsolutePath());
			if (indexes.length > 1) {
				packageMapping.put(controller, indexes);
				controller++;
			}
			if (histogram.containsKey(indexes.length))
				histogram
						.put(indexes.length, histogram.get(indexes.length) + 1);
			else
				histogram.put(indexes.length, 1);

		}
		System.out.println("Number of evaluated commits: " + listing.length);

		System.out.println("#Classes \t #Commits");
		Iterator<Integer> iterator = histogram.keySet().iterator();
		while (iterator.hasNext()) {
			int key = iterator.next();
			System.out.println(key + ";" + histogram.get(key));
		}

		listing = null;
		System.gc();
	}

	public Map<String, Integer[]> openClusterCommits()
			throws FileNotFoundException, IOException {
		Map<String, Integer[]> mapping = new HashMap<String, Integer[]>();

		File[] listing = new File(Properties.getFilesPath()).listFiles();
		for (File commitFile : listing) {

			Integer[] indexes = this.readCommit(commitFile.getAbsolutePath());
			if (indexes.length > 1) {
				mapping.put(commitFile.getName(), indexes);
			}

		}
		return mapping;
	}

	public Map<String, ArrayList<String>> openTestCommits()
			throws FileNotFoundException, IOException {
		Map<String, ArrayList<String>> testHash = new HashMap<String, ArrayList<String>>();
		File[] listing = new File(Properties.getAbsolutPath() + "filesTest\\")
				.listFiles();
		for (File commitFile : listing) {
			ArrayList<String> testPackages = readTestCommit(commitFile
					.getAbsolutePath());
			if (testPackages.size() > 0)
				testHash.put(commitFile.getName(), testPackages);
		}
		return testHash;
	}

	private ArrayList<String> readTestCommit(String absolutePath)
			throws FileNotFoundException, IOException {
		String[] openedFile = new CarryFileMemory(absolutePath)
				.carryCompleteFile();
		String name = null;
		ArrayList<String> associatedPackages = new ArrayList<String>();

		for (String line : openedFile) {
			if (Utils.isValid(line) && line.contains(Properties.TRUNK)) {
				// name = Utils.readPackage(line.split(Properties.TRUNK)[1]);
				name = Utils.readClass(line.split(Properties.TRUNK)[1]);
				if (!associatedPackages.contains(name))
					associatedPackages.add(name);
			}
		}

		return associatedPackages;
	}



	public void filterCommits() throws FileNotFoundException, IOException {
		StringBuilder cache = new StringBuilder();
		File[] listing = new File(Properties.getFilesPath()).listFiles();

		File path = new File(Properties.getFilesPath()).getParentFile();
		String file = path.getAbsolutePath() + Properties.COMMENTS_CLUSTER
				+ Properties.getClusterId();
		File commentFolder = new File(file);
		commentFolder.mkdir();
		file = path.getAbsolutePath() + Properties.FILES_CLUSTER
				+ Properties.getClusterId();
		File fileFolder = new File(file);
		fileFolder.mkdir();

		for (File commitFile : listing) {

			this.read(commitFile, cache, path);
		}
		Utils.writeFile(cache.toString(), Properties.getResultPath()
				+ Properties.CLUSTER_COMMITS);
	}

	private void initialize(String absolutPath) {
		Properties.setDefaultPaths(absolutPath);
		Properties.setFilesPath(Properties.FILES_COMMITS);
	}

	public void updateMapping(ArrayList<Integer> removedIndexes) {
		Iterator<Integer> keySet = packageMapping.keySet().iterator();
		Map<Integer, Integer[]> hash = new HashMap<Integer, Integer[]>();

		ArrayList<String> temp = null;
		ArrayList<String> packageTemp = new ArrayList<String>();
		for (String pkg : packages) {
			if (!removedIndexes.contains(packages.indexOf(pkg)))
				packageTemp.add(pkg);
		}
		System.out.println("Final graph.");
		//StringBuilder apriori = new StringBuilder();

		while (keySet.hasNext()) {
			temp = new ArrayList<String>();
			int key = keySet.next();
			Integer[] indexes = packageMapping.get(key);
			for (int index : indexes) {
				if (!removedIndexes.contains(index))
					temp.add(packages.get(index));
			}
			indexes = new Integer[temp.size()];
			int i = 0;
			for (String value : temp) {
				indexes[i] = packageTemp.indexOf(value);
				i++;
			}

			if (indexes.length > 0) {
				hash.put(key, indexes);
			//	apriori.append(temp.toString()).append(Properties.NEW_LINE);
			}
		}
		packageMapping = hash;
		packages = new ArrayList<String>();
		packages.addAll(packageTemp);

	/*	try {
			Utils.writeFile(apriori.toString(), Properties.getResultPath()
					+ "apriori.data");
		} catch (IOException e) {
			e.printStackTrace();
		}*/
	}

	public void setEdges(ArrayList<String[]> edge) {
		edges = new ArrayList<String[]>();
		edges.addAll(edge);
	}

	public void setVertexes(ArrayList<String> vertex) {
		vertexes = new ArrayList<String>();
		vertexes.addAll(vertex);
	}

}
