package br.ufmg.dcc.labsoft.aserg.modularitycheck.enhancements.processing.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import data.handler.CarryFileMemory;

import br.ufmg.dcc.labsoft.aserg.modularitycheck.enhancements.properties.util.Properties;
import br.ufmg.dcc.labsoft.aserg.modularitycheck.enhancements.properties.util.Utils;

public class AssociationRule {
	private static byte[][] term_cluster_matrix;
	private static ArrayList<String> clusterClasses;
	private static ArrayList<String> classesName;
	private static byte[] intersection;// Contains the index of the class in
										// Classesname
	private static byte[] intersectionItemSet;
	private static int[] clusterSize;
	private static byte[][] itemSet_cluster_matrix;
	private static File[] clusters;
	private static final int MIN_SIZE = 2;
	public static final String SUB_TITLE = "ItemSet of Number ";
	private static String path = "C:\\Users\\Luciana\\Dropbox\\Testes\\GeronimoNew\\Results\\Clusters\\";
	private static String apriori = "C:\\Users\\Luciana\\Dropbox\\Testes\\GeronimoNew\\Results\\Apriori\\regras.txt";

	/***
	 * 1 - Criar um arquivo onde cada linha cont�m as classes de um cluster 2
	 * - Aplicar o Apriori 3 - Selecionar um cluster para deixar a regra e
	 * remover a regra dos demais clusters
	 * 
	 * @throws IOException
	 * @throws FileNotFoundException
	 */

	public static void setClusterDataSet(String path2)
			throws FileNotFoundException, IOException {
		File[] clusters = new File(path).listFiles();
		StringBuilder dataSet = new StringBuilder();
		for (File cluster : clusters) {
			String[] clusterContent = new CarryFileMemory(
					cluster.getAbsolutePath()).carryCompleteFile();
			ArrayList<String> content = new ArrayList<String>();
			for (int i = 1; i < clusterContent.length; i++) {
				if (clusterContent[i].length() > 1
						&& !content.contains(clusterContent[i].trim()))
					content.add(clusterContent[i].trim()); // dataSet.append(clusterContent[i].trim()).append(Properties.COMMA);
			}
			for (String term : content) {
				dataSet.append(term).append(Properties.COMMA);
			}
			dataSet.deleteCharAt(dataSet.length() - 1);
			dataSet.append(Properties.NEW_LINE);
		}
		Utils.writeFile(
				dataSet.toString(),
				"C:\\Users\\Luciana\\Dropbox\\Testes\\GeronimoNew\\Results\\Apriori\\dataAprioriLSICosine.txt");
	}

	public static void readAprioriRules() throws FileNotFoundException,
			IOException {
		ArrayList<String[]> itemSets = new ArrayList<String[]>();
		int[] itemSetSize = null;
		// int numberOfClusters = 0;

		retrieveItems(itemSets);
		retrieveClasses();
		createMatrixOfClusters();
		intersectClusters();
		int zeroClusters = countZeroClusters();

		itemSetSize = new int[itemSets.size()];
		createMatrixOfItemSets(itemSets, itemSetSize);
		intersectItemSets();

		if (zeroClusters > 0) {

			saveExtraClusters(zeroClusters, itemSets, itemSetSize);
			createUnitaryItemsetCluster(itemSetSize, itemSets);// at� aqui o
																// n�mero de
																// clusters
																// tende voltar
																// ao normal

			ArrayList<String> miscellaneousList = getClusterMiscellaneous();
			if (miscellaneousList.size() > 0) {
				createNewClusters();
				saveClusterMiscellaneous(miscellaneousList);
			}

		} else {
			createUnitaryItemsetCluster(itemSetSize, itemSets);
			createNewClusters();

			ArrayList<String> miscellaneousList = getClusterMiscellaneous();
			if (miscellaneousList.size() > 0) {
				createNewClusters();
				saveClusterMiscellaneous(miscellaneousList);
			}

			// int[] selectedClusters = getSmallestClusters();
			// if(selectedClusters[1] == -1) {//save miscellaneous with c1
			// StringBuilder dataC1 = readContent(selectedClusters[0]);
			// saveClusterMiscellaneous(dataC1);
			// }
			// else{//union between c1 and c2
			// unionBetweenTwoClusters(selectedClusters);
			// }
		}
		removeItemsFromClusterFiles();
		deleteFiles();
	}

