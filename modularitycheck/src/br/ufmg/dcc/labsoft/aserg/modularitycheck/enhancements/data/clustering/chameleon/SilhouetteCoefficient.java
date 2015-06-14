package br.ufmg.dcc.labsoft.aserg.modularitycheck.enhancements.data.clustering.chameleon;

import java.util.ArrayList;
import java.util.List;


/**
 * Pojo class that save all silhouette results
 * @author Luciana
 *
 */
public class SilhouetteCoefficient {

	private List<Double> coefficient;
	private List<Integer> valueM;
	private List<Double> meanISim;
	private double maximumSilhouette;
	private int round;
	private int pos;
	
	
	public SilhouetteCoefficient(){
		pos = -1;
		maximumSilhouette = -1;
		round = 0;
		coefficient = new ArrayList<Double>();
		valueM = new ArrayList<Integer>();
		meanISim = new ArrayList<Double>();
	}
	
	public double getCoefficient() {
		return coefficient.get(pos);
	}

	public List<Integer> getValueM() {
		return valueM;
	}

	public double getMeanISim() {
		return meanISim.get(pos);
	}
	
	public Double getMaximumCoefficient(){
		return maximumSilhouette;
	}
	
	

	public int getPos() {
		return pos;
	}


	/***
	 * Returns the best M value or uses the tieBreaker condition
	 * 
	 * @return
	 */
	public int getBestM(){
		
		pos = (coefficient.indexOf(maximumSilhouette) < 0) ? 0 : coefficient.indexOf(maximumSilhouette);

		if(round == 0) valueM.get(pos);
			
		for(int index = 0; index < coefficient.size(); index++){
			if(coefficient.get(index) == maximumSilhouette && meanISim.get(index) > meanISim.get(pos)){
				pos = index;				
			}
		}
				
		return valueM.get(pos);
	}
	
	public void setMeanISim(double meanISim) {
		this.meanISim.add(meanISim);
	}
	
	public void setCoefficient(double coefficient) {
		this.coefficient.add(coefficient);
		if(maximumSilhouette == coefficient) round++;
		if(maximumSilhouette < coefficient) maximumSilhouette = coefficient;	
	}
	
	public void setValueM(int valueM) {
		this.valueM.add(valueM);
	}
	
}
