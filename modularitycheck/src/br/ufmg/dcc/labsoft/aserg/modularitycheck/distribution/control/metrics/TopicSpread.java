package br.ufmg.dcc.labsoft.aserg.modularitycheck.distribution.control.metrics;

import java.util.HashMap;
import java.util.Map;

import br.ufmg.dcc.labsoft.aserg.modularitycheck.distribution.control.DistributionMap;

public class TopicSpread extends AbstractSemanticTopicMetric {
	
	public TopicSpread(DistributionMap distributionMap, int numClusters) {
		super(distributionMap, numClusters);
	}
	
	private double calculate(int topicIndex) {
		double spread = 0D;
		for (String packageName : this.distributionMap.getPackages())
			spread += (this.calculateTopicTouch(topicIndex, packageName) > 0) ? 1 : 0;
		return spread;
	}

	public Map<String, Double> calculate() {
		Map<String, Double> metricMapping = new HashMap<String, Double>();
		for (int topicIndex = 0; topicIndex < this.numClusters; topicIndex++)
			metricMapping.put(String.valueOf(topicIndex), calculate(topicIndex));
		return metricMapping;
	}
}