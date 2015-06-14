package br.ufmg.dcc.labsoft.aserg.modularitycheck.enhancements.properties.util;

import java.util.ArrayList;

public class MathImpl {

	public static double getMean(int value, int size) {
		return value / size;
	}

	public static double getMedian(int[] clusterSize) {
		double median = 0;
		if (clusterSize.length % 2 == 0) {
			median = (clusterSize[clusterSize.length / 2] + clusterSize[clusterSize.length / 2 + 1]) / 2;
		} else {
			median = (clusterSize.length + 1) / 2;
		}
		return median;
	}

	public static int getMedian(ArrayList<Integer> clusterSize) {
		int median = 0;
		if (clusterSize.size() % 2 == 0) {
			median = (clusterSize.get(clusterSize.size() / 2) + clusterSize
					.get(clusterSize.size() / 2 + 1)) / 2;
		} else {
			median = clusterSize.get((clusterSize.size() + 1) / 2);
		}
		return median;
	}

	private static double getSumSquare(int[] array) {
		double total = 0;
		for (int counter = 0; counter < array.length; counter++)
			total += Math.pow(array[counter], 2);

		return total;
	}

	private static double getSumOfValues(int[] array) {
		double total = 0;
		for (int counter = 0; counter < array.length; counter++)
			total += array[counter];

		return total;

	}

	/***
	 * Get the variancy
	 * 
	 * @param array
	 * @return
	 */
	public static double getVariancy(int[] array) {
		double value1 = 1 / Double.valueOf(array.length - 1);
		double value2 = getSumSquare(array)
				- (Math.pow(getSumOfValues(array), 2) / Double
						.valueOf(array.length));

		return value1 * value2;
	}

	/***
	 * Get the standard deviation
	 * 
	 * @param array
	 * @return
	 */
	public static double getDeviation(int[] array) {
		return Math.sqrt(getVariancy(array));
	}
}
