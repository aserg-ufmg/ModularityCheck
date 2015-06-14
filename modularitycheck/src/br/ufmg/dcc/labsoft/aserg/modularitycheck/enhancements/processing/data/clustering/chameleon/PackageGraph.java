package br.ufmg.dcc.labsoft.aserg.modularitycheck.enhancements.processing.data.clustering.chameleon;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import br.ufmg.dcc.labsoft.aserg.modularitycheck.bugparser.parser.Parser;
import br.ufmg.dcc.labsoft.aserg.modularitycheck.enhancements.metrics.Focus;

import data.handler.CarryFileMemory;

import br.ufmg.dcc.labsoft.aserg.modularitycheck.enhancements.enhancement.Enhancement;
import br.ufmg.dcc.labsoft.aserg.modularitycheck.enhancements.properties.util.Properties;
import br.ufmg.dcc.labsoft.aserg.modularitycheck.enhancements.properties.util.Utils;

public class PackageGraph {
	private static double[][] packageGraph;
	private static int edgeNumber;
	private static int size;
	private static int countVertexes;
	private static double maximumWeight;
	private static ArrayList<String> keys;
	public static List<String> list;

	public static final double DENSITY = 0.5;
	public static final int LIMIAR = 2;
	private static final int SUPPORT = 2;// threshold
	public static final String CLUSTER_FILE = "sparse.graph.clustering."; // nome
																			// arquivo
																			// de
																			// saida
																			// do
																			// chameleon

	private static final String R_CLASS_FILE = "packages.rlabel";
	private static final String REPORT = "testReport.csv";
	private static final String RECALL = "recallTest.csv";
	private static final String FINAL_REPORT = "finalReport.csv";
	public static int MIN_SIZE;


	public static int getCountVertexes() {
		return countVertexes;
	}



}
