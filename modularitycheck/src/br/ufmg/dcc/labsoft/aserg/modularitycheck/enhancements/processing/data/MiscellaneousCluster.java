package br.ufmg.dcc.labsoft.aserg.modularitycheck.enhancements.processing.data;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import data.handler.CarryFileMemory;

import br.ufmg.dcc.labsoft.aserg.modularitycheck.enhancements.properties.util.MathImpl;
import br.ufmg.dcc.labsoft.aserg.modularitycheck.enhancements.properties.util.Properties;
import br.ufmg.dcc.labsoft.aserg.modularitycheck.enhancements.properties.util.Quicksort;
import br.ufmg.dcc.labsoft.aserg.modularitycheck.enhancements.properties.util.Utils;

public class MiscellaneousCluster {
	private static String[] topTen;
	private static int[] count;

	private int[] clusterSize;
	private int median;
	private int mean;
	private int theChosenValue;
	private int deviation;
	Map<String, ArrayList<String>> hashClusters;

	private int biggest;

	public MiscellaneousCluster() {
		median = 0;
		mean = 0;
		biggest = 0;
	}

	/***
	 * Read the size of each cluster
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void getClusterSize() throws FileNotFoundException, IOException {
		hashClusters = new HashMap<String, ArrayList<String>>();
		ArrayList<String> classesName = null;
		ArrayList<String> clusters = null;

		for (int i = 0; i < topTen.length; i++) {
			clusters = TestData.getHashFiles().get(topTen[i]);

			for (String cluster : clusters) {
				classesName = new ArrayList<String>();
				classesName.add(topTen[i]);

				if (hashClusters.containsKey(cluster))
					classesName.addAll(hashClusters.get(cluster));
				hashClusters.put(cluster, classesName);
			}
		}
	}

	/***
	 * Get the size for each cluster and calculate the mean
	 */
	private void searchMean() {
		clusterSize = new int[hashClusters.size()];
		Iterator<String> iterator = hashClusters.keySet().iterator();
		mean = 0;
		int index = 0;
		while (iterator.hasNext()) {
			String key = iterator.next();
			clusterSize[index] = hashClusters.get(key).size();
			mean += clusterSize[index];
			System.out.println("Cluster: " + key + "Size: "
					+ clusterSize[index] + "Median:" + median);
			if (clusterSize[index] > biggest)
				biggest = clusterSize[index];
			index++;
		}
		Arrays.sort(clusterSize);
		assessMean();
	}

	/***
	 * Calculate the mean of the clusters mean = (int)mean/clusterSize.length;
	 */
	private void assessMean() {
		mean = (int) MathImpl.getMean(mean, clusterSize.length);
	}

	/***
	 * Calculate the median of the clusters
	 * 
	 */
	private void assessMedian() {
		median = (int) MathImpl.getMedian(clusterSize);
	}

	private void assessDeviation() {
		deviation = (int) MathImpl.getDeviation(clusterSize);
	}

	/***
	 * Retrieve the frequency of each class in the commits
	 */
	private void getClasseFrequency() {
		int top = TestData.getHashFiles().keySet().size();
		topTen = new String[top];
		count = new int[top];
		int index = 0;
		Iterator<String> iterator = TestData.getHashFiles().keySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			topTen[index] = key;
			count[index] = TestData.getHashFiles().get(key).size();
			index++;
		}
		Quicksort.sort(count, topTen);
	}

	/***
	 * 
	 * @param beginInFile
	 *            1 for pam clusters and 2 for lda
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void createMiscellaneousCluster(int beginInFile)
			throws FileNotFoundException, IOException {
		getClasseFrequency();
		getClusterSize();
		searchMean();
		assessMedian();
		assessDeviation();
		printClusterMiscellaneous();
		cleanClusters(beginInFile);
	}

	/***
	 * Save the new cluster
	 * 
	 * @throws IOException
	 */
	private void printClusterMiscellaneous() throws IOException {
		int i = 0;
		StringBuilder finalReport = new StringBuilder();
		// theChosenValue = mean;
		// if(median > theChosenValue) theChosenValue = median;
		theChosenValue = mean + deviation;
		int conta = theChosenValue - 1;
		int temp = count[theChosenValue];
		while (conta >= 0 && count[conta] == temp) {
			conta--;
		}
		int index = conta;
		int quant = theChosenValue - conta;
		conta = theChosenValue - 1;
		temp = count[theChosenValue];
		while (conta < count.length && count[conta] == temp) {
			conta++;
		}
		if (quant > (conta - theChosenValue))
			index = conta - 1;
		theChosenValue = index;
		// theChosenValue = biggest;
		finalReport.append(Properties.NEW_LINE);
		while (i <= theChosenValue && count[i] > 1) {
			finalReport.append(topTen[i]).append(Properties.NEW_LINE);
			i++;
		}
		Utils.writeFile(finalReport.toString(), Properties.getClusterPath()
				+ "Miscellaneous");
	}

	/***
	 * Discard in the clusters frequent classes
	 * 
	 * @param className
	 * @return
	 */
	private boolean removeFrequentClasses(String className) {
		for (int i = 0; i <= theChosenValue; i++) {
			if (className.equals(topTen[i])) {
				return true;
			}
		}
		return false;
	}

	/***
	 * Remove from clusters all frequent classes
	 * 
	 * @param index
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void cleanClusters(int index) throws FileNotFoundException,
			IOException {
		Iterator<String> iterator = hashClusters.keySet().iterator();
		StringBuilder filteredClasses = null;
		ArrayList<String> classesName = null;
		String key = null;
		while (iterator.hasNext()) {
			key = iterator.next();
			classesName = hashClusters.get(key);
			String[] clusterContent = new CarryFileMemory(
					Properties.getClusterPath() + key).carryCompleteFile();
			filteredClasses = new StringBuilder();
			if (index == 1)
				filteredClasses.append(clusterContent[0]).append(
						Properties.NEW_LINE);
			else
				filteredClasses.append(clusterContent[0])
						.append(Properties.NEW_LINE).append(clusterContent[1])
						.append(Properties.NEW_LINE);
			for (String className : classesName) {
				if (!removeFrequentClasses(className))
					filteredClasses.append(className).append(
							Properties.NEW_LINE);
			}
			Utils.writeFile(filteredClasses.toString(),
					Properties.getClusterPath() + key);
		}
	}
}
