package br.ufmg.dcc.labsoft.aserg.modularitycheck.enhancements.data.clustering.chameleon;

import java.util.List;
import java.util.ArrayList;
import java.io.*;
import java.nio.file.Paths;





import br.ufmg.dcc.labsoft.aserg.modularitycheck.enhancements.data.clustering.chameleon.SilhouetteCoefficient;
import br.ufmg.dcc.labsoft.aserg.modularitycheck.enhancements.properties.util.Properties;
import br.ufmg.dcc.labsoft.aserg.modularitycheck.enhancements.properties.util.Utils;

public class SClusterScript {
	public static final int MIN_M_CLUSTERS = 20, M_DECREMENT = 1, INITIAL = 18;// threshold
	public static final int MIN_SIZE_GRAPH = 30;
	public static String workingDir = Paths.get("").toAbsolutePath().toString(); // "C:\\Users\\Daniel\\workspace\\eclipse-modularity-check\\lib\\enhancements-Dependencies";
	public static int MIN_CLUSTER_SZ;
	public static String path;
	public static String sClusterPath;
	public static SilhouetteCoefficient silhouette;

	public static String getOsName() {
		return System.getProperty("os.name");
	}

	public static boolean systemIsWindows() {
		return getOsName().startsWith("Windows");
	}

	/***
	 * Runs the scluster with its parameters
	 * 
	 * @param agglofrom
	 * @return
	 * @throws IOException
	 */
	private static List<String> executeSCluster(int agglofrom)
			throws IOException {
		
		//sClusterPath = workingDir + "\\lib\\enhancements-Dependencies\\scluster.exe";
		String comando = sClusterPath
				+ " -clmethod=graph -cstype=large -agglocrfun=i2 -mincomponent=1 -nnbrs=60 -agglofrom="
				+ agglofrom + " " + Properties.getGraphPath() + " 10";

		//System.out.println(comando);
		
		Runtime rt = Runtime.getRuntime();
		Process pr = rt.exec(comando);
		BufferedReader br = new BufferedReader(new InputStreamReader(
				pr.getInputStream()));
		String linha = null;
		List<String> results = new ArrayList<String>();
		while ((linha = br.readLine()) != null) {
			results.add(linha);
		}
		return results;
	}

	/***
	 * Runs the sclusters and applies the heuristic
	 * 
	 * @param agglofrom
	 * @throws IOException
	 */
	private static void calculateHeuristic(int agglofrom) throws IOException {
		List<String> results = executeSCluster(agglofrom);
		double numberOfClusters = 0;
		double iSim = 0, eSim = 0, coefficient = 0, aux, meanISim = 0;

		for (int line = INITIAL; line < results.size(); line++) {
			if (!results.get(line).contains(Properties.HASH)) {
				String[] values = results.get(line).split(Properties.PLUS);
				if (values.length > 1) {
					String[] temp = values[0].trim().split(Properties.BLANK);
					if (Integer.valueOf(temp[temp.length - 1]) >= MIN_CLUSTER_SZ) {
						iSim = Double.valueOf(values[1]);
						eSim = Double.valueOf(values[3]);
						aux = iSim - eSim;
						meanISim += iSim;
						if (iSim > eSim)
							aux = aux / iSim;
						else
							aux = aux / eSim;
						coefficient += aux;
						numberOfClusters++;
					}
				}
			}

		}

		if(numberOfClusters > 0){
			coefficient = coefficient / numberOfClusters;
			coefficient = Double.valueOf(String.format("%.4f", coefficient)
					.replace(",", "."));
			
	
			if(!Double.isNaN(coefficient)){
				silhouette.setCoefficient(coefficient);
				silhouette.setValueM(agglofrom);
				silhouette.setMeanISim(Double.valueOf(String.format("%.4f",
						meanISim / numberOfClusters).replace(",", ".")));
				
				/*System.out.println("Coefficient:" + coefficient + "\tM: " + agglofrom
						+ "\t Mean: " + meanISim / numberOfClusters);*/
			}
		}

	}

	private static void saveData(int agglofrom) throws IOException {
		List<String> results = executeSCluster(agglofrom);
		StringBuilder cache = new StringBuilder();

		cache.append("Id_Chameleon; Size; ISim; ESim").append(
				Properties.NEW_LINE);

		for (int line = INITIAL; line < results.size(); line++) {
			if (!results.get(line).contains(Properties.HASH)) {
				String[] values = results.get(line).split(Properties.PLUS);
				if (values.length > 1) {
					String[] temp = values[0].trim().split(Properties.BLANK);
					int size = Integer.valueOf(temp[temp.length - 1]);
					if (size >= MIN_CLUSTER_SZ) {
						cache.append(temp[0]).append(Properties.COMMA_PERIOD);
						cache.append(size).append(Properties.COMMA_PERIOD);
						cache.append(Double.valueOf(values[1])).append(
								Properties.COMMA_PERIOD);
						cache.append(Double.valueOf(values[3])).append(
								Properties.NEW_LINE);
					}
				}
			}
		}

		Utils.writeFile(
				cache.toString().replace(Properties.PERIOD, Properties.COMMA),
				path + "report.csv");

	}

	private static void chooseTheBestM() throws IOException {
		int m = silhouette.getBestM();
		System.out.println("Silhouette Coefficient: "
				+ silhouette.getCoefficient());
		System.out.println("ISim average: " + silhouette.getMeanISim());
		System.out.println("The fittest M: " + m);
		saveData(m);

	}

	/***
	 * To run the chameleon algorithm
	 * 
	 * @param args
	 *            [1] -- numero de vertices
	 * @throws IOException
	 */
	public static void runSCluster(String scluster, int vertex, int minClusterSize)
			throws IOException {
		int M_INITIAL = (vertex * 20) / 100;

		System.out.println("Mining co-change clusters ...");
		silhouette  = new SilhouetteCoefficient();
		sClusterPath = scluster;
		MIN_CLUSTER_SZ = minClusterSize;
		// SClusterScript.graphFile = Properties.getGraphPath();
		SClusterScript.path = new File(Properties.getGraphPath())
				.getAbsolutePath();
		
		if(M_INITIAL >= MIN_M_CLUSTERS){
			for (int m = M_INITIAL; m >= MIN_M_CLUSTERS; m -= M_DECREMENT) {
				calculateHeuristic(m);
			}
			if (M_INITIAL >= MIN_M_CLUSTERS && silhouette.getMaximumCoefficient() != -1) chooseTheBestM();
			else System.out.println("There is no co-change clusters in this system");
		}else{
			M_INITIAL = vertex;
			for(int m = M_INITIAL; m >= MIN_CLUSTER_SZ; m-=M_DECREMENT){
				calculateHeuristic(m);
			}
			
			if (silhouette.getMaximumCoefficient() != -1) chooseTheBestM();
			else System.out.println("There is no co-change clusters in this system");
		}
	}

}
