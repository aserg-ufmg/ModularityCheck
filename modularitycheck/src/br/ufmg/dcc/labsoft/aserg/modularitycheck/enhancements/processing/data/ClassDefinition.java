package br.ufmg.dcc.labsoft.aserg.modularitycheck.enhancements.processing.data;

import java.io.IOException;

import ptstemmer.exceptions.PTStemmerException;
import br.ufmg.dcc.labsoft.aserg.modularitycheck.enhancements.vocabulary.extraction.VocabularyExtractionPAM;


/****
 * This class retrieve the classes:
 * 	0 - well-encapsulated
 *  1 - partially encapusulated
 *  3 - crosscutting
 *  
 *  It reads the maximal items from the output of apriori, prunes issue's comments, and insert the classId
 * 
 * @author Luciana
 *
 */
public class ClassDefinition {
	private static String path;
	
	public static void prepareDataSet(String fullPath){
		path = fullPath;
		executeVocubularyExtraction();
		readMaximalItems();
		joinData();
		
		
	}

	private static void joinData() {
		
		
	}

	private static void readMaximalItems() {
		
		
	}

	private static void executeVocubularyExtraction() {
		VocabularyExtractionPAM vocabularyExtraction = new VocabularyExtractionPAM(path + "\\comments", path, path + "\\files");
		try {
			vocabularyExtraction.createIRInfo();
		} catch (PTStemmerException e) {e.printStackTrace();} catch (IOException e) {e.printStackTrace();}
	}
	
	
}
