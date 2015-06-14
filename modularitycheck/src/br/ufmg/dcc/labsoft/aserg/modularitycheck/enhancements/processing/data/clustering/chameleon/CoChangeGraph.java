package br.ufmg.dcc.labsoft.aserg.modularitycheck.enhancements.processing.data.clustering.chameleon;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import data.handler.CarryFileMemory;
import br.ufmg.dcc.labsoft.aserg.modularitycheck.bugparser.parser.Parser;
import br.ufmg.dcc.labsoft.aserg.modularitycheck.enhancements.metrics.Focus;
import br.ufmg.dcc.labsoft.aserg.modularitycheck.enhancements.properties.util.Properties;
import br.ufmg.dcc.labsoft.aserg.modularitycheck.enhancements.properties.util.Utils;

public class CoChangeGraph {
	
	private static List<int[]> edges;
	private static Map<Integer, List<int[]>> packageGraph;
	private static int edgeNumber;
	private static int countVertexes;
	protected static List<String> keys;
	public static List<String> list;
	private static List<String> allPackages;
	private static ClusterMetrics[] clusterMetrics;
	
	public static final double DENSITY = 0.5;
	private static final double SUPPORT = 2;
	public static final int MIN_SIZE = 4;
	private static final String R_CLASS_FILE = "packages.rlabel";
	public static final String CLUSTER_FILE = "sparse.graph.clustering."; // chameleon's output
	
	/**** class interface 
	 * @throws IOException ************/
	
	
	public static void retrieveGraph(String path, int type, boolean preProcessed) throws IOException {
		Commits commit = null;

		switch (type) {
		case Parser.MANAGER_GIT:
			commit = new GITCommit();
		case Parser.MANAGER_SVN:
			commit = new SVNCommit();
		default:
			break;
		}

		try {
			commit.readCommits(path);
		} catch (IOException e) {
			e.printStackTrace();
		}

		List<String> packages = commit.getPackages();
		
		System.out.println("Creating co-change Graph");
		upDateAttributes();
		
		createAdjacencyList(commit.getMapping(), packages);
		
		System.out.println("Pruning edges with small weight");
		packages = preProcessGraph(packages);
		
		System.out.println("Detecting connected components");
		packages = detectGraphComponents(packages);
		
		System.out.println("Updating indexes in hash table");
		clearPackages(packages);
		updateAdjacencyList(packages);
		
		if (!preProcessed) {
			preProcessPhase(packages);
			
			System.out.println("Co-change graph created");
			allPackages = new ArrayList<String>();
			allPackages.addAll(packages);
		}else{
			detectPatterns();
		}
		
		
	}
	
	


	
	private static void updateAdjacencyList(List<String> packages) {
		
		List<String> packageTemp = new ArrayList<String>();
		Map<Integer, List<int[]>> packageMapTemp = new HashMap<Integer, List<int[]>>();
		
		Set<Integer> keys = packageGraph.keySet();
		String className = "";
		String keyName = "";
		List<int[]> adjacents = null;
		List<int[]> tempAdjacents = null;
		
		
	    for(Integer key : keys){
	    	
	    	keyName = packages.get(key);
	    	if(!packageTemp.contains(keyName)) packageTemp.add(keyName);
	    	
	    	adjacents = packageGraph.get(key);
	    	tempAdjacents = new ArrayList<int[]>();
	    	
	    	for(int[] adjacent : adjacents){
	    		className = packages.get(adjacent[0]);
	    		
	    		if(!packageTemp.contains(className)) packageTemp.add(className);
	    		
	    		tempAdjacents.add(new int[]{packageTemp.indexOf(className), adjacent[1]});
	    	}
	    	
	    	packageMapTemp.put(packageTemp.indexOf(keyName), tempAdjacents);
	    }
	    packageGraph.clear();
	    packageGraph.putAll(packageMapTemp);
	    packageMapTemp = null;
	    packages.clear();
	    packages.addAll(packageTemp);
	    packageTemp = null;
		
	}

	
	private static void clearPackages(List<String> packages) {
		int index = -1;
		for(String p : packages){
			index = packages.indexOf(p);
			if(!p.isEmpty() && !packageGraph.containsKey(index)) packages.set(index, ""); 
		}
	}