	/***
	 * Creates new clusters from itemsets
	 * 
	 * @param itemSets
	 * @param itemSetSize
	 * @throws IOException
	 */
	private static void createNewClusters() throws IOException {

		int[] selectedClusters = getTwoSmallClusters();
		StringBuilder items = new StringBuilder();
		items.append(Properties.NEW_LINE);

		for (int clusterId : selectedClusters) {
			for (int item = 0; item < term_cluster_matrix[0].length; item++) {
				if (term_cluster_matrix[clusterId][item] == 1) {
					items.append(clusterClasses.get(item)).append(
							Properties.NEW_LINE);
					term_cluster_matrix[clusterId][item] = 0;
				}
			}
			clusterSize[clusterId] = 0;
		}

		Utils.writeFile(items.toString(), path + "joinedClusters"
				+ selectedClusters[0]);
	}

	/***
	 * Gets the two smallest clusters
	 * 
	 * @return
	 */
	private static int[] getTwoSmallClusters() {

		int size = -1;
		int[] theSmallestClusters = new int[2];
		int count = 0;

		while (count < 2) {
			int smallest = 99999;
			int index = -1;
			for (int value = 0; value < clusterSize.length; value++) {
				if (clusterSize[value] < smallest && size != value
						&& clusterSize[value] > 0) {
					smallest = clusterSize[value];
					index = value;
				}
			}
			size = index;
			theSmallestClusters[count] = index;
			count++;
		}
		return theSmallestClusters;
	}

	/***
	 * Creates the cluster containg only the classes that are spread and are not
	 * in any itemset
	 * 
	 * @param selectedClusters
	 * @throws IOException
	 */
	@SuppressWarnings("unused")
	private static void createSpreadClassCluster(int[] selectedClusters)
			throws IOException {
		StringBuilder items = new StringBuilder();
		items.append(Properties.NEW_LINE);

		// for(int i = 0; i < intersection.length; i++){
		// if(intersection[i] == 1 &&
		// !classesName.contains(clusterClasses.indexOf(i))){
		// items.append(clusterClasses.get(i)).append(Properties.NEW_LINE);
		// intersection[i] = 0;
		// }
		// }

		for (int clusterId : selectedClusters) {
			for (int item = 0; item < term_cluster_matrix[0].length; item++) {
				if (term_cluster_matrix[clusterId][item] == 1) {
					items.append(clusterClasses.get(item)).append(
							Properties.NEW_LINE);
					term_cluster_matrix[clusterId][item] = 0;
				}
			}
		}
		Utils.writeFile(items.toString(), path + "joinedClusters");
	}

