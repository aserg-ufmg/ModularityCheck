package br.ufmg.dcc.labsoft.aserg.modularitycheck.distribution.control;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import br.ufmg.dcc.labsoft.aserg.modularitycheck.distribution.control.metrics.TopicFocus;
import br.ufmg.dcc.labsoft.aserg.modularitycheck.distribution.control.metrics.TopicSpread;

public class DistributionMap {
	private String projectName;
	private Map<String, List<String>> packageMapping;
	private Map<String, Integer> classMapping;
	private Map<Integer, TopicInfo> topicMapping;

	private DistributionMap(String projectName) {
		this.projectName = projectName;
		this.packageMapping = new HashMap<String, List<String>>();
		this.classMapping = new HashMap<String, Integer>();
		this.topicMapping = new HashMap<Integer, TopicInfo>();
	}

	public void put(String packageName, String className, int clusterIndex) {
		List<String> classes = this.packageMapping.containsKey(packageName) ? (List<String>) this.packageMapping
				.get(packageName) : new LinkedList<String>();
		classes.add(className);

		this.packageMapping.put(packageName, classes);
		this.classMapping.put(className, Integer.valueOf(clusterIndex));
	}

	public void put(int clusterIndex, double spread, double focus) {
		this.topicMapping.put(Integer.valueOf(clusterIndex), new TopicInfo(
				spread, focus));
	}

	public String getProjectName() {
		return this.projectName;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<String> getPackages() {
		List<String> packages = new LinkedList<String>();
		packages.addAll(this.packageMapping.keySet());

		Collections.sort(packages, new Comparator() {

			@Override
			public int compare(Object package1, Object package2) {
				return Integer.compare(
						((List<String>) DistributionMap.this.packageMapping
								.get(package2.toString())).size(),
						((List<String>) DistributionMap.this.packageMapping
								.get(package1.toString())).size());
			}
		});
		return packages;
	}

	public List<String> getClasses(String packageName) {
		return (List<String>) this.packageMapping.get(packageName);
	}

	public Integer getCluster(String className) {
		return (Integer) this.classMapping.get(className);
	}

	public Double getSpread(int clusterIndex) {
		return Double.valueOf(((TopicInfo) this.topicMapping.get(Integer
				.valueOf(clusterIndex))).spread);
	}

	public Double getFocus(int clusterIndex) {
		return Double.valueOf(((TopicInfo) this.topicMapping.get(Integer
				.valueOf(clusterIndex))).focus);
	}

	public void organize() {
		for (String packageName : this.packageMapping.keySet()) {
			List<String> classes = (List<String>) this.packageMapping.get(packageName);
			Collections.sort(classes);
		}
	}

	static class TopicInfo {
		double spread;
		double focus;

		TopicInfo(double spread, double focus) {
			this.spread = spread;
			this.focus = focus;
		}
	}

	public static DistributionMap generateDistributionMap(String projectName,
			String[] documentIds, int[][] clusters) {
		DistributionMap distributionMap = new DistributionMap(projectName);
		for (int i = 0; i < clusters.length; i++) {
			for (int documentId : clusters[i]) {
				String document = documentIds[documentId];
				String packageName = document.substring(0,
						document.lastIndexOf(File.separatorChar)).replace(
						File.separatorChar, '.');
				String className = document;

				distributionMap.put(packageName, className, i);
			}
		}
		distributionMap = addSemanticClustersMetrics(distributionMap,
				clusters.length);
		distributionMap.organize();
		return distributionMap;
	}

	public static DistributionMap addSemanticClustersMetrics(
			DistributionMap distributionMap, int numClusters) {
		Map<String, Double> spread = new TopicSpread(distributionMap,
				numClusters).calculate();
		Map<String, Double> focus = new TopicFocus(distributionMap, numClusters)
				.calculate();
		for (int i = 0; i < numClusters; i++) {
			distributionMap.put(i,
					((Double) spread.get(String.valueOf(i))).doubleValue(),
					((Double) focus.get(String.valueOf(i))).doubleValue());
		}
		return distributionMap;
	}
}