	/***
	 * After performing chameleon the co-change patterns are detected from clusters
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static void detectPatterns() throws FileNotFoundException, IOException {
		Map<String, List<String>> hash = readClusterFile();
		clusterMetrics = new ClusterMetrics[keys.size()];
		
		Focus.measureFocus(hash, keys, clusterMetrics);
		calculateClusterDensity(allPackages, hash);
	}
	
	
	public static int getCountVertexes() {
		return countVertexes;
	}
	
	public static boolean statusCoChangeGraph(){
		return (packageGraph!=null && packageGraph.size() > 9);
	}
	
/********* private methods *****************/
	
	
	/****** second phase **********/
	
	
	public static Map<String, List<String>> readClusterFile() throws FileNotFoundException, IOException {
		
		Map<String, List<String>> hash = new HashMap<String, List<String>>();

		File[] files = new File(Properties.getResultPath()).listFiles();
		int k;

		for (k = 0; k < files.length; k++) {
			if (files[k].getName().startsWith(CLUSTER_FILE))
				break;
		}

		String[] openedFile = new CarryFileMemory(Properties.getResultPath()
				+ files[k].getName()).carryCompleteFile();

		List<String> temp = null;

		for (int i = 0; i < allPackages.size(); i++) {
			String line = openedFile[i].trim();
			if (!line.isEmpty() && !line.equals("-1")) {
				if (!hash.containsKey(line))
					temp = new ArrayList<String>();
				else
					temp = hash.get(line);

				temp.add(allPackages.get(i));
				hash.put(line, temp);
			}
		}

		saveClusters(hash, Properties.CLUSTER_GLUTO);
		return hash;
	}
	
	
	public static void saveClusters(Map<String, List<String>> hash, String clusterFile) throws IOException {
		
		Iterator<String> keySet = hash.keySet().iterator();
		list = new ArrayList<String>();
		while (keySet.hasNext()) {
			list.add(keySet.next());
		}

		Collections.sort(list);
		keySet = list.iterator();
		
		

		ArrayList<String> clusters = new ArrayList<String>();
		keys = new ArrayList<String>();

		while (keySet.hasNext()) {
			StringBuilder cache = new StringBuilder();
			String key = keySet.next();


			List<String> packages = hash.get(key);
			if (packages.size() >= MIN_SIZE) {
				keys.add(key);
				for (String pkg : packages)
					cache.append(pkg).append(Properties.COMMA);
				cache.deleteCharAt(cache.lastIndexOf(Properties.COMMA));
				clusters.add(cache.toString());
			}

		}
		StringBuilder cache = new StringBuilder();
		int index = 0;
		for (String value : clusters) {
			if (value != null) {
				cache.append(value).append(Properties.NEW_LINE);
				clusters.set(index, null);
			}
			index++;
		}

		clusters = null;
		Utils.writeFile(cache.toString(), Properties.getResultPath() + clusterFile);
	}
	
	
	