	/***
	 * Creates the cluster containing only the itemset with size 1, removes from
	 * the intersection
	 * 
	 * @param itemSetSize
	 * @param itemSets
	 * @throws IOException
	 */
	private static void createUnitaryItemsetCluster(int[] itemSetSize,
			ArrayList<String[]> itemSets) throws IOException {
		StringBuilder items = new StringBuilder();
		String temp = null;
		items.append(Properties.NEW_LINE);
		for (int i = itemSetSize.length - 1; i >= 0; i--) {
			if (itemSetSize[i] == 1) {
				for (int j = 0; j < itemSet_cluster_matrix[0].length; j++) {
					if (itemSet_cluster_matrix[i][j] == 1) {
						boolean canTakeIt = true;
						temp = classesName.get(j);
						itemSet_cluster_matrix[i][j] = 0;

						if (intersectionItemSet[j] == 1) {// search in itemsets
							for (int isfm = 0; isfm < itemSet_cluster_matrix.length; isfm++) {
								if (isfm != i
										&& itemSet_cluster_matrix[isfm][j] == 1
										&& itemSetSize[isfm] > itemSetSize[i])
									canTakeIt = false;
								else if (isfm != i
										&& itemSet_cluster_matrix[isfm][j] == 1) {
									itemSet_cluster_matrix[isfm][j] = 0;
									itemSetSize[isfm] = itemSetSize[isfm] - 1;
								}
							}
						}

						if (canTakeIt) {
							items.append(temp).append(Properties.NEW_LINE);
							intersectionItemSet[j] = 0;
						}
						break;
					}
				}

				itemSetSize[i] = 0;
			}
		}
		Utils.writeFile(items.toString(), path + "UnitaryItemsets");
	}

	/***
	 * Removes the intersection of the item from the term matrix
	 * 
	 * @param name
	 */
	@SuppressWarnings("unused")
	private static void removeFromTermMatrix(String name) {
		int item = clusterClasses.indexOf(name);
		for (int clusterId = 0; clusterId < term_cluster_matrix.length; clusterId++) {
			term_cluster_matrix[clusterId][item] = 0;
			intersection[item] = 0;
			clusterSize[clusterId] = clusterSize[clusterId] - 1;
		}
	}

	/***
	 * Makes the intersection between itemsets and items
	 */
	private static void intersectItemSets() {
		int intersectionTemp = 0;
		intersectionItemSet = new byte[classesName.size()];

		for (int item = 0; item < itemSet_cluster_matrix[0].length; item++) {
			for (int itemSetId = 0; itemSetId < itemSet_cluster_matrix.length; itemSetId++) {
				if (itemSet_cluster_matrix[itemSetId][item] == 1)
					intersectionTemp++;
			}

			if (intersectionTemp > 1) {
				intersectionItemSet[item] = 1;
			}
		}

	}

	/***
	 * Creates the matrix of maximal item sets
	 * 
	 * @param itemSets
	 */
	private static void createMatrixOfItemSets(ArrayList<String[]> itemSets,
			int[] itemSetSize) {
		itemSet_cluster_matrix = new byte[itemSets.size()][classesName.size()];

		for (int itemSet = 0; itemSet < itemSets.size(); itemSet++) {
			String[] items = itemSets.get(itemSet);
			itemSetSize[itemSet] = items.length;

			for (String item : items) {
				itemSet_cluster_matrix[itemSet][classesName
						.indexOf(item.trim())] = 1;
			}
		}

	}

