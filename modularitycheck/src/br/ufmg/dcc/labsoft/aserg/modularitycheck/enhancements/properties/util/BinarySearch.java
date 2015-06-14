package br.ufmg.dcc.labsoft.aserg.modularitycheck.enhancements.properties.util;

public class BinarySearch {

	public static final int NOT_FOUND = -1;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static int binarySearch(Comparable[] a, Comparable x) {
		int low = 0;
		int high = a.length - 1;
		int mid;

		while (low <= high) {
			mid = (low + high) / 2;

			if (a[mid].compareTo(x) < 0)
				low = mid + 1;
			else if (a[mid].compareTo(x) > 0)
				high = mid - 1;
			else
				return mid;
		}

		return NOT_FOUND;
	}

}