	public static void calculateClusterDensity(List<String> packages,
			Map<String, List<String>> hash) {
		
		StringBuilder densityCache = new StringBuilder();
		StringBuilder weightCache = new StringBuilder();

		System.out.println("Cluster Density");
		int clusterId = 0;
		int numberOfEdges = 0;
		double sumOfEdges;
		double weight = 0;
		double density = 0;
		int completeGraph = 0;
		
		Iterator<String> iterator = keys.iterator();
		
		dropDuplicates();
		
		while (iterator.hasNext()) {//for each cluster
			String key = iterator.next();
			List<String> classes = hash.get(key);

				int[] indexes = new int[classes.size()];
				for (int i = 0; i < classes.size(); i++) {
					indexes[i] = packages.indexOf(classes.get(i));
				}
				
				numberOfEdges = 0;
				sumOfEdges = 0;
				for(int[] edge : edges){
					if(contains(indexes, edge[0]) && contains(indexes, edge[1])) {
						numberOfEdges++;
						int value = calculateClusterWeight(clusterId, edge);
						if(value != -1 ) sumOfEdges += value;
						else System.out.println("Error: " + edge.toString());
					}
				}
				

				completeGraph = (classes.size() * (classes.size() - 1)) / 2;
				density = (double) numberOfEdges / completeGraph;
				weight = sumOfEdges / (double) numberOfEdges;
				
				clusterMetrics[clusterId].setDensity(density);
				clusterMetrics[clusterId].setSize(classes.size());
				clusterMetrics[clusterId].setWeight(weight);
				
				densityCache.append(String.valueOf(density)).append(
						Properties.NEW_LINE);
				
				weightCache.append(String.valueOf(weight)).append(
						Properties.NEW_LINE);
				
				clusterId++;
		}//end
		
		try {
			Utils.writeFile(densityCache.toString(), Properties.getResultPath()
					+ Properties.DENSITY_DATA);
			Utils.writeFile(densityCache.toString(), Properties.getResultPath()
					+ Properties.WEIGHT_DATA);			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	private static void dropDuplicates() {
		
		List<int[]> edge = new ArrayList<int[]>();
		Set<Integer> keys = packageGraph.keySet();
		List<int[]> adjacents = null;
		List<Integer> analyzedKeys = new ArrayList<Integer>();
		
		for(int key : keys) {
			
			adjacents = packageGraph.get(key);
			analyzedKeys.add(key);
			
			for(int[] vj : adjacents) 
				if(!analyzedKeys.contains(vj[0])) edge.add(new int[]{key, vj[0]});
		}
		edges.clear();
		edges.addAll(edge);
	}

	/***
	 * 
	 * @param indexes
	 * @param v
	 * @return
	 */
	private static boolean contains(int[] indexes, int v) {
		for(int vertice : indexes){ 
			if(vertice == v) {
				return true;
			}
		}
	
		return false;
	}
	
	/***
	 * 
	 * @param clusterId
	 * @param edge
	 */
	public static int calculateClusterWeight(int clusterId, int[] edge) {
		
				List<int[]> adjacents = packageGraph.get(edge[0]);
				
				for(int[] v : adjacents)
						if(v[0] == edge[1]) return v[1];

				return -1;

	}

	
	
	/*** First phase *******/
	
	/***
	 * Prepare the base to run chameleon
	 * @param packages
	 * @throws IOException
	 */
	private static void preProcessPhase(List<String> packages) throws IOException {
		createFormatClutoAdjacencyList(packages);
		saveRClassFile(packages);
		countVertexes = packageGraph.size();
	}
	
	private static void saveRClassFile(List<String> packages)
			throws IOException {
		StringBuilder cache = new StringBuilder();
		for (String pkg : packages) 
			cache.append(pkg).append(Properties.NEW_LINE);
		
		Utils.writeFile(cache.toString(), Properties.getResultPath()
				+ R_CLASS_FILE);
	}

	public static void createFormatClutoAdjacencyList(List<String> packages) throws IOException {
		StringBuilder cache = new StringBuilder();
		StringBuilder temp = new StringBuilder();
		List<int[]> edges = null;
		edgeNumber = 0;
		
		countNumberEdges();	
				
		for(int key = 0; key < packages.size(); key++){
			edges = packageGraph.get(key);
			for(int[] adjacents : edges) 
				temp.append(String.valueOf(adjacents[0]+1) + Properties.BLANK + String.valueOf((double)adjacents[1])+ Properties.BLANK);
			
			temp.deleteCharAt(temp.lastIndexOf(Properties.BLANK));
			temp.append(Properties.NEW_LINE);
		}
		

		cache.append(String.valueOf(packages.size()) + Properties.BLANK + String.valueOf(edgeNumber) + Properties.NEW_LINE);
		cache.append(temp.toString());
		temp = null;

		Utils.writeFile(cache.toString(), Properties.getResultPath()
				+ Properties.CLUTO_GRAPH);
	}
	

	private static void countNumberEdges() {
		edges = getEdges(packageGraph.size());
		
		Collections.sort(edges, new Comparator<int[]>() {
            public int compare(int[] vi, int[] vj) {
            	return new Integer(vi[1]).compareTo(new Integer(vj[1]));
            }
        });
		
		for(int[] edge : edges) Arrays.sort(edge);
		
		Set<int[]> graphEdges = new HashSet<int[]>(edges);
		edges.clear();
		edges.addAll(graphEdges);
		edgeNumber = edges.size();
		graphEdges = null;
	}

	
	private static void createAdjacencyList(Map<Integer, Integer[]> packageMapping,
			List<String> packages) {

		Iterator<Integer> keySet = packageMapping.keySet().iterator();
		
		while (keySet.hasNext()) {
			int key = keySet.next();
			Integer[] indexes = packageMapping.get(key);
			if (indexes.length > 1) {
				for (int i = 0; i < indexes.length; i++) {
					String temp = packages.get(indexes[i]);
					if (temp.startsWith(Properties.BAR))
						temp = temp.substring(1);
					for (int j = i + 1; j < indexes.length; j++) {

						String temp2 = packages.get(indexes[j]);
						if (temp2.startsWith(Properties.BAR))
							temp2 = temp2.substring(1);
						
						if(packageGraph.containsKey(indexes[i])) insertAdjacentVertice(indexes[i], indexes[j]);
						else insertNewKey(indexes[i], indexes[j]);

						
						if(packageGraph.containsKey(indexes[j])) insertAdjacentVertice(indexes[j], indexes[i]);
						else insertNewKey(indexes[j], indexes[i]);

					}
				}
			}
		}
		System.out.println("Tamanho do co-change graph:"+packageGraph.size());
	}

	
	private static void insertNewKey(Integer vi, Integer vj) {
		List<int[]> adjacents = new ArrayList<int[]>();
		adjacents.add(new int[]{vj, 1});
		packageGraph.put(vi, adjacents);
		
	}

	private static void insertAdjacentVertice(Integer key, int vj) {
		List<int[]> adjacents = null;
		int[] adjacentVertex = null;
		int index = -1;

		adjacents = packageGraph.get(key);
		index = searchAdjacentVertex(vj, adjacents);
		if(index == -1) {
			adjacentVertex = new int[]{vj, 1};
			adjacents.add(adjacentVertex);
		}
		else {
			adjacentVertex = adjacents.get(index);
			adjacentVertex[1] += 1.0;
			adjacents.set(index, adjacentVertex);
		}
		packageGraph.put(key, adjacents);
		
	}

	private static int searchAdjacentVertex(Integer vj,
			List<int[]> adjacents) {
		for(int i = 0; i < adjacents.size(); i++) {
			if(adjacents.get(i)[0] == vj) return i;
		}
		return -1;
	}

	/***
	 * Prune edges with small weights and update the co-change graph
	 * @param packages
	 * @return
	 */
	private static List<String> preProcessGraph(
			List<String> packages) {
		System.out.println("Number of vertices: " + packageGraph.size());
		List<Integer> removedIndexes = pruneGraph();
		System.out.println("Number of removed vertices");
		packages = updateClasses(removedIndexes, packages);
		
		return packages;
	}



	/***
	 * update the matrix packageGraph and the list packages 
	 * 
	 * @param removedIndexes
	 * @param packages
	 * @return
	 */
	private static List<String> updateClasses(List<Integer> removedIndexes, List<String> packages) {
		
		for(int remove : removedIndexes) {
			packageGraph.remove(remove);
			packages.set(remove, "");
		}
		
		List<Integer> newRemoving = new ArrayList<Integer>();
		List<int[]> adjacent = null;
		List<int[]> updatedEdges = null;
		Set<Integer> keySet = packageGraph.keySet();
		
		for(int key : keySet){
			adjacent = packageGraph.get(key);
			updatedEdges = new ArrayList<int[]>();
			
			for(int[] vj : adjacent) if(!removedIndexes.contains(vj[0])) updatedEdges.add(vj);
			
			if(updatedEdges.size() > 0) packageGraph.put(key, updatedEdges);
			else newRemoving.add(key);
		}
		
		for(int key : newRemoving) packageGraph.remove(key);
		
		return packages;
	}


	private static List<Integer> pruneGraph() {

		List<Integer> removedVertices = new ArrayList<Integer>();
		Set<Integer> keySet = packageGraph.keySet();
		List<int[]> adjacents = null;
		List<int[]> newAdjacents = null;
		
		
		for(Integer key : keySet){
			adjacents = packageGraph.get(key);
			newAdjacents = new ArrayList<int[]>();
			
			if(adjacents.size() == 0) removedVertices.add(key);
			else{
				for(int[] temp : adjacents){
					if(temp[1] >= SUPPORT) newAdjacents.add(temp);
				}
				if(newAdjacents.size() > 0) packageGraph.put(key, newAdjacents);
				else removedVertices.add(key);
			}
		}
		
		return removedVertices;
	}
	
	
	
	private static List<String>  detectGraphComponents(List<String> packages) throws IOException {
		
		List<Integer> vertexes = new ArrayList<Integer>(packageGraph.keySet());
		List<Integer> adjacentVertex = new ArrayList<Integer>();
		List<Integer> removeKeys = new ArrayList<Integer>();
		int nAdjacency = 0;
		
		Collections.sort(vertexes);
		

		while(!vertexes.isEmpty()){
			nAdjacency = packageGraph.get(vertexes.get(0)).size();
			if((nAdjacency + 1) < MIN_SIZE){
				
				adjacentVertex.add(vertexes.get(0));
				
				getVerticesAdjacents(vertexes.get(0), adjacentVertex, 1);
				
				if(adjacentVertex.size() < MIN_SIZE) {
					removeKeys.addAll(adjacentVertex);					
				}
				vertexes.removeAll(adjacentVertex);
			}else vertexes.remove(0);
			nAdjacency = 0;
			adjacentVertex = new ArrayList<Integer>();
		}
		
		packages = posProcessGraph(packages, removeKeys);
			
		return packages;
		}
	
	
	private static void getVerticesAdjacents(Integer vertex, List<Integer> adjacentVertex, int index) {
		List<int[]> adjacents = packageGraph.get(vertex);
		
		for(int i = 0; i < adjacents.size(); i++) if(!adjacentVertex.contains(adjacents.get(i)[0])) adjacentVertex.add(adjacents.get(i)[0]);
		
		if(index < adjacentVertex.size() && adjacentVertex.size() < MIN_SIZE) 
			getVerticesAdjacents(adjacentVertex.get(index), adjacentVertex, index+1);
		
		
	}

	private static List<String> posProcessGraph(List<String> packages,
			List<Integer> removeKeys) {
		
		for(Integer key : removeKeys) {
			packageGraph.remove(key);
			packages.set(key, "");
		}
		
		return packages;
	}

	/***
	 * Retrieve graph's edges 
	 * @param size
	 * @return
	 */
	private static List<int[]> getEdges(int size){
		List<int[]> edges = new ArrayList<int[]>();
		Set<Integer> keys = packageGraph.keySet();
		List<int[]> adjacents = null;
		
		for(int key : keys) {
			adjacents = packageGraph.get(key);
			for(int[] vj : adjacents) edges.add(new int[]{key, vj[0]});
		}
		
		return edges;
	}


	public static ClusterMetrics[] getClusterMetrics() {
		return clusterMetrics;
	}

	public static void dispose() {
		packageGraph = null;
		clusterMetrics = null;
		keys = list = allPackages = null;
		edges = null;
		countVertexes = edgeNumber = 0;
		
	}

	private static void upDateAttributes() {
		packageGraph = new HashMap<Integer, List<int[]>>();
	}


}