	/***
	 * Reads the apriori file that contains the maximal itemset
	 * 
	 * @param itemSets
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private static void retrieveItems(ArrayList<String[]> itemSets)
			throws FileNotFoundException, IOException {
		classesName = new ArrayList<String>();
		String[] maximalItemset = new CarryFileMemory(apriori)
				.carryCompleteFile();
		for (String line : maximalItemset) {
			String[] items = line.split(" ")[0].split(Properties.COMMA);
			itemSets.add(items);
			for (String item : items) {
				item = item.trim();
				if (!classesName.contains(item))
					classesName.add(item);
			}
		}
	}

	/***
	 * Reads the clusters and retrieve the class names that are not into
	 * itemsets
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private static void retrieveClasses() throws FileNotFoundException,
			IOException {
		clusterClasses = new ArrayList<String>();
		clusters = new File(path).listFiles();
		for (File cluster : clusters) {
			String[] clusterContent = new CarryFileMemory(
					cluster.getAbsolutePath()).carryCompleteFile();

			for (int i = 1; i < clusterContent.length; i++) {
				String term = clusterContent[i].trim();

				if (!term.isEmpty()) {
					if (!clusterClasses.contains(term)
							&& !classesName.contains(term))
						clusterClasses.add(term);
				}
			}
		}
	}

	/***
	 * Creates the matrix cluster versus classes and get the size of each
	 * cluster
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private static void createMatrixOfClusters() throws FileNotFoundException,
			IOException {
		term_cluster_matrix = new byte[clusters.length][clusterClasses.size()];
		clusterSize = new int[clusters.length];
		ArrayList<String> classes = null;
		int clusterId = 0;
		int column = -1;
		for (File cluster : clusters) {
			String[] clusterContent = new CarryFileMemory(
					cluster.getAbsolutePath()).carryCompleteFile();
			classes = new ArrayList<String>();
			for (int i = 1; i < clusterContent.length; i++) {
				String term = clusterContent[i].trim();
				if (!term.isEmpty()) {
					column = clusterClasses.indexOf(term);
					if (column != -1) {
						if (!classes.contains(term))
							classes.add(term);
						term_cluster_matrix[clusterId][column] = 1;
					}
				}
			}
			clusterSize[clusterId] = classes.size();
			clusterId++;
		}
	}

	/***
	 * Detects the intersection, remove it from clusters and save the classes in
	 * intersection when its a itemset class otherwise the smallest clusters
	 * keep the classes
	 * 
	 */
	private static void intersectClusters() {
		// intersection = new byte[term_cluster_matrix[0].length];
		ArrayList<Integer> clusterIdIntersection = null;
		for (int item = 0; item < term_cluster_matrix[0].length; item++) {
			clusterIdIntersection = new ArrayList<Integer>();

			for (int clusterId = 0; clusterId < term_cluster_matrix.length; clusterId++) {
				if (term_cluster_matrix[clusterId][item] == 1)
					clusterIdIntersection.add(clusterId);
			}

			if (clusterIdIntersection.size() > 1) {
				// if(!classesName.contains(clusterClasses.get(item))){
				removeSpreadIntersection(clusterIdIntersection);
				// }else
				// intersection[item] = 0;

				for (int cluster : clusterIdIntersection) {
					term_cluster_matrix[cluster][item] = 0;
					clusterSize[cluster] = clusterSize[cluster] - 1;
				}
			}
		}
	}

	/***
	 * In case when the class does not belong to any itemset, but it has an
	 * intersection, this class remains in the smallest cluster
	 * 
	 * @param clusterIdIntersection
	 * @param item
	 */
	private static void removeSpreadIntersection(
			ArrayList<Integer> clusterIdIntersection) {

		int smallestCluster = clusterIdIntersection.get(0);
		int size = clusterSize[smallestCluster];
		int temp = -1;

		for (int cluster = 1; cluster < clusterIdIntersection.size(); cluster++) {
			temp = clusterIdIntersection.get(cluster);
			if (clusterSize[temp] < size && clusterSize[temp] > 0) {
				smallestCluster = temp;
				size = clusterSize[cluster];
			}
		}

		clusterIdIntersection.remove((Object) smallestCluster);

	}

	/***
	 * Counts how many clusters are empty
	 * 
	 * @return
	 */
	private static int countZeroClusters() {
		int count = 0;
		for (int value : clusterSize) {
			if (value == 0)
				count++;
		}
		return count;
	}

	/***
	 * Saves the extra clusters
	 * 
	 * @param zeroClusters
	 * @param itemSets
	 * @throws IOException
	 */
	private static void saveExtraClusters(int zeroClusters,
			ArrayList<String[]> itemSets, int[] itemSetSize) throws IOException {
		int index = 0;
		while (zeroClusters > 1) {
			index = getTheLargestItemSet(itemSetSize);
			saveNewCluster(index);
			updateItemSetMatrix(itemSetSize, index, itemSets.get(index));
			zeroClusters--;
		}
	}

