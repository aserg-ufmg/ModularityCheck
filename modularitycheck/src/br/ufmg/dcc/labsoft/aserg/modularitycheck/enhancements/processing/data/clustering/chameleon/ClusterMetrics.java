package br.ufmg.dcc.labsoft.aserg.modularitycheck.enhancements.processing.data.clustering.chameleon;
public class ClusterMetrics {

	private String key;
	private double focus;
	private int spread;
	private int size;
	private double density;
	private double weight;
	private String pattern;
	private double focusT;
	private double body;
	
	
	
	
	
	public ClusterMetrics(){
		key = pattern = "";
		focus = density = weight = focusT = body = 0;
		spread = size = 0;
	}
	
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

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public double getDensity() {
		return density;
	}

	public void setDensity(double density) {
		this.density = density;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public double getBody() {
		return body;
	}

	public void setBody(double body) {
		this.body = body;
	}

	public double getFocusT() {
		return focusT;
	}

	public void setFocusT(double focusT) {
		this.focusT = focusT;
	}


	
}
