package br.ufmg.dcc.labsoft.aserg.modularitycheck.enhancements.processing.data.clustering.chameleon;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;

import br.ufmg.dcc.labsoft.aserg.modularitycheck.enhancements.properties.util.Properties;
import br.ufmg.dcc.labsoft.aserg.modularitycheck.enhancements.properties.util.Utils;
import data.handler.CarryFileMemory;

public class SVNCommit extends Commits {
	/****
	 * Read the commit and split packages
	 * 
	 * @param absolutePath
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public Integer[] readCommit(String absolutePath)
			throws FileNotFoundException, IOException {
		String[] openedFile = new CarryFileMemory(absolutePath)
				.carryCompleteFile();
		String name = null;
		ArrayList<String> associatedPackages = new ArrayList<String>();
		Integer[] indexes;

		for (String line : openedFile) {
			// if(Utils.isValid(line) && line.contains(Properties.TRUNK)){
			if (Utils.isValid(line) && line.contains(Properties.TRUNK)) {
				// name = Utils.readPackage(line.split(Properties.TRUNK)[1]);
				name = Utils.readClass(line.split(Properties.TRUNK)[1]);

				if (!packages.contains(name))
					packages.add(name);
				if (!associatedPackages.contains(name))
					associatedPackages.add(name);
			}
		}
		// System.out.println(absolutePath + " \t" +
		// associatedPackages.toString());
		indexes = new Integer[associatedPackages.size()];
		int i = 0;
		for (String nm : associatedPackages) {
			indexes[i] = packages.indexOf(nm);
			i++;
		}

		return indexes;
	}

	public void read(File commitFile, StringBuilder cache, File path)
			throws FileNotFoundException, IOException {
		String absolutePath = commitFile.getAbsolutePath();
		String[] openedFile = new CarryFileMemory(absolutePath)
				.carryCompleteFile();
		String name = null;

		ArrayList<String> associatedPackages = new ArrayList<String>();

		for (String line : openedFile) {
			if (Utils.isValid(line) && line.contains(Properties.TRUNK)) {
				name = Utils.readClass(line.split(Properties.TRUNK)[1]);

				if (!associatedPackages.contains(name)
						&& vertexes.contains(name))
					associatedPackages.add(name);
			}
		}
		if (associatedPackages.size() > 1) {
			cache.append(absolutePath).append(Properties.NEW_LINE);
			File file = new File(absolutePath);
			File commentsPath = new File(new File(Properties.getFilesPath())
					.getParentFile().getAbsolutePath(),
					Properties.COMMENTS_ISSUES + Properties.SEPARATOR
							+ file.getName());

			FileUtils.copyFile(commentsPath, new File(path.getAbsolutePath()
					+ Properties.COMMENTS_CLUSTER + Properties.getClusterId(),
					file.getName()));
			FileUtils.copyFile(file, new File(path.getAbsolutePath()
					+ Properties.FILES_CLUSTER + Properties.getClusterId(),
					file.getName()));
		}

	}
}