	private static void updateItemSetMatrix(int[] itemSetSize,
			int itemSetIndex, String[] itemSet) {

		for (String item : itemSet) {
			int index = classesName.indexOf(item);
			if (intersectionItemSet[index] == 1) {
				for (int itemSetId = 0; itemSetId < itemSet_cluster_matrix.length; itemSetId++) {
					if (itemSet_cluster_matrix[itemSetId][index] == 1) {
						itemSet_cluster_matrix[itemSetId][index] = 0;
						itemSetSize[itemSetId] = itemSetSize[itemSetId] - 1;
					}
				}
				intersectionItemSet[index] = 0;
			}
			// intersection[clusterClasses.indexOf(item)] = 0;
		}
	}

	private static int getTheLargestItemSet(int[] itemSetSize) {
		int theLargest = itemSetSize[0];
		int index = 0;
		for (int i = 1; i < itemSetSize.length; i++) {
			if (itemSetSize[i] > theLargest) {
				theLargest = itemSetSize[i];
				index = i;
			}
		}
		itemSetSize[index] = -1;

		return index;
	}

	/***
	 * Saves the itemset as the extra cluster and remove from miscelanea
	 * 
	 * @param id
	 * @throws IOException
	 */
	private static void saveNewCluster(int id) throws IOException {
		StringBuilder content = new StringBuilder();
		content.append(Properties.NEW_LINE);

		for (int item = 0; item < itemSet_cluster_matrix[id].length; item++) {
			if (itemSet_cluster_matrix[id][item] == 1)
				content.append(classesName.get(item)).append(
						Properties.NEW_LINE);

		}
		Utils.writeFile(content.toString(), path + "extra" + id);
	}

	/***
	 * Saves the cluster miscellaneous that maybe contais maximal itemsets
	 * greater than 1
	 * 
	 * @throws IOException
	 */
	private static void saveClusterMiscellaneous(
			ArrayList<String> miscellaneousList) throws IOException {
		StringBuilder miscellaneous = new StringBuilder();
		miscellaneous.append(Properties.NEW_LINE);
		boolean changedItemSet = false;

		for (int itemSetId = 0; itemSetId < itemSet_cluster_matrix.length; itemSetId++) {

			for (String term : miscellaneousList) {
				if (itemSet_cluster_matrix[itemSetId][classesName.indexOf(term)] == 1) {
					if (!changedItemSet) {
						miscellaneous.append(Properties.NEW_LINE)
								.append(SUB_TITLE + itemSetId)
								.append(Properties.NEW_LINE);
						changedItemSet = true;
					}
					miscellaneous.append(term).append(Properties.NEW_LINE);

				}
			}
			changedItemSet = false;
		}

		// for(int index = 0; index < miscellaneousList.size(); index++){
		// miscellaneous.append(miscellaneousList.get(index)).append(Properties.NEW_LINE);
		// }
		Utils.writeFile(miscellaneous.toString(), path + "Miscellaneous");
	}

	// private static void saveClusterMiscellaneous(StringBuilder data) throws
	// IOException {
	// StringBuilder miscellaneous = new StringBuilder();
	// miscellaneous.append(Properties.NEW_LINE);
	// if(data != null) miscellaneous.append(data.toString());
	// for(int index = 0; index < intersectionItemSet.length; index++){
	// if(intersectionItemSet[index] == 1)
	// miscellaneous.append(classesName.get(index)).append(Properties.NEW_LINE);
	// }
	// Utils.writeFile(miscellaneous.toString(), path + "Miscellaneous");
	// }

	/***
	 * Get the classes from miscellaneous
	 * 
	 * @return
	 * @throws IOException
	 */
	private static ArrayList<String> getClusterMiscellaneous()
			throws IOException {
		ArrayList<String> miscellaneous = new ArrayList<String>();
		for (int index = 0; index < intersectionItemSet.length; index++) {
			if (intersectionItemSet[index] == 1)
				miscellaneous.add(classesName.get(index));
		}
		return miscellaneous;
	}

