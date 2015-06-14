package br.ufmg.dcc.labsoft.aserg.modularitycheck.enhancements.processing.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.ufmg.dcc.labsoft.aserg.modularitycheck.enhancements.properties.util.Properties;
import br.ufmg.dcc.labsoft.aserg.modularitycheck.enhancements.properties.util.Utils;
import data.handler.CarryFileMemory;

public class PackageExtraction {
	private static final String PACKAGE = "\\PackagesCommits.data";

	/***
	 * Reads the file commits and save the packages to run apriori program
	 * 
	 * @param fullPath
	 */
	public static void retrievePackages(String fullPath) {
		File[] files = new File(fullPath).listFiles();
		StringBuilder cache = new StringBuilder();
		for (File file : files) {
			readFileCommit(file, cache);
		}
		try {
			Utils.writeFile(cache.toString(), files[0].getParentFile()
					.getParent().toString()
					+ PACKAGE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/***
	 * Read and select each commit file
	 * 
	 * @param file
	 * @param cache
	 */
	private static void readFileCommit(File file, StringBuilder cache) {
		String[] openedFile = null;
		String pkgName = null;
		List<String> packages = new ArrayList<String>();

		try {
			openedFile = new CarryFileMemory(file.getAbsolutePath())
					.carryCompleteFile();
			for (String line : openedFile) {
				if (Utils.isValid(line)) {
					pkgName = Utils.readPackage(line);
					if (!packages.contains(pkgName))
						packages.add(pkgName);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		storeCommit(cache, packages);
	}

	/***
	 * Insert the changed packages in the StringBuilder representing a commit
	 * 
	 * @param cache
	 * @param packages
	 */
	private static void storeCommit(StringBuilder cache, List<String> packages) {
		for (String pkgName : packages)
			cache.append(pkgName).append(Properties.COMMA);
		if (packages.size() > 0) {
			cache.deleteCharAt(cache.lastIndexOf(Properties.COMMA));
			cache.append(Properties.NEW_LINE);
		}
	}
}
