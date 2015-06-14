package br.ufmg.dcc.labsoft.aserg.modularitycheck.enhancements.processing.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import data.handler.CarryFileMemory;

import br.ufmg.dcc.labsoft.aserg.modularitycheck.enhancements.properties.util.Properties;
import br.ufmg.dcc.labsoft.aserg.modularitycheck.enhancements.properties.util.Utils;

public class Partitioning {

	public static int getBestPartitioning(String path, String filePath)
			throws FileNotFoundException, IOException {
		Properties.setAbsolutePath(path);
		Properties.setFilesPath(filePath);
		return getPackages();
	}

	private static int getPackages() throws FileNotFoundException, IOException {
		ArrayList<String> packages = new ArrayList<String>();
		String[] commits = new File(Properties.getFilesPath()).list();
		for (String commit : commits) {
			String[] clusterContent = new CarryFileMemory(
					Properties.getFilesPath() + commit).carryCompleteFile();
			for (String line : clusterContent) {
				if (Utils.isValid(line)) {
					line = Utils.getPackage(line);
					if (!packages.contains(line))
						packages.add(line);
				}
			}
		}
		System.out.println(packages.toString());
		return packages.size();
	}
}
