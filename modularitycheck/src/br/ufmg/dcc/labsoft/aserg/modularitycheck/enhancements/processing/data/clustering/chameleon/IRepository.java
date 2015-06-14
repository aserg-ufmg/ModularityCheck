package br.ufmg.dcc.labsoft.aserg.modularitycheck.enhancements.processing.data.clustering.chameleon;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public interface IRepository {

	public void readCommits(String path) throws FileNotFoundException,
			IOException;

	public ArrayList<String> getPackages();

	public Map<Integer, Integer[]> getMapping();

	public Map<String, Integer[]> openClusterCommits()
			throws FileNotFoundException, IOException;

	public Map<String, ArrayList<String>> openTestCommits()
			throws FileNotFoundException, IOException;

	public void filterCommits() throws FileNotFoundException, IOException;

	public void updateMapping(ArrayList<Integer> removedIndexes);

	public void setEdges(ArrayList<String[]> edge);

	public void setVertexes(ArrayList<String> vertex);

	public Integer[] readCommit(String absolutePath)
			throws FileNotFoundException, IOException;

	public void read(File commitFile, StringBuilder cache, File path)
			throws FileNotFoundException, IOException;
}