	/***
	 * Verifies if exists unit clusters and returns them. Otherwise, return the
	 * smallest
	 * 
	 * @return
	 */
	@SuppressWarnings("unused")
	private static int[] getSmallestClusters() {
		int smallest = clusterSize[0];
		int index = 0;
		int[] theSmallestClusters = new int[2];
		int count = 0;
		for (int value = 1; value < clusterSize.length; value++) {
			if (count == 2)
				break;
			else if (clusterSize[value] <= MIN_SIZE && clusterSize[value] > 0) {
				theSmallestClusters[count] = value; // get the clusterId
				count++;
			}
		}
		if (count == 1)
			theSmallestClusters[count + 1] = -1;
		else if (count == 0) {
			for (int value = 1; value < clusterSize.length; value++) {
				if (clusterSize[value] < smallest) {
					smallest = clusterSize[value];
					index = value;
				}
			}
			theSmallestClusters[0] = index;
			theSmallestClusters[1] = -1;
		}

		return theSmallestClusters;
	}

	/***
	 * Reads the smallest cluster
	 * 
	 * @param clusterId
	 * @return
	 */
	@SuppressWarnings("unused")
	private static StringBuilder readContent(int clusterId) {
		StringBuilder data = new StringBuilder();
		for (int item = 0; item < term_cluster_matrix[0].length; item++) {
			if (term_cluster_matrix[clusterId][item] == 1)
				data.append(clusterClasses.get(item)).append(
						Properties.NEW_LINE);
		}

		return data;
	}

	/***
	 * Union two clusters, save it and update the clusterSize
	 * 
	 * @param selectedClusters
	 * @throws IOException
	 */
	@SuppressWarnings("unused")
	private static void unionBetweenTwoClusters(int[] selectedClusters)
			throws IOException {
		StringBuilder data = new StringBuilder();
		data.append(Properties.NEW_LINE);
		for (int clusterId : selectedClusters) {
			for (int item = 0; item < term_cluster_matrix[0].length; item++) {
				if (term_cluster_matrix[clusterId][item] == 1) {
					data.append(clusterClasses.get(item)).append(
							Properties.NEW_LINE);
					term_cluster_matrix[clusterId][item] = 0;
				}
			}
		}
		clusterSize[selectedClusters[0]] = clusterSize[selectedClusters[0]]
				+ clusterSize[selectedClusters[1]];
		clusterSize[selectedClusters[1]] = 0;
		Utils.writeFile(data.toString(), path + clusters[selectedClusters[0]]);
	}

	/***
	 * Deletes the cluster files which size is zero
	 */
	private static void deleteFiles() throws IOException {
		System.gc();
		for (int index = 0; index < clusterSize.length; index++) {
			if (clusterSize[index] == 0) {
				System.out.println("Deleting ... " + clusters[index].getName());
				clusters[index].delete();
			}
		}
	}

	/***
	 * Updates the cluster files
	 * 
	 * @param hashClusters
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private static void removeItemsFromClusterFiles()
			throws FileNotFoundException, IOException {
		StringBuilder cleanedCluster = null;

		for (int clusterId = 0; clusterId < term_cluster_matrix.length; clusterId++) {
			if (clusterSize[clusterId] > 0) {
				cleanedCluster = new StringBuilder();
				String[] clusterContent = new CarryFileMemory(
						clusters[clusterId].getAbsolutePath())
						.carryCompleteFile();
				cleanedCluster.append(clusterContent[0].trim()).append(
						Properties.NEW_LINE);
				for (int item = 0; item < term_cluster_matrix[0].length; item++) {
					if (term_cluster_matrix[clusterId][item] == 1) {
						cleanedCluster.append(clusterClasses.get(item)).append(
								Properties.NEW_LINE);
					}
				}
				Utils.writeFile(cleanedCluster.toString(),
						clusters[clusterId].getAbsolutePath());
			}
		}
	}

}
