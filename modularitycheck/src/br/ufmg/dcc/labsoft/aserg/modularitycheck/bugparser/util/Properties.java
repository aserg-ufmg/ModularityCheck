package br.ufmg.dcc.labsoft.aserg.modularitycheck.bugparser.util;

import java.io.File;

public final class Properties {

	private static String ABSOLUTE_PATH;
	private static String FILES_PATH; // address of the commit files
	private static String RESULT_PATH;
	private static String REPORT_PATH;
	private static String CLUSTER_PATH;
	private static String LSI_PATH;

	public static final String WEIGHT_EDGES = "weightEdges.csv";
	public static final String WEIGHT_EDGES_CLUSTER = "weightEdgesCluster.csv";
	private static final String REPORT_DIRECTORY = "Report";
	public static final String REPORT_FILE_NAME = "report";
	public static final String VOCABULARY = "vocabulary.data";
	public static final String VOCABULARY_EXTRACTION_PAM_FILE = "output.stem";
	public static final String CLUTO_GRAPH = "sparse.graph";
	public static final String PAM_FILE_NAME = "PamClusterOrdenado.data";
	public static final String PAM_LSI_FILE_NAME = "PamClusterOrdenadoLSI.data";
	public static final String LDA_FILE_NAME = "resultLDA.data";
	public static final String PAM_DIRECTORY_NAME = "Clusters";
	public static final String PAM_LSI_DIRECTORY_NAME = "ClustersLSI";
	public static final String PAM_CLUSTER_FILE_NAME = "ClusterFinal";
	public static final String LDA_DIRECTORY_NAME = "Topics";
	public static final String COMMENTS_ISSUES = "comments";
	public static final String FILES_COMMITS = "files";
	public static final String TRAINING_PHASE = "Training";
	public static final String SRC = "src";
	public static final String PAM_CLUSTER_FILE = "cluster";
	public static final String LDA_TOPIC_FILE = "Topic";
	public static final String ISSUE = "Issue";
	public static final String TRUNK = "/trunk/";
	public static final String[] FILTER_TESTS = { "testsuite", "/test/",
			"/test-", "testsupport", "/testing", "/tests", "/junit" };
	
	public static final String SIZE_DATA = "size.data";
	public static final String DENSITY_DATA = "density.data";
	public static final String FOCUS_DATA = "focus.data";
	public static final String SPREAD_DATA = "spread.data";
	public static final String WEIGHT_DATA = "weight.data";

	public static final String SEPARATOR = System.getProperty("file.separator");
	public static final String NEW_LINE = System.getProperty("line.separator");
	public static final String TAB = "\t";
	public static final String ARROW = "--";
	public static final String PERIOD = ".";
	public static final String COMMA_PERIOD = ";";
	public static final String BAR = "/";
	public static final String COMMA = ",";
	public static final String EQ = "=";
	public static final String BLANK = " ";
	public static final String DOT = "[.]";
	public static final String ESCAPE = "\"";
	public static final String EMPTY = "";
	public static final String DOTS = ":";
	public static final String CLUSTER_GLUTO = "CLUSTERS_GLUTO.data";
	public static final String CLUSTER_COMMITS = "CLUSTER_COMMIT.DATA";
	public static final String COMMENTS_CLUSTER = SEPARATOR + "commentsC";
	public static final String FILES_CLUSTER = SEPARATOR + "filesC";
	private static String clusterId;

	/**
	 * Sets all valid paths
	 * 
	 * @param absolutePath
	 */
	public static void setDefaultPaths(String absolutePath) {
		ABSOLUTE_PATH = absolutePath + SEPARATOR;
		RESULT_PATH = ABSOLUTE_PATH + "Results" + SEPARATOR;
		checkFolder(RESULT_PATH);
	}

	public static void setFilesPath(String directory) {
		FILES_PATH = ABSOLUTE_PATH + directory + SEPARATOR;
	}

	public static void setAbsolutePath(String absolutePath) {
		ABSOLUTE_PATH = absolutePath + SEPARATOR;
	}

	public static void setClusterId(String id) {
		clusterId = id;
	}

	public static String getClusterId() {
		return clusterId;
	}

	/***
	 * Set the path where the clusters will be stored
	 * 
	 * @param folderName
	 */
	public static void setClusterPath(String folderName) {
		CLUSTER_PATH = RESULT_PATH + folderName + SEPARATOR;
		checkFolder(CLUSTER_PATH);
	}

	/***
	 * Verify if the folder already exists. On the other hand, a new folder is
	 * created.
	 * 
	 * @param folderName
	 */
	public static void checkFolder(String folderName) {
		File resultFolder = new File(folderName);
		if (!resultFolder.exists())
			System.out.println(resultFolder.mkdirs());
	}

	public static String getClusterPath() {
		return CLUSTER_PATH;
	}

	public static String getAbsolutPath() {
		return ABSOLUTE_PATH;
	}

	public static String getFilesPath() {
		return FILES_PATH;
	}

	public static String getResultPath() {
		return RESULT_PATH;
	}

	public static String getReportPath() {
		return REPORT_PATH;
	}

	public static void setReportPath() {
		REPORT_PATH = RESULT_PATH + REPORT_DIRECTORY + SEPARATOR;
		checkFolder(REPORT_PATH);
	}

	public static void setTestFilePath() {
		setFilesPath("filesTest");
	}

	public static void setResultPathLSI() {
		LSI_PATH = RESULT_PATH + "LSI" + SEPARATOR;
		checkFolder(LSI_PATH);
	}

	public static String getResultPathLSI() {
		return LSI_PATH;
	}

	/***
	 * Set the root where the chameleon clusters are
	 * 
	 * @param cluster
	 */
	public static void setClustersPath(String cluster) {
		CLUSTER_PATH = cluster;
	}

	/****
	 * Returns the root where the chameleon clusters are
	 * 
	 * @return
	 */
	public static String getClustersPath() {
		return CLUSTER_PATH;
	}
}
