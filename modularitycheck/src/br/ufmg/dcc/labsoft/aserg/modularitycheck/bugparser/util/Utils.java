package br.ufmg.dcc.labsoft.aserg.modularitycheck.bugparser.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class Utils {
	public static final String CLASS = ".java";
	public static final String JSP = ".jsp";

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
		// String[] className = line.split(Properties.BAR);
		// return
		// className[className.length-5]+className[className.length-4]+className[className.length-3]+
		// Properties.PERIOD+className[className.length-2]+ Properties.PERIOD+
		// className[className.length-1];
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
		return ((line.endsWith(CLASS) || line.endsWith(JSP)) && (!line
				.contains(Properties.FILTER_TESTS[0])
				&& !line.contains(Properties.FILTER_TESTS[1])
				&& !line.contains(Properties.FILTER_TESTS[2])
				&& !line.contains(Properties.FILTER_TESTS[3])
				&& !line.contains(Properties.FILTER_TESTS[4])
				&& !line.contains(Properties.FILTER_TESTS[5]) && !line
					.contains(Properties.FILTER_TESTS[6])));
	}

	public static boolean isTest(String line) {
		return ((!line.contains(Properties.FILTER_TESTS[0])
				&& !line.contains(Properties.FILTER_TESTS[1])
				&& !line.contains(Properties.FILTER_TESTS[2])
				&& !line.contains(Properties.FILTER_TESTS[3])
				&& !line.contains(Properties.FILTER_TESTS[4])
				&& !line.contains(Properties.FILTER_TESTS[5]) && !line
					.contains(Properties.FILTER_TESTS[6])));
	}

	public static String replaceChar(String term) {
		return term.replace("\"", "").trim();
	}

	public static String readPackage(String line) {
		// if(line.contains("org/apache/")) line =
		// "org/apache/"+line.split("org/apache/")[1];//lucene
		// else if(line.contains("src/java")) line =
		// "src/java"+line.split("src/java")[1];//lucene

		// if(line.contains("org/argouml")) line =
		// "org/argouml"+line.split("org/argouml")[1];
		if (line.contains("org/apache/geronimo"))
			line = "org/apache/geronimo" + line.split("org/apache/geronimo")[1];
		else if (line.contains("src/main"))
			line = "src/main" + line.split("src/main")[1];// Geronimo
		else if (line.contains("src/java"))
			line = "src/java" + line.split("src/java")[1];// lucene
		else if (line.contains("WEB-INF"))
			line = line.split("WEB-INF")[1];
		else if (line.contains("src/webapp"))
			line = "src/webapp" + line.split("src/webapp")[1];// Geronimo
		else
			System.out.println(line);

		String[] pgk = line.split(Properties.BAR);
		StringBuilder pkg = new StringBuilder();
		for (int in = 0; in < pgk.length - 1; in++) {
			pkg.append(pgk[in]).append(Properties.BAR);
		}
		pkg.deleteCharAt(pkg.lastIndexOf(Properties.BAR));
		return pkg.toString();
	}

	public static String readClass(String line) {
		// if(line.contains("org/apache/")) line =
		// "org/apache/"+line.split("org/apache/")[1];
		// if(line.contains("org/argouml")) line =
		// "org/argouml"+line.split("org/argouml")[1];
		if (line.contains("org/apache/geronimo"))
			line = "org/apache/geronimo" + line.split("org/apache/geronimo")[1];
		else if (line.contains("src/main"))
			line = "src/main" + line.split("src/main")[1];// Geronimo
		else if (line.contains("src/java"))
			line = "src/java" + line.split("src/java")[1];// lucene
		else if (line.contains("WEB-INF"))
			line = line.split("WEB-INF")[1];
		else if (line.contains("src/webapp"))
			line = "src/webapp" + line.split("src/webapp")[1];// Geronimo
		else
			System.out.println(line);

		// if(line.contains("org/apache/")) line =
		// "org/apache/"+line.split("org/apache/")[1];//lucene
		// else if(line.contains("src/java")) line =
		// "src/java"+line.split("src/java")[1];//lucene

		// if(line.contains("org/apache/geronimo")) line =
		// "org/apache/geronimo"+line.split("org/apache/geronimo")[1];
		//
		// if(line.contains("org/argouml/")) line =
		// "org/argouml/"+line.split("org/argouml/")[1];//lucene
		// else if(line.contains("org/apache/")) line =
		// "org/apache/"+line.split("org/apache/")[1];//lucene e geronimo
		// else if(line.contains("src/main")) line =
		// "src/main"+line.split("src/main")[1];//geronimo
		// else if(line.contains("src/java")) line =
		// "src/java"+line.split("src/java")[1];//lucene e geronimo
		// else if(line.contains("WEB-INF")) line = line.split("WEB-INF")[1];
		// else System.out.println(line);

		String[] pgk = line.trim().split(Properties.BAR);
		StringBuilder pkg = new StringBuilder();
		for (int in = 0; in < pgk.length; in++) {
			pkg.append(pgk[in]).append(Properties.BAR);
		}
		pkg.deleteCharAt(pkg.lastIndexOf(Properties.BAR));
		return pkg.toString();
	}

}
