package br.ufmg.dcc.labsoft.aserg.modularitycheck.enhancements.metrics;

import java.util.HashMap;
import java.util.Map;

public class ClusterData {
	
	private Map<String, Integer> packages = new HashMap<String, Integer>();
	
	public boolean containsPackage(String packageName) {		
		return packages.containsKey(packageName);
	}

	public void addPackage(String packageName){
		packages.put(packageName, 1);
	}
	
	public void updatePackage(String packageName){
		packages.put(packageName, packages.get(packageName) + 1);
	}
	
	public Map<String, Integer> getClusterPackages(){
		return packages;
	}
	
}
