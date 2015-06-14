package br.ufmg.dcc.labsoft.aserg.modularitycheck.enhancements.metrics;

public class ClusterMetrics {

	private String key;
	private double focus;
	private int spread;
	
	public void setKey(String key){
		this.key = key;
	}
	
	public void setFocus(double focus){
		this.focus = focus;
	}
	
	public void setSpread(int spread){
		this.spread = spread;
	}
	
	public String getKey(){
		return key;
	}
	
	public double getFocus(){
		return focus;
	}
	
	public int getSpread(){
		return spread;
	}
	
}
