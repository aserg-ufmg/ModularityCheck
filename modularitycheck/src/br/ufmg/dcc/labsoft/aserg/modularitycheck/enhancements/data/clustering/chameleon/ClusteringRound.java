package br.ufmg.dcc.labsoft.aserg.modularitycheck.enhancements.data.clustering.chameleon;

import java.util.ArrayList;
import java.util.List;


/***
 * Pojo Class
 * @author Luciana
 *
 */
public class ClusteringRound {
	
	private List<String> id_clusters;
	private List<Integer> size;
	private List<Double> iSim;
	private List<Double> eSim;
	private List<Integer> valueM;
	
	
	public ClusteringRound(){
		id_clusters = new ArrayList<String>();
		size = new ArrayList<Integer>();
		iSim = new ArrayList<Double>();
		eSim = new ArrayList<Double>();
		valueM = new ArrayList<Integer>();
	}
	
	public void setIdCluster(String id){
		id_clusters.add(id);		
	}
	
	public void setSize(int sizeCluster){
		size.add(sizeCluster);
	}
	
	public void setISim(Double isim){
		iSim.add(isim);
	}
	
	public void setESim(Double esim){
		eSim.add(esim);
	}
	
	public void setM(int m){
		valueM.add(m);
	}
	
}
