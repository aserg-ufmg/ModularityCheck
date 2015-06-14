package br.ufmg.dcc.labsoft.aserg.modularitycheck.views.tables;

import java.util.Arrays;
import java.util.HashMap;

public class Statistics {

	private double array[];

	public double getMax() {
		sortArray();
		return array[array.length - 1];
	}
	
	public double getMin() {
		sortArray();
		return array[0];
	}
	
	// Coeficiente de Variação de Pearson
	public double getPearson() {
		return (getStandardDeviation() / getArithmeticAverage()) * 100;
	}

	public double getArithmeticAverage() {
		double total = 0;

		for (int i = 0; i < array.length; i++)
			total += array[i];

		return total / array.length;
	}

	public double getSum() {
		double total = 0;

		for (int i = 0; i < array.length; i++)
			total += array[i];

		return total;

	}

	public double getSquareSum() {
		double total = 0;

		for (int i = 0; i < array.length; i++)
			total += Math.pow(array[i], 2);

		return total;
	}

	public double getArithmeticAverage(double array[]) {
		double total = 0;

		for (int i = 0; i < array.length; i++)
			total += array[i];

		return total / array.length;
	}

	public double getSum(double array[]) {
		double total = 0;

		for (int i = 0; i < array.length; i++)
			total += array[i];

		return total;
	}

	public void sortArray() {
		Arrays.sort(array);
	}

	public void printArray() {
		for (int i = 0; i < array.length; i++)
			System.out.print(array[i] + " ");
	}

	// Array não pode conter valores duplicados
	public int searchItem(int value) {
		return Arrays.binarySearch(array, value);
	}

	// Variância Amostral
	public double getVariance() {
		double p1 = 1 / Double.valueOf(array.length - 1);
		double p2 = getSquareSum()
				- (Math.pow(getSum(), 2) / Double.valueOf(array.length));
		return p1 * p2;
	}

	// Desvio Padrão Amostral
	public double getStandardDeviation() {
		return Math.sqrt(getVariance());
	}

	public double getMedian() {
		this.sortArray();
		int type = array.length % 2;

		if (type == 1) {
			return array[((array.length + 1) / 2) - 1];
		} else {
			int m = array.length / 2;
			return (array[m - 1] + array[m]) / 2;
		}
	}

	public double getMode() {
		HashMap<Double, Integer> map = new HashMap<Double, Integer>();
		Integer i;
		Double mode = 0.0;
		Integer aux, greater = 0;

		for (int count = 0; count < array.length; count++) {
			i = (Integer) map.get(new Double(array[count]));

			if (i == null) {
				map.put(new Double(array[count]), new Integer(1));
			} else {
				map.put(new Double(array[count]), new Integer(i.intValue() + 1));
				aux = i.intValue() + 1;

				if (aux > greater) {
					greater = aux;
					mode = new Double(array[count]);
				}
			}
		}

		return mode;
	}

	public double getAssimetricCoeficients() {
		return (getArithmeticAverage() - getMode()) / getStandardDeviation();
	}

	public double[] getArray() {
		return array;
	}

	public void setArray(double[] array) {
		this.array = array;
	}
}
