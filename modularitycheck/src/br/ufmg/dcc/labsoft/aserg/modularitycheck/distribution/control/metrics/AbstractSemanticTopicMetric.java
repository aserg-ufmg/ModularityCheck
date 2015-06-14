package br.ufmg.dcc.labsoft.aserg.modularitycheck.distribution.control.metrics;

import java.util.List;
import java.util.Map;

import br.ufmg.dcc.labsoft.aserg.modularitycheck.distribution.control.DistributionMap;

public abstract class AbstractSemanticTopicMetric {
	
	protected int numClusters;
	protected DistributionMap distributionMap;
	
	protected AbstractSemanticTopicMetric(DistributionMap distributionMap, int numClusters) {
		this.numClusters = numClusters;
		this.distributionMap = distributionMap;
	}
	
	public abstract Map<String, Double> calculate();
	
	protected double calculateTopicTouch(int topicIndex, String packageName) {
		List<String> classes = this.distributionMap.getClasses(packageName); 
		double numClasses = classes.size();
		
		double touchedClasses = 0D;
		for (String className : classes)
			if (this.distributionMap.getCluster(className) == topicIndex)
				touchedClasses++;
		
		return (numClasses == 0) ? 0D : touchedClasses / numClasses;
	}
	
	protected double calculatePackageTouch(String packageName, int topicIndex) {
		double numClasses = 0D;
		double touchedClasses = 0D;
		
		for (String packag : this.distributionMap.getPackages()) {
			boolean isPackage = packag.equals(packageName);
			
			for (String className : this.distributionMap.getClasses(packag)) {
				boolean isMarkedClass = this.distributionMap.getCluster(className) == topicIndex;
				
				touchedClasses += (isPackage && isMarkedClass) ? 1 : 0;
				numClasses += (isMarkedClass) ? 1 : 0;
			}
		}
		
		return (numClasses == 0) ? 0D : touchedClasses / numClasses;
	}
}