package br.ufmg.dcc.labsoft.aserg.modularitycheck.views.tables;

import br.ufmg.dcc.labsoft.aserg.modularitycheck.enhancements.properties.util.Properties;
import data.handler.CarryFileMemory;

public class FillTables {

	private static double[] loadArray(String fileName) {
		try {
			CarryFileMemory carry = new CarryFileMemory(fileName);
			String[] buffer = carry.carryCompleteFile();
			double array[] = new double[buffer.length];

			for (int i = 0; i < buffer.length; i++)
				array[i] = Double.parseDouble(buffer[i]);

			return array;
		} catch (Exception e) {
			return null;
		}
	}

	public static String[] fillTableClusterSize(String root) {
		Statistics st = new Statistics();
		//Properties.setDefaultPaths(root);
		st.setArray(loadArray(Properties.getResultPath()
				+ Properties.SIZE_DATA));
		return new String[] { String.format("%.2f",st.getMin()),
				String.format("%.2f",st.getMax()),
				String.format("%.2f",st.getArithmeticAverage()),
				String.format("%.2f",st.getStandardDeviation()) };
	}

	public static String[] fillTableClusterDensity(String root) {
		Statistics st = new Statistics();
		//Properties.setDefaultPaths(root);
		st.setArray(loadArray(Properties.getResultPath()
				+ Properties.DENSITY_DATA));
		return new String[] { String.format("%.2f",st.getMin()),
				String.format("%.2f",st.getMax()),
				String.format("%.2f",st.getArithmeticAverage()),
				String.format("%.2f",st.getStandardDeviation()) };
	}

	public static String[] fillTableClusterFocus(String root) {
		Statistics st = new Statistics();
		//Properties.setDefaultPaths(root);
		st.setArray(loadArray(Properties.getResultPath()
				+ Properties.FOCUS_DATA));
		return new String[] { String.format("%.2f", st.getMin()),
				String.format("%.2f",st.getMax()),
				String.format("%.2f",st.getArithmeticAverage()),
				String.format("%.2f",st.getStandardDeviation()) };
	}

	public static String[] fillTableClusterSpread(String root) {
		Statistics st = new Statistics();
	//	Properties.setDefaultPaths(root);
		st.setArray(loadArray(Properties.getResultPath()
				+ Properties.SPREAD_DATA));
		return new String[] { String.format("%.2f",st.getMin()),
				String.format("%.2f",st.getMax()),
				String.format("%.2f",st.getArithmeticAverage()),
				String.format("%.2f",st.getStandardDeviation()) };
	}

	public static String[] fillTableClusterWeight(String root) {
		Statistics st = new Statistics();
	//	Properties.setDefaultPaths(root);
		st.setArray(loadArray(Properties.getResultPath()
				+ Properties.WEIGHT_DATA));
		return new String[] { String.format("%.2f",st.getMin()),
				String.format("%.2f",st.getMax()),
				String.format("%.2f",st.getArithmeticAverage()),
				String.format("%.2f",st.getStandardDeviation()) };
	}
}
