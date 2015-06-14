package br.ufmg.dcc.labsoft.aserg.modularitycheck.enhancements.properties.util;

public class Quicksort {

	private static int[] numbers;
	private static String[] classes;
	private static int number;

	public static void sort(int[] values, String[] classNames) {
		if (values == null || values.length == 0) {
			return;
		}
		numbers = values;
		classes = classNames;
		number = values.length;
		quicksort(0, number - 1);
	}

	private static void quicksort(int low, int high) {
		int i = low, j = high;
		int pivot = numbers[low + (high - low) / 2];
		while (i <= j) {
			while (numbers[i] > pivot) {
				i++;
			}
			while (numbers[j] < pivot) {
				j--;
			}

			if (i <= j) {
				exchange(i, j);
				i++;
				j--;
			}
		}
		if (low < j)
			quicksort(low, j);
		if (i < high)
			quicksort(i, high);
	}

	private static void exchange(int i, int j) {
		int temp = numbers[i];
		numbers[i] = numbers[j];
		numbers[j] = temp;

		String aux = classes[i];
		classes[i] = classes[j];
		classes[j] = aux;
	}

}
