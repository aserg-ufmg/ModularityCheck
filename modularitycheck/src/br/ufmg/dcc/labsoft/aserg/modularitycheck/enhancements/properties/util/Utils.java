package br.ufmg.dcc.labsoft.aserg.modularitycheck.enhancements.properties.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Utils {
	public static final String CLASS = ".java";
	public static final String JSP = ".jsp";
	public static String qualifiedName;
	public static ArrayList<String> vets = new ArrayList<String>();
	public static Map<String, ArrayList<String>> hash = new HashMap<String, ArrayList<String>>();

	/***
	 * It is called to save a file
	 * 
	 * @param content
	 * @param path
	 * @throws IOException
	 */
	public static void writeFile(String content, String path)
			throws IOException {
		Writer writer = new BufferedWriter(new FileWriter(new File(path)));
		writer.write(content);
		writer.close();
	}

	/***
	 * Retrieve the full qualified name
	 * 
	 * @param line
	 * @return
	 */
	public static String getClass(String line) {
		StringBuilder pkg = new StringBuilder();

		if (Utils.isValid(line) && line.contains("/trunk/")) {
			line = line.trim();
			String[] pgk = line.split(Properties.BAR);

			for (int in = 0; in < pgk.length; in++) {
				pkg.append(pgk[in]).append(Properties.PERIOD);
			}
		}

		return pkg.toString();
	}

	/**
	 * Get the name of the package
	 * 
	 * @param line
	 * @return
	 */
	public static String getPackage(String line) {
		StringBuilder getPackg = new StringBuilder();
		String[] packageTerms = getClass(line).split(Properties.BAR);
		for (int i = 1; i < packageTerms.length - 1; i++) {
			getPackg.append(packageTerms[i]).append(Properties.PERIOD);
		}
		getPackg.deleteCharAt(getPackg.length() - 1);
		getPackg.append(Properties.NEW_LINE);
		return getPackg.toString();
	}

	/***
	 * Verify if the issue and commit files are the same. Otherwise, the files
	 * which differ from each other are deleted.
	 * 
	 */
	public static void compareFiles() {
		File[] comments = new File(Properties.getAbsolutPath()
				+ Properties.COMMENTS_ISSUES).listFiles();
		File[] files = new File(Properties.getAbsolutPath()
				+ Properties.FILES_COMMITS).listFiles();
		int i = 0;
		int j = 0;
		while (i < files.length && j < comments.length) {
			if (!comments[j].getName().equals(files[i].getName())) {
				files[i].delete();
			} else
				j++;
			i++;
		}
	}

	public static boolean isValid(String line) {
		line = line.trim();
		return (line.endsWith(JSP) || line.endsWith(CLASS)
				&& (!line.contains(Properties.FILTER_TESTS[0])
						&& !line.contains(Properties.FILTER_TESTS[1])
						&& !line.contains(Properties.FILTER_TESTS[2])
						&& !line.contains(Properties.FILTER_TESTS[3])
						&& !line.contains(Properties.FILTER_TESTS[4])
						&& !line.contains(Properties.FILTER_TESTS[5])
						&& !line.contains(Properties.FILTER_TESTS[6])
						&& !line.contains(Properties.FILTER_TESTS[7]) && !line
							.contains(Properties.FILTER_TESTS[8])));
	}



	public static String replaceChar(String term) {
		return term.replace("\"", "").trim();
	}

	private static void splitName() {

		if (qualifiedName.contains("src")) {

			int index = qualifiedName.lastIndexOf("src");
			qualifiedName = qualifiedName.substring(index);

			String[] temp = qualifiedName.split("src");
			qualifiedName = temp[temp.length - 1];
			qualifiedName = qualifiedName.substring(qualifiedName
					.indexOf(Properties.BAR));
		} else if (qualifiedName.contains("WEB-INF"))
			qualifiedName = qualifiedName.split("WEB-INF")[1];

	
	}

	public static String readPackage(String line) {



		qualifiedName = line;
		splitName();
		line = qualifiedName;
		StringBuilder pkg = new StringBuilder();
		if (qualifiedName != null) {
			String[] pgk = line.split(Properties.BAR);

			for (int in = 0; in < pgk.length - 1; in++) {
				pkg.append(pgk[in]).append(Properties.BAR);
			}
			if (pkg.lastIndexOf(Properties.BAR) >= 0)
				pkg.deleteCharAt(pkg.lastIndexOf(Properties.BAR));
		}
		return pkg.toString();
	}

	public static String readClass(String line) {

		qualifiedName = line;
		splitName();
		line = qualifiedName;

	StringBuilder pkg = new StringBuilder();
		if (qualifiedName != null) {
			String[] pgk = line.trim().split(Properties.BAR);
			for (int in = 0; in < pgk.length; in++) {
				pkg.append(pgk[in]).append(Properties.BAR);
			}
			pkg.deleteCharAt(pkg.lastIndexOf(Properties.BAR));
		}
		return pkg.toString();
	}

	public static void print() {
		StringBuilder cache = new StringBuilder();
		Iterator<String> it = hash.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			cache.append(hash.get(key).toString()).append("\n");
		}

		try {
			Utils.writeFile(cache.toString(), Properties.getResultPath()
					+ "classes.data");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
