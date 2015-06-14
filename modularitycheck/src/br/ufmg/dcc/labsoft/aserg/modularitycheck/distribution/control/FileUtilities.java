package br.ufmg.dcc.labsoft.aserg.modularitycheck.distribution.control;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class FileUtilities {

	public static String[] readClassIds(String fileName) throws IOException {
		final String SEPARATOR = ",";
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		
		String str;
		List<String> classNames = new LinkedList<String>();
		while ((str = reader.readLine()) != null) {
			for (String className : str.split(SEPARATOR))
				classNames.add(className.replace('/', File.separatorChar));
		}
		
		reader.close();
		return classNames.toArray(new String[]{});
	}
	
	public static int[][] readClusters(String fileName) throws IOException {
		final String SEPARATOR = ",";
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		
		int numClusters = 0;
		@SuppressWarnings("unused")
		String str;
		while ((str = reader.readLine()) != null)
			numClusters++;
		reader.close();
		
		reader = new BufferedReader(new FileReader(fileName));
		int[][] clusters = new int[numClusters][0];
		
		int index = 0;
		for (int i = 0; i < numClusters; i++) {
			String[] values = reader.readLine().split(SEPARATOR);
			int[] cluster = new int[values.length];
			
			for (int j = 0; j < cluster.length; j++) {
				cluster[j] = index;
				index++;
			}
			clusters[i] = cluster;
		}
		
		return clusters;
	}
}