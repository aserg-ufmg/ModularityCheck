package br.ufmg.dcc.labsoft.aserg.modularitycheck.bugparser.util;

import java.io.File;

public class FileUtils {

	private FileUtils() {
	}

	public static void deleteRecursive(File file) {
		if (file.exists()) {
			if (file.isDirectory()) {
				for (File f : file.listFiles()) {
					deleteRecursive(f);
				}
			}
			boolean deleted = file.delete();
			if (deleted)
				System.out.println("      deleted " + file.getAbsolutePath());
			else
				System.out.println("  NOT deleted " + file.getAbsolutePath());
		}
	}
}
