package br.ufmg.dcc.labsoft.aserg.modularitycheck.enhancements.metrics;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import br.ufmg.dcc.labsoft.aserg.modularitycheck.enhancements.processing.data.clustering.chameleon.CoChangeGraph;
import br.ufmg.dcc.labsoft.aserg.modularitycheck.enhancements.properties.util.Properties;
import br.ufmg.dcc.labsoft.aserg.modularitycheck.enhancements.properties.util.Utils;
import  br.ufmg.dcc.labsoft.aserg.modularitycheck.enhancements.processing.data.clustering.chameleon.ClusterMetrics;;

public class Focus {
		private static Map<String, Integer> packages;// All packages remained in the
		// clustering process
	private static Map<String, ClusterData> clusterPackages;// All packages for
				// each cluster
	private static List<String> keys;


	public static void measureFocus(Map<String, List<String>> hash,
			List<String> key, ClusterMetrics[] clusterMetrics) {
		keys = key;
		retrievePackages(hash);
		calculateFocus(hash, clusterMetrics);

	}

	private static void calculateFocus(Map<String, List<String>> hash, ClusterMetrics[] clusterMetrics) {
		Iterator<String> iterator = clusterPackages.keySet().iterator();

		StringBuilder focusCache = new StringBuilder();
		StringBuilder spreadCache = new StringBuilder();
		
		List<String> list = new ArrayList<String>();
		while (iterator.hasNext()) {
			list.add(iterator.next());
		}

		Collections.sort(list);
		iterator = list.iterator();


		int clusterId = 0;

		while (iterator.hasNext()) {//for each cluster
			ClusterMetrics temp = new ClusterMetrics();
			calculateMetrics(iterator, hash, temp);
			clusterMetrics[clusterId] = temp;
			clusterId++;
			
			focusCache.append(String.valueOf(Math.round(temp.getFocus() * 1e3) / 1e3)).append(Properties.NEW_LINE);
			spreadCache.append(String.valueOf(temp.getSpread())).append(Properties.NEW_LINE);
		}

		try {
			Utils.writeFile(focusCache.toString(), Properties.getResultPath()
					+ Properties.FOCUS_DATA);
			Utils.writeFile(spreadCache.toString(), Properties.getResultPath()
					+ Properties.SPREAD_DATA);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	
	private static void calculateMetrics(Iterator<String> iterator, Map<String, List<String>> hash, ClusterMetrics clusterMetrics) {
		
		double focus = 0;
		double focusT = 0;
		int spread = 0;
		double touch = 0;
		double body = 0;
		double bodyTouch = 0; 
		

	
		String key = iterator.next();
		
		Map<String, Integer> packageCluster = clusterPackages.get(key).getClusterPackages();
		Iterator<String> iteratorCluster = packageCluster.keySet().iterator();
		
		
		System.out.println("Focus");
		StringBuilder cache = new StringBuilder();
		cache.append("Spread").append(Properties.NEW_LINE);


		
		
		while (iteratorCluster.hasNext()) {
			String pkg = iteratorCluster.next();
			int numberOfClassesPackageStructure = packages.get(pkg);
			int numberOfClassesPj = packageCluster.get(pkg);
			
			touch = ((double) numberOfClassesPj / (double)numberOfClassesPackageStructure);
			
			focus +=  touch	* ((double) numberOfClassesPj / (double)hash.get(key).size());
			
			if (touch > 0) spread++;
			if (touch > 0.5) {
				body = body + touch;
				bodyTouch++;
			}else {
				focusT +=  touch * ((double) numberOfClassesPj / (double)hash.get(key).size());
			}
		}

		
		body = body/bodyTouch;

		focus = (Math.round(focus * 1e6) / 1e6);
		if(focus == 1) clusterMetrics.setPattern("E");
		else if(spread >= 4 && focus <= 0.3) clusterMetrics.setPattern("C");
		else if(focus > 0.3 && focusT <= 0.25 && body > 0.5 && spread > 1) {
			clusterMetrics.setPattern("O");
			clusterMetrics.setBody(body);
			clusterMetrics.setFocusT(focusT);
		}
		
		clusterMetrics.setFocus(focus);
		clusterMetrics.setSpread(spread);
		
		
		System.out.printf("Cluster: " + key + "\t Focus: %.3f", focus);



		cache.append("Cluster: " + key + "\t Spread: " + String.valueOf(spread))
				.append(Properties.NEW_LINE);
	
		System.out.println(cache.toString());
	

		
		
	}

	
	
	
	

	/***
	 * Retrieve packages used to cluster the graph. By cluster and for the whole
	 * system.
	 * 
	 * @param hash
	 */
	private static void retrievePackages(Map<String, List<String>> hash) {
		packages = new HashMap<String, Integer>();
		clusterPackages = new HashMap<String, ClusterData>();

		for (String key : keys) {
			List<String> classes = hash.get(key);
			if (classes.size() >= CoChangeGraph.MIN_SIZE) {
				ClusterData data = new ClusterData();
				for (String classesName : classes) {
					String packageName = Utils.readPackage(classesName);
					if (!data.containsPackage(packageName))
						data.addPackage(packageName);
					else
						data.updatePackage(packageName);
					if (packages.containsKey(packageName))
						packages.put(packageName, packages.get(packageName) + 1);
					else
						packages.put(packageName, 1);
				}
				clusterPackages.put(key, data);
			}
		}
	}
	

}
