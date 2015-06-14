package br.ufmg.dcc.labsoft.aserg.modularitycheck.enhancements.processing.data.clustering.pam;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import data.handler.CarryFileMemory;
import br.ufmg.dcc.labsoft.aserg.modularitycheck.enhancements.properties.util.Properties;
import br.ufmg.dcc.labsoft.aserg.modularitycheck.enhancements.properties.util.Utils;

public class TrainingDataLSI extends TrainingData {

	public TrainingDataLSI(String absolutPath) {
		super(absolutPath);
		Properties.setClusterPath(Properties.PAM_LSI_DIRECTORY_NAME);
	}

	/***
	 * Read the R file built after clustering the issues and identify the issues
	 * ID
	 * 
	 * @param termsEntitiesMap
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	protected void readFile() throws FileNotFoundException, IOException {
		String[] openedClusterFile = new CarryFileMemory(
				Properties.getResultPath() + Properties.PAM_LSI_FILE_NAME)
				.carryCompleteFile();// File of R
		for (String line : openedClusterFile) {
			String[] terms = line.split(Properties.COMMA);
			String nameFile = Utils.replaceChar(terms[0]);
			String key = Utils.replaceChar(terms[1]);
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
