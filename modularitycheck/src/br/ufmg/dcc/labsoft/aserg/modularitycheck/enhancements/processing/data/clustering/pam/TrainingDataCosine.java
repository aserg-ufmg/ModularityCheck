package br.ufmg.dcc.labsoft.aserg.modularitycheck.enhancements.processing.data.clustering.pam;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import br.ufmg.dcc.labsoft.aserg.modularitycheck.enhancements.properties.util.Properties;
import br.ufmg.dcc.labsoft.aserg.modularitycheck.enhancements.properties.util.Utils;
import data.handler.CarryFileMemory;

public class TrainingDataCosine extends TrainingData {

	public TrainingDataCosine(String absolutPath) {
		super(absolutPath);
		Properties.setClusterPath(Properties.PAM_DIRECTORY_NAME);
	}

	/***
	 * Read the R file built after clustering the issues and identify the issues
	 * ID
	 * 
	 * @param pathClusteredData
	 * @param termsEntitiesMap
	 * @param comments
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	protected void readFile() throws FileNotFoundException, IOException {
		File[] comments = new File(Properties.getAbsolutPath()
				+ Properties.COMMENTS_ISSUES).listFiles();// Refresh the vector
		String[] openedClusterFile = new CarryFileMemory(
				Properties.getResultPath() + Properties.PAM_FILE_NAME)
				.carryCompleteFile();// File of R
		for (String line : openedClusterFile) {
			String[] terms = line.split(Properties.COMMA);
			int idFile = Integer.parseInt(Utils.replaceChar(terms[0]));
			String nameFile = comments[idFile - 1].getName();
			String key = Utils.replaceChar(terms[2]);
			if (termsEntitiesMap.containsKey(key))
				termsEntitiesMap.get(key).add(nameFile);
			else {
				List<String> currentEntityTerms = new ArrayList<String>();
				currentEntityTerms.add(nameFile);
				termsEntitiesMap.put(key, currentEntityTerms);
			}
		}
	}
}